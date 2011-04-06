/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.simulator;

import com.xmlgateway.server.helper.ISOMsgHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

/**
 *
 * @author ifnu
 */
public class BankMessageCreator implements MessageCreator {
    private static final Log LOGGER = LogFactory.getLog(BankMessageCreator.class);
    private SimpleDateFormat bit7DateFormat = new SimpleDateFormat("MMddHHmmss");
    private SimpleDateFormat bit12DateFormat = new SimpleDateFormat("HHmmss");
    private SimpleDateFormat bit13DateFormat = new SimpleDateFormat("MMdd");

    public ISOMsg create0200(String amount, String mobile) {
        amount = ISOMsgHelper.padZeroRightAlign(amount,12);
        try {
            ISOMsg isoMsg = new ISOMsg();
            Date date = new Date();
            isoMsg.setMTI("0200");
            isoMsg.set(2,"13");
            isoMsg.set(3,"550000");
            isoMsg.set(4, amount);
            isoMsg.set(7, bit7DateFormat.format(date));
            isoMsg.set(11, ISOMsgHelper.generateBit11());
            isoMsg.set(12, bit12DateFormat.format(date));
            isoMsg.set(13, bit13DateFormat.format(date));
            isoMsg.set(15, bit13DateFormat.format(date));
            isoMsg.set(18,"6015");
            isoMsg.set(22,"000");
            isoMsg.set(26,"12");
            isoMsg.set(32,"13");
            isoMsg.set(33,"13");
            isoMsg.set(37,"000000007093");
            isoMsg.set(41,"00010981");
            isoMsg.set(43,"PERMATABANK INDONESIA  mataMobile       ");
            isoMsg.set(47,"EXL");
            isoMsg.set(49,"360");
            isoMsg.set(61,"101090010009");
            isoMsg.set(103,mobile);
            return isoMsg;
        } catch (ISOException ex) {
            LOGGER.info(ex.getMessage(),ex);
        }
        return null;
    }

    public ISOMsg create0400() {
        String mobile = "0899929991";
        String amount = "10000";
        try {
            ISOMsg isoMsg = new ISOMsg();
            Date date = new Date();
            isoMsg.setMTI("0420");
            isoMsg.set(2,"13");
            isoMsg.set(3,"550000");
            isoMsg.set(4, amount);
            isoMsg.set(7, bit7DateFormat.format(date));
            isoMsg.set(11, ISOMsgHelper.generateBit11());
            isoMsg.set(12, bit12DateFormat.format(date));
            isoMsg.set(13, bit13DateFormat.format(date));
            isoMsg.set(15, bit13DateFormat.format(date));
            isoMsg.set(18,"6015");
            isoMsg.set(22,"000");
            isoMsg.set(26,"12");
            isoMsg.set(32,"13");
            isoMsg.set(33,"13");
            isoMsg.set(37,"000000007093");
            isoMsg.set(41,"00010981");
            isoMsg.set(43,"PERMATABANK INDONESIA  mataMobile       ");
            isoMsg.set(47,"EXL");
            isoMsg.set(49,"360");
            isoMsg.set(61,"101090010009");
            isoMsg.set(90, "0200" + bit7DateFormat.format(date) + ISOMsgHelper.generateBit11() +"0000000001300000000013");
            isoMsg.set(103,mobile);
            return isoMsg;
        } catch (ISOException ex) {
            LOGGER.info(ex.getMessage(),ex);
        }
        return null;
    }

    public ISOMsg create0400(ISOMsg msg0200) {
        try {
            if (msg0200 == null || !msg0200.getMTI().equals("0200")) {
                throw new IllegalArgumentException("parameter harus message 0200");
            }
            ISOMsg isoMsg = (ISOMsg) msg0200.clone();
            isoMsg.setMTI("0420");
            isoMsg.set(90, "0200" +msg0200.getString(11) + msg0200.getString(7) +  "0000000001300000000013");
            return isoMsg;
        } catch (ISOException ex) {
            LOGGER.info(ex.getMessage(),ex);
        }
        return null;
    }

    public List<ISOMsg> create0200Bulk(String amount, String mobile, int num) {
        List<ISOMsg> messages = new ArrayList<ISOMsg>();
        for(int i=0;i<num;i++){
            messages.add(create0200(amount, mobile));
        }
        return messages;
    }

    public ISOMsg create0800() {
        try {
            ISOMsg msg = new ISOMsg();
            msg.setMTI("0800");
            msg.set(7, bit7DateFormat.format(new Date()));
            msg.set(11, "123456");
            msg.set(70,"001");
            return msg;
        } catch (ISOException ex) {
           LOGGER.info(ex.getMessage(), ex);
        }
        return null;
    }

    public List<ISOMsg> create0800Bulk(int num) {
        try {
            List<ISOMsg> messages = new ArrayList<ISOMsg>();
            for(int i=1;i<=num;i++){
                ISOMsg msg = new ISOMsg();
                msg.setMTI("0800");
                msg.set(7, bit7DateFormat.format(new Date()));
                msg.set(11, ISOMsgHelper.padZeroRightAlign(String.valueOf(i), 6));
                msg.set(70, "001");
                messages.add(msg);
            }
            return messages;
        } catch (ISOException ex) {
           LOGGER.info(ex.getMessage(), ex);
        }
        return new ArrayList<ISOMsg>();
    }

//    public static void main(String[] args) throws UnsupportedEncodingException, ISOException {
//        MessageCreator m = new GatewayMessageCreator();
//        ISOPackager packager = PackagerFactory.getGatewayPackager();
//        ISOMsg msg = m.create0200("10000", "085692118687");
//        msg.setPackager(packager);
//        System.out.println(new String(msg.pack(),"ASCII"));
//        ISOMsg reversal = m.create0400(msg);
//        reversal.setPackager(packager);
//        System.out.println(new String(reversal.pack(),"ASCII"));
//    }

}
