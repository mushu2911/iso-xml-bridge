package com.xmlgateway.server.helper;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.ISOMsg;

/**
 * Helper to create ISO Message.
 *
 * @author endy
 */
public class ISOMsgHelper {

    public static String isoMsgToString(ISOMsg message) {
        try {
            StringBuffer result = new StringBuffer();
            result.append("====== Start ISO Message ======\n");
            result.append("full message :  " + new String(message.pack(), "ASCII") + "\n");
            for (int i = 0; i <= message.getMaxField(); i++) {
                if (message.hasField(i)) {
                    result.append("Field ");
                    result.append(i);
                    result.append(" : ");
                    result.append(message.getString(i));
                    result.append("\n");
                }
            }
            result.append("====== End ISO Message ======\n");
            return result.toString();
        } catch (Exception ex) {
            Logger.getLogger(ISOMsgHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

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

}
