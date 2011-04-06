/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PermataSenderFrame.java
 *
 * Created on May 20, 2009, 12:49:44 PM
 */

package com.xmlgateway.server.simulator;

import com.xmlgateway.api.transformer.XmlTransformer;
import com.xmlgateway.api.util.StringUtils;
import com.xmlgateway.server.configuration.Connector;
import com.xmlgateway.server.configuration.ServerConfiguration;
import com.xmlgateway.server.mina.encoder.HutCodecFactory;
import com.xmlgateway.server.xml.XPathHandler;
import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.IoBufferWrapper;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 *
 * @author ifnu
 */
public class HutSenderFrame extends javax.swing.JFrame {
    private static final Log LOGGER = LogFactory.getLog(HutSenderFrame.class);
    private ConnectFuture connectFuture;
    private ServerConfiguration serverConfiguration;
    private SAXParser parser;
    
    /** Creates new form PermataSenderFrame */
    public HutSenderFrame() throws Exception {
        initComponents();
        setLocationRelativeTo(null);
        serverConfiguration = ServerConfiguration.getConfiguration("conf/server.xml");
        NioSocketConnector connector = new NioSocketConnector();
        TextLineCodecFactory factory = new TextLineCodecFactory(Charset.forName("UTF-8"));
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
        connector.setHandler(new HutIoHandler());
        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 100);
        Connector c = serverConfiguration.getConnectors().get(0);
        connectFuture = connector.connect(new InetSocketAddress(c.getOutbound().getIp(),
                c.getOutbound().getPort()));

        LOGGER.info("Hut Server @ " + c.getOutbound().getIp() + ":" + c.getOutbound().getPort());

