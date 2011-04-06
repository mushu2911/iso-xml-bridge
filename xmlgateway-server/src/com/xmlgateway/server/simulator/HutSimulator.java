/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.simulator;

import com.xmlgateway.api.model.XmlData;
import com.xmlgateway.api.transformer.XmlTransformer;
import com.xmlgateway.server.XmlGatewayServerManager;
import com.xmlgateway.server.configuration.Connector;
import com.xmlgateway.server.configuration.ServerConfiguration;
import com.xmlgateway.server.mina.encoder.HutCodecFactory;
import com.xmlgateway.server.xml.XPathHandler;
import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.xml.sax.SAXException;

/**
 *
 * @author ifnu
 */
public class HutSimulator implements IoHandler  {
    private static final Log LOGGER = LogFactory.getLog(HutSimulator.class);

    public static void main(String[] args) throws Exception {
        XmlGatewayServerManager manager = new XmlGatewayServerManager();
        manager.initializeConfiguration("conf/server.xml");
        ServerConfiguration configuration = manager.getServerConfiguration();
        Connector connector = configuration.getConnectors().get(0);
        IoAcceptor acceptor = new NioSocketAcceptor();
//        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        HutCodecFactory factory = new HutCodecFactory(Charset.forName("UTF-8"));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
        acceptor.setHandler(new HutSimulator());
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        acceptor.bind(new InetSocketAddress(connector.getOutbound().getIp(),
                connector.getOutbound().getPort()));
        LOGGER.info("HUT simulator " + connector.getId() + " started at " +
                connector.getOutbound().getIp() + "@" + connector.getOutbound().getPort());
    }

    private SAXParser parser;

