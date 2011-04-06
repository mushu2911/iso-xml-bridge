/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.api.transformer;

import com.xmlgateway.api.model.IsoData;
import com.xmlgateway.api.model.TransaksiWrapper;
import com.xmlgateway.api.model.XmlData;

/**
 *
 * @author ifnu
 */
public interface XmlTransformer {
    public static final String ISO_INQUIRY_RESPONSE_CODE = "0210";
    public static final String ISO_PAYMENT_RESPONSE_CODE = "0210";
    public static final String ISO_CANCEL_PAYMENT_RESPONSE_CODE = "0410";
    public static final String ISO_INQUIRY_CODE = "0200";
    public static final String ISO_PAYMENT_CODE = "0200";
    public static final String ISO_CANCEL_PAYMENT_CODE = "0400";

    public static final String XML_INQUIRY_CODE ="GetSubBillInfo";
    public static final String XML_PAYMENT_CODE ="BillPayment";
    public static final String XML_CANCEL_PAYMENT_CODE ="CancelPayment";

    public static final String NULL_VALUE="null";

    public static final Integer F_PAN = 2;
    public static final Integer F_PROCESSING_CODE = 3;
    public static final Integer F_AMOUNT = 4;
    public static final Integer F_TRANSMISSION_DATE_TIME = 7;
    public static final Integer F_AUDIT_NUMBER = 11;
    public static final Integer F_LOCAL_TIME = 12;
    public static final Integer F_LOCAL_DATE = 13;
    public static final Integer F_SETTLEMENT_DATE = 15;
    public static final Integer F_MERCHANT_TYPE = 18;
    public static final Integer F_INSTITUTION_CODE = 32;
    public static final Integer F_INSTITUTION_FORWARDING_CODE = 32;
    public static final Integer F_RETRIEVAL_REFERENCE_NUMBER = 37;
    public static final Integer F_RESPONSE_CODE = 39;
    public static final Integer F_ATM_CODE = 41;
    public static final Integer F_ADDITIONAL_DATA = 48;
    public static final Integer F_CURRENCY_CODE = 49;
    public static final Integer F_ORIGINAL_DATA_ELEMENT = 90;
    
    public String isoToXml(IsoData isoData) throws Exception;
    public IsoData xmlToIso(XmlData xmlData, IsoData originalMsg) throws Exception;
    public TransaksiWrapper isoToTransaksi(IsoData isoData);
    public TransaksiWrapper xmlToTransaksi(XmlData xmlData);
    public TransaksiWrapper createResponse(TransaksiWrapper request, XmlData xmlData,String xml);
//    public TransaksiWrapper createCancelPaymentResponse(TransaksiWrapper request, XmlData xmlData,String xml);
//    public TransaksiWrapper createInquiryResponse(TransaksiWrapper request, XmlData xmlData,String xml);

}
