/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.g4sei.id.xmlgateway.server.configuration;

import com.xmlgateway.server.configuration.ServerConfiguration;
import com.xmlgateway.server.configuration.Connector;
import com.xmlgateway.server.XmlGatewayServerManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ifnu
 */
public class ServerConfigurationTest {

    public ServerConfigurationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetDataSource() {

        XmlGatewayServerManager manager = new XmlGatewayServerManager();
        manager.initializeConfiguration("test/fixture/server.xml");
        ServerConfiguration configuration = manager.getServerConfiguration();
        Connector c = configuration.getConnectors().get(0);

        assertEquals("euronet", c.getId());
        assertEquals("127.0.0.1", c.getInbound().getIp());
        assertEquals(new Integer(9901), c.getInbound().getPort());
        assertEquals("dtd", c.getInbound().getXmlValidationType());
        assertEquals("OnlineRequest.dtd", c.getInbound().getDtd());

        assertEquals("127.0.0.1", c.getOutbound().getIp());
        assertEquals(new Integer(9904), c.getOutbound().getPort());
        assertEquals("dtd", c.getOutbound().getXmlValidationType());
        assertEquals("OnlineRequest.dtd", c.getOutbound().getDtd());

        assertEquals("com.g4sei.id.xmlgateway.euronet.transformer.euronetTransformer", c.getXmlTransformerClass());

        assertNotNull(configuration.getServer());
        assertEquals("itm",configuration.getServer().getId());
        assertEquals("127.0.0.1",configuration.getServer().getIp());
        assertEquals(new Integer(9904),configuration.getServer().getPort());
        assertEquals("relative",configuration.getServer().getIsoConfType());
        assertEquals("conf/itm-iso-conf.xml",configuration.getServer().getIsoConfPath());
        assertEquals(new Integer(70000),configuration.getServer().getTimeout());

    }


}