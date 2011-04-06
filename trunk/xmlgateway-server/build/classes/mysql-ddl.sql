 CREATE TABLE `TRANSAKSI_XML` (
  `ID` bigint(20) NOT NULL auto_increment,
  `AMOUNT` decimal(19,2) default NULL,
  `ISO` text,
  `MESSAGE_ID` varchar(255) default NULL,
  `MSISDN` varchar(19) NOT NULL,
  `REQUEST_RESPONSE` int(11) NOT NULL,
  `STAN` varchar(6) NOT NULL,
  `STATUS` int(11) NOT NULL,
  `TRANSACTION_DATE` datetime NOT NULL,
  `TRANSACTION_ID` varchar(30) default NULL,
  `TRANSAKSI_TYPE` int(11) NOT NULL,
  `XML` text,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB;