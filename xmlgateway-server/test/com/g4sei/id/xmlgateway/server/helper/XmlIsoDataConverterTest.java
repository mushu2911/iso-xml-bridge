/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.g4sei.id.xmlgateway.server.helper;

import com.xmlgateway.server.helper.XmlIsoDataConverter;
import com.xmlgateway.api.model.IsoData;
import com.xmlgateway.api.util.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.xmlgateway.api.transformer.XmlTransformer.*;

/**
 *
 * @author ifnu
 */
public class XmlIsoDataConverterTest {

    public XmlIsoDataConverterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testConvert_IsoData() throws ISOException, ParseException {
        ISOMsg isomsg = new ISOMsg();
        isomsg.set(0, ISO_PAYMENT_CODE);
        isomsg.set(F_PAN, "000000000000000000");
        isomsg.set(F_PROCESSING_CODE, "320000");
        isomsg.set(F_AMOUNT, "000000000010");
        Date transmissionDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("11/03/09 10:44:42");
        isomsg.set(F_TRANSMISSION_DATE_TIME, new SimpleDateFormat("MMddyyHHmmss").format(transmissionDate));
        String auditNumber = StringUtils.generateBit11();
        isomsg.set(F_AUDIT_NUMBER, auditNumber);
        isomsg.set(F_LOCAL_TIME, new SimpleDateFormat("HHmmss").format(transmissionDate));
        isomsg.set(F_LOCAL_DATE, new SimpleDateFormat("MMdd").format(transmissionDate));
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(transmissionDate);
        cal.add(Calendar.DATE, 1);
        Date settlementDate = cal.getTime();
        isomsg.set(F_SETTLEMENT_DATE, new SimpleDateFormat("MMdd").format(settlementDate));
        isomsg.set(F_MERCHANT_TYPE, "6011");
        //TODO euronet berapa ya?
        isomsg.set(F_INSTITUTION_CODE, "110");
        isomsg.set(F_INSTITUTION_FORWARDING_CODE, "110");
        isomsg.set(F_RETRIEVAL_REFERENCE_NUMBER, "06B3A3818315");
        isomsg.set(F_RESPONSE_CODE, "00");
        isomsg.set(F_ATM_CODE, "B1203DCH");
        isomsg.set(F_ADDITIONAL_DATA, "6289681000294      0000000226      Test Uat Vip Post 6           000000000000");
        isomsg.set(F_CURRENCY_CODE, "360");

        IsoData isoData = XmlIsoDataConverter.convert(isomsg);

        assertEquals(ISO_PAYMENT_CODE, isoData.getValue(0));
        assertEquals("000000000000000000", isoData.getValue(F_PAN));
        assertEquals("320000", isoData.getValue(F_PROCESSING_CODE));
        assertEquals("000000000010", isoData.getValue(F_AMOUNT));
        assertEquals(new SimpleDateFormat("MMddyyHHmmss").format(transmissionDate), isoData.getValue(F_TRANSMISSION_DATE_TIME));
        assertEquals(auditNumber, isoData.getValue(F_AUDIT_NUMBER));
        assertEquals(new SimpleDateFormat("HHmmss").format(transmissionDate), isoData.getValue(F_LOCAL_TIME));
        assertEquals(new SimpleDateFormat("MMdd").format(transmissionDate), isoData.getValue(F_LOCAL_DATE));
        assertEquals(new SimpleDateFormat("MMdd").format(settlementDate), isoData.getValue(F_SETTLEMENT_DATE));
        assertEquals("6011", isoData.getValue(F_MERCHANT_TYPE));
        assertEquals("110", isoData.getValue(F_INSTITUTION_CODE));
        assertEquals("110", isoData.getValue(F_INSTITUTION_FORWARDING_CODE));
        assertEquals("06B3A3818315", isoData.getValue(F_RETRIEVAL_REFERENCE_NUMBER));
        assertEquals("00", isoData.getValue(F_RESPONSE_CODE));
        assertEquals("B1203DCH", isoData.getValue(F_ATM_CODE));
        assertEquals("6289681000294      0000000226      Test Uat Vip Post 6           000000000000", isoData.getValue(F_ADDITIONAL_DATA));
        assertEquals("360", isoData.getValue(F_CURRENCY_CODE));
        for(int i=0;i<128;i++){
            Object val = isomsg.getValue(i);
            if(val!=null){
                assertNotNull(isoData.getValue(i));
            }
        }
    }

