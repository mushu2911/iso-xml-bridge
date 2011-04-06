/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.configuration;

/**
 *
 * @author ifnu
 */
public class Server {

    public static final String SERVER ="server";
    public static final String SIMULATOR ="simulator";
    
    public static final String ISO_PATH_RELATIVE ="relative";
    public static final String ISO_PATH_ABSOLUTE ="absolute";

    private String ip;

    private String id;

    private Integer port;

    private String isoConfType;

    private String isoConfPath;

    private Integer timeout;

    private String serverType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIsoConfPath() {
        return isoConfPath;
    }

    public void setIsoConfPath(String isoConfPath) {
        this.isoConfPath = isoConfPath;
    }

    public String getIsoConfType() {
        return isoConfType;
    }

    public void setIsoConfType(String isoConfType) {
        this.isoConfType = isoConfType;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }


}
