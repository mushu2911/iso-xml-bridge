/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.persistence.service.impl;

import com.xmlgateway.api.model.RequestResponse;
import com.xmlgateway.server.persistence.model.Audit;
import com.xmlgateway.server.persistence.model.Transaksi;
import com.xmlgateway.server.persistence.service.TransaksiService;
import java.util.Date;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ifnu
 */
@Transactional(readOnly=true)
@Service("transaksiService")
public class TransaksiServiceImpl implements TransaksiService{

    @Autowired private SessionFactory sessionFactory;

    @Override
    public Transaksi getRequest(String messageId) {
        return  (Transaksi) sessionFactory.getCurrentSession()
                .createQuery("from Transaksi t " +
                "where t.messageId=:id " +
                "and t.requestResponse=:request")
                .setString("id", messageId)
                .setParameter("request", RequestResponse.REQUEST)
                .uniqueResult();
    }

    @Override @Transactional(readOnly=false)
    public void save(Transaksi transaksi) {
        sessionFactory.getCurrentSession().save(transaksi);
    }

    @Override @Transactional(readOnly=false)
    public void save(Transaksi request, Transaksi response) {
        sessionFactory.getCurrentSession().save(response);
        sessionFactory.getCurrentSession().update(request);
    }

    @Override @Transactional(readOnly=false)
    public void update(Transaksi transaksi) {
        sessionFactory.getCurrentSession().update(transaksi);
    }

    @Override @Transactional(readOnly=false)
    public void save(Audit audit){
        sessionFactory.getCurrentSession().save(audit);
    }

    @Override
    public Transaksi getByStan(String stan, Date transactionDate) {
        return (Transaksi) sessionFactory.getCurrentSession()
                .createQuery("from Transaksi t " +
                "where t.stan=:stan " +
                "and t.transactionDate=:transactionDate")
                .setString("stan", stan)
                .setDate("transactionDate", transactionDate)
                .uniqueResult();
    }

}
