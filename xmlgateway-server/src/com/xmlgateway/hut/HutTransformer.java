    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.hut;

import com.xmlgateway.api.model.IsoData;
import com.xmlgateway.api.model.RequestResponse;
import com.xmlgateway.api.model.Status;
import com.xmlgateway.api.model.TransaksiType;
import com.xmlgateway.api.model.TransaksiWrapper;
import com.xmlgateway.api.model.XmlData;
import com.xmlgateway.api.transformer.XmlTransformer;
import com.xmlgateway.api.util.StringUtils;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ifnu
 */
public class HutTransformer implements XmlTransformer{

    public static final Map<String,String> responseDescMap = new HashMap<String, String>();
    public static final Map<String,String> billInquiryProcessingCodeMap = new HashMap<String, String>();
    public static final Map<String,String> billPaymentProcessingCodeMap = new HashMap<String, String>();
    public static final Map<String,String> responseCodeMap = new HashMap<String, String>();
    public static final Map<String,String> cancelPaymentResponseCodeMap = new HashMap<String, String>();

    public HutTransformer() {
        responseDescMap.put("00", "Approve");
        responseDescMap.put("10", "Phone number not exist");
        responseDescMap.put("20", "Bill already paid");
        responseDescMap.put("99", "Other errors");
 
        billPaymentProcessingCodeMap.put("320000", "Bill Payment dari account yang tidak specific");
        billPaymentProcessingCodeMap.put("32000", "Bill Payment dari account tabungan");
        billPaymentProcessingCodeMap.put("322000", "Bill Payment dari account giro");
        billPaymentProcessingCodeMap.put("323000", "Bill Payment dari account kartu kredit");
        billInquiryProcessingCodeMap.put("310000", "Bill Inquiry dari account yang tidak specific");
        billInquiryProcessingCodeMap.put("311000", "Bill Inquiry dari account tabungan");
        billInquiryProcessingCodeMap.put("312000", "Bill Inquiry dari account giro");
        billInquiryProcessingCodeMap.put("313000", "Bill Inquiry dari account kartu kredit");

        responseCodeMap.put("00000","00");
        responseCodeMap.put("-9000","99");
        responseCodeMap.put("-9001","99");
        responseCodeMap.put("-9002","10");
        responseCodeMap.put("-9003","20");
        responseCodeMap.put("-9006","99");
        responseCodeMap.put("-9009","99");


    }

