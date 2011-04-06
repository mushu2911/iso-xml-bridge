/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.xml;

import com.xmlgateway.api.model.XmlData;
import java.util.Stack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author ifnu
 */
public class XPathHandler extends DefaultHandler{

    private String value;
    private Stack<String> currentPath = new Stack<String>();
    private XmlData xmlData = new XmlData();
    private static final Log LOGGER = LogFactory.getLog(XPathHandler.class);

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        value = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(value!=null && value.length()>0){
            String path = getCurrentPath();
            xmlData.setXmlValue(path, value);
            LOGGER.debug(path + ":" + value);
        }
        currentPath.pop();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentPath.push(qName);
        String path = getCurrentPath();
        for(int i=0;i<attributes.getLength();i++){
            String attributeName = attributes.getQName(i);
            String attributeValue = attributes.getValue(i);
            xmlData.setXmlValue(path+"/@" + attributeName, attributeValue);
            LOGGER.debug(path+"/@" + attributeName + ":" + attributeValue);
        }
    }

    private String getCurrentPath(){
        StringBuilder builder = new StringBuilder();
        for(String val : currentPath){
            builder.append("/");
            builder.append(val);
        }
        return builder.toString();
    }

    public XmlData getXmlData() {
        return xmlData;
    }


}
