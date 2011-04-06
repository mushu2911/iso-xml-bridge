/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.api.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author ifnu
 */
public class TransaksiWrapper {
    private String messageId;

    private BigDecimal amount;

    private String msisdn;

    private String stan;

    private Date transactionDate;

    private String transactionId;

    private RequestResponse requestResponse;

    private Status status =Status.WAIT_FOR_RESPONSE;

    private TransaksiType transaksiType;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
    
}