    @Override
    public String isoToXml(IsoData isoData) throws Exception{
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        builder.append("<!DOCTYPE OnlineRequest SYSTEM \"OnlineRequest.dtd\">");
        builder.append("<OnlineRequest>");
        builder.append("<serviceAddressing>");
        //kalau inquiry response amount harus 0
        if(isoData.getValue(0).equals(ISO_INQUIRY_CODE) &&
                billInquiryProcessingCodeMap.get(isoData.getValue(F_PROCESSING_CODE)) != null){

            //buat tanggal dari iso msg bit 13 MMdd
            String localDate = isoData.getValue(F_LOCAL_DATE);
            String localTime = isoData.getValue(F_LOCAL_TIME);
            GregorianCalendar calendar = new GregorianCalendar(
                    Calendar.getInstance().get(Calendar.YEAR), //year
                    Integer.parseInt(localDate.substring(0, 2))-1, //month
                    Integer.parseInt(localDate.substring(2, 4)), //date
                    Integer.parseInt(localTime.substring(0,2)), //hour
                    Integer.parseInt(localTime.substring(2,4)), //minute
                    Integer.parseInt(localTime.substring(4,6)) //second
                    );
            builder.append("<From>euronet</From>");
            builder.append("<MerchantType>"+isoData.getValue(F_MERCHANT_TYPE)+"</MerchantType>");
            builder.append("<MessageId>"+new SimpleDateFormat("ddMMyy").format(calendar.getTime())
                    + StringUtils.padZeroRightAlign(isoData.getValue(F_AUDIT_NUMBER), 6)+"</MessageId>");
            builder.append("<Action>"+ XML_INQUIRY_CODE +"</Action>");
            builder.append("</serviceAddressing>");
            String bit48 = isoData.getValue(48).trim();
            builder.append("<MSISDN>" + bit48 +"</MSISDN>");
            builder.append("<BillingResponse>");

            builder.append("<ResponseResult>");
            //check response code di iso
            builder.append("<ResponseCode>null</ResponseCode>");
            builder.append("<ResponseDescription>null</ResponseDescription>");
            builder.append("<ResponseAction>null</ResponseAction>");
            builder.append("</ResponseResult>");
            //bill reference number diletakkan di bit 48 dari posisi 19 sepanjang 16
            builder.append("<CurrBillnum>null</CurrBillnum>"); //nilai apa nih?
            //jumlah tagihan. Trim left zero
            builder.append("<TotBillAmtDue>null</TotBillAmtDue>");
            //nama yang punya nomor telpon
            builder.append("<Accountholder>null</Accountholder>");
            //format data tanggal dd/MM/yyyy HH:mm:ss
            builder.append("<TransactionId>null</TransactionId>");
            builder.append("<GenevaPayStatus>null</GenevaPayStatus>");
            builder.append("<Date>"+ new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(calendar.getTime())+"</Date>");
            //TODO due date diambil dari mana ya?
            builder.append("<DueDate>null</DueDate>");
            builder.append("<ATMCode>"+isoData.getValue(F_ATM_CODE)+"</ATMCode>");
            builder.append("<Amount>0</Amount>");

            builder.append("</BillingResponse>");
            
        } else if(isoData.getValue(0).equals(ISO_PAYMENT_CODE) &&
                billPaymentProcessingCodeMap.get(isoData.getValue(F_PROCESSING_CODE)) != null){
            //buat tanggal dari iso msg bit 13 MMdd
            String localDate = isoData.getValue(F_LOCAL_DATE);
            String localTime = isoData.getValue(F_LOCAL_TIME);
            GregorianCalendar calendar = new GregorianCalendar(
                    Calendar.getInstance().get(Calendar.YEAR), //year
                    Integer.parseInt(localDate.substring(0, 2))-1, //month
                    Integer.parseInt(localDate.substring(2, 4)), //date
                    Integer.parseInt(localTime.substring(0,2)), //hour
                    Integer.parseInt(localTime.substring(2,4)), //minute
                    Integer.parseInt(localTime.substring(4,6)) //second
                    );

            builder.append("<From>euronet</From>");
            builder.append("<MerchantType>"+isoData.getValue(F_MERCHANT_TYPE)+"</MerchantType>");
            builder.append("<MessageId>"+new SimpleDateFormat("ddMMyy").format(calendar.getTime())
                    + StringUtils.padZeroRightAlign(isoData.getValue(F_AUDIT_NUMBER), 6)+"</MessageId>");
            builder.append("<Action>"+ XML_PAYMENT_CODE +"</Action>");
            builder.append("</serviceAddressing>");
            String additionalData = isoData.getValue(F_ADDITIONAL_DATA);
            String msisdn = additionalData.substring(0, 19).trim();
            builder.append("<MSISDN>"+ msisdn +"</MSISDN>");
            builder.append("<BillingResponse>");
            builder.append("<ResponseResult>");
            //check response code di iso
            builder.append("<ResponseCode>null</ResponseCode>");
            builder.append("<ResponseDescription>null</ResponseDescription>");
            builder.append("<ResponseAction>null</ResponseAction>");
            builder.append("</ResponseResult>");
            //bill reference number diletakkan di bit 48 dari posisi 19 sepanjang 16
            builder.append("<CurrBillnum>"+additionalData.substring(19, 35).trim()+"</CurrBillnum>"); //nilai apa nih?
            //jumlah tagihan. Trim left zero
            builder.append("<TotBillAmtDue>"+additionalData.substring(65,77)+"</TotBillAmtDue>");
            //nama yang punya nomor telpon
            builder.append("<Accountholder>"+additionalData.substring(35, 65)+"</Accountholder>");
            //diambil dari ddMMyy + bit 11. Di dokumen euronet diminta panjangnya 12
            builder.append("<TransactionId>null</TransactionId>");
            builder.append("<GenevaPayStatus>null</GenevaPayStatus>");
            //format data tanggal dd/MM/yyyy HH:mm:ss
            builder.append("<Date>"+ new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(calendar.getTime())+"</Date>");
            //TODO due date diambil dari mana ya?
            builder.append("<DueDate>"+additionalData.substring(77, additionalData.length())+"</DueDate>");
            builder.append("<ATMCode>"+isoData.getValue(F_ATM_CODE)+"</ATMCode>");
            builder.append("<Amount>"+StringUtils.trimLeftZero(isoData.getValue(F_AMOUNT))+"</Amount>");

            builder.append("</BillingResponse>");
        } else if(isoData.getValue(0).equals(ISO_CANCEL_PAYMENT_RESPONSE_CODE)){
            //buat tanggal dari iso msg bit 13 MMdd
            String localDate = isoData.getValue(F_LOCAL_DATE);
            String localTime = isoData.getValue(F_LOCAL_TIME);
            GregorianCalendar calendar = new GregorianCalendar(
                    Calendar.getInstance().get(Calendar.YEAR), //year
                    Integer.parseInt(localDate.substring(0, 2))-1, //month
                    Integer.parseInt(localDate.substring(2, 4)), //date
                    Integer.parseInt(localTime.substring(0,2)), //hour
                    Integer.parseInt(localTime.substring(2,4)), //minute
                    Integer.parseInt(localTime.substring(4,6)) //second
                    );
            builder.append("<From>euronet</From>");
            builder.append("<MerchantType>"+isoData.getValue(F_MERCHANT_TYPE)+"</MerchantType>");
            builder.append("<MessageId>"+new SimpleDateFormat("ddMMyy").format(calendar.getTime())
                    + StringUtils.padZeroRightAlign(isoData.getValue(F_AUDIT_NUMBER), 6)+"</MessageId>");
            builder.append("<Action>"+ XML_CANCEL_PAYMENT_CODE +"</Action>");
            builder.append("</serviceAddressing>");
            String additionalData = isoData.getValue(F_ADDITIONAL_DATA);
            String msisdn = additionalData.substring(0, 19).trim();
            builder.append("<MSISDN>"+ msisdn +"</MSISDN>");
            builder.append("<BillingResponse>");
            builder.append("<ResponseResult>");
            //check response code di iso
            builder.append("<ResponseCode>null</ResponseCode>");
            builder.append("<ResponseDescription>null</ResponseDescription>");
            builder.append("<ResponseAction>null</ResponseAction>");
            builder.append("</ResponseResult>");
            builder.append("<CurrBillnum>null</CurrBillnum>"); //nilai apa nih?
            builder.append("<TotBillAmtDue>null</TotBillAmtDue>");
            //ada nih harusnya
            builder.append("<Accountholder>"+additionalData.substring(35, 65)+"</Accountholder>");
            //ITM tidak mencatat transactionid dari HUT sehingga harus lookup ke database
            //TODO cara lookup transaction ID ini gimana ya?
            builder.append("<TransactionId>"+isoData.getTransactionId()+"</TransactionId>");
            builder.append("<GenevaPayStatus>null</GenevaPayStatus>");
            //format data tanggal dd/MM/yyyy HH:mm:ss
            builder.append("<Date>"+ new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(calendar.getTime())+"</Date>");
            //TODO due date diambil dari mana ya?
            builder.append("<DueDate>null</DueDate>");
            builder.append("<ATMCode>"+isoData.getValue(F_ATM_CODE)+"</ATMCode>");
            //diambil dari 48 atau dari 4 ?
            builder.append("<Amount>"+isoData.getValue(F_AMOUNT)+"</Amount>");

            builder.append("</BillingResponse>");
        }
        builder.append("</OnlineRequest>");
        return builder.toString();
    }

