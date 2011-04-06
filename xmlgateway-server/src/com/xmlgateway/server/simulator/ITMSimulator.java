/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.simulator;

import com.xmlgateway.server.configuration.Connector;
import com.xmlgateway.server.configuration.ServerConfiguration;
import com.xmlgateway.server.helper.ISOMsgHelper;
import com.xmlgateway.server.helper.PackagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequest;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.iso.channel.PostChannel;
import org.jpos.iso.packager.GenericPackager;

/**
 *
 * @author ifnu
 */
public class ITMSimulator implements ISORequestListener  {

    private static final Log LOGGER = LogFactory.getLog(ITMSimulator.class);

    public static void main(String[] args) throws ISOException {
        new ITMSimulator();
    }

    private ISOMUX isomux;
    private ServerConfiguration conf;

    private final Map<String,String> billInquiryProcessingCodeMap = new HashMap<String, String>();
    private final Map<String,String> billPaymentProcessingCodeMap = new HashMap<String, String>();
    private final Map<String,String> permataToItmProcessingCodeMap = new HashMap<String, String>();


    public ITMSimulator() throws ISOException {
        permataToItmProcessingCodeMap.put("320000","320000");
        permataToItmProcessingCodeMap.put("550000","320000");
        permataToItmProcessingCodeMap.put("800000","320000");
        permataToItmProcessingCodeMap.put("530000","320000");
        permataToItmProcessingCodeMap.put("560000","320000");
        permataToItmProcessingCodeMap.put("560000","320000");
        permataToItmProcessingCodeMap.put("180000","310000");
        
        billPaymentProcessingCodeMap.put("320000", "Bill Payment dari account yang tidak specific");
        billPaymentProcessingCodeMap.put("32000", "Bill Payment dari account tabungan");
        billPaymentProcessingCodeMap.put("322000", "Bill Payment dari account giro");
        billPaymentProcessingCodeMap.put("323000", "Bill Payment dari account kartu kredit");


        billInquiryProcessingCodeMap.put("310000", "Bill Inquiry dari account yang tidak specific");
        billInquiryProcessingCodeMap.put("311000", "Bill Inquiry dari account tabungan");
        billInquiryProcessingCodeMap.put("312000", "Bill Inquiry dari account giro");
        billInquiryProcessingCodeMap.put("313000", "Bill Inquiry dari account kartu kredit");

        try {
            conf = ServerConfiguration.getConfiguration("conf/server.xml");
            LOGGER.info("ITM @ " + conf.getServer().getIp() + ":" + conf.getServer().getPort());

            //connect to itm
            InputStream inputStream = new FileInputStream("conf/itm-iso-conf.xml");
            GenericPackager itmPackager = new GenericPackager(inputStream);
            PostChannel itmServerChannel = new PostChannel(conf.getServer().getIp(),conf.getServer().getPort(),itmPackager);
            LOGGER.info("ITM Simulator Listening Message @ " + conf.getServer().getIp() + ":" + conf.getServer().getPort());
            ISOServer server = new ISOServer(conf.getServer().getPort(), itmServerChannel, null);
            server.addISORequestListener(this);
            // listening for connection
            new Thread(server).start();
            Connector c = conf.getConnectors().get(0);
            LOGGER.info("Gateway @ " + c.getInbound().getIp() + ":" + c.getInbound().getPort());
            PostChannel gatewayChannel = new PostChannel(c.getInbound().getIp(), c.getInbound().getPort(), itmPackager);
            isomux = new ISOMUX(gatewayChannel) {

                @Override
                protected String getKey(ISOMsg m) throws ISOException {
                    return m.getString(11);
                }
            };
            new Thread(isomux).start();
        } catch (IOException ex) {
            LOGGER.info(ex.getMessage(),ex);
        }
    }

