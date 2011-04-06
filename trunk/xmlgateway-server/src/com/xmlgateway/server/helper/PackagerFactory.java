/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.helper;

import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.packager.GenericPackager;

/**
 *
 * @author ifnu
 */
public class PackagerFactory {
    private static final Log LOGGER = LogFactory.getLog(PackagerFactory.class);
    private static GenericPackager gatewayPackager;
    private static GenericPackager axisPackager;
    private static GenericPackager permataPackager;
    static{
        try {
            InputStream inputStream = new FileInputStream("conf/itm-iso-conf.xml");
            gatewayPackager =  new GenericPackager(inputStream);
            InputStream axisInputStream = new FileInputStream("conf/itm-iso-conf.xml");
            axisPackager = new GenericPackager(axisInputStream);
            InputStream permataInputStream = new FileInputStream("conf/permata.xml");
            permataPackager = new GenericPackager(permataInputStream);
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage(),ex);
        }
    }

    public static GenericPackager getGatewayPackager(){
        if(gatewayPackager!=null) return gatewayPackager;
        try {
            InputStream inputStream = new FileInputStream("conf/gateway.xml");
            gatewayPackager =  new GenericPackager(inputStream);
            return gatewayPackager;
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage(),ex);
        }
        return null;
    }

    public static GenericPackager getAxisPackager(){
        if(axisPackager != null) return axisPackager;
        try {
            InputStream inputStream = new FileInputStream("conf/axis.xml");
            axisPackager =  new GenericPackager(inputStream);
            return axisPackager;
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage(),ex);
        }
        return null;
    }

    public static GenericPackager getPermataPackager(){
        if(permataPackager != null) return permataPackager;
        try {
            InputStream inputStream = new FileInputStream("conf/permata.xml");
            permataPackager = new GenericPackager(inputStream);
            return permataPackager;
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage(),ex);
        }
        return null;
    }

}
