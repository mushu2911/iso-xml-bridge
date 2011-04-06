/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server;

import com.xmlgateway.api.model.IsoData;
import com.xmlgateway.api.model.Status;
import com.xmlgateway.api.model.TransaksiWrapper;
import com.xmlgateway.api.transformer.XmlTransformer;
import com.xmlgateway.hut.HutTransformer;
import com.xmlgateway.server.configuration.Connector;
import com.xmlgateway.server.configuration.Server;
import com.xmlgateway.server.configuration.ServerConfiguration;
import com.xmlgateway.server.helper.ISOMsgHelper;
import com.xmlgateway.server.helper.XmlIsoDataConverter;
import com.xmlgateway.server.mina.encoder.HutCodecFactory;
import com.xmlgateway.server.persistence.model.Transaksi;
import com.xmlgateway.server.persistence.service.TransaksiService;
import com.xmlgateway.server.xml.XPathHandler;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.IoBufferWrapper;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.joda.time.DateTime;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 *
 * @author ifnu
 */
@Component
public class XmlGatewayIsoServer implements ISORequestListener,IoHandler{

    private static final Log LOGGER = LogFactory.getLog(XmlGatewayIsoServer.class);
    private ServerConfiguration serverConfiguration;
    private ConnectFuture connectFuture;
    @Autowired private TransaksiService transaksiService;
    private XmlTransformer xmlTransformer = new HutTransformer();
    private SAXParser parser;
    private Map<String,ISOSource> isoSourceMap = new HashMap<String, ISOSource>();
    private ISOPackager packager;

    public XmlGatewayIsoServer() {
        this(ServerConfiguration.getConfiguration("conf/server.xml"));
    }

