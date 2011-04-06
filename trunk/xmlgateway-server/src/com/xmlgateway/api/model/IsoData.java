/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.api.model;

import com.xmlgateway.api.transformer.XmlTransformer;
import com.xmlgateway.api.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ifnu
 */
public class IsoData {

    private Map<Integer,String> valueMap = new HashMap<Integer, String>();
    private String transactionId = "null";


    public void setValue(Integer field, String value){
        if(field == null || field > 128 || field < 0){
            throw new IllegalArgumentException("field cannot null or negative or more than 128");
        }
        if(value == null){
            throw new IllegalArgumentException("value of iso field cannot be null");
        }
        valueMap.put(field, value);
    }

    public String getValue(Integer field){
        return valueMap.get(field);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Map<Integer, String> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<Integer, String> valueMap) {
        this.valueMap = valueMap;
    }

}
