/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.configuration;

/**
 *
 * @author ifnu
 */
public class Outbound {
    private String ip;
    private Integer port;
    private String xmlValidationType;
    private String dtd;
    private String xmlRootTag;
    private String xmlSchema;

    public String getDtd() {
        return dtd;
    }

    public void setDtd(String dtd) {
        this.dtd = dtd;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getXmlRootTag() {
        return xmlRootTag;
    }

    public void setXmlRootTag(String xmlRootTag) {
        this.xmlRootTag = xmlRootTag;
    }

    public String getXmlSchema() {
        return xmlSchema;
    }

    public void setXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    public String getXmlValidationType() {
        return xmlValidationType;
    }

    public void setXmlValidationType(String xmlValidationType) {
        this.xmlValidationType = xmlValidationType;
    }
    
}