    public IsoData xmlToIso(XmlData xmlData, IsoData originalMsg) throws Exception{
        IsoData isoData = new IsoData();
        String actionCode = xmlData.getXmlValue("/OnlineRequest/serviceAddressing/Action");
        isoData.setValue(F_PAN, originalMsg.getValue(F_PAN));
        isoData.setValue(F_AMOUNT, StringUtils.padZeroRightAlign(xmlData.getXmlValue("/OnlineRequest/BillingResponse/Amount"), 12));
        Date transmissionDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(xmlData.getXmlValue("/OnlineRequest/BillingResponse/Date"));
        isoData.setValue(F_TRANSMISSION_DATE_TIME, new SimpleDateFormat("MMddHHmmss").format(transmissionDate));
        isoData.setValue(F_AUDIT_NUMBER, originalMsg.getValue(F_AUDIT_NUMBER));
        isoData.setValue(F_LOCAL_TIME, new SimpleDateFormat("HHmmss").format(transmissionDate));
        isoData.setValue(F_LOCAL_DATE, new SimpleDateFormat("MMdd").format(transmissionDate));
        isoData.setValue(F_SETTLEMENT_DATE, originalMsg.getValue(F_SETTLEMENT_DATE));
        isoData.setValue(F_MERCHANT_TYPE, xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MerchantType"));
        isoData.setValue(F_INSTITUTION_CODE, originalMsg.getValue(F_INSTITUTION_CODE));
        isoData.setValue(F_INSTITUTION_FORWARDING_CODE, originalMsg.getValue(F_INSTITUTION_FORWARDING_CODE));
        isoData.setValue(F_RETRIEVAL_REFERENCE_NUMBER, originalMsg.getValue(F_RETRIEVAL_REFERENCE_NUMBER));
        String responseCode = responseCodeMap.get(xmlData.getXmlValue("/OnlineRequest/BillingResponse/ResponseResult/ResponseCode"));
        if(responseCode == null){
            responseCode = "99";
        }
        isoData.setValue(F_RESPONSE_CODE, responseCode);
        isoData.setValue(F_ATM_CODE, xmlData.getXmlValue("/OnlineRequest/BillingResponse/ATMCode"));
        isoData.setValue(F_CURRENCY_CODE, originalMsg.getValue(F_CURRENCY_CODE));
        
        if(actionCode.equals(XML_INQUIRY_CODE)){
            isoData.setValue(0, ISO_INQUIRY_RESPONSE_CODE);
            isoData.setValue(F_PROCESSING_CODE, originalMsg.getValue(F_PROCESSING_CODE));
            //compose bit 48
            String bit48 = StringUtils.padSpaceLeftAlign(xmlData.getXmlValue("/OnlineRequest/MSISDN"),19);
            bit48 += StringUtils.padSpaceLeftAlign(xmlData.getXmlValue("/OnlineRequest/BillingResponse/CurrBillnum"), 16);
            bit48 += StringUtils.padSpaceLeftAlign(xmlData.getXmlValue("/OnlineRequest/BillingResponse/Accountholder"), 30);
            bit48 += StringUtils.padZeroRightAlign(xmlData.getXmlValue("/OnlineRequest/BillingResponse/Amount"),12);
            bit48 += xmlData.getXmlValue("/OnlineRequest/BillingResponse/DueDate");
            isoData.setValue(F_ADDITIONAL_DATA, bit48);
        } else if(actionCode.equals(XML_PAYMENT_CODE)){
            isoData.setValue(0, ISO_PAYMENT_RESPONSE_CODE);
            isoData.setValue(F_PROCESSING_CODE, originalMsg.getValue(F_PROCESSING_CODE));
            String bit48 = StringUtils.padSpaceLeftAlign(xmlData.getXmlValue("/OnlineRequest/MSISDN"),19);
            bit48 += xmlData.getXmlValue("/OnlineRequest/BillingResponse/CurrBillnum").equals(NULL_VALUE) ? StringUtils.padSpaceLeftAlign("",16) : StringUtils.padSpaceLeftAlign(xmlData.getXmlValue("/OnlineRequest/BillingResponse/CurrBillnum"), 16);
            bit48 += StringUtils.padSpaceLeftAlign(xmlData.getXmlValue("/OnlineRequest/BillingResponse/Accountholder"), 30);
            bit48 += StringUtils.padZeroRightAlign(xmlData.getXmlValue("/OnlineRequest/BillingResponse/Amount"),12);
            isoData.setValue(F_ADDITIONAL_DATA, bit48);
        } else if(actionCode.equals(XML_CANCEL_PAYMENT_CODE)){
            isoData.setValue(0, ISO_CANCEL_PAYMENT_RESPONSE_CODE);
            //bit48 MSISDN + BillAmount
            String bit48 = StringUtils.padSpaceLeftAlign(xmlData.getXmlValue("/OnlineRequest/MSISDN"),19);
            bit48 += StringUtils.padZeroRightAlign(xmlData.getXmlValue("/OnlineRequest/BillingResponse/Amount"),12);
            isoData.setValue(F_ADDITIONAL_DATA, bit48);
            isoData.setValue(F_ORIGINAL_DATA_ELEMENT, originalMsg.getValue(F_ORIGINAL_DATA_ELEMENT));
        }
        return isoData;
    }

    @Override
    public TransaksiWrapper isoToTransaksi(IsoData isoData) {
        TransaksiWrapper transaksi = new TransaksiWrapper();
        transaksi.setAmount(new BigDecimal(isoData.getValue(XmlTransformer.F_AMOUNT)));
        transaksi.setMessageId(isoData.getValue(XmlTransformer.F_AUDIT_NUMBER));
        //TODO parse additional data
        String additionalData = isoData.getValue(XmlTransformer.F_ADDITIONAL_DATA);
        if(additionalData!=null && additionalData.length()>19){
            additionalData = additionalData.substring(0, 19).trim();
        }
            transaksi.setMsisdn(additionalData);
        transaksi.setRequestResponse(RequestResponse.REQUEST);
        transaksi.setStan(isoData.getValue(XmlTransformer.F_PAN));
        transaksi.setStatus(Status.WAIT_FOR_RESPONSE);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String transactionDate = year + isoData.getValue(XmlTransformer.F_TRANSMISSION_DATE_TIME);
        try {
            transaksi.setTransactionDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(transactionDate));
        } catch (ParseException ex) {
//            LOGGER.error("error parsing ",ex);
            transaksi.setTransactionDate(new Date());
        }
        if(isoData.getValue(0).equals(XmlTransformer.ISO_CANCEL_PAYMENT_CODE)
                || isoData.getValue(0).equals(XmlTransformer.ISO_CANCEL_PAYMENT_RESPONSE_CODE)){
            transaksi.setTransaksiType(TransaksiType.CANCEL_PAYMENT);
        } else if(isoData.getValue(0).equals(XmlTransformer.ISO_PAYMENT_CODE)
                || isoData.getValue(0).equals(XmlTransformer.ISO_PAYMENT_RESPONSE_CODE)){
            transaksi.setTransaksiType(TransaksiType.PAYMENT);
        } else {
            transaksi.setTransaksiType(TransaksiType.INQUIRY);
        }
        return transaksi;
    }

