/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.persistence.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author ifnu
 */
@Entity
@Table(name="AUDIT")
public class Audit implements Serializable {

    public static enum AuditType{
        CONNECTION_ESTABLISH,CONNECION_CLOSE
    }

    @Id
    @GeneratedValue
    @Column(name="ID")
    private Long id;

    @Column(name="REMOTE_HOSE",length=30,nullable=false)
    private String remoteHost;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="AUDIT_DATE",nullable=false)
    private Date auditDate;

    private AuditType auditType;

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public AuditType getAuditType() {
        return auditType;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }


}
