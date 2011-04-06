/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.api.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ifnu
 */
public class XmlData {

    private Map<String,String> xpathMap = new HashMap<String, String>();

    public String getXmlValue(String xpath){
        return xpathMap.get(xpath);
    }

    public void setXmlValue(String xpath,String value){
        xpathMap.put(xpath, value);
    }

}
