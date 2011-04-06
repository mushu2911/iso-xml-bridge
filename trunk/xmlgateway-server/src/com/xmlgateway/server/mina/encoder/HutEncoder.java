/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.mina.encoder;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 *
 * @author ifnu
 */
public class HutEncoder implements ProtocolEncoder {

    private final Charset charset;

    public HutEncoder(Charset charset) {
        this.charset = charset;
    }

        private final AttributeKey ENCODER = new AttributeKey(getClass(), "encoder");

    private int maxLineLength = Integer.MAX_VALUE;

    /**
     * Returns the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     */
    public int getMaxLineLength() {
        return maxLineLength;
    }

    /**
     * Sets the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     */
    public void setMaxLineLength(int maxLineLength) {
        if (maxLineLength <= 0) {
            throw new IllegalArgumentException("maxLineLength: "
                    + maxLineLength);
        }

        this.maxLineLength = maxLineLength;
    }

    @Override
    public void encode(IoSession session, Object message,
            ProtocolEncoderOutput out) throws Exception {
        CharsetEncoder encoder = (CharsetEncoder) session.getAttribute(ENCODER);
        if (encoder == null) {
            encoder = charset.newEncoder();
            session.setAttribute(ENCODER, encoder);
        }

        String value = message.toString();
        IoBuffer buf = IoBuffer.allocate(value.length())
                .setAutoExpand(true);
        buf.putString(value, encoder);
        if (buf.position() > maxLineLength) {
            throw new IllegalArgumentException("Line length: " + buf.position());
        }
//        buf.putString(delimiter.getValue(), encoder);
        buf.flip();
        out.write(buf);
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        //do nothing
    }
}
