/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.mina.encoder;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.RecoverableProtocolDecoderException;

/**
 *
 * @author ifnu
 */
public class HutDecoder implements ProtocolDecoder {

    private final Charset charset;

    private final String delimiter = "</OnlineRequest>";

    private final AttributeKey CONTEXT = new AttributeKey(getClass(), "context");

    private IoBuffer delimBuf;

    private int maxLineLength = Integer.MAX_VALUE;

    public HutDecoder(Charset charset) {
        this.charset = charset;
    }

    public int getMaxLineLength() {
        return maxLineLength;
    }

    public void setMaxLineLength(int maxLineLength) {
        if (maxLineLength <= 0) {
            throw new IllegalArgumentException("maxLineLength: "
                    + maxLineLength);
        }

        this.maxLineLength = maxLineLength;
    }

    @Override
    public void decode(IoSession session, IoBuffer in,
            ProtocolDecoderOutput out) throws Exception {
        Context ctx = getContext(session);
        int matchCount = ctx.getMatchCount();

        // Convert delimiter to ByteBuffer if not done yet.
        if (delimBuf == null) {
            IoBuffer tmp = IoBuffer.allocate(2).setAutoExpand(true);
            tmp.putString(delimiter, charset.newEncoder());
            tmp.flip();
            delimBuf = tmp;
        }

        // Try to find a match
        int oldPos = in.position();
        int oldLimit = in.limit();
        while (in.hasRemaining()) {
            byte b = in.get();
            if (delimBuf.get(matchCount) == b) {
                matchCount++;
                if (matchCount == delimBuf.limit()) {
                    // Found a match.
                    int pos = in.position();
                    in.limit(pos);
                    in.position(oldPos);

                    ctx.append(in);

                    in.limit(oldLimit);
                    in.position(pos);
                    if (ctx.getOverflowPosition() == 0) {
                        IoBuffer buf = ctx.getBuffer();
                        buf.flip();
                        buf.limit(buf.limit() - matchCount);
                        try {
                            writeText(session, buf.getString(ctx.getDecoder()) + delimiter, out);
                        } finally {
                            buf.clear();
                        }
                    } else {
                        int overflowPosition = ctx.getOverflowPosition();
                        ctx.reset();
                        throw new RecoverableProtocolDecoderException(
                                "Line is too long: " + overflowPosition);
                    }

                    oldPos = pos;
                    matchCount = 0;
                }
            } else {
                // fix for DIRMINA-506 & DIRMINA-536
                in.position(Math.max(0, in.position() - matchCount));
                matchCount = 0;
            }
        }

        // Put remainder to buf.
        in.position(oldPos);
        ctx.append(in);

        ctx.setMatchCount(matchCount);
    }

    private Context getContext(IoSession session) {
        Context ctx;
        ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx == null) {
            ctx = new Context();
            session.setAttribute(CONTEXT, ctx);
        }
        return ctx;
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out)
            throws Exception {
        // Do nothing
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx != null) {
            session.removeAttribute(CONTEXT);
        }
    }

    protected void writeText(IoSession session, String text, ProtocolDecoderOutput out) {
        out.write(text);
    }


    private class Context {
        private final CharsetDecoder decoder;
        private final IoBuffer buf;
        private int matchCount = 0;
        private int overflowPosition = 0;

        private Context() {
            decoder = charset.newDecoder();
            buf = IoBuffer.allocate(80).setAutoExpand(true);
        }

        public CharsetDecoder getDecoder() {
            return decoder;
        }

        public IoBuffer getBuffer() {
            return buf;
        }

        public int getOverflowPosition() {
            return overflowPosition;
        }

        public int getMatchCount() {
            return matchCount;
        }

        public void setMatchCount(int matchCount) {
            this.matchCount = matchCount;
        }

        public void reset() {
            overflowPosition = 0;
            matchCount = 0;
            decoder.reset();
        }

        public void append(IoBuffer in) {
            if (overflowPosition != 0) {
                discard(in);
            } else if (buf.position() > maxLineLength - in.remaining()) {
                    overflowPosition = buf.position();
                    buf.clear();
                    discard(in);
            } else {
                getBuffer().put(in);
            }
        }

        private void discard(IoBuffer in) {
            if (Integer.MAX_VALUE - in.remaining() < overflowPosition) {
                overflowPosition = Integer.MAX_VALUE;
            } else {
                overflowPosition += in.remaining();
            }
            in.position(in.limit());
        }
    }

}
