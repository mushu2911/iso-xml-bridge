/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.simulator;

import com.xmlgateway.server.helper.ISOMsgHelper;
import java.io.FileInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.iso.channel.PostChannel;
import org.jpos.iso.packager.GenericPackager;

/**
 *
 * @author ifnu
 */
public class BankSimulator implements ISORequestListener {
    private static final Log LOGGER = LogFactory.getLog(HutSimulator.class);

    public static void main(String[] args) throws Exception {



        PostChannel permataServerChannel = new PostChannel(new GenericPackager(new FileInputStream("conf/permata.xml")));

        LOGGER.info("Permata Simulator Listening Message  @ " + "localhost" + ":" + 9095);
        ISOServer server = new ISOServer(9095, permataServerChannel, null);

//        server.addISORequestListener(new HutSimulator());
        // listening for connection
        new Thread(server).start();

    }

    public boolean process(ISOSource source, ISOMsg msg) {
        try {
            LOGGER.info("1. Incoming message " + ISOMsgHelper.isoMsgToString(msg));
            ISOMsg reply = (ISOMsg) msg.clone();

            reply.set(39, "99");

            LOGGER.info("2. Reply message " + ISOMsgHelper.isoMsgToString(reply));
            source.send(reply);
            return true;
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage(),ex);
        }
        return false;
    }
}