    @Test
    public void testConvert_ISOMsg() throws ParseException, ISOException {
        IsoData isoData = new IsoData();
        isoData.setValue(0, ISO_PAYMENT_CODE);
        isoData.setValue(F_PAN, "000000000000000000");
        isoData.setValue(F_PROCESSING_CODE, "320000");
        isoData.setValue(F_AMOUNT, "000000000010");
        Date transmissionDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("11/03/09 10:44:42");
        isoData.setValue(F_TRANSMISSION_DATE_TIME, new SimpleDateFormat("MMddyyHHmmss").format(transmissionDate));
        String auditNumber = StringUtils.generateBit11();
        isoData.setValue(F_AUDIT_NUMBER, auditNumber);
        isoData.setValue(F_LOCAL_TIME, new SimpleDateFormat("HHmmss").format(transmissionDate));
        isoData.setValue(F_LOCAL_DATE, new SimpleDateFormat("MMdd").format(transmissionDate));
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(transmissionDate);
        cal.add(Calendar.DATE, 1);
        Date settlementDate = cal.getTime();
        isoData.setValue(F_SETTLEMENT_DATE, new SimpleDateFormat("MMdd").format(settlementDate));
        isoData.setValue(F_MERCHANT_TYPE, "6011");
        //TODO euronet berapa ya?
        isoData.setValue(F_INSTITUTION_CODE, "110");
        isoData.setValue(F_INSTITUTION_FORWARDING_CODE, "110");
        isoData.setValue(F_RETRIEVAL_REFERENCE_NUMBER, "06B3A3818315");
        isoData.setValue(F_RESPONSE_CODE, "00");
        isoData.setValue(F_ATM_CODE, "B1203DCH");
        isoData.setValue(F_ADDITIONAL_DATA, "6289681000294      0000000226      Test Uat Vip Post 6           000000000000");
        isoData.setValue(F_CURRENCY_CODE, "360");

        ISOMsg isomsg = XmlIsoDataConverter.convert(isoData);

        assertEquals(ISO_PAYMENT_CODE, isomsg.getValue(0));
        assertEquals("000000000000000000", isomsg.getValue(F_PAN));
        assertEquals("320000", isomsg.getValue(F_PROCESSING_CODE));
        assertEquals("000000000010", isomsg.getValue(F_AMOUNT));
        assertEquals(new SimpleDateFormat("MMddyyHHmmss").format(transmissionDate), isomsg.getValue(F_TRANSMISSION_DATE_TIME));
        assertEquals(auditNumber, isomsg.getValue(F_AUDIT_NUMBER));
        assertEquals(new SimpleDateFormat("HHmmss").format(transmissionDate), isomsg.getValue(F_LOCAL_TIME));
        assertEquals(new SimpleDateFormat("MMdd").format(transmissionDate), isomsg.getValue(F_LOCAL_DATE));
        assertEquals(new SimpleDateFormat("MMdd").format(settlementDate), isomsg.getValue(F_SETTLEMENT_DATE));
        assertEquals("6011", isomsg.getValue(F_MERCHANT_TYPE));
        assertEquals("110", isomsg.getValue(F_INSTITUTION_CODE));
        assertEquals("110", isomsg.getValue(F_INSTITUTION_FORWARDING_CODE));
        assertEquals("06B3A3818315", isomsg.getValue(F_RETRIEVAL_REFERENCE_NUMBER));
        assertEquals("00", isomsg.getValue(F_RESPONSE_CODE));
        assertEquals("B1203DCH", isomsg.getValue(F_ATM_CODE));
        assertEquals("6289681000294      0000000226      Test Uat Vip Post 6           000000000000", isoData.getValue(F_ADDITIONAL_DATA));
        assertEquals("360", isomsg.getValue(F_CURRENCY_CODE));
        for(int i=0;i<128;i++){
            Object val = isoData.getValue(i);
            if(val!=null){
                assertNotNull(isomsg.getValue(i));
            }
        }

    }

    @Test
    public void testCreateIsoData() {
//        IsoData createIsoData(Transaksi request,ServerConfiguration serverConfiguration);
    }

    @Test
    public void testCreatecreateInquiryResponse() {
//        public static Transaksi createInquiryResponse(Transaksi request, XmlData xmlData,String xml);

    }
    @Test
    public void testCreatecreateCancelPaymentResponse() {
//        public static Transaksi createCancelPaymentResponse(Transaksi request, XmlData xmlData,String xml);

    }
    @Test
    public void testCreatecreatePaymenntResponse() {
//        public static Transaksi createPaymentResponse(Transaksi request, XmlData xmlData,String xml);

    }

}