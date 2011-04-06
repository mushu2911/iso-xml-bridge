/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.configuration;

/**
 *
 * @author ifnu
 */
public class Simulator {

    private String ip;

    private Integer port;

    private String isoConfType;

    private String isoConfPath;

    private Long timeout;

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

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
    

}
