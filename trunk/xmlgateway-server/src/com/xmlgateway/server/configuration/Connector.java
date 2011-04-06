/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.configuration;

import com.xmlgateway.api.transformer.XmlTransformer;

/**
 *
 * @author ifnu
 */
public class Connector {

    private String id;

    private Inbound inbound;

    private Outbound outbound;

    private String xmlTransformerClass;

    private XmlTransformer xmlTransformer;

    public XmlTransformer getXmlTransformer() {
        return xmlTransformer;
    }

    public void setXmlTransformer(XmlTransformer xmlTransformer) {
        this.xmlTransformer = xmlTransformer;
    }

    public String getXmlTransformerClass() {
        return xmlTransformerClass;
    }

    public void setXmlTransformerClass(String xmlTransformerClass) {
        this.xmlTransformerClass = xmlTransformerClass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Inbound getInbound() {
        return inbound;
    }

    public void setInbound(Inbound inbound) {
        this.inbound = inbound;
    }

    public Outbound getOutbound() {
        return outbound;
    }

    public void setOutbound(Outbound outbound) {
        this.outbound = outbound;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Connector other = (Connector) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

}
