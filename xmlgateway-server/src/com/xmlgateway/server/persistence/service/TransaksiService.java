/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.persistence.service;

import com.xmlgateway.server.persistence.model.Audit;
import com.xmlgateway.server.persistence.model.Transaksi;
import java.util.Date;

/**
 *
 * @author ifnu
 */
public interface TransaksiService {

    public Transaksi getRequest(String idMessage);

    public Transaksi getByStan(String stan, Date transactionDate);
        
    public void save(Transaksi transaksi);

    public void save(Transaksi request, Transaksi response);
    
    public void update(Transaksi transaksi);

    public void save(Audit audit);

}
