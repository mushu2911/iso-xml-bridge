/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ifnu
 */
public class XmlGatewayServer {
    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        //start servers
        XmlGatewayServerManager manager = (XmlGatewayServerManager) applicationContext.getBean("xmlGatewayServerManager");
        manager.initialize();
    }
}