    public HutSimulator() {
        try {
            parser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (SAXException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        System.out.println("Client connect " + session.getRemoteAddress().toString());
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("Open session " + session.getRemoteAddress().toString());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("Client say goodbye " + session.getRemoteAddress().toString());
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus is1) throws Exception {
        System.out.println("Client iddle " + session.getRemoteAddress().toString());
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable ex) throws Exception {
        System.out.println("Client error " + session.getRemoteAddress().toString());
        ex.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object msg) throws Exception {
        String xml = msg.toString();
        System.out.println("1. Message Recieved from : " + session.getRemoteAddress().toString() + "\nMessage " + xml);
        XPathHandler handler = new XPathHandler();
        parser.parse(new ByteArrayInputStream(xml.getBytes()), handler);
        String xmlResponse = createResponse(handler.getXmlData());
        System.out.println("2. Message Response to : " + session.getRemoteAddress().toString() + "\nMessage " + xmlResponse);
        session.write(xmlResponse);
    }

    public String createResponse(XmlData xmlData){

        if(xmlData.getXmlValue("/OnlineRequest/serviceAddressing/Action")
                .equals(XmlTransformer.XML_PAYMENT_CODE)){
            StringBuilder builder = new StringBuilder();
            builder.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
            builder.append("<!DOCTYPE OnlineRequest SYSTEM \"OnlineRequest.dtd\">");
            builder.append("<OnlineRequest>");
            builder.append("<serviceAddressing>");
            builder.append("<From>"+xmlData.getXmlValue("/OnlineRequest/serviceAddressing/From")+"</From>");
            builder.append("<MerchantType>"+xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MerchantType")+"</MerchantType>");
            builder.append("<MessageId>"+xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MessageId")+"</MessageId>");
            builder.append("<Action>"+XmlTransformer.XML_PAYMENT_CODE+"</Action>");
            builder.append("</serviceAddressing>");
            builder.append("<MSISDN>"+xmlData.getXmlValue("/OnlineRequest/MSISDN")+"</MSISDN>");
            builder.append("<BillingResponse>");
            builder.append("<ResponseResult>");
            builder.append("<ResponseCode>00000</ResponseCode>");
            builder.append("<ResponseDescription>Approve</ResponseDescription>");
            builder.append("<ResponseAction>null</ResponseAction>");
            builder.append("</ResponseResult>");
            builder.append("<CurrBillnum>"+xmlData.getXmlValue("/OnlineRequest/BillingResponse/CurrBillnum")+"</CurrBillnum>");
            builder.append("<TotBillAmtDue>0</TotBillAmtDue>");
            builder.append("<Accountholder>"+xmlData.getXmlValue("/OnlineRequest/BillingResponse/Accountholder")+"</Accountholder>");
            builder.append("<TransactionId>"+String.valueOf(System.currentTimeMillis()).substring(0, 12)+"</TransactionId>");
            builder.append("<GenevaPayStatus>null</GenevaPayStatus>");
            builder.append("<Date>"+xmlData.getXmlValue("/OnlineRequest/BillingResponse/Date")+"</Date>");
            builder.append("<DueDate>"+xmlData.getXmlValue("/OnlineRequest/BillingResponse/DueDate")+"</DueDate>");
            builder.append("<ATMCode>"+xmlData.getXmlValue("/OnlineRequest/BillingResponse/ATMCode")+"</ATMCode>");
            builder.append("<Amount>0</Amount>");
            builder.append("</BillingResponse>");
            builder.append("</OnlineRequest>");
            return builder.toString();
        } else if(xmlData.getXmlValue("/OnlineRequest/serviceAddressing/Action")
                .equals(XmlTransformer.XML_INQUIRY_CODE)){
            StringBuilder builder = new StringBuilder();
            builder.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
            builder.append("<!DOCTYPE OnlineRequest SYSTEM \"OnlineRequest.dtd\">");
            builder.append("<OnlineRequest>");
            builder.append("<serviceAddressing>");
            builder.append("<From>"+xmlData.getXmlValue("/OnlineRequest/serviceAddressing/From")+"</From>");
            builder.append("<MerchantType>"+xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MerchantType")+"</MerchantType>");
            builder.append("<MessageId>"+xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MessageId")+"</MessageId>");
            builder.append("<Action>"+XmlTransformer.XML_INQUIRY_CODE+"</Action>");
            builder.append("</serviceAddressing>");
            builder.append("<MSISDN>"+xmlData.getXmlValue("/OnlineRequest/MSISDN")+"</MSISDN>");
            builder.append("<BillingResponse>");
            builder.append("<ResponseResult>");
            builder.append("<ResponseCode>00000</ResponseCode>");
            builder.append("<ResponseDescription>Approve</ResponseDescription>");
            builder.append("<ResponseAction>null</ResponseAction>");
            builder.append("</ResponseResult>");
            builder.append("<CurrBillnum>0000000226</CurrBillnum>");
            builder.append("<TotBillAmtDue>55000</TotBillAmtDue>");
            builder.append("<Accountholder>Test Uat Vip Post 6</Accountholder>");
            builder.append("<TransactionId>"+String.valueOf(System.currentTimeMillis()).substring(0, 12)+"</TransactionId>");
            builder.append("<GenevaPayStatus>null</GenevaPayStatus>");
            builder.append("<Date>"+xmlData.getXmlValue("/OnlineRequest/BillingResponse/Date")+"</Date>");
            builder.append("<DueDate>"+DateTimeFormat.forPattern("ddMMyyyy").print(new DateTime().plusDays(20))+"</DueDate>");
            builder.append("<ATMCode>"+xmlData.getXmlValue("/OnlineRequest/BillingResponse/ATMCode")+"</ATMCode>");
            builder.append("<Amount>0</Amount>");
            builder.append("</BillingResponse>");
            builder.append("</OnlineRequest>");
            return builder.toString();
        } else if(xmlData.getXmlValue("/OnlineRequest/serviceAddressing/Action")
                .equals(XmlTransformer.XML_CANCEL_PAYMENT_CODE)){
            StringBuilder builder = new StringBuilder();
            builder.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
            builder.append("<!DOCTYPE OnlineRequest SYSTEM \"OnlineRequest.dtd\">");
            builder.append("<OnlineRequest>");
            builder.append("<serviceAddressing>");
            builder.append("<From>"+xmlData.getXmlValue("/OnlineRequest/serviceAddressing/From")+"</From>");
            builder.append("<MerchantType>"+xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MerchantType")+"</MerchantType>");
            builder.append("<MessageId>"+xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MessageId")+"</MessageId>");
            builder.append("<Action>"+XmlTransformer.XML_CANCEL_PAYMENT_CODE+"</Action>");
            builder.append("</serviceAddressing>");
            builder.append("<MSISDN>"+xmlData.getXmlValue("/OnlineRequest/MSISDN")+"</MSISDN>");
            builder.append("<BillingResponse>");
            builder.append("<ResponseResult>");
            builder.append("<ResponseCode>00000</ResponseCode>");
            builder.append("<ResponseDescription>Approve</ResponseDescription>");
            builder.append("<ResponseAction>null</ResponseAction>");
            builder.append("</ResponseResult>");
            builder.append("<CurrBillnum>null</CurrBillnum>");
            builder.append("<TotBillAmtDue>0</TotBillAmtDue>");
            builder.append("<Accountholder>Test Uat Vip Post 6</Accountholder>");
            builder.append("<TransactionId>"+String.valueOf(System.currentTimeMillis()).substring(0, 12)+"</TransactionId>");
            builder.append("<GenevaPayStatus>null</GenevaPayStatus>");
            builder.append("<Date>"+xmlData.getXmlValue("/OnlineRequest/BillingResponse/Date")+"</Date>");
            builder.append("<DueDate>"+DateTimeFormat.forPattern("ddMMyyyy").print(new DateTime().plusDays(20))+"</DueDate>");
            builder.append("<ATMCode>"+xmlData.getXmlValue("/OnlineRequest/BillingResponse/ATMCode")+"</ATMCode>");
            builder.append("<Amount>000000055000</Amount>");
            builder.append("</BillingResponse>");
            builder.append("</OnlineRequest>");
            return builder.toString();
        }
        return "";
    }

    @Override
    public void messageSent(IoSession is, Object msg) throws Exception {
//        System.out.println("Message sent " + msg.toString());
    }

}