    @Override
    public TransaksiWrapper xmlToTransaksi(XmlData xmlData) {
        TransaksiWrapper response = new TransaksiWrapper();
//        response.setXml(xml);
        String messageId = xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MessageId");
        response.setMessageId(messageId.substring(6,messageId.length()));
        response.setAmount(new BigDecimal(xmlData.getXmlValue("/OnlineRequest/BillingResponse/Amount")));
        response.setMsisdn(xmlData.getXmlValue("/OnlineRequest/MSISDN"));
        response.setRequestResponse(RequestResponse.RESPONSE);
        response.setStan(messageId.substring(6, 12));
        response.setStatus(Status.RESPONSE);
        try {
            response.setTransactionDate(new SimpleDateFormat("yyyymmdd").parse("20"+messageId.substring(0, 6)));
        } catch (ParseException ex) {
//            Logger.getLogger(class.getName()).log(Level.SEVERE, null, ex);
            response.setTransactionDate(new Date());
        }
        response.setTransactionId(xmlData.getXmlValue("/OnlineRequest/BillingResponse/TransactionId"));
        return response;
    }

    public TransaksiWrapper createPaymentResponse(TransaksiWrapper request, XmlData xmlData,String xml) {
        TransaksiWrapper response = createDefaultTransaksi(request, xmlData, xml);
        response.setTransaksiType(TransaksiType.PAYMENT);
        return response;
    }

