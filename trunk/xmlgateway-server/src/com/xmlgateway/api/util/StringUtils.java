/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.api.util;

/**
 *
 * @author ifnu
 */
public class StringUtils {

    public static String padZeroLeftAlign(String data,int length){
        StringBuilder builder = new StringBuilder();
        builder.append(data);
        for(int i=0;i<length-data.length();i++){
            builder.append("0");
        }
        return builder.toString();
    }

    public static String padSpaceLeftAlign(String data,int length){
        StringBuilder builder = new StringBuilder();
        builder.append(data);
        for(int i=0;i<length-data.length();i++){
            builder.append(" ");
        }
        return builder.toString();
    }

    public static String padZeroRightAlign(String data, int length){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<length-data.length();i++){
            builder.append("0");
        }
        builder.append(data);
        return builder.toString();
    }
    public static String padSpaceRightAlign(String data, int length){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<length-data.length();i++){
            builder.append(" ");
        }
        builder.append(data);
        return builder.toString();
    }

    public static String generateBit11(){
        String time = String.valueOf(System.currentTimeMillis());
        if(time.length()<6){
            return padZeroRightAlign(time, 6);
        } else {
            return time.substring(time.length()-6, time.length());
        }
    }

    public static String trimLeftZero(String amount){
        char[] chars = amount.toCharArray();
        for(int i=0;i<chars.length;i++){
            if(chars[i]!='0'){
                return new String(chars,i,chars.length-i);
            }
        }
        return "";
    }
    public static String trimRightZero(String amount){
        char[] chars = amount.toCharArray();
        for(int i=chars.length-1;i>=0;i--){
            if(chars[i]!='0'){
                return new String(chars,0,i+1);
            }
        }
        return "";
    }
}