    public XmlGatewayIsoServer(ServerConfiguration serverConfiguration) {
        {
            InputStream inputStream = null;
            try {
                this.serverConfiguration = serverConfiguration;
                NioSocketConnector connector = new NioSocketConnector();
                HutCodecFactory factory = new HutCodecFactory(Charset.forName("UTF-8"));
                connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
                connector.setHandler(this);
                connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 100);
                Connector c = serverConfiguration.getConnectors().get(0);
                connectFuture = connector.connect(new InetSocketAddress(c.getOutbound().getIp(), c.getOutbound().getPort()));
                parser = SAXParserFactory.newInstance().newSAXParser();
                String path = null;
                if (serverConfiguration.getServer().getIsoConfType().equals(Server.ISO_PATH_RELATIVE)) {
                    String isoConfPath = serverConfiguration.getServer().getIsoConfPath().replaceAll("/", Character.toString(File.separatorChar));
                    path = System.getProperty("user.dir") + File.separatorChar + isoConfPath;
                } else if (serverConfiguration.getServer().getIsoConfType().equals(Server.ISO_PATH_RELATIVE)) {
                    path = serverConfiguration.getServer().getIsoConfPath();
                } else {
                    throw new IllegalStateException("nilai dari iso-conf[@path] adalah {" + Server.ISO_PATH_RELATIVE + "," + Server.ISO_PATH_ABSOLUTE + "}");
                }
                File packagerFile = new File(path);
                if (packagerFile.exists()) {
                    inputStream = new FileInputStream(packagerFile);
                    packager = new GenericPackager(inputStream);
                }
            } catch (ISOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (FileNotFoundException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (ParserConfigurationException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (SAXException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(XmlGatewayIsoServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public boolean process(ISOSource isos, ISOMsg isomsg) {
        LOGGER.info("1.Receive from euronet server : \n" + ISOMsgHelper.isoMsgToString(isomsg));

        try {
            //convert euronet message to external message
            //simpan transaksi di database
            LOGGER.info("2.simpan transaksi di database ");
            IsoData isoDataRequest = XmlIsoDataConverter.convert(isomsg);
            //cek kalau transaksinya cancel payment, perlu ambil data payment aslinya untuk dapet transaction id
            if(isoDataRequest.getValue(0).equals(XmlTransformer.ISO_CANCEL_PAYMENT_RESPONSE_CODE)){
                String originalData = isoDataRequest.getValue(XmlTransformer.F_ORIGINAL_DATA_ELEMENT);
                String stan = originalData.substring(4, 10);
                Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(
                        new DateTime().getYear() +
                        originalData.substring(14, 18) +
                        originalData.substring(18, 24)
                        );
                Transaksi payement = transaksiService.getByStan(stan,date);
                //TODO cek kalau payment null. Response error
                isoDataRequest.setTransactionId(payement.getTransactionId());
            }
            String xml = xmlTransformer.isoToXml(isoDataRequest);
            TransaksiWrapper transaksiWrapper = xmlTransformer.isoToTransaksi(isoDataRequest);
            Transaksi transaksi = new Transaksi();
            BeanUtils.copyProperties(transaksiWrapper, transaksi);
            transaksi.setXml(xml);
            transaksi.setIso(new String(isomsg.pack()));
            transaksiService.save(transaksi);
            LOGGER.info("3.convert iso msg to xml \n" + xml);
            //forward message to external server and wait for reply
            LOGGER.info("4.Forward to external server");
            //length prefix
            final IoBuffer ioBuffer = IoBufferWrapper.wrap(xml.getBytes());
            //TODO cek session di connect future
            if(connectFuture.isConnected() == false){
                NioSocketConnector connector = new NioSocketConnector();
                TextLineCodecFactory factory = new TextLineCodecFactory(Charset.forName("UTF-8"));
                connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
                connector.setHandler(this);
                connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 100);
                Connector c = serverConfiguration.getConnectors().get(0);
                connectFuture = connector.connect(new InetSocketAddress(c.getOutbound().getIp(),
                        c.getOutbound().getPort()));
                connectFuture.addListener( new  IoFutureListener() {

                    @Override
                    public void operationComplete(IoFuture ioFuture) {
                        ConnectFuture future = (ConnectFuture) ioFuture;
                        if(future.isConnected()){
                            ioFuture.getSession().write(ioBuffer);
                        }
                    }
                });
                
            } else {
                connectFuture.getSession().write(ioBuffer);
            }
            isoSourceMap.put(transaksi.getMessageId(), isos);
            return true;
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        return true;
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
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        System.out.println("Client iddle " + session.getRemoteAddress().toString());
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable ex) throws Exception {
        System.out.println("Client error " + session.getRemoteAddress().toString());
        ex.printStackTrace();
    }
    @Override
    public void messageReceived(IoSession session, Object msg) throws Exception {
        if(msg.toString().trim().length()==0) return;
        LOGGER.info("5.Response recieved \n" + msg);
        XPathHandler handler = new XPathHandler();
        parser.parse(new ByteArrayInputStream(msg.toString().getBytes()), handler);
        String messageId = handler.getXmlData().getXmlValue("/OnlineRequest/serviceAddressing/MessageId");
        Transaksi request = transaksiService.getRequest(messageId.substring(6,messageId.length()));
        //TODO handle kalau request ga ada
        TransaksiWrapper requestWrapper = new TransaksiWrapper();
        BeanUtils.copyProperties(request, requestWrapper);
        TransaksiWrapper responseWrapper = xmlTransformer.createResponse(requestWrapper, handler.getXmlData(), msg.toString());
        Transaksi response = new Transaksi();
        BeanUtils.copyProperties(responseWrapper, response);
        request.setStatus(Status.RESPONSE_RECIEVED);
        //Simpan response dan update request
        
        IsoData isoDataRequest = XmlIsoDataConverter.createIsoData(request,serverConfiguration);
        IsoData isoResponse = xmlTransformer.xmlToIso(handler.getXmlData(), isoDataRequest);
        ISOMsg isomsg = XmlIsoDataConverter.convert(isoResponse);
        isomsg.setPackager(packager);
        response.setIso(new String(isomsg.pack()));
        transaksiService.save(request,response);
        
        ISOSource isos = isoSourceMap.get(messageId.substring(6,messageId.length()));
        isos.send(isomsg);
    }
    @Override
    public void messageSent(IoSession session, Object msg) throws Exception {
        System.out.println("Message sent " + msg.toString());
    }

}