    public TransaksiWrapper createCancelPaymentResponse(TransaksiWrapper request, XmlData xmlData,String xml) {
        TransaksiWrapper response = createDefaultTransaksi(request, xmlData, xml);
        response.setTransaksiType(TransaksiType.CANCEL_PAYMENT);
        return response;
    }
    
    public TransaksiWrapper createInquiryResponse(TransaksiWrapper request, XmlData xmlData,String xml) {
        TransaksiWrapper response = createDefaultTransaksi(request, xmlData, xml);
        response.setTransaksiType(TransaksiType.INQUIRY);
        return response;
    }

    private TransaksiWrapper createDefaultTransaksi(TransaksiWrapper request, XmlData xmlData,String xml){
        TransaksiWrapper response = new TransaksiWrapper();
//        response.setXml(xml);
        String messageId = xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MessageId");
        response.setMessageId(messageId.substring(6,messageId.length()));
        response.setAmount(new BigDecimal(xmlData.getXmlValue("/OnlineRequest/BillingResponse/Amount")));
        response.setMsisdn(xmlData.getXmlValue("/OnlineRequest/MSISDN"));
        response.setRequestResponse(RequestResponse.RESPONSE);
        response.setStan(messageId.substring(6, 12));
        response.setStatus(Status.RESPONSE);
        try {
            response.setTransactionDate(new SimpleDateFormat("yyyymmdd").parse("20"+messageId.substring(0, 6)));
        } catch (ParseException ex) {
//            Logger.getLogger(class.getName()).log(Level.SEVERE, null, ex);
            response.setTransactionDate(new Date());
        }
        response.setTransactionId(xmlData.getXmlValue("/OnlineRequest/BillingResponse/TransactionId"));
        return response;
    }

    @Override
    public TransaksiWrapper createResponse(TransaksiWrapper request, XmlData xmlData, String xml) {
        TransaksiWrapper response = null;
        String transactionType = xmlData.getXmlValue("/OnlineRequest/serviceAddressing/Action");
        if(transactionType.equals(XmlTransformer.XML_INQUIRY_CODE)){
            response = createInquiryResponse(request, xmlData,xml);
        } else if(transactionType.equals(XmlTransformer.XML_PAYMENT_CODE)){
            response = createPaymentResponse(request, xmlData,xml);
        } else if(transactionType.equals(XmlTransformer.XML_CANCEL_PAYMENT_CODE)){
            response = createCancelPaymentResponse(request, xmlData,xml);
        } else {
            //TODO gimana nih?
        }
        return response;
    }
}
