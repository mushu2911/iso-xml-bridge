/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.helper;

import com.xmlgateway.api.model.IsoData;
import com.xmlgateway.server.XmlGatewayIsoServer;
import com.xmlgateway.server.configuration.Server;
import com.xmlgateway.server.configuration.ServerConfiguration;
import com.xmlgateway.server.persistence.model.Transaksi;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.GenericPackager;

/**
 *
 * @author ifnu
 */
public class XmlIsoDataConverter {
    private static final Log LOGGER = LogFactory.getLog(XmlGatewayIsoServer.class);

    public static ISOMsg convert(IsoData isoData){
        ISOMsg msg = new ISOMsg();
        try {
            msg.setMTI(isoData.getValue(0));
            for(Integer i = 1;i<=128;i++){
                if(isoData.getValue(i)!=null){
                    msg.set(i, isoData.getValue(i));
                }
            }
        return msg;
        } catch (ISOException ex) {
            Logger.getLogger(XmlIsoDataConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static IsoData convert(ISOMsg msg){
        try {
            IsoData isoData = new IsoData();
            isoData.setValue(0, msg.getMTI());
            for (Integer i = 1; i <= 128; i++) {
                //test this
                if(msg.getValue(i) !=null){
                    isoData.setValue(i, msg.getValue(i).toString());
                }
            }
            return isoData;
        } catch (ISOException ex) {
            Logger.getLogger(XmlIsoDataConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static IsoData convertToIsoData(Transaksi transaksi){
        try {
            ISOMsg msg = new ISOMsg();
            msg.setPackager(null);
            msg.unpack(transaksi.getIso().getBytes());
            return convert(msg);
        } catch (ISOException ex) {
            Logger.getLogger(XmlIsoDataConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    public static Transaksi createTransaksiRequest(IsoData isoData){
//        Transaksi transaksi = new Transaksi();
//        transaksi.setAmount(new BigDecimal(isoData.getValue(XmlTransformer.F_AMOUNT)));
//        transaksi.setMessageId(isoData.getValue(XmlTransformer.F_AUDIT_NUMBER));
//
//        transaksi.setMsisdn(isoData.getValue(XmlTransformer.F_ADDITIONAL_DATA));
//        transaksi.setRequestResponse(RequestResponse.REQUEST);
//        transaksi.setStan(isoData.getValue(XmlTransformer.F_PAN));
//        transaksi.setStatus(Status.WAIT_FOR_RESPONSE);
//        int year = new DateTime().getYear();
//        String transactionDate = year + isoData.getValue(XmlTransformer.F_TRANSMISSION_DATE_TIME);
//        try {
//            transaksi.setTransactionDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(transactionDate));
//        } catch (ParseException ex) {
//            LOGGER.error("error parsing ",ex);
//            transaksi.setTransactionDate(new Date());
//        }
//        if(isoData.getValue(0).equals(XmlTransformer.ISO_CANCEL_PAYMENT_CODE)
//                || isoData.getValue(0).equals(XmlTransformer.ISO_CANCEL_PAYMENT_RESPONSE_CODE)){
//            transaksi.setTransaksiType(TransaksiType.CANCEL_PAYMENT);
//        } else if(isoData.getValue(0).equals(XmlTransformer.ISO_PAYMENT_CODE)
//                || isoData.getValue(0).equals(XmlTransformer.ISO_PAYMENT_RESPONSE_CODE)){
//            transaksi.setTransaksiType(TransaksiType.PAYMENT);
//        } else {
//            transaksi.setTransaksiType(TransaksiType.INQUIRY);
//        }
//        return transaksi;
//    }
    
//    public static Transaksi createPaymentResponse(Transaksi request, XmlData xmlData,String xml) {
//        Transaksi response = createDefaultTransaksi(request, xmlData, xml);
//        response.setTransaksiType(TransaksiType.PAYMENT);
//        return response;
//    }
//    public static Transaksi createCancelPaymentResponse(Transaksi request, XmlData xmlData,String xml) {
//        Transaksi response = createDefaultTransaksi(request, xmlData, xml);
//        response.setTransaksiType(TransaksiType.CANCEL_PAYMENT);
//        return response;
//    }
//    public static Transaksi createInquiryResponse(Transaksi request, XmlData xmlData,String xml) {
//        Transaksi response = createDefaultTransaksi(request, xmlData, xml);
//        response.setTransaksiType(TransaksiType.INQUIRY);
//        return response;
//    }

//    private static Transaksi createDefaultTransaksi(Transaksi request, XmlData xmlData,String xml){
//        Transaksi response = new Transaksi();
//        response.setXml(xml);
//        String messageId = xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MessageId");
//        response.setMessageId(messageId.substring(6,messageId.length()));
//        response.setAmount(new BigDecimal(xmlData.getXmlValue("/OnlineRequest/BillingResponse/Amount")));
//        response.setMsisdn(xmlData.getXmlValue("/OnlineRequest/MSISDN"));
//        response.setRequestResponse(RequestResponse.RESPONSE);
//        response.setStan(messageId.substring(6, 12));
//        response.setStatus(Status.RESPONSE);
//        try {
//            response.setTransactionDate(new SimpleDateFormat("yyyymmdd").parse("20"+messageId.substring(0, 6)));
//        } catch (ParseException ex) {
//            Logger.getLogger(XmlIsoDataConverter.class.getName()).log(Level.SEVERE, null, ex);
//            response.setTransactionDate(new Date());
//        }
//        response.setTransactionId(xmlData.getXmlValue("/OnlineRequest/BillingResponse/TransactionId"));
//        return response;
//    }

    public static IsoData createIsoData(Transaksi request,ServerConfiguration serverConfiguration) {
        String path = null;
        //start simulator
        if(serverConfiguration.getServer().getIsoConfType().equals(Server.ISO_PATH_RELATIVE)){
            String isoConfPath = serverConfiguration.getServer().getIsoConfPath().replaceAll("/", Character.toString(File.separatorChar));
            path = System.getProperty("user.dir") + File.separatorChar + isoConfPath;
        } else if(serverConfiguration.getServer().getIsoConfType().equals(Server.ISO_PATH_RELATIVE)){
            path = serverConfiguration.getServer().getIsoConfPath();
        } else {
            throw new IllegalStateException("nilai dari iso-conf[@path] adalah {"+Server.ISO_PATH_RELATIVE+","+Server.ISO_PATH_ABSOLUTE+"}" );
        }
        File packagerFile = new File(path);
        if(packagerFile.exists()){
            try {
                ISOPackager packager = new GenericPackager(new FileInputStream(packagerFile));
                ISOMsg isom = new ISOMsg();
                isom.setPackager(packager);
                isom.unpack(request.getIso().getBytes());
                return convert(isom);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(XmlIsoDataConverter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ISOException ex) {
                Logger.getLogger(XmlIsoDataConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return null;
    }

}
