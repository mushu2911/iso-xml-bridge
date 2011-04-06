/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.persistence.model;

import com.xmlgateway.api.model.RequestResponse;
import com.xmlgateway.api.model.Status;
import com.xmlgateway.api.model.TransaksiType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 *
 * @author ifnu
 */
@Entity
@Table(name="TRANSAKSI_XML")
public class Transaksi implements Serializable {

    @Id
    @GeneratedValue
    @Column(name="ID")
    private Long id;

    @Column(name="MESSAGE_ID")
    private String messageId;

    @Column(name="AMOUNT")
    private BigDecimal amount;

    @Column(name="MSISDN",length=19,nullable=false)
    private String msisdn;

    @Column(name="STAN",length=6,nullable=false)
    private String stan;

    @Column(name="TRANSACTION_DATE",nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    @Column(name="TRANSACTION_ID",length=30)
    private String transactionId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="REQUEST_RESPONSE",nullable=false)
    private RequestResponse requestResponse;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="STATUS",nullable=false)
    private Status status =Status.WAIT_FOR_RESPONSE;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="TRANSAKSI_TYPE",nullable=false)
    private TransaksiType transaksiType;

    @Lob
    @Column(name="XML")
    private String xml;
    
    @Lob
    @Column(name="ISO")
    private String iso;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public RequestResponse getRequestResponse() {
        return requestResponse;
    }

    public void setRequestResponse(RequestResponse requestResponse) {
        this.requestResponse = requestResponse;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransaksiType getTransaksiType() {
        return transaksiType;
    }

    public void setTransaksiType(TransaksiType transaksiType) {
        this.transaksiType = transaksiType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
