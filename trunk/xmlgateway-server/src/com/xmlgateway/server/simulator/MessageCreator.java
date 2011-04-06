/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.simulator;

import java.util.List;
import org.jpos.iso.ISOMsg;

/**
 *
 * @author ifnu
 */
public interface MessageCreator {

    public ISOMsg create0200(String amount, String mobile);
    public ISOMsg create0400();
    public ISOMsg create0800();
    public ISOMsg create0400(ISOMsg msg0200);
    public List<ISOMsg> create0200Bulk(String amount, String mobile, int num);
    public List<ISOMsg> create0800Bulk(int num);
}
