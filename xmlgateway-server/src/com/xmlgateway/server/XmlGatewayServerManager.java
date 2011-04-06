  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server;

import com.xmlgateway.api.transformer.XmlTransformer;
import com.xmlgateway.server.configuration.Connector;
import com.xmlgateway.server.configuration.Server;
import com.xmlgateway.server.configuration.ServerConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequest;
import org.jpos.iso.ISOServer;
import org.jpos.iso.channel.PostChannel;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ifnu
 */
@Component("xmlGatewayServerManager")
public class XmlGatewayServerManager {

    private static final Log LOGGER = LogFactory.getLog(XmlGatewayIsoServer.class);

    private ServerConfiguration serverConfiguration;

    private ISOMUX isomux;

    private final String SERVER_XML=System.getProperty("user.dir") + "/conf/server.xml";

    @Autowired private XmlGatewayIsoServer xmlGatewayIsoServer;

    public XmlGatewayServerManager() {
    }

    public void initialize(){
        
        initializeConfiguration(SERVER_XML);

        initializeLogService();

        initializeTransformer();

        startServer();

        startConnectors();
    }

    public void initializeLogService(){
        //Log4j Initializer here
    }

    public void startConnectors(){
        
//        for(Connector c : serverConfiguration.getConnectors()){
//            //start mina server for each connector
//            ConnectorMinaServer connectorMinaServer = new ConnectorMinaServer(c, this);
//            connectorMinaServer.start();
//            connectorMap.put(c,connectorMinaServer);
//        }

    }

    public ISOMsg sendIsoMessage(ISOMsg isomsg){
        ISORequest request = new ISORequest(isomsg);
        isomux.queue(request);
        ISOMsg msg = request.getResponse(serverConfiguration.getServer().getTimeout());
        if(msg == null){
            //log tidak ada balesan
            return null;
        }
        return msg;
    }

    public void initializeConfiguration(String fileConfigurationAbsolutePath){
        serverConfiguration = ServerConfiguration.getConfiguration(fileConfigurationAbsolutePath);
    }

    public void startServer(){
        String path = null;
        try {
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
                InputStream inputStream = new FileInputStream(packagerFile);
                GenericPackager itmPackager = new GenericPackager(inputStream);
                PostChannel postChannel = new PostChannel(
                        serverConfiguration.getServer().getIp(),
                        serverConfiguration.getServer().getPort(),
                        itmPackager);
                Connector c = serverConfiguration.getConnectors().get(0);
                ISOServer server = new ISOServer(c.getInbound().getPort(), postChannel, null);
                server.addISORequestListener(xmlGatewayIsoServer);
                new Thread(server).start();
                LOGGER.info("XmlGateway running @ "+c.getInbound().getIp()+":" + c.getInbound().getPort());

            } else {
                throw new IllegalStateException("File " + path + " tidak ditemukan");
            }

        } catch (FileNotFoundException ex) {
            throw new IllegalStateException("File " + path + " tidak ditemukan");
        } catch (ISOException ex) {
            Logger.getLogger(XmlGatewayServerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initializeTransformer(){
        try {
            for(Connector c : serverConfiguration.getConnectors()){
                Object o = Class.forName(c.getXmlTransformerClass()).newInstance();
                c.setXmlTransformer((XmlTransformer) o);
            }
        } catch (Exception ex) {
            Logger.getLogger(XmlGatewayServerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

}
