/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.configuration;

import com.xmlgateway.server.simulator.BankSenderFrame;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author ifnu
 */
public class ServerConfiguration {

    private String id;

    private Set<Connector> connectors = new HashSet<Connector>();

    private Server server;

    private DataSource dataSource;

    private static final Log LOGGER = LogFactory.getLog(BankSenderFrame.class);

    public static ServerConfiguration getConfiguration(String path){
        ServerConfiguration serverConfiguration = new ServerConfiguration();
            try{
            //mencari file server.xml di folder conf
            File f = new File(path);
            if(f.exists()){
                XMLConfiguration configuration = new XMLConfiguration(f);
                //ambil Connection
                if(configuration.configurationAt("connector") instanceof Collection){
                    int connectors = ((Collection)configuration.configurationAt("connector")).size();
                    for(int i=0;i<connectors;i++){
                        Connector c = new Connector();
                        String connectorPath = "connector(" + i +")";
                        c.setId(configuration.getString(connectorPath + "[@id]"));
                        c.setXmlTransformerClass(configuration.getString(connectorPath + ".transformer[@class]"));
                        Inbound inbound = new Inbound();
                        String inboundPath = "connector(" + i +").inbound";
                        inbound.setIp(configuration.getString(inboundPath + ".ip"));
                        inbound.setPort(Integer.valueOf(configuration.getString(inboundPath + ".port")));
                        inbound.setDtd(configuration.getString(inboundPath + ".dtd"));
                        inbound.setXmlRootTag(configuration.getString(inboundPath + ".xml-root-tag"));
                        inbound.setXmlSchema(configuration.getString(inboundPath + ".xml-schema"));
                        inbound.setXmlValidationType(configuration.getString(inboundPath + ".xml-validation-type"));

                        String outboundPath = "connector(" + i +").outbound";
                        Outbound outbound = new Outbound();
                        outbound.setIp(configuration.getString(outboundPath + ".ip"));
                        outbound.setPort(Integer.valueOf(configuration.getString(outboundPath + ".port")));
                        outbound.setDtd(configuration.getString(outboundPath + ".dtd"));
                        outbound.setXmlRootTag(configuration.getString(outboundPath + ".xml-root-tag"));
                        outbound.setXmlSchema(configuration.getString(outboundPath + ".xml-schema"));
                        outbound.setXmlValidationType(configuration.getString(outboundPath + ".xml-validation-type"));
                        c.setInbound(inbound);
                        c.setOutbound(outbound);

                        serverConfiguration.addConnector(c);
                    }
                } else {
                        Connector c = new Connector();
                        String connectorPath = "connector";
                        c.setId(configuration.getString(connectorPath + "[@id]"));
                        c.setXmlTransformerClass(configuration.getString(connectorPath + ".transformer[@class]"));
                        Inbound inbound = new Inbound();
                        String inboundPath = "connector.inbound";
                        inbound.setIp(configuration.getString(inboundPath + ".ip"));
                        inbound.setPort(Integer.valueOf(configuration.getString(inboundPath + ".port")));
                        inbound.setDtd(configuration.getString(inboundPath + ".dtd"));
                        inbound.setXmlRootTag(configuration.getString(inboundPath + ".xml-root-tag"));
                        inbound.setXmlSchema(configuration.getString(inboundPath + ".xml-schema"));
                        inbound.setXmlValidationType(configuration.getString(inboundPath + ".xml-validation-type"));

                        String outboundPath = "connector.outbound";
                        Outbound outbound = new Outbound();
                        outbound.setIp(configuration.getString(outboundPath + ".ip"));
                        outbound.setPort(Integer.valueOf(configuration.getString(outboundPath + ".port")));
                        outbound.setDtd(configuration.getString(outboundPath + ".dtd"));
                        outbound.setXmlRootTag(configuration.getString(outboundPath + ".xml-root-tag"));
                        outbound.setXmlSchema(configuration.getString(outboundPath + ".xml-schema"));
                        outbound.setXmlValidationType(configuration.getString(outboundPath + ".xml-validation-type"));
                        c.setInbound(inbound);
                        c.setOutbound(outbound);
                        serverConfiguration.addConnector(c);
                }
                try{//try digunakan untuk mengantisipasi salah satu daru server atau simulator tidak ada nilainya
                    Server simulator = new Server();
                    if(configuration.configurationAt("simulator") != null){
                        simulator.setId(configuration.getString("simulator[@id]"));
                        simulator.setIp(configuration.getString("simulator.ip"));
                        simulator.setPort(Integer.valueOf(configuration.getString("simulator.port")));
                        simulator.setIsoConfType(configuration.getString("simulator.iso-conf[@path]"));
                        simulator.setIsoConfPath(configuration.getString("simulator.iso-conf"));
                        simulator.setTimeout(Integer.valueOf(configuration.getString("simulator.timeout")));
                        simulator.setServerType(Server.SIMULATOR);
                    }
                    serverConfiguration.setServer(simulator);
                } catch(Exception ex){
                    Server server = new Server();
                    //kalau tag server juga ada berarti yang digunakan server, simulator diabaikan
                    if(configuration.configurationAt("server")!=null){
                        server.setId(configuration.getString("server[@id]"));
                        server.setIp(configuration.getString("server.ip"));
                        server.setPort(Integer.valueOf(configuration.getString("server.port")));
                        server.setIsoConfType(configuration.getString("server.iso-conf[@path]"));
                        server.setIsoConfPath(configuration.getString("server.iso-conf"));
                        server.setTimeout(Integer.valueOf(configuration.getString("server.timeout")));
                        server.setServerType(Server.SERVER);
                    }
                    serverConfiguration.setServer(server);
                }
            }
            return serverConfiguration;
            //validate dengan xml schema
            //atau validate secara manual

        } catch (Exception ex) {
            LOGGER.info(ex);
        }
        return null;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void addConnector(Connector connector){
        connectors.add(connector);
    }

    public List<Connector> getConnectors() {
        List<Connector> connectorList = new ArrayList<Connector>(connectors);
        return connectorList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