    @Override
    public boolean process(ISOSource source, ISOMsg msg) {
        try {
            //recieve from permata
            LOGGER.info("1. Incoming message " + ISOMsgHelper.isoMsgToString(msg));
            ISOMsg forward = (ISOMsg) msg.clone();
            String itmProcessingCode = permataToItmProcessingCodeMap.get(msg.getString(3));
            if(msg.getMTI().equals("0200")
                    && billInquiryProcessingCodeMap.containsKey(itmProcessingCode)){
                forward.unset(22);
                forward.unset(26);
                forward.unset(33);
                forward.unset(43);
                forward.unset(47);
                forward.unset(61);
                forward.unset(103);
                forward.set(3,itmProcessingCode);
                forward.set(63, "110");
                //bit48
                String bit48 = msg.getString(103);
                if(bit48!=null && bit48.startsWith("0")){
                    //TODO comply ke spek euronet
                    bit48 = ISOMsgHelper.padSpaceLeftAlign("62" + bit48.substring(1,bit48.length()),19);
                }
                forward.set(48, bit48);
                //bit14 diambil dari bit7 ditambah 2 bulan
                String bit14 = new DateTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(new DateTime().getYear() + msg.getString(7))).plusMonths(2).toString("MMdd");
                forward.set(14,bit14);
                //bit41 panjangnya 15, pad dengan 0
                forward.set(41,ISOMsgHelper.padZeroRightAlign(msg.getString(41), 15));
            } else if(msg.getMTI().equals("0200")){
                forward.unset(22);
                forward.unset(26);
                forward.unset(33);
                forward.unset(43);
                forward.unset(47);
                forward.unset(61);
                forward.unset(103);
                forward.set(3,itmProcessingCode);
                forward.set(63, "110");

                // comply ke spek euronet 2009-12-08 bab 5.17 DE-48 Additional Data – Private
                String bit103 = msg.getString(103);
                //MSISDN
                String bit48 = ISOMsgHelper.padSpaceLeftAlign("62" + bit103.substring(1,bit103.length()),19);
                //Bill Reference number
                bit48 += ISOMsgHelper.padSpaceLeftAlign("18201200038",16);
                //Customer name
                bit48 += ISOMsgHelper.padSpaceLeftAlign("Test Uat Vip Post 6",30);
                //Bill amount
                bit48 += ISOMsgHelper.padZeroRightAlign(msg.getString(4), 12);

                forward.set(48, bit48);
                forward.set(48, bit48);
                //bit14 diambil dari bit7 ditambah 2 bulan
                String bit14 = new DateTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(new DateTime().getYear() + msg.getString(7))).plusMonths(2).toString("MMdd");
                forward.set(14,bit14);
                //bit41 panjangnya 15, pad dengan 0
                forward.set(41,ISOMsgHelper.padZeroRightAlign(msg.getString(41), 15));
            }
            else if(msg.getMTI().equals("0420")){
                //ganti MTI dari 420 ke 400
                forward.setMTI("0400");
                forward.unset(33);
                forward.unset(39);
                forward.unset(47);
                forward.unset(103);
                forward.set(3,itmProcessingCode);
                String bit14 = new DateTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(new DateTime().getYear() + msg.getString(7))).plusMonths(2).toString("MMdd");
                forward.set(14,bit14);
                forward.set(41,ISOMsgHelper.padZeroRightAlign(msg.getString(41), 15));

                //TODO comply ke spek euronet 2009-12-08 bab 5.17 DE-48 Additional Data – Private
                String bit103 = msg.getString(103);
                //MSISDN
                String bit48 = ISOMsgHelper.padSpaceLeftAlign("62" + bit103.substring(1,bit103.length()),19);
                //Bill Reference number
                bit48 += ISOMsgHelper.padSpaceLeftAlign("18201200038",16);
                //Customer name
                bit48 += ISOMsgHelper.padSpaceLeftAlign("Test Uat Vip Post 6",30);
                //Bill amount
                bit48 += ISOMsgHelper.padZeroRightAlign(msg.getString(4), 12);

                forward.set(48, bit48);
                //potong bit 90 jadi 38
                String bit90 = forward.getString(90);
                bit90 = bit90.substring(0, bit90.length()-4);
                forward.set(90, bit90);
            }
            forward.setPackager(PackagerFactory.getGatewayPackager());
            LOGGER.info("2.Forward and wait to external server : \n" + ISOMsgHelper.isoMsgToString(forward));
            //forward to gateway
            ISORequest request = new ISORequest(forward);
            isomux.queue(request);
            //recieve from gateway
            ISOMsg replyFromGateway = request.getResponse(conf.getServer().getTimeout());
            if(replyFromGateway==null){
                LOGGER.info("tidak ada balasan hingga timeout");
            } else {
                LOGGER.info("3.Get reply from external server : \n" + ISOMsgHelper.isoMsgToString(replyFromGateway));
                ISOMsg forwardedMessage = (ISOMsg) replyFromGateway.clone();
                if(!replyFromGateway.getMTI().equals("0810")){
                    forwardedMessage.set(33, msg.getString(33));
                    forwardedMessage.set(47, msg.getString(47));
                    forwardedMessage.set(103, msg.getString(103));
                    forwardedMessage.set(41, replyFromGateway.getString(41).substring(0, 8));
                    forwardedMessage.unset(63);
                    forwardedMessage.unset(48);
                    forwardedMessage.set(90, msg.getString(90));
                    if(replyFromGateway.getMTI().equals("410")){
                        forwardedMessage.setMTI("430");
                    }
                }
                forwardedMessage.setPackager(PackagerFactory.getPermataPackager());
                LOGGER.info("4.Send back to euronet server : \n" + ISOMsgHelper.isoMsgToString(forwardedMessage));
                source.send(forwardedMessage);

            }
            //response to permata
            return true;
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage(),ex);
        }
        return false;
    }
 }
