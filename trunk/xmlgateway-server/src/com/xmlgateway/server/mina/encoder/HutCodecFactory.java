/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.mina.encoder;

import java.nio.charset.Charset;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 *
 * @author ifnu
 */
public class HutCodecFactory implements ProtocolCodecFactory{

    private final HutDecoder decoder;
    private final HutEncoder encoder;

    public HutCodecFactory() {
        this(Charset.defaultCharset());
    }

    public HutCodecFactory(Charset charset) {
        encoder = new HutEncoder(charset);
        decoder = new HutDecoder(charset);
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) {
        return decoder;
    }
}
