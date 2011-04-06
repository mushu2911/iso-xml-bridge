/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.g4sei.id.xmlgateway.server.xml;

import com.xmlgateway.server.xml.XPathHandler;
import com.xmlgateway.api.model.XmlData;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author ifnu
 */
public class XPathHandlerTest {

    public XPathHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testXpathCreation() throws ParserConfigurationException, SAXException, IOException{

        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        XPathHandler handler = new XPathHandler();
        parser.parse(new File("test/fixture/Bill_inquiry.xml"), handler);

        XmlData xmlData = handler.getXmlData();
        assertNotNull(xmlData);
        
        assertEquals("euronet",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/From"));
        assertEquals("6011",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MerchantType"));
        assertEquals("06B3A3818315",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MessageId"));
        assertEquals("GetSubBillInfo",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/Action"));
        assertEquals("6289681000294",xmlData.getXmlValue("/OnlineRequest/MSISDN"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ResponseResult/ResponseCode"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ResponseResult/ResponseDescription"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ResponseResult/ResponseAction"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/CurrBillnum"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/TotBillAmtDue"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/Accountholder"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/TransactionId"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/GenevaPayStatus"));
        assertEquals("11/03/09 10:44:42",xmlData.getXmlValue("/OnlineRequest/BillingResponse/Date"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/DueDate"));
        assertEquals("B1203DCH",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ATMCode"));
        assertEquals("0",xmlData.getXmlValue("/OnlineRequest/BillingResponse/Amount"));

        handler = new XPathHandler();
        parser.parse(new File("test/fixture/Bill_Payment.xml"), handler);

        xmlData = handler.getXmlData();
        assertNotNull(xmlData);

        assertEquals("100",xmlData.getXmlValue("/OnlineRequest/@id"));
        assertEquals("euronet",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/From"));
        assertEquals("6011",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MerchantType"));
        assertEquals("06B3A3818315",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MessageId"));
        assertEquals("BillPayment",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/Action"));
        assertEquals("6289681000294",xmlData.getXmlValue("/OnlineRequest/MSISDN"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ResponseResult/ResponseCode"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ResponseResult/ResponseDescription"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ResponseResult/ResponseAction"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/CurrBillnum"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/TotBillAmtDue"));
        assertEquals("Test Uat Vip Post 6",xmlData.getXmlValue("/OnlineRequest/BillingResponse/Accountholder"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/TransactionId"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/GenevaPayStatus"));
        assertEquals("11/03/09 10:44:42",xmlData.getXmlValue("/OnlineRequest/BillingResponse/Date"));
        assertEquals("21102006",xmlData.getXmlValue("/OnlineRequest/BillingResponse/DueDate"));
        assertEquals("B1203DCH",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ATMCode"));
        assertEquals("000000055000",xmlData.getXmlValue("/OnlineRequest/BillingResponse/Amount"));

        handler = new XPathHandler();
        parser.parse(new File("test/fixture/Cancel_payment.xml"), handler);

        xmlData = handler.getXmlData();
        assertNotNull(xmlData);

        assertEquals("euronet",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/From"));
        assertEquals("6011",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MerchantType"));
        assertEquals("06B3A3818315",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/MessageId"));
        assertEquals("CancelPayment",xmlData.getXmlValue("/OnlineRequest/serviceAddressing/Action"));
        assertEquals("6289681000294",xmlData.getXmlValue("/OnlineRequest/MSISDN"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ResponseResult/ResponseCode"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ResponseResult/ResponseDescription"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ResponseResult/ResponseAction"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/CurrBillnum"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/TotBillAmtDue"));
        assertEquals("Test Uat Vip Post 6",xmlData.getXmlValue("/OnlineRequest/BillingResponse/Accountholder"));
        assertEquals("184154475024",xmlData.getXmlValue("/OnlineRequest/BillingResponse/TransactionId"));
        assertEquals("null",xmlData.getXmlValue("/OnlineRequest/BillingResponse/GenevaPayStatus"));
        assertEquals("11/03/09 10:44:42",xmlData.getXmlValue("/OnlineRequest/BillingResponse/Date"));
        assertEquals("23102006",xmlData.getXmlValue("/OnlineRequest/BillingResponse/DueDate"));
        assertEquals("B1203DCH",xmlData.getXmlValue("/OnlineRequest/BillingResponse/ATMCode"));
        assertEquals("000000055000",xmlData.getXmlValue("/OnlineRequest/BillingResponse/Amount"));


    }

}