        Date date = new Date();
        txtDate.setText(new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(date));
        txtDate1.setText(new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(date));
        txtDate2.setText(new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(date));
        String currTime = String.valueOf(date.getTime());
        txtMessageId.setText(new SimpleDateFormat("yyyyMMdd").format(date) + currTime.substring(currTime.length()-6, currTime.length()));
        txtMessageId1.setText(new SimpleDateFormat("yyyyMMdd").format(date) + currTime.substring(currTime.length()-6, currTime.length()));
        txtMessageId2.setText(new SimpleDateFormat("yyyyMMdd").format(date) + currTime.substring(currTime.length()-6, currTime.length()));
        txtAction.setText(XmlTransformer.XML_INQUIRY_CODE);
        txtAction1.setText(XmlTransformer.XML_PAYMENT_CODE);
        txtAction2.setText(XmlTransformer.XML_CANCEL_PAYMENT_CODE);
        setTitle("HUT Sender ");
        parser = SAXParserFactory.newInstance().newSAXParser();
    }

    private class HutIoHandler implements IoHandler{

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            System.out.println("Client connect " + session.getRemoteAddress().toString());
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            System.out.println("Open session " + session.getRemoteAddress().toString());
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            System.out.println("Client say goodbye " + session.getRemoteAddress().toString());
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            System.out.println("Client iddle " + session.getRemoteAddress().toString());
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable ex) throws Exception {
            System.out.println("Client error " + session.getRemoteAddress().toString());
            ex.printStackTrace();
        }
        @Override
        public void messageReceived(IoSession session, Object msg) throws Exception {
//            if(msg.toString().trim().length()==0) return;
            LOGGER.info("2.Response recieved \n" + msg.toString());
            XPathHandler handler = new XPathHandler();
            parser.parse(new ByteArrayInputStream(msg.toString().getBytes()), handler);
            String action = handler.getXmlData().getXmlValue("/OnlineRequest/serviceAddressing/Action");
            if(action.equals(XmlTransformer.XML_INQUIRY_CODE)){
                txtInquiryMessage.setText(txtInquiryMessage.getText() + "\n" + msg);
            } else if(action.equals(XmlTransformer.XML_PAYMENT_CODE)){
                txtPaymentMessage.setText(txtPaymentMessage.getText() + "\n" + msg);
            } else if(action.equals(XmlTransformer.XML_CANCEL_PAYMENT_CODE)){
                txtCancelPaymentMessage.setText(txtCancelPaymentMessage.getText() + "\n" + msg);
            }
            LOGGER.info("Xml berhasil diparsing dengan baik");
        }
        @Override
        public void messageSent(IoSession session, Object msg) throws Exception {
//            System.out.println("1. Send message " + msg.toString());
        }

    }

    private void sendXml(String xml){
        final IoBuffer ioBuffer = IoBufferWrapper.wrap(xml.getBytes());
        System.out.println("1.Send Message " + xml);
        if(connectFuture.isConnected() == false){
            NioSocketConnector connector = new NioSocketConnector();
            HutCodecFactory factory = new HutCodecFactory(Charset.forName("UTF-8"));
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
            connector.setHandler(new HutIoHandler());
            connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 100);
            Connector c = serverConfiguration.getConnectors().get(0);
            connectFuture = connector.connect(new InetSocketAddress(c.getOutbound().getIp(),
                    c.getOutbound().getPort()));

            connectFuture.addListener(new IoFutureListener<IoFuture>() {

                @Override
                public void operationComplete(IoFuture future) {
                    ConnectFuture connectF = (ConnectFuture) future;
                    if(connectF.isConnected()){
                        connectF.getSession().write(ioBuffer);
                    }
                }
            });

            LOGGER.info("Hut Server @ " + c.getOutbound().getIp() + ":" + c.getOutbound().getPort());
        } else {
            connectFuture.getSession().write(ioBuffer);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JTextField();
        txtMerchantType = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        txtMessageId = new javax.swing.JTextField();
        txtAction = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        txtMsisdn = new javax.swing.JTextField();
        txtResponseCode = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        txtResponseDescription = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        txtResponseAction = new javax.swing.JTextField();
        txtCurrBillnum = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        txtATMCode = new javax.swing.JTextField();
        txtDueDate = new javax.swing.JTextField();
        txtDate = new javax.swing.JTextField();
        txtGenevaPayStatus = new javax.swing.JTextField();
        txtTransactionId = new javax.swing.JTextField();
        txtAccountholder = new javax.swing.JTextField();
        txtTotBillAmtDue = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        btnSendInquiry = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtInquiryMessage = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        txtFrom1 = new javax.swing.JTextField();
        txtMerchantType1 = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        txtMessageId1 = new javax.swing.JTextField();
        txtAction1 = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        txtMsisdn1 = new javax.swing.JTextField();
        txtResponseCode1 = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        txtResponseDescription1 = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        txtResponseAction1 = new javax.swing.JTextField();
        txtCurrBillnum1 = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        txtAmount1 = new javax.swing.JTextField();
        txtATMCode1 = new javax.swing.JTextField();
        txtDueDate1 = new javax.swing.JTextField();
        txtDate1 = new javax.swing.JTextField();
        txtGenevaPayStatus1 = new javax.swing.JTextField();
        txtTransactionId1 = new javax.swing.JTextField();
        txtAccountholder1 = new javax.swing.JTextField();
        txtTotBillAmtDue1 = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        btnSendPayment = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtPaymentMessage = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        txtFrom2 = new javax.swing.JTextField();
        txtMerchantType2 = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        txtMessageId2 = new javax.swing.JTextField();
        txtAction2 = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        txtMsisdn2 = new javax.swing.JTextField();
        txtResponseCode2 = new javax.swing.JTextField();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        txtResponseDescription2 = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        txtResponseAction2 = new javax.swing.JTextField();
        txtCurrBillnum2 = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        txtAmount2 = new javax.swing.JTextField();
        txtATMCode2 = new javax.swing.JTextField();
        txtDueDate2 = new javax.swing.JTextField();
        txtDate2 = new javax.swing.JTextField();
        txtGenevaPayStatus2 = new javax.swing.JTextField();
        txtTransactionId2 = new javax.swing.JTextField();
        txtAccountholder2 = new javax.swing.JTextField();
        txtTotBillAmtDue2 = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        btnSendCancelPayment = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtCancelPaymentMessage = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel38.setText("From");

        txtFrom.setText("euronet");

        txtMerchantType.setText("180000");

        jLabel39.setText("Merchant Type");

        jLabel40.setText("Message Id");

        txtMessageId.setText("100000");

        txtAction.setText("GetSubBillInfo");

        jLabel41.setText("Action");

        jLabel42.setText("MSISDN");

        txtMsisdn.setText("6289999999");

        txtResponseCode.setText(null);

        jLabel43.setText("Response Code");

        jLabel44.setText("Response Description");

        txtResponseDescription.setText(null);

        jLabel45.setText("Response Action");

        txtResponseAction.setText(null);

        txtCurrBillnum.setText(null);

        jLabel46.setText("Curr Bill num");

        jLabel49.setText("Amount");

        txtAmount.setText("0");

        txtATMCode.setText("B1203DCH");

        txtDueDate.setText(null);

        txtGenevaPayStatus.setText(null);

        txtTransactionId.setText(null);

        txtAccountholder.setText(null);

        txtTotBillAmtDue.setText(null);

        jLabel50.setText("Tot Bill Amt Due");

        jLabel51.setText("Account Holder");

        jLabel52.setText("Transaction Id");

        jLabel53.setText("Geneva Pay Status");

        jLabel54.setText("Date");

        jLabel55.setText("Due Date");

        jLabel56.setText("ATM Code");

        btnSendInquiry.setText("Send");
        btnSendInquiry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendInquiryActionPerformed(evt);
            }
        });

        txtInquiryMessage.setColumns(20);
        txtInquiryMessage.setRows(5);
        jScrollPane3.setViewportView(txtInquiryMessage);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSendInquiry)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(jLabel40)
                            .addComponent(jLabel39)
                            .addComponent(jLabel41)
                            .addComponent(jLabel42)
                            .addComponent(jLabel43)
                            .addComponent(jLabel44)
                            .addComponent(jLabel45)
                            .addComponent(jLabel46))
                        .addGap(60, 60, 60)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCurrBillnum, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtResponseCode, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMerchantType, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMessageId, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAction, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(txtResponseAction, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtResponseDescription, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtMsisdn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)))
                                .addGap(32, 32, 32)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel51)
                                    .addComponent(jLabel50)
                                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel53)
                                    .addComponent(jLabel54)
                                    .addComponent(jLabel55)
                                    .addComponent(jLabel56)
                                    .addComponent(jLabel49))
                                .addGap(55, 55, 55)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTotBillAmtDue, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtATMCode, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAccountholder, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTransactionId, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtGenevaPayStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 876, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(380, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38)
                            .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel39))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMerchantType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel40))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txtMessageId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel42))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtResponseCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtResponseDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtResponseAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel45))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCurrBillnum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel46))
                        .addGap(7, 7, 7)
                        .addComponent(btnSendInquiry))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotBillAmtDue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel50))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAccountholder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel51))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTransactionId, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel52))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtGenevaPayStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel53))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel54))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel55))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtATMCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel56))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49))))
                .addGap(7, 7, 7)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("GetSubBillInfo", jPanel3);

        jLabel47.setText("From");

        txtFrom1.setText("euronet");

        txtMerchantType1.setText("180000");

        jLabel48.setText("Merchant Type");

        jLabel57.setText("Message Id");

        txtMessageId1.setText("100000");

        txtAction1.setText("GetSubBillInfo");

        jLabel58.setText("Action");

        jLabel59.setText("MSISDN");

        txtMsisdn1.setText("6289999999");

        txtResponseCode1.setText(null);

        jLabel60.setText("Response Code");

        jLabel61.setText("Response Description");

        txtResponseDescription1.setText(null);

        jLabel62.setText("Response Action");

        txtResponseAction1.setText(null);

        txtCurrBillnum1.setText(null);

        jLabel63.setText("Curr Bill num");

        jLabel64.setText("Amount");

        txtAmount1.setText("10000");

        txtATMCode1.setText("B1203DCH");

        txtDueDate1.setText(null);

        txtDate1.setText(null);

        txtGenevaPayStatus1.setText(null);

        txtTransactionId1.setText(null);

        txtAccountholder1.setText(null);

        txtTotBillAmtDue1.setText(null);

        jLabel65.setText("Tot Bill Amt Due");

        jLabel66.setText("Account Holder");

        jLabel67.setText("Transaction Id");

        jLabel68.setText("Geneva Pay Status");

        jLabel69.setText("Date");

        jLabel70.setText("Due Date");

        jLabel71.setText("ATM Code");

        btnSendPayment.setText("Send");
        btnSendPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendPaymentActionPerformed(evt);
            }
        });

        txtPaymentMessage.setColumns(20);
        txtPaymentMessage.setRows(5);
        jScrollPane4.setViewportView(txtPaymentMessage);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 877, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSendPayment)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47)
                            .addComponent(jLabel57)
                            .addComponent(jLabel48)
                            .addComponent(jLabel58)
                            .addComponent(jLabel59)
                            .addComponent(jLabel60)
                            .addComponent(jLabel61)
                            .addComponent(jLabel62)
                            .addComponent(jLabel63))
                        .addGap(60, 60, 60)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCurrBillnum1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtResponseAction1, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                                    .addComponent(txtResponseCode1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMerchantType1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFrom1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMessageId1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAction1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(txtResponseDescription1, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtMsisdn1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel66)
                                    .addComponent(jLabel65)
                                    .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel68)
                                    .addComponent(jLabel69)
                                    .addComponent(jLabel70)
                                    .addComponent(jLabel71)
                                    .addComponent(jLabel64))
                                .addGap(55, 55, 55)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtAmount1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTotBillAmtDue1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtATMCode1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAccountholder1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTransactionId1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtGenevaPayStatus1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDueDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(390, 390, 390)))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel47)
                            .addComponent(txtFrom1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel48))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMerchantType1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel57))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txtMessageId1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAction1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel58))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMsisdn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel59))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtResponseCode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel60))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtResponseDescription1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel61))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtResponseAction1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel62))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCurrBillnum1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel63))
                        .addGap(7, 7, 7)
                        .addComponent(btnSendPayment))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotBillAmtDue1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel65))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAccountholder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel66))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTransactionId1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel67))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtGenevaPayStatus1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel68))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel69))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDueDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel70))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtATMCode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel71))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAmount1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel64))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Payment", jPanel4);

        jLabel72.setText("From");

        txtFrom2.setText("euronet");

        txtMerchantType2.setText("180000");

        jLabel73.setText("Merchant Type");

        jLabel74.setText("Message Id");

        txtMessageId2.setText("100000");

        txtAction2.setText("GetSubBillInfo");

        jLabel75.setText("Action");

        jLabel76.setText("MSISDN");

        txtMsisdn2.setText("6289999999");

        txtResponseCode2.setText(null);

        jLabel77.setText("Response Code");

        jLabel78.setText("Response Description");

        txtResponseDescription2.setText(null);

        jLabel79.setText("Response Action");

        txtResponseAction2.setText(null);

        txtCurrBillnum2.setText(null);

        jLabel80.setText("Curr Bill num");

        jLabel81.setText("Amount");

        txtAmount2.setText("0");

        txtATMCode2.setText("B1203DCH");

        txtDueDate2.setText(null);

        txtDate2.setText(null);

        txtGenevaPayStatus2.setText(null);

        txtTransactionId2.setText(null);

        txtAccountholder2.setText(null);

        txtTotBillAmtDue2.setText(null);

        jLabel82.setText("Tot Bill Amt Due");

        jLabel83.setText("Account Holder");

        jLabel84.setText("Transaction Id");

        jLabel85.setText("Geneva Pay Status");

        jLabel86.setText("Date");

        jLabel87.setText("Due Date");

        jLabel88.setText("ATM Code");

        btnSendCancelPayment.setText("Send");
        btnSendCancelPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendCancelPaymentActionPerformed(evt);
            }
        });

        txtCancelPaymentMessage.setColumns(20);
        txtCancelPaymentMessage.setRows(5);
        jScrollPane5.setViewportView(txtCancelPaymentMessage);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel72)
                            .addComponent(jLabel74)
                            .addComponent(jLabel73)
                            .addComponent(jLabel75)
                            .addComponent(jLabel76)
                            .addComponent(jLabel77)
                            .addComponent(jLabel78)
                            .addComponent(jLabel79)
                            .addComponent(jLabel80))
                        .addGap(60, 60, 60)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtResponseAction2, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                                    .addComponent(txtResponseCode2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMerchantType2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFrom2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMessageId2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAction2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(txtResponseDescription2, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtMsisdn2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(27, 27, 27))
                            .addComponent(txtCurrBillnum2, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel83)
                            .addComponent(jLabel82)
                            .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel85)
                            .addComponent(jLabel86)
                            .addComponent(jLabel87)
                            .addComponent(jLabel88)
                            .addComponent(jLabel81))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAmount2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotBillAmtDue2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtATMCode2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDate2, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAccountholder2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTransactionId2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtGenevaPayStatus2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDueDate2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(348, 348, 348))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnSendCancelPayment)
                        .addContainerGap(812, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 871, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel72)
                            .addComponent(txtFrom2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel73))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMerchantType2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel74))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txtMessageId2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAction2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel75))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMsisdn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel76))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtResponseCode2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel77))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtResponseDescription2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel78))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtResponseAction2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel79))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCurrBillnum2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel80))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSendCancelPayment))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotBillAmtDue2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel82))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAccountholder2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel83))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTransactionId2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel84))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtGenevaPayStatus2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel85))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel86))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDueDate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel87))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtATMCode2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel88))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAmount2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel81))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        jTabbedPane1.addTab("CancelPayment", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 928, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("BillInfo");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSendInquiryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendInquiryActionPerformed
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        builder.append("<!DOCTYPE OnlineRequest SYSTEM \"OnlineRequest.dtd\">");
        builder.append("<OnlineRequest>");
        builder.append("<serviceAddressing>");
        //kalau inquiry response amount harus 0
        //buat tanggal dari iso msg bit 13 MMdd
        builder.append("<From>euronet</From>");
        builder.append("<MerchantType>"+txtMerchantType.getText()+"</MerchantType>");
        builder.append("<MessageId>"+txtMessageId.getText()+"</MessageId>");
        builder.append("<Action>"+ XmlTransformer.XML_INQUIRY_CODE +"</Action>");
        builder.append("</serviceAddressing>");
        builder.append("<MSISDN>" + txtMsisdn.getText() +"</MSISDN>");
        builder.append("<BillingResponse>");

        builder.append("<ResponseResult>");
        //check response code di iso
        builder.append("<ResponseCode>null</ResponseCode>");
        builder.append("<ResponseDescription>null</ResponseDescription>");
        builder.append("<ResponseAction>null</ResponseAction>");
        builder.append("</ResponseResult>");
        //bill reference number diletakkan di bit 48 dari posisi 19 sepanjang 16
        builder.append("<CurrBillnum>null</CurrBillnum>"); //nilai apa nih?
        //jumlah tagihan. Trim left zero
        builder.append("<TotBillAmtDue>null</TotBillAmtDue>");
        //nama yang punya nomor telpon
        builder.append("<Accountholder>null</Accountholder>");
        //format data tanggal dd/MM/yyyy HH:mm:ss
        builder.append("<TransactionId>null</TransactionId>");
        builder.append("<GenevaPayStatus>null</GenevaPayStatus>");
        builder.append("<Date>"+ txtDate.getText()+"</Date>");
        //TODO due date diambil dari mana ya?
        builder.append("<DueDate>null</DueDate>");
        builder.append("<ATMCode>"+txtATMCode.getText()+"</ATMCode>");
        builder.append("<Amount>0</Amount>");

        builder.append("</BillingResponse>");
        builder.append("</OnlineRequest>");
        txtInquiryMessage.setText(builder.toString());
        sendXml(builder.toString());
    }//GEN-LAST:event_btnSendInquiryActionPerformed

    private void btnSendPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendPaymentActionPerformed
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        builder.append("<!DOCTYPE OnlineRequest SYSTEM \"OnlineRequest.dtd\">");
        builder.append("<OnlineRequest>");
        builder.append("<serviceAddressing>");
        builder.append("<From>euronet</From>");
        builder.append("<MerchantType>"+txtMerchantType1.getText()+"</MerchantType>");
        builder.append("<MessageId>"+txtMessageId1.getText()+"</MessageId>");
        builder.append("<Action>"+ XmlTransformer.XML_PAYMENT_CODE +"</Action>");
        builder.append("</serviceAddressing>");
        builder.append("<MSISDN>" + txtMsisdn1.getText() +"</MSISDN>");
        builder.append("<BillingResponse>");
        builder.append("<ResponseResult>");
        //check response code di iso
        builder.append("<ResponseCode>null</ResponseCode>");
        builder.append("<ResponseDescription>null</ResponseDescription>");
        builder.append("<ResponseAction>null</ResponseAction>");
        builder.append("</ResponseResult>");
        //bill reference number diletakkan di bit 48 dari posisi 19 sepanjang 16
        builder.append("<CurrBillnum>null</CurrBillnum>"); //nilai apa nih?
        //jumlah tagihan. Trim left zero
        builder.append("<TotBillAmtDue>null</TotBillAmtDue>");
        //nama yang punya nomor telpon
        builder.append("<Accountholder>null</Accountholder>");
        //diambil dari ddMMyy + bit 11. Di dokumen euronet diminta panjangnya 12
        builder.append("<TransactionId>null</TransactionId>");
        builder.append("<GenevaPayStatus>null</GenevaPayStatus>");
        //format data tanggal dd/MM/yyyy HH:mm:ss
        builder.append("<Date>"+ txtDate1.getText()+"</Date>");
        //TODO due date diambil dari mana ya?
        builder.append("<DueDate>null</DueDate>");
        builder.append("<ATMCode>"+txtATMCode1.getText()+"</ATMCode>");
        builder.append("<Amount>"+StringUtils.padZeroRightAlign(txtAmount1.getText(), 12)+"</Amount>");

        builder.append("</BillingResponse>");
        builder.append("</OnlineRequest>");
        txtPaymentMessage.setText(builder.toString());
        sendXml(builder.toString());

    }//GEN-LAST:event_btnSendPaymentActionPerformed

    private void btnSendCancelPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendCancelPaymentActionPerformed
        // TODO add your handling code here:
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        builder.append("<!DOCTYPE OnlineRequest SYSTEM \"OnlineRequest.dtd\">");
        builder.append("<OnlineRequest>");
        builder.append("<serviceAddressing>");
        builder.append("<From>euronet</From>");
        builder.append("<MerchantType>"+txtMerchantType2.getText()+"</MerchantType>");
        builder.append("<MessageId>"+txtMessageId2.getText()+"</MessageId>");
        builder.append("<Action>"+ XmlTransformer.XML_CANCEL_PAYMENT_CODE +"</Action>");
        builder.append("</serviceAddressing>");
        builder.append("<MSISDN>" + txtMsisdn2.getText() +"</MSISDN>");
        builder.append("<BillingResponse>");
        builder.append("<ResponseResult>");
        //check response code di iso
        builder.append("<ResponseCode>null</ResponseCode>");
        builder.append("<ResponseDescription>null</ResponseDescription>");
        builder.append("<ResponseAction>null</ResponseAction>");
        builder.append("</ResponseResult>");
        builder.append("<CurrBillnum>null</CurrBillnum>"); //nilai apa nih?
        builder.append("<TotBillAmtDue>null</TotBillAmtDue>");
        //ada nih harusnya
        builder.append("<Accountholder>null</Accountholder>");
        //ITM tidak mencatat transactionid dari HUT sehingga harus lookup ke database
        //TODO cara lookup transaction ID ini gimana ya?
        builder.append("<TransactionId></TransactionId>");
        builder.append("<GenevaPayStatus>null</GenevaPayStatus>");
        //format data tanggal dd/MM/yyyy HH:mm:ss
        builder.append("<Date>"+ txtDate2.getText()+"</Date>");
        //TODO due date diambil dari mana ya?
        builder.append("<DueDate>null</DueDate>");
        builder.append("<ATMCode>"+txtATMCode2.getText()+"</ATMCode>");
        builder.append("<Amount>"+StringUtils.padZeroRightAlign(txtAmount2.getText(), 12)+"</Amount>");

        builder.append("</BillingResponse>");
        builder.append("</OnlineRequest>");
        txtCancelPaymentMessage.setText(builder.toString());
        sendXml(builder.toString());
    }//GEN-LAST:event_btnSendCancelPaymentActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new HutSenderFrame().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(HutSenderFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSendCancelPayment;
    private javax.swing.JButton btnSendInquiry;
    private javax.swing.JButton btnSendPayment;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField txtATMCode;
    private javax.swing.JTextField txtATMCode1;
    private javax.swing.JTextField txtATMCode2;
    private javax.swing.JTextField txtAccountholder;
    private javax.swing.JTextField txtAccountholder1;
    private javax.swing.JTextField txtAccountholder2;
    private javax.swing.JTextField txtAction;
    private javax.swing.JTextField txtAction1;
    private javax.swing.JTextField txtAction2;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtAmount1;
    private javax.swing.JTextField txtAmount2;
    private javax.swing.JTextArea txtCancelPaymentMessage;
    private javax.swing.JTextField txtCurrBillnum;
    private javax.swing.JTextField txtCurrBillnum1;
    private javax.swing.JTextField txtCurrBillnum2;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDate1;
    private javax.swing.JTextField txtDate2;
    private javax.swing.JTextField txtDueDate;
    private javax.swing.JTextField txtDueDate1;
    private javax.swing.JTextField txtDueDate2;
    private javax.swing.JTextField txtFrom;
    private javax.swing.JTextField txtFrom1;
    private javax.swing.JTextField txtFrom2;
    private javax.swing.JTextField txtGenevaPayStatus;
    private javax.swing.JTextField txtGenevaPayStatus1;
    private javax.swing.JTextField txtGenevaPayStatus2;
    private javax.swing.JTextArea txtInquiryMessage;
    private javax.swing.JTextField txtMerchantType;
    private javax.swing.JTextField txtMerchantType1;
    private javax.swing.JTextField txtMerchantType2;
    private javax.swing.JTextField txtMessageId;
    private javax.swing.JTextField txtMessageId1;
    private javax.swing.JTextField txtMessageId2;
    private javax.swing.JTextField txtMsisdn;
    private javax.swing.JTextField txtMsisdn1;
    private javax.swing.JTextField txtMsisdn2;
    private javax.swing.JTextArea txtPaymentMessage;
    private javax.swing.JTextField txtResponseAction;
    private javax.swing.JTextField txtResponseAction1;
    private javax.swing.JTextField txtResponseAction2;
    private javax.swing.JTextField txtResponseCode;
    private javax.swing.JTextField txtResponseCode1;
    private javax.swing.JTextField txtResponseCode2;
    private javax.swing.JTextField txtResponseDescription;
    private javax.swing.JTextField txtResponseDescription1;
    private javax.swing.JTextField txtResponseDescription2;
    private javax.swing.JTextField txtTotBillAmtDue;
    private javax.swing.JTextField txtTotBillAmtDue1;
    private javax.swing.JTextField txtTotBillAmtDue2;
    private javax.swing.JTextField txtTransactionId;
    private javax.swing.JTextField txtTransactionId1;
    private javax.swing.JTextField txtTransactionId2;
    // End of variables declaration//GEN-END:variables

}
