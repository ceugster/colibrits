-- MySQL dump 10.13  Distrib 5.1.44, for Win32 (ia32)
--
-- Host: localhost    Database: colibri
-- ------------------------------------------------------
-- Server version	5.1.44-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `colibri`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `temp` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `temp`;

--
-- Table structure for table `obj_nrm`
--

DROP TABLE IF EXISTS `obj_nrm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `obj_nrm` (
  `name` varchar(100) NOT NULL DEFAULT '',
  `oid_` longblob NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `obj_nrm`
--

LOCK TABLES `obj_nrm` WRITE;
/*!40000 ALTER TABLE `obj_nrm` DISABLE KEYS */;
/*!40000 ALTER TABLE `obj_nrm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dlist`
--

DROP TABLE IF EXISTS `ojb_dlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ojb_dlist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `size_` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ojb_dlist`
--

LOCK TABLES `ojb_dlist` WRITE;
/*!40000 ALTER TABLE `ojb_dlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dlist_entries`
--

DROP TABLE IF EXISTS `ojb_dlist_entries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ojb_dlist_entries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dlist_id` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `oid_` longblob NOT NULL,
  `position_` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ojb_dlist_entries`
--

LOCK TABLES `ojb_dlist_entries` WRITE;
/*!40000 ALTER TABLE `ojb_dlist_entries` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dlist_entries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dmap`
--

DROP TABLE IF EXISTS `ojb_dmap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ojb_dmap` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `size_` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ojb_dmap`
--

LOCK TABLES `ojb_dmap` WRITE;
/*!40000 ALTER TABLE `ojb_dmap` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dmap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dmap_entries`
--

DROP TABLE IF EXISTS `ojb_dmap_entries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ojb_dmap_entries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dmap_id` int(11) NOT NULL DEFAULT '0',
  `key_oid` longblob NOT NULL,
  `value_oid` longblob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ojb_dmap_entries`
--

LOCK TABLES `ojb_dmap_entries` WRITE;
/*!40000 ALTER TABLE `ojb_dmap_entries` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dmap_entries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dset`
--

DROP TABLE IF EXISTS `ojb_dset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ojb_dset` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `size_` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ojb_dset`
--

LOCK TABLES `ojb_dset` WRITE;
/*!40000 ALTER TABLE `ojb_dset` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_dset_entries`
--

DROP TABLE IF EXISTS `ojb_dset_entries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ojb_dset_entries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dlist_id` int(11) NOT NULL DEFAULT '0',
  `position_` int(11) NOT NULL DEFAULT '0',
  `oid_` longblob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ojb_dset_entries`
--

LOCK TABLES `ojb_dset_entries` WRITE;
/*!40000 ALTER TABLE `ojb_dset_entries` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_dset_entries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_hl_seq`
--

DROP TABLE IF EXISTS `ojb_hl_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ojb_hl_seq` (
  `tablename` varchar(100) NOT NULL DEFAULT '',
  `fieldname` varchar(100) NOT NULL DEFAULT '',
  `max_key` int(11) NOT NULL DEFAULT '0',
  `grab_size` int(11) NOT NULL DEFAULT '0',
  `version` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`tablename`,`fieldname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ojb_hl_seq`
--

LOCK TABLES `ojb_hl_seq` WRITE;
/*!40000 ALTER TABLE `ojb_hl_seq` DISABLE KEYS */;
INSERT INTO `ojb_hl_seq` VALUES ('SEQ_pos_key','deprecatedColumn',220,20,11),('SEQ_pos_payment','deprecatedColumn',8992888,20,56275),('SEQ_pos_payment_type','',38,20,1),('SEQ_pos_position','deprecatedColumn',9098926,20,61577),('SEQ_pos_product_group','',250,20,9),('SEQ_pos_receipt','deprecatedColumn',3263460,20,37950),('SEQ_pos_salespoint','deprecatedColumn',375,20,6),('SEQ_pos_settlement','',21,20,1),('SEQ_pos_stock','',61,20,3),('SEQ_pos_tab','deprecatedColumn',100,20,5),('SEQ_pos_user','deprecatedColumn',22,20,1);
/*!40000 ALTER TABLE `ojb_hl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_lockentry`
--

DROP TABLE IF EXISTS `ojb_lockentry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ojb_lockentry` (
  `oid_` varchar(100) NOT NULL DEFAULT '',
  `tx_id` varchar(100) NOT NULL DEFAULT '',
  `timestamp_` bigint(20) NOT NULL DEFAULT '0',
  `isolationlevel` int(11) NOT NULL DEFAULT '0',
  `locktype` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`oid_`,`tx_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ojb_lockentry`
--

LOCK TABLES `ojb_lockentry` WRITE;
/*!40000 ALTER TABLE `ojb_lockentry` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_lockentry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ojb_nrm`
--

DROP TABLE IF EXISTS `ojb_nrm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ojb_nrm` (
  `name` varchar(50) DEFAULT NULL,
  `oid_` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ojb_nrm`
--

LOCK TABLES `ojb_nrm` WRITE;
/*!40000 ALTER TABLE `ojb_nrm` DISABLE KEYS */;
/*!40000 ALTER TABLE `ojb_nrm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_access`
--

DROP TABLE IF EXISTS `pos_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_access` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `name` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_access`
--

LOCK TABLES `pos_access` WRITE;
/*!40000 ALTER TABLE `pos_access` DISABLE KEYS */;
/*!40000 ALTER TABLE `pos_access` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_block`
--

DROP TABLE IF EXISTS `pos_block`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_block` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `visible` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `name` varchar(100) NOT NULL DEFAULT '',
  `class` varchar(255) NOT NULL DEFAULT '',
  `font_size` float DEFAULT '14',
  `font_style` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `class` (`class`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_block`
--

LOCK TABLES `pos_block` WRITE;
/*!40000 ALTER TABLE `pos_block` DISABLE KEYS */;
INSERT INTO `pos_block` VALUES (1,'2005-02-24 01:45:28',1,'Warengruppen','ch.eugster.pos.client.gui.ProductGroupBlock',20,1),(2,'2005-02-24 01:45:28',1,'Zahlungsarten','ch.eugster.pos.client.gui.PaymentTypeBlock',22,1),(3,'2005-02-24 01:45:28',0,'','ch.eugster.pos.client.gui.CurrentReceiptTableBlock',14,1),(4,'2005-02-24 01:45:28',1,'Funktionen','ch.eugster.pos.client.gui.ABlockFunction',22,1),(5,'2005-02-24 01:45:28',0,'','ch.eugster.pos.client.gui.PositionBlock',14,1),(6,'2005-02-24 01:45:28',0,'','ch.eugster.pos.client.gui.ABlockNumeric',14,1),(7,'2005-02-24 01:45:28',0,'','ch.eugster.pos.client.gui.PaymentBlock',14,1),(8,'2005-02-24 01:45:28',1,'Münzzähler','ch.eugster.pos.client.gui.CoinCounter',14,1);
/*!40000 ALTER TABLE `pos_block` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_coin`
--

DROP TABLE IF EXISTS `pos_coin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_coin` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `value` double unsigned NOT NULL DEFAULT '0',
  `foreign_currency_id` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_coin`
--

LOCK TABLES `pos_coin` WRITE;
/*!40000 ALTER TABLE `pos_coin` DISABLE KEYS */;
INSERT INTO `pos_coin` VALUES (1,0.05,22),(2,0.1,22),(3,0.2,22),(4,0.5,22),(5,1,22),(6,2,22),(7,5,22),(8,10,22),(9,20,22),(10,50,22),(11,100,22),(12,200,22);
/*!40000 ALTER TABLE `pos_coin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_current_tax`
--

DROP TABLE IF EXISTS `pos_current_tax`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_current_tax` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `tax_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fibu_id` varchar(10) NOT NULL DEFAULT '',
  `percentage` double NOT NULL DEFAULT '0',
  `validation_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `tax_id` (`tax_id`),
  KEY `validation_date` (`validation_date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_current_tax`
--

LOCK TABLES `pos_current_tax` WRITE;
/*!40000 ALTER TABLE `pos_current_tax` DISABLE KEYS */;
INSERT INTO `pos_current_tax` VALUES (1,'2005-02-24 17:59:51',0,1,'',0,'2001-01-01 00:00:00'),(2,'2005-02-24 17:59:52',0,2,'',0,'2001-01-01 00:00:00'),(3,'2005-02-24 17:59:52',0,3,'',0,'2001-01-01 00:00:00'),(4,'2005-02-24 17:59:52',0,4,'',2.4,'2001-01-01 00:00:00'),(5,'2005-02-24 17:59:52',0,5,'',2.4,'2001-01-01 00:00:00'),(6,'2005-02-24 17:59:52',0,6,'',2.4,'2001-01-01 00:00:00'),(7,'2005-02-24 17:59:52',0,7,'',7.6,'2001-01-01 00:00:00'),(8,'2005-02-24 17:59:52',0,8,'',7.6,'2001-01-01 00:00:00'),(9,'2005-02-24 17:59:52',0,9,'',7.6,'2001-01-01 00:00:00');
/*!40000 ALTER TABLE `pos_current_tax` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_fixkey`
--

DROP TABLE IF EXISTS `pos_fixkey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_fixkey` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `fix_key_group_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `block` varchar(255) NOT NULL DEFAULT '',
  `text_editable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `row` int(10) unsigned NOT NULL DEFAULT '0',
  `col` int(10) unsigned NOT NULL DEFAULT '0',
  `bg_red` int(3) unsigned NOT NULL DEFAULT '255',
  `bg_green` int(3) unsigned NOT NULL DEFAULT '255',
  `bg_blue` int(3) unsigned NOT NULL DEFAULT '255',
  `fg_red` int(11) NOT NULL DEFAULT '0',
  `fg_green` int(11) NOT NULL DEFAULT '0',
  `fg_blue` int(11) NOT NULL DEFAULT '0',
  `font_size` double NOT NULL DEFAULT '0',
  `font_style` int(11) NOT NULL DEFAULT '0',
  `align` int(11) NOT NULL DEFAULT '0',
  `valign` int(11) NOT NULL DEFAULT '0',
  `image_path` varchar(255) NOT NULL DEFAULT '',
  `rel_horizontal_text_pos` int(11) NOT NULL DEFAULT '0',
  `rel_vertical_text_pos` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `command` varchar(100) NOT NULL DEFAULT '',
  `class_name` varchar(255) NOT NULL DEFAULT '',
  `action_type` int(10) unsigned NOT NULL DEFAULT '0',
  `bg_red_failover` int(3) NOT NULL DEFAULT '255',
  `bg_green_failover` int(3) NOT NULL DEFAULT '255',
  `bg_blue_failover` int(3) NOT NULL DEFAULT '255',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_button` (`block`,`row`,`col`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_fixkey`
--

LOCK TABLES `pos_fixkey` WRITE;
/*!40000 ALTER TABLE `pos_fixkey` DISABLE KEYS */;
INSERT INTO `pos_fixkey` VALUES (0,'2010-03-04 18:51:11',6,'ch.eugster.pos.client.gui.CoinCounter',0,0,2,255,255,255,0,0,0,12,1,0,0,'',0,0,'Leeren','clear','ch.eugster.pos.events.ClearAllCoinsAction',0,255,222,222),(11,'2005-02-24 01:45:29',4,'ch.eugster.pos.client.gui.PositionBlock',0,0,0,222,255,222,0,0,0,12,1,0,0,'',0,0,'Menge','quantity','ch.eugster.pos.events.QuantityAction',201,255,222,222),(12,'2005-02-24 01:45:29',4,'ch.eugster.pos.client.gui.PositionBlock',0,0,1,222,255,222,0,0,0,12,1,0,0,'',0,0,'Preis','price','ch.eugster.pos.events.PriceAction',202,255,222,222),(13,'2005-02-24 01:45:29',4,'ch.eugster.pos.client.gui.PositionBlock',0,0,2,222,255,222,0,0,0,12,1,0,0,'',0,0,'Rabatt','discount','ch.eugster.pos.events.DiscountAction',203,255,222,222),(16,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,0,0,232,222,211,0,0,0,12,1,0,0,'',0,0,'7','7','ch.eugster.pos.events.DigitAction',0,255,222,222),(17,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,0,1,232,222,211,0,0,0,12,1,0,0,'',0,0,'8','8','ch.eugster.pos.events.DigitAction',0,255,222,222),(18,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,0,2,232,222,211,0,0,0,12,1,0,0,'',0,0,'9','9','ch.eugster.pos.events.DigitAction',0,255,222,222),(19,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,1,3,232,222,211,0,0,0,12,1,0,0,'',0,0,'Leeren','clear','ch.eugster.pos.events.ClearAction',1,255,222,222),(20,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,1,0,232,222,211,0,0,0,12,1,0,0,'',0,0,'4','4','ch.eugster.pos.events.DigitAction',0,255,222,222),(21,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,1,1,232,222,211,0,0,0,12,1,0,0,'',0,0,'5','5','ch.eugster.pos.events.DigitAction',0,255,222,222),(22,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,1,2,232,222,211,0,0,0,12,1,0,0,'',0,0,'6','6','ch.eugster.pos.events.DigitAction',0,255,222,222),(23,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,0,3,232,222,211,0,0,0,12,1,0,0,'',0,0,'Lschen','delete','ch.eugster.pos.events.DeleteAction',401,255,222,222),(24,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,2,0,232,222,211,0,0,0,12,1,0,0,'',0,0,'1','1','ch.eugster.pos.events.DigitAction',0,255,222,222),(25,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,2,1,232,222,211,0,0,0,12,1,0,0,'',0,0,'2','2','ch.eugster.pos.events.DigitAction',0,255,222,222),(26,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,2,2,232,222,211,0,0,0,12,1,0,0,'',0,0,'3','3','ch.eugster.pos.events.DigitAction',0,255,222,222),(27,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,2,3,232,222,211,0,0,0,12,1,0,0,'',0,0,'Eingabe','enter','ch.eugster.pos.events.EnterAction',400,255,222,222),(28,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,3,0,232,222,211,0,0,0,12,1,0,0,'',0,0,'0','0','ch.eugster.pos.events.DigitAction',0,255,222,222),(29,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,3,1,232,222,211,0,0,0,12,1,0,0,'',0,0,'00','00','ch.eugster.pos.events.DigitAction',0,255,222,222),(30,'2005-02-24 01:45:29',1,'ch.eugster.pos.client.gui.ABlockNumeric',0,3,2,232,222,211,0,0,0,12,1,0,0,'',0,0,'.','.','ch.eugster.pos.events.DigitAction',0,255,222,222),(33,'2005-02-24 01:45:29',4,'ch.eugster.pos.client.gui.PositionBlock',0,0,3,192,192,192,0,0,0,12,1,0,0,'',0,0,'Auf','up','ch.eugster.pos.events.ReceiptTableListAction',0,255,222,222),(42,'2005-02-24 01:45:30',2,'ch.eugster.pos.client.gui.CurrentReceiptTableBlock',0,0,0,255,255,255,0,0,0,12,1,0,0,'',0,0,'Ab','down','ch.eugster.pos.events.ReceiptTableListAction',0,255,222,222),(43,'2005-02-24 01:45:30',2,'ch.eugster.pos.client.gui.CurrentReceiptTableBlock',0,0,1,255,255,255,0,0,0,12,1,0,0,'',0,0,'Auf','up','ch.eugster.pos.events.ReceiptTableListAction',0,255,222,222),(44,'2005-02-24 01:45:30',2,'ch.eugster.pos.client.gui.CurrentReceiptTableBlock',0,0,3,255,255,255,0,0,0,12,1,0,0,'',0,0,'Storno','reverse','ch.eugster.pos.events.ReverseAction',0,255,222,222),(45,'2005-02-24 01:45:30',2,'ch.eugster.pos.client.gui.CurrentReceiptTableBlock',0,0,4,255,255,255,0,0,0,12,1,0,0,'',0,0,'Zurck','back','ch.eugster.pos.events.ReceiptTableListAction',0,255,222,222),(51,'2005-02-24 01:45:30',5,'ch.eugster.pos.client.gui.PaymentBlock',1,0,0,255,255,255,0,0,0,12,1,0,0,'',0,0,'Speichern','store0','ch.eugster.pos.events.StoreReceiptAction',100,255,222,222),(52,'2005-02-24 01:45:30',2,'ch.eugster.pos.client.gui.CurrentReceiptTableBlock',0,0,2,255,255,255,0,0,0,12,1,0,0,'',0,0,'Drucken','print','ch.eugster.pos.events.PrintReceiptAction',0,255,222,222),(60,'2005-02-24 01:45:30',3,'ch.eugster.pos.client.gui.ParkedReceiptTableBlock',0,0,0,255,255,255,0,0,0,12,1,0,0,'',0,0,'Ab','down','ch.eugster.pos.events.ReceiptTableListAction',0,255,222,222),(61,'2005-02-24 01:45:30',3,'ch.eugster.pos.client.gui.ParkedReceiptTableBlock',0,0,1,255,255,255,0,0,0,12,1,0,0,'',0,0,'Auf','up','ch.eugster.pos.events.ReceiptTableListAction',0,255,222,222),(62,'2005-02-24 01:45:30',3,'ch.eugster.pos.client.gui.ParkedReceiptTableBlock',0,0,2,255,255,255,0,0,0,12,1,0,0,'',0,0,'Hole','get','ch.eugster.pos.events.SelectReceiptAction',0,255,222,222),(64,'2005-02-24 01:45:30',3,'ch.eugster.pos.client.gui.ParkedReceiptTableBlock',0,0,3,255,255,255,0,0,0,12,1,0,0,'',0,0,'Zurck','back','ch.eugster.pos.events.ReceiptTableListAction',0,255,222,222),(65,'2005-02-24 01:45:30',5,'ch.eugster.pos.client.gui.PaymentBlock',1,0,1,255,255,255,0,0,0,12,1,0,0,'',0,0,'Speichern','store1','ch.eugster.pos.events.StoreReceiptAction',101,255,222,222),(66,'2005-02-24 01:45:30',5,'ch.eugster.pos.client.gui.PaymentBlock',1,0,2,255,255,255,0,0,0,12,1,0,0,'',0,0,'Coupon drucken','store2','ch.eugster.pos.events.ExpressStoreReceiptAction',102,255,222,222),(67,'2005-02-24 01:45:30',5,'ch.eugster.pos.client.gui.PaymentBlock',0,0,3,192,192,192,0,0,0,12,1,0,0,'',0,0,'Auf','up','ch.eugster.pos.events.ReceiptTableListAction',0,255,222,222),(68,'2005-02-24 01:45:30',4,'ch.eugster.pos.client.gui.PositionBlock',0,0,4,192,192,192,0,0,0,12,1,0,0,'',0,0,'Ab','down','ch.eugster.pos.events.ReceiptTableListAction',0,255,222,222),(69,'2005-02-24 01:45:30',5,'ch.eugster.pos.client.gui.PaymentBlock',0,0,4,192,192,192,0,0,0,12,1,0,0,'',0,0,'Ab','down','ch.eugster.pos.events.ReceiptTableListAction',0,255,222,222),(70,'2005-02-24 01:45:30',6,'ch.eugster.pos.client.gui.CoinCounter',0,0,1,255,255,255,0,0,0,12,1,0,0,'',0,0,'Abschliessen','settleDay','ch.eugster.pos.events.SettleDayAction',900,255,222,222),(71,'2005-02-24 01:45:30',6,'ch.eugster.pos.client.gui.CoinCounter',0,0,0,255,255,255,0,0,0,12,1,0,0,'',0,0,'Zurck','back','ch.eugster.pos.events.BackAction',0,255,222,222);
/*!40000 ALTER TABLE `pos_fixkey` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_fixkey_group`
--

DROP TABLE IF EXISTS `pos_fixkey_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_fixkey_group` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_fixkey_group`
--

LOCK TABLES `pos_fixkey_group` WRITE;
/*!40000 ALTER TABLE `pos_fixkey_group` DISABLE KEYS */;
INSERT INTO `pos_fixkey_group` VALUES (1,'2005-02-24 01:45:30','Numerischer Block'),(2,'2005-02-24 01:45:30','Belegliste'),(3,'2005-02-24 01:45:30','Parkierliste'),(4,'2005-02-24 01:45:30','Positionendetails'),(5,'2005-02-24 01:45:30','Zahlungsdetails'),(6,'2005-02-24 01:45:30','Abschluss');
/*!40000 ALTER TABLE `pos_fixkey_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_foreign_currency`
--

DROP TABLE IF EXISTS `pos_foreign_currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_foreign_currency` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `code` varchar(5) NOT NULL DEFAULT '',
  `name` varchar(20) NOT NULL DEFAULT '',
  `region` varchar(100) NOT NULL DEFAULT '',
  `quotation` double unsigned NOT NULL DEFAULT '1',
  `account` varchar(100) NOT NULL DEFAULT '',
  `round_factor` double unsigned NOT NULL DEFAULT '0.01',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniquekey` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_foreign_currency`
--

LOCK TABLES `pos_foreign_currency` WRITE;
/*!40000 ALTER TABLE `pos_foreign_currency` DISABLE KEYS */;
INSERT INTO `pos_foreign_currency` VALUES (1,'2005-02-24 01:45:30',0,'AED','Dirham','Vereinigte Arabische Emirate',1,'',0.01),(2,'2005-02-24 01:45:30',0,'ALL','Lek','Albanien',1,'',0.01),(3,'2005-02-24 01:45:30',0,'ANG','Gulden','Niederlndische Antillen',1,'',0.01),(4,'2005-02-24 01:45:30',0,'AOA','Kwanza','Angola',1,'',0.01),(5,'2005-02-24 01:45:30',0,'ARS','Peso','Argentinien',1,'',0.01),(6,'2005-02-24 01:45:30',0,'ATS','Schilling','sterreich (bis 2001 ab 2002 Euro)',1,'',0.01),(7,'2005-02-24 01:45:30',0,'AUD','Dollar','Australien',0.9467,'',0.01),(8,'2005-02-24 01:45:30',0,'AWG','Aruba-Florin','Aruba',1,'',0.01),(9,'2005-02-24 01:45:31',0,'BBD','Dollar','Barbados',1,'',0.01),(10,'2005-02-24 01:45:31',0,'BEF','Franc','Belgien (bis 2001 ab 2002 Euro)',1,'',0.01),(11,'2005-02-24 01:45:31',0,'BGL','alter Lew','Bulgarien',1,'',0.01),(12,'2005-02-24 01:45:31',0,'BGN','neuer Lew','Bulgarien',1,'',0.01),(13,'2005-02-24 01:45:31',0,'BHD','Dinar','Bahrein',1,'',0.01),(14,'2005-02-24 01:45:31',0,'BMD','Dollar','Bermuda',1,'',0.01),(15,'2005-02-24 01:45:31',0,'BND','Dollar','Brunei',1,'',0.01),(16,'2005-02-24 01:45:31',0,'BOB','Boliviano','Bolivien',1,'',0.01),(17,'2005-02-24 01:45:31',0,'BRL','Real','Brasilien',1,'',0.01),(18,'2005-02-24 01:45:31',0,'BSD','Dollar','Bahamas',1,'',0.01),(19,'2005-02-24 01:45:31',0,'BYR','Rubel','Weirussland',1,'',0.01),(20,'2005-02-24 01:45:31',0,'BZD','Dollar','Belize',1,'',0.01),(21,'2005-02-24 01:45:31',0,'CAD','Dollar','Kanada',0.9496,'',0.01),(22,'2005-02-24 01:45:31',0,'CHF','Franken','Schweiz',1,'',0.05),(23,'2005-02-24 01:45:31',0,'CLP','Peso','Chile',1,'',0.01),(24,'2005-02-24 01:45:31',0,'COP','Peso','Kolumbien',1,'',0.01),(25,'2005-02-24 01:45:31',0,'CRC','Colon','Costa Rica',1,'',0.01),(26,'2005-02-24 01:45:31',0,'CSK','Krone','Tschechien',1,'',0.01),(27,'2005-02-24 01:45:31',0,'CUP','Peso','Kuba',1,'',0.01),(28,'2005-02-24 01:45:31',0,'CYP','Pfund','Zypern (griechischer Teil)',1,'',0.01),(29,'2005-02-24 01:45:31',0,'DEM','Mark','Deutschland (bis 2001 ab 2002 Euro)',1,'',0.01),(30,'2005-02-24 01:45:31',0,'DKK','Krone','Dnemark',0.2028,'',0.01),(31,'2005-02-24 01:45:31',0,'DOP','Peso','Dominikanische Republik',1,'',0.01),(32,'2005-02-24 01:45:31',0,'DZD','Dinar','Algerien',1,'',0.01),(33,'2005-02-24 01:45:31',0,'ECS','Sucre','Ecuador',1,'',0.01),(34,'2005-02-24 01:45:31',0,'EEK','Krone','Estland',1,'',0.01),(35,'2005-02-24 01:45:31',0,'EGP','Pfund','gypten',1,'',0.01),(36,'2005-02-24 01:45:32',0,'ESP','Peseta','Spanien (bis 2001 ab 2002 Euro)',1,'',0.01),(37,'2005-02-24 01:45:32',0,'EUR','Euro','Groteil der EU',1.5524,'',0.01),(38,'2005-02-24 01:45:32',0,'FIK','Kronur','Frer',1,'',0.01),(39,'2005-02-24 01:45:32',0,'FIM','Markka','Finnland (bis 2001 ab 2002 Euro)',1,'',0.01),(40,'2005-02-24 01:45:32',0,'FJD','Dollar','Fidschi Inseln',1,'',0.01),(41,'2005-02-24 01:45:32',0,'FRF','Franc','Frankreich (bis 2001 ab 2002 Euro)',1,'',0.01),(42,'2005-02-24 01:45:32',0,'GBP','Pfund','Vereinigtes Knigreich',2.3327,'',0.01),(43,'2005-02-24 01:45:32',0,'GEL','Lari','Georgien',1,'',0.01),(44,'2005-02-24 01:45:32',0,'GHC','Cedi','Ghana',1,'',0.01),(45,'2005-02-24 01:45:32',0,'GIP','Pfund','Gibraltar',1,'',0.01),(46,'2005-02-24 01:45:32',0,'GMD','Dalasi','Gambia',1,'',0.01),(47,'2005-02-24 01:45:32',0,'GNF','Franc','Guinea',1,'',0.01),(48,'2005-02-24 01:45:32',0,'GRD','Drachmen','Griechenland (bis 2001 ab 2002 Euro)',1,'',0.01),(49,'2005-02-24 01:45:32',0,'GTQ','Quetzal','Guatemala',1,'',0.01),(50,'2005-02-24 01:45:32',0,'HKD','Dollar','Hongkong',1,'',0.01),(51,'2005-02-24 01:45:32',0,'HRK','Kuna','Kroatien',1,'',0.01),(52,'2005-02-24 01:45:32',0,'HTG','Gourde','Haiti',1,'',0.01),(53,'2005-02-24 01:45:32',0,'HUF','Forint','Ungarn',1,'',0.01),(54,'2005-02-24 01:45:32',0,'IDR','Rupiah','Indonesien',1,'',0.01),(55,'2005-02-24 01:45:32',0,'IEP','Pfund','Republik Irland (bis 2001 ab 2002 Euro)',1,'',0.01),(56,'2005-02-24 01:45:32',0,'ILS','Schekel','Israel',1,'',0.01),(57,'2005-02-24 01:45:32',0,'INR','Rupie','Indien',1,'',0.01),(58,'2005-02-24 01:45:32',0,'IQD','Dinar','Irak',1,'',0.01),(59,'2005-02-24 01:45:32',0,'IRR','Rial','Iran',1,'',0.01),(60,'2005-02-24 01:45:32',0,'ISK','Krone','Island',1,'',0.01),(61,'2005-02-24 01:45:32',0,'ITL','Lira','Italien (bis 2001 ab 2002 Euro)',1,'',0.01),(62,'2005-02-24 01:45:32',0,'JMD','Dollar','Jamaika',1,'',0.01),(63,'2005-02-24 01:45:32',0,'JOD','Dinar','Jordanien',1,'',0.01),(64,'2005-02-24 01:45:32',0,'JPY','Yen','Japan',0.0119,'',0.01),(65,'2005-02-24 01:45:32',0,'KES','Schilling','Kenia',1,'',0.01),(66,'2005-02-24 01:45:32',0,'KRW','Won','Sdkorea',1,'',0.01),(67,'2005-02-24 01:45:32',0,'KWD','Dinar','Kuwait',1,'',0.01),(68,'2005-02-24 01:45:32',0,'KYD','Dollar','Kayman-Inseln',1,'',0.01),(69,'2005-02-24 01:45:33',0,'LBP','Pfund','Libanon',1,'',0.01),(70,'2005-02-24 01:45:33',0,'LKR','Rupie','Sri Lanka',1,'',0.01),(71,'2005-02-24 01:45:33',0,'LUF','Franc','Luxemburg (bis 2001 ab 2002 Euro)',1,'',0.01),(72,'2005-02-24 01:45:33',0,'LVL','Lats','Lettland',1,'',0.01),(73,'2005-02-24 01:45:33',0,'LYD','Dinar','Libyen',1,'',0.01),(74,'2005-02-24 01:45:33',0,'MAD','Dirham','Marokko',1,'',0.01),(75,'2005-02-24 01:45:33',0,'MGF','Franc','Madagaskar',1,'',0.01),(76,'2005-02-24 01:45:33',0,'MTL','Lira','Malta',1,'',0.01),(77,'2005-02-24 01:45:33',0,'MUR','Rupie','Mauritius',1,'',0.01),(78,'2005-02-24 01:45:33',0,'MVR','Rufiyaa','Malediven',1,'',0.01),(79,'2005-02-24 01:45:33',0,'MXN','New Peso','Mexiko',1,'',0.01),(80,'2005-02-24 01:45:33',0,'MYR','Ringgit','Malaysia',1,'',0.01),(81,'2005-02-24 01:45:33',0,'NAD','Dollar','Namibia',1,'',0.01),(82,'2005-02-24 01:45:33',0,'NGN','Naira','Nigeria',1,'',0.01),(83,'2005-02-24 01:45:33',0,'NLG','Gulden','Niederlande (bis 2001 ab 2002 Euro)',1,'',0.01),(84,'2005-02-24 01:45:33',0,'NOK','Krone','Norwegen',0.1841,'',0.01),(85,'2005-02-24 01:45:33',0,'NPR','Rupie','Nepal',1,'',0.01),(86,'2005-02-24 01:45:33',0,'NZD','Dollar','Neuseeland',0.8286,'',0.01),(87,'2005-02-24 01:45:33',0,'OMR','Rial','Oman',1,'',0.01),(88,'2005-02-24 01:45:33',0,'PEN','Nuovo Sol','Peru',1,'',0.01),(89,'2005-02-24 01:45:33',0,'PGK','Kina','Papua-Neuguinea',1,'',0.01),(90,'2005-02-24 01:45:33',0,'PHP','Peso','Philippinen',1,'',0.01),(91,'2005-02-24 01:45:33',0,'PKR','Rupie','Pakistan',1,'',0.01),(92,'2005-02-24 01:45:33',0,'PLN','neue Zloty','Polen',1,'',0.01),(93,'2005-02-24 01:45:33',0,'PTE','Escudo','Portugal (bis 2001 ab 2002 Euro)',1,'',0.01),(94,'2005-02-24 01:45:33',0,'PYG','Guarani','Paraguay',1,'',0.01),(95,'2005-02-24 01:45:33',0,'QAR','Riyal','Katar',1,'',0.01),(96,'2005-02-24 01:45:33',0,'ROL','Lei','Rumnien',1,'',0.01),(97,'2005-02-24 01:45:33',0,'RUR','Rubel','Russland',1,'',0.01),(98,'2005-02-24 01:45:33',0,'SAR','Riyal','Saudi-Arabien',1,'',0.01),(99,'2005-02-24 01:45:33',0,'SCR','Rupie','Seychellen',1,'',0.01),(100,'2005-02-24 01:45:33',0,'SDP','Pfund','Sudan',1,'',0.01),(101,'2005-02-24 01:45:33',0,'SEK','Krone','Schweden',0.1678,'',0.01),(102,'2005-02-24 01:45:34',0,'SGD','Dollar','Singapur',1,'',0.01),(103,'2005-02-24 01:45:34',0,'SIT','Talar','Slowenien',1,'',0.01),(104,'2005-02-24 01:45:34',0,'SKK','Krone','Slowakei',1,'',0.01),(105,'2005-02-24 01:45:34',0,'SLL','Leone','Sierra Leone',1,'',0.01),(106,'2005-02-24 01:45:34',0,'SRG','Gulden','Surinam',1,'',0.01),(107,'2005-02-24 01:45:34',0,'SVC','Colon','El Salvador',1,'',0.01),(108,'2005-02-24 01:45:34',0,'SYP','Pfund','Syrien',1,'',0.01),(109,'2005-02-24 01:45:34',0,'THB','Bath','Thailand',1,'',0.01),(110,'2005-02-24 01:45:34',0,'TMD','Dinar','Tunesien',1,'',0.01),(111,'2005-02-24 01:45:34',0,'TRL','Lira','Trkei',1,'',0.01),(112,'2005-02-24 01:45:34',0,'TTD','Dollar','Trinidad und Tobago',1,'',0.01),(113,'2005-02-24 01:45:34',0,'TWD','Dollar','Taiwan',1,'',0.01),(114,'2005-02-24 01:45:34',0,'TZS','Schilling','Tansania',1,'',0.01),(115,'2005-02-24 01:45:34',0,'UAH','Griwna','Ukraine',1,'',0.01),(116,'2005-02-24 01:45:34',0,'USD','Dollar','USA',1.2683,'',0.01),(117,'2005-02-24 01:45:34',0,'UYP','Peso','Uruguay',1,'',0.01),(118,'2005-02-24 01:45:34',0,'VEB','Bolivar','Venezuela',1,'',0.01),(119,'2005-02-24 01:45:34',0,'VND','Dong','Vietnam',1,'',0.01),(120,'2005-02-24 01:45:34',0,'YUN','Dinar','Serbien',1,'',0.01),(121,'2005-02-24 01:45:34',0,'XAF','Franc','quatorial-Afrika',1,'',0.01),(122,'2005-02-24 01:45:34',0,'XCD','Dollar','Westindien',1,'',0.01),(123,'2005-02-24 01:45:34',0,'XOF','Franc','Westafrika',1,'',0.01),(124,'2005-02-24 01:45:34',0,'XPF','CPF-Franc','Neukaledonien',1,'',0.01),(125,'2005-02-24 01:45:34',0,'ZAR','Rand','Sdafrika',0.1915,'',0.01),(126,'2005-02-24 01:45:34',0,'ZRN','Zaire','Zaire',1,'',0.01),(127,'2005-02-24 01:45:34',0,'ZWD','Dollar','Simbabwe',1,'',0.01),(128,'2005-02-24 01:45:34',0,'CNY','Renminbi Yuan','China (Volksrepublik)',1,'',0.01);
/*!40000 ALTER TABLE `pos_foreign_currency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_function`
--

DROP TABLE IF EXISTS `pos_function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_function` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `class` varchar(255) NOT NULL DEFAULT '',
  `action_type` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(100) NOT NULL DEFAULT '',
  `shortname` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_function`
--

LOCK TABLES `pos_function` WRITE;
/*!40000 ALTER TABLE `pos_function` DISABLE KEYS */;
INSERT INTO `pos_function` VALUES (1,'2005-02-24 01:45:34','ch.eugster.pos.events.LockAction',0,'Sperrt die Kasse des aktuellen Benutzers','Sperren'),(2,'2005-02-24 01:45:34','ch.eugster.pos.events.LogoffAction',0,'Abmelden des aktuellen Benutzers','Abmelden'),(3,'2005-02-24 01:45:34','ch.eugster.pos.events.ExitAction',0,'Beendet das Kassenprogramm','Beenden'),(4,'2005-02-24 01:45:34','ch.eugster.pos.events.ToggleAction',0,'Wechselt zwischen Positionen- und Zahlungseingaben','Zahlung'),(5,'2005-02-24 01:45:35','ch.eugster.pos.events.ReturnAction',250,'Setzt den Status einer Position auf Rcknahme (Menge wird negativ gesetzt)','Rcknahme'),(6,'2005-02-24 01:45:35','ch.eugster.pos.events.ParkAction',0,'Parkiert den aktuellen Beleg','Parkieren'),(7,'2005-08-24 10:42:10','ch.eugster.pos.events.SalesPerUserAction',901,'Zeigt den Umsatz des aktuellen Benutzers an','Umsatz Benutzer'),(8,'2005-02-24 01:45:35','ch.eugster.pos.events.ShowReceiptListAction',620,'Zeigt die Liste der nicht abgeschlossenen Belege an','Belegliste'),(9,'2005-02-24 01:45:35','ch.eugster.pos.events.CashDrawerAction',800,'ffnet die Schublade 1, wenn vorhanden','Schubl. 1'),(10,'2005-02-24 01:45:35','ch.eugster.pos.events.CashDrawerAction',801,'ffnet die Schublade 2, wenn vorhanden','Schubl. 2'),(11,'2005-02-24 01:45:35','ch.eugster.pos.events.SalesAction',901,'Zeigt den aktuellen Umsatz des Benutzers an','Umsatz'),(12,'2005-02-24 01:45:35','ch.eugster.pos.events.ShowCoinCounterAction',0,'Wechselt in die Mnzzhlungsansicht','Abschluss'),(13,'2005-02-24 01:45:35','ch.eugster.pos.events.StoreReceiptAction',100,'Abschliessen-Button','Abschliessen'),(14,'2005-02-24 01:45:35','ch.eugster.pos.events.ExpressStoreReceiptAction',102,'Expressabschluss-Button','Express'),(15,'2005-02-24 01:45:35','ch.eugster.pos.events.PreDefinedDiscountAction',203,'Vordefinierter Rabattsatz','discount'),(16,'2005-02-24 01:45:35','ch.eugster.pos.events.StoreReceiptVoucherAction',102,'Belegabschluss mit Gutscheinrckgabe','Gutscheinabschluss'),(17,'2005-08-24 10:42:10','ch.eugster.pos.events.SalesPerSalespointAction',901,'Zeigt den Umsatz der aktuellen Kasse an','Umsatz Kasse'),(18,'2007-08-06 11:24:19','ch.eugster.pos.events.ChooseCustomerAction',0,'Öffnet das Kundenauswahlfenster','Kunden'),(19,'2007-11-22 10:16:44','ch.eugster.pos.events.PrintLastReceiptAction',0,'Letzten Beleg drucken','Drucken'),(20,'2010-02-09 14:25:02','ch.eugster.pos.events.UpdateCustomerAccountAction',206,'Position auf Kundenkarte (nicht) verbuchen','Titel KK +-');
/*!40000 ALTER TABLE `pos_function` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_key`
--

DROP TABLE IF EXISTS `pos_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_key` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `tab_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `editable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `row` int(10) unsigned NOT NULL DEFAULT '0',
  `col` int(10) unsigned NOT NULL DEFAULT '0',
  `bg_red` int(3) unsigned NOT NULL DEFAULT '255',
  `bg_green` int(3) unsigned NOT NULL DEFAULT '255',
  `bg_blue` int(3) unsigned NOT NULL DEFAULT '255',
  `bg_red2` int(10) unsigned NOT NULL DEFAULT '255',
  `bg_green2` int(10) unsigned NOT NULL DEFAULT '255',
  `bg_blue2` int(10) unsigned NOT NULL DEFAULT '255',
  `fg_red` int(11) NOT NULL DEFAULT '0',
  `fg_green` int(11) NOT NULL DEFAULT '0',
  `fg_blue` int(11) NOT NULL DEFAULT '0',
  `font_size` double NOT NULL DEFAULT '0',
  `font_style` int(11) NOT NULL DEFAULT '0',
  `align` int(11) NOT NULL DEFAULT '0',
  `valign` int(11) NOT NULL DEFAULT '0',
  `name` varchar(100) NOT NULL DEFAULT '',
  `command` varchar(100) NOT NULL DEFAULT '',
  `image_path` varchar(255) DEFAULT '',
  `rel_horizontal_text_pos` int(10) unsigned DEFAULT '11',
  `rel_vertical_text_pos` int(10) unsigned DEFAULT '0',
  `class_name` varchar(255) NOT NULL DEFAULT '',
  `value` double NOT NULL DEFAULT '0',
  `parent_class_name` varchar(255) DEFAULT '',
  `parent_id` int(10) unsigned NOT NULL DEFAULT '0',
  `action_type` int(10) unsigned NOT NULL DEFAULT '0',
  `set_default_tab` tinyint(1) NOT NULL DEFAULT '0',
  `payment_type_id` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_button` (`tab_id`,`row`,`col`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_key`
--

LOCK TABLES `pos_key` WRITE;
/*!40000 ALTER TABLE `pos_key` DISABLE KEYS */;
INSERT INTO `pos_key` VALUES (1,'1970-01-01 01:00:00',5,0,0,0,255,128,0,255,255,255,0,0,0,12,1,0,0,'<html>Coupon drucken<br>Abschliessen','','',11,0,'ch.eugster.pos.events.ExpressStoreReceiptAction',0,'ch.eugster.pos.db.Function',14,102,0,1),(2,'1970-01-01 01:00:00',6,0,0,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Bar','','',11,0,'ch.eugster.pos.events.ExpressPaymentAction',0,'ch.eugster.pos.db.PaymentType',1,301,0,0),(3,'1970-01-01 01:00:00',6,0,0,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'','','C:\\Programme\\ColibriTS\\icons\\money\\10.gif',11,0,'ch.eugster.pos.events.ExpressPaymentAction',10,'ch.eugster.pos.db.PaymentType',1,301,0,0),(4,'1970-01-01 01:00:00',6,0,0,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'','','C:\\Programme\\ColibriTS\\icons\\money\\20.gif',11,0,'ch.eugster.pos.events.ExpressPaymentAction',20,'ch.eugster.pos.db.PaymentType',1,301,0,0),(5,'1970-01-01 01:00:00',6,0,1,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'','','C:\\Programme\\ColibriTS\\icons\\money\\50.gif',11,0,'ch.eugster.pos.events.ExpressPaymentAction',50,'ch.eugster.pos.db.PaymentType',1,301,0,0),(6,'1970-01-01 01:00:00',6,0,1,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'','','C:\\Programme\\ColibriTS\\icons\\money\\100.gif',11,0,'ch.eugster.pos.events.ExpressPaymentAction',100,'ch.eugster.pos.db.PaymentType',1,301,0,0),(7,'1970-01-01 01:00:00',6,0,1,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'','','C:\\Programme\\ColibriTS\\icons\\money\\200.gif',11,0,'ch.eugster.pos.events.ExpressPaymentAction',200,'ch.eugster.pos.db.PaymentType',1,301,0,0),(21,'1970-01-01 01:00:00',4,0,0,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>Schublade<br>öffnen','','',11,0,'ch.eugster.pos.events.CashDrawerAction',0,'ch.eugster.pos.db.Function',9,800,0,0),(22,'1970-01-01 01:00:00',3,0,0,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'Sperren','','',11,0,'ch.eugster.pos.events.LockAction',0,'ch.eugster.pos.db.Function',1,0,0,0),(23,'1970-01-01 01:00:00',3,0,1,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'Abmelden','','',11,0,'ch.eugster.pos.events.LogoffAction',0,'ch.eugster.pos.db.Function',2,0,0,0),(25,'1970-01-01 01:00:00',3,0,0,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'Umsatz','','',11,0,'ch.eugster.pos.events.SalesPerSalespointAction',0,'ch.eugster.pos.db.Function',17,901,0,0),(26,'1970-01-01 01:00:00',4,0,0,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'MwSt. frei','','',11,0,'ch.eugster.pos.events.TaxAction',0,'ch.eugster.pos.db.TaxRate',1,204,0,0),(27,'1970-01-01 01:00:00',4,0,1,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'MwSt. reduziert','','',11,0,'ch.eugster.pos.events.TaxAction',0,'ch.eugster.pos.db.TaxRate',2,204,0,0),(28,'1970-01-01 01:00:00',4,0,2,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'MwSt. normal','','',11,0,'ch.eugster.pos.events.TaxAction',0,'ch.eugster.pos.db.TaxRate',3,204,0,0),(30,'1970-01-01 01:00:00',4,0,2,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>Programm<br>Beenden','','',11,0,'ch.eugster.pos.events.ExitAction',0,'ch.eugster.pos.db.Function',3,0,0,0),(31,'1970-01-01 01:00:00',4,0,0,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Lager','','',11,0,'ch.eugster.pos.events.OptionAction',0,'ch.eugster.pos.db.Option',1,205,0,0),(32,'1970-01-01 01:00:00',3,0,0,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Besorgung','','',11,0,'ch.eugster.pos.events.OptionAction',0,'ch.eugster.pos.db.Option',2,205,0,0),(36,'1970-01-01 01:00:00',1,0,0,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>22<br>Belletristk HC','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',33,200,0,0),(41,'1970-01-01 01:00:00',1,0,0,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>02<br>Kinder/Jugend','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',3,200,0,0),(42,'1970-01-01 01:00:00',1,0,1,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>03<br>Humor/Geschenk','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',15,200,0,0),(43,'1970-01-01 01:00:00',1,0,0,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>01<br>Belletristik TB','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',2,200,0,0),(44,'1970-01-01 01:00:00',1,0,1,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>04<br>Comics','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',16,200,0,0),(45,'1970-01-01 01:00:00',1,0,1,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>05<br>Kunst/Musik','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',17,200,0,0),(46,'1970-01-01 01:00:00',1,0,2,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>06<br>Reisen','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',18,200,0,0),(47,'1970-01-01 01:00:00',1,0,2,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>07<br>Landkarten','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',19,200,0,0),(48,'1970-01-01 01:00:00',1,0,2,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>08<br>Hobby/Freizeit','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',20,200,0,0),(49,'1970-01-01 01:00:00',2,0,0,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>09<br>Sport','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',21,200,0,0),(50,'1970-01-01 01:00:00',2,0,0,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>10<br>Sachbuch','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',12,200,0,0),(51,'1970-01-01 01:00:00',2,0,0,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>11<br>Naturwissen-<br>schaft','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',22,200,0,0),(52,'1970-01-01 01:00:00',2,0,1,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>12<br>Familie/Erziehung','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',23,200,0,0),(53,'1970-01-01 01:00:00',2,0,1,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>13<br>Lebenshilfe','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',24,200,0,0),(54,'1970-01-01 01:00:00',2,0,1,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>14<br>Gesundheit','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',25,200,0,0),(55,'1970-01-01 01:00:00',2,0,2,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>15<br>Tiere/Pflanzen','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',26,200,0,0),(56,'1970-01-01 01:00:00',2,0,2,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>16<br>Kochen','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',27,200,0,0),(57,'1970-01-01 01:00:00',2,0,2,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>17<br>Wissen/Lernen','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',28,200,0,0),(58,'1970-01-01 01:00:00',21,0,0,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>18<br>Multimedia-Bücher','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',29,200,0,0),(59,'1970-01-01 01:00:00',21,0,0,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>19<br>Fremdsprachig','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',30,200,0,0),(60,'1970-01-01 01:00:00',21,0,0,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>20<br>A + A','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',31,200,0,0),(61,'1970-01-01 01:00:00',21,0,1,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>21<br>CD-ROM','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',32,200,0,0),(62,'1970-01-01 01:00:00',21,0,1,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>23<br>Aktivitäten','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',34,200,0,0),(63,'1970-01-01 01:00:00',21,0,1,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>24<br>AV-Medien','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',35,200,0,0),(64,'1970-01-01 01:00:00',21,0,2,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>25<br>Glückwunschkarten','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',36,200,0,0),(65,'1970-01-01 01:00:00',21,0,2,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>26<br>Kalender','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',37,200,0,0),(66,'1970-01-01 01:00:00',21,0,2,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>29<br>Pap./Geschenkboutique','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',54,200,0,0),(67,'1970-01-01 01:00:00',22,0,0,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Verrechnete Porti','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',51,200,0,0),(69,'1970-01-01 01:00:00',22,0,0,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'Kassastand Ist','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',57,200,0,0),(71,'1970-01-01 01:00:00',22,0,2,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'Verk. Schweiz. Bücherbon','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',41,200,0,0),(72,'1970-01-01 01:00:00',22,0,2,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'Bezahlte Rechnung','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',38,200,0,0),(73,'1970-01-01 01:00:00',23,0,0,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Ausgaben','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',42,200,0,0),(74,'1970-01-01 01:00:00',23,0,0,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'Ausgaben  Dekomaterial','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',45,200,0,0),(75,'1970-01-01 01:00:00',23,0,0,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'Ausgaben  Porti','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',44,200,0,0),(76,'1970-01-01 01:00:00',23,0,1,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Ausgaben  Reinigungsmaterial','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',43,200,0,0),(77,'1970-01-01 01:00:00',23,0,1,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'Ausgaben Büromaterial','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',47,200,0,0),(78,'1970-01-01 01:00:00',23,0,1,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'Ausgaben Reisespesen','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',46,200,0,0),(79,'1970-01-01 01:00:00',24,0,0,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Kreditkarte','','',11,0,'ch.eugster.pos.events.ExpressPaymentAction',0,'ch.eugster.pos.db.PaymentType',5,301,0,0),(80,'1970-01-01 01:00:00',24,0,0,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'Rechnung','','',11,0,'ch.eugster.pos.events.ExpressPaymentAction',0,'ch.eugster.pos.db.PaymentType',8,301,0,0),(81,'1970-01-01 01:00:00',24,0,1,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Buch Shopping Gutschein','','',11,0,'ch.eugster.pos.events.ExpressPaymentAction',0,'ch.eugster.pos.db.PaymentType',6,301,0,0),(82,'1970-01-01 01:00:00',24,0,1,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'Schweizer Bücherbon','','',11,0,'ch.eugster.pos.events.ExpressPaymentAction',0,'ch.eugster.pos.db.PaymentType',7,301,0,0),(101,'1970-01-01 01:00:00',4,0,1,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'<html>Tages-<br>abschluss','','',11,0,'ch.eugster.pos.events.ShowCoinCounterAction',0,'ch.eugster.pos.db.Function',12,0,0,0),(121,'1970-01-01 01:00:00',3,0,1,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'Parkieren','','',11,0,'ch.eugster.pos.events.ParkAction',0,'ch.eugster.pos.db.Function',6,0,0,0),(122,'1970-01-01 01:00:00',3,0,2,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Belegliste','','',11,0,'ch.eugster.pos.events.ShowReceiptListAction',0,'ch.eugster.pos.db.Function',8,620,0,0),(123,'1970-01-01 01:00:00',4,0,2,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Rücknahme','','',11,0,'ch.eugster.pos.events.ReturnAction',0,'ch.eugster.pos.db.Function',5,250,0,0),(141,'1970-01-01 01:00:00',23,0,2,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Einzahlung Bank','','',11,0,'ch.eugster.pos.events.ProductGroupAction',0,'ch.eugster.pos.db.ProductGroup',58,200,0,0),(181,'1970-01-01 01:00:00',81,0,0,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Euro','','',11,0,'ch.eugster.pos.events.ExpressPaymentAction',0,'ch.eugster.pos.db.PaymentType',19,301,0,0),(182,'1970-01-01 01:00:00',81,0,0,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'','','C:\\Programme\\ColibriTS\\icons\\money\\5 Euro.gif',11,0,'ch.eugster.pos.events.ExpressPaymentAction',5,'ch.eugster.pos.db.PaymentType',19,301,0,0),(183,'1970-01-01 01:00:00',81,0,0,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'','','C:\\Programme\\ColibriTS\\icons\\money\\10 Euro.gif',11,0,'ch.eugster.pos.events.ExpressPaymentAction',10,'ch.eugster.pos.db.PaymentType',19,301,0,0),(184,'1970-01-01 01:00:00',81,0,1,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'','','C:\\Programme\\ColibriTS\\icons\\money\\20 Euro.gif',11,0,'ch.eugster.pos.events.ExpressPaymentAction',20,'ch.eugster.pos.db.PaymentType',19,301,0,0),(185,'1970-01-01 01:00:00',81,0,1,1,255,255,255,255,255,255,0,0,0,12,1,0,0,'','','C:\\Programme\\ColibriTS\\icons\\money\\50 Euro.gif',11,0,'ch.eugster.pos.events.ExpressPaymentAction',50,'ch.eugster.pos.db.PaymentType',19,301,0,0),(186,'1970-01-01 01:00:00',81,0,1,2,255,255,255,255,255,255,0,0,0,12,1,0,0,'','','C:\\Programme\\ColibriTS\\icons\\money\\100 Euro.gif',11,0,'ch.eugster.pos.events.ExpressPaymentAction',100,'ch.eugster.pos.db.PaymentType',19,301,0,0),(201,'1970-01-01 01:00:00',5,0,1,0,255,255,255,255,255,255,0,0,0,12,1,0,0,'Express','','',11,0,'ch.eugster.pos.events.ExpressStoreReceiptAction',0,'ch.eugster.pos.db.Function',14,102,0,19);
/*!40000 ALTER TABLE `pos_key` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_key_group`
--

DROP TABLE IF EXISTS `pos_key_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_key_group` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `name` varchar(255) NOT NULL DEFAULT '',
  `class` varchar(255) NOT NULL DEFAULT '',
  `action_class` varchar(100) NOT NULL DEFAULT '',
  `action_type` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `class` (`class`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_key_group`
--

LOCK TABLES `pos_key_group` WRITE;
/*!40000 ALTER TABLE `pos_key_group` DISABLE KEYS */;
INSERT INTO `pos_key_group` VALUES (1,'2005-02-24 01:45:35','Warengruppen','ch.eugster.pos.db.ProductGroup','ch.eugster.pos.events.ProductGroupAction',200),(2,'2005-02-24 01:45:35','Zahlungsarten','ch.eugster.pos.db.PaymentTypeGroup','ch.eugster.pos.events.PaymentTypeAction',301),(3,'2005-02-24 01:45:35','Mehrwertsteuern','ch.eugster.pos.db.TaxRate','ch.eugster.pos.events.TaxAction',204),(4,'2005-02-24 01:45:35','Optionen','ch.eugster.pos.db.Option','ch.eugster.pos.events.OptionAction',205),(5,'2005-02-24 01:45:35','Funktionen','ch.eugster.pos.db.Function','',0);
/*!40000 ALTER TABLE `pos_key_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_option`
--

DROP TABLE IF EXISTS `pos_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_option` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `code` char(3) NOT NULL DEFAULT '',
  `name` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_option`
--

LOCK TABLES `pos_option` WRITE;
/*!40000 ALTER TABLE `pos_option` DISABLE KEYS */;
INSERT INTO `pos_option` VALUES (1,'2005-02-24 01:45:35',0,'L','Laden'),(2,'2005-02-24 01:45:35',0,'B','Bestellt');
/*!40000 ALTER TABLE `pos_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_payment`
--

DROP TABLE IF EXISTS `pos_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_payment` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `receipt_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `payment_type_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `foreign_currency_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `quotation` double NOT NULL DEFAULT '1',
  `amount` double NOT NULL DEFAULT '0',
  `amount_fc` double NOT NULL DEFAULT '0',
  `round_factor` double NOT NULL DEFAULT '0',
  `round_factor_fc` double NOT NULL DEFAULT '0',
  `back` tinyint(1) DEFAULT '0',
  `settlement` bigint(20) DEFAULT NULL,
  `salespoint_id` bigint(20) DEFAULT NULL,
  `is_input_or_withdraw` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `payment_type_id` (`payment_type_id`),
  KEY `receipt_id` (`receipt_id`),
  KEY `idx_settlement` (`settlement`),
  KEY `idx_salespoint_id` (`salespoint_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pos_payment_type`
--

DROP TABLE IF EXISTS `pos_payment_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_payment_type` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `payment_type_group_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `foreign_currency_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `removeable` tinyint(1) NOT NULL DEFAULT '1',
  `name` varchar(100) DEFAULT '[NULL]',
  `code` varchar(100) DEFAULT '[NULL]',
  `account` varchar(100) DEFAULT '',
  `is_voucher` tinyint(1) NOT NULL DEFAULT '0',
  `export_id` varchar(10) NOT NULL DEFAULT '',
  `open_cashdrawer` tinyint(4) DEFAULT '1',
  `back` tinyint(1) DEFAULT '0',
  `cash` tinyint(1) DEFAULT '0',
  `sort` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `code` (`code`),
  KEY `payment_type_group_id` (`payment_type_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_payment_type`
--

LOCK TABLES `pos_payment_type` WRITE;
/*!40000 ALTER TABLE `pos_payment_type` DISABLE KEYS */;
INSERT INTO `pos_payment_type` VALUES (1,'2005-02-24 17:59:53',0,1,22,0,'Barzahlung','Barzahlung','1001',0,'1',1,1,1,0),(5,'2005-02-24 17:59:54',0,1,22,1,'Kreditkarte','Kreditkarte','1111',0,'5',1,0,0,0),(6,'2005-02-24 17:59:54',0,1,22,1,'Buch Shopping Gutschein','Buch Shopping Gutschein','2111',0,'2',1,0,0,0),(7,'2005-02-24 17:59:54',0,1,22,1,'Schweizer Bücherbon','Schweizer Bücherbon','1251',0,'3',1,0,0,0),(8,'2005-02-24 17:59:54',0,1,22,1,'Rechnung','Rechnung','1201',0,'4',1,0,0,0),(9,'2005-02-24 17:59:54',0,1,22,1,'BB2000 Gutschein','BB2000 Gutschein','2100',0,'',1,0,0,0),(11,'2005-02-24 17:59:54',0,1,22,1,'Vulkan Gutschein','Vulkan Gutschein','2020',0,'',1,0,0,0),(18,'2005-02-24 17:59:54',0,1,22,1,'Vulkan Gutschein','Vulkan Gutschein','2020',0,'',1,0,0,0),(19,'1970-01-01 01:00:00',0,1,37,1,'Euro','Euro','',0,'9',1,1,0,0);
/*!40000 ALTER TABLE `pos_payment_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_payment_type_group`
--

DROP TABLE IF EXISTS `pos_payment_type_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_payment_type_group` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `visible` tinyint(4) NOT NULL DEFAULT '0',
  `code` varchar(100) DEFAULT '',
  `name` varchar(100) DEFAULT '',
  `default_account` varchar(100) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_payment_type_group`
--

LOCK TABLES `pos_payment_type_group` WRITE;
/*!40000 ALTER TABLE `pos_payment_type_group` DISABLE KEYS */;
INSERT INTO `pos_payment_type_group` VALUES (1,'2005-02-24 01:45:35',0,1,'','Bar',''),(2,'2005-02-24 01:45:35',0,0,'','',''),(3,'2005-02-24 01:45:35',0,1,'','Whrungen','');
/*!40000 ALTER TABLE `pos_payment_type_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_position`
--

DROP TABLE IF EXISTS `pos_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_position` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `receipt_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `product_id` varchar(50) DEFAULT '',
  `product_group_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `current_tax_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `price` decimal(12,2) NOT NULL DEFAULT '0.00',
  `discount` double unsigned NOT NULL DEFAULT '0',
  `galileo_book` tinyint(4) NOT NULL DEFAULT '0',
  `galileo_booked` tinyint(1) NOT NULL DEFAULT '0',
  `opt_code` char(1) NOT NULL DEFAULT '',
  `author` varchar(100) DEFAULT '',
  `title` varchar(100) DEFAULT '',
  `publisher` varchar(100) DEFAULT '',
  `isbn` varchar(100) DEFAULT '',
  `bznr` varchar(100) DEFAULT '',
  `product_number` varchar(100) DEFAULT '',
  `ordered` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `order_id` varchar(100) DEFAULT '',
  `stock` tinyint(1) NOT NULL DEFAULT '0',
  `update_customer_account` tinyint(4) DEFAULT '0',
  `payed_invoice` tinyint(4) DEFAULT '0',
  `invoice` int(11) DEFAULT '0',
  `invoice_date` datetime DEFAULT NULL,
  `tax` double DEFAULT '0',
  `type` int(11) DEFAULT '0',
  `amount_fc` double DEFAULT '0',
  `amount` double DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `current_tax_id` (`current_tax_id`),
  KEY `product_group_id` (`product_group_id`),
  KEY `receipt` (`receipt_id`),
  KEY `galileo_booked` (`galileo_booked`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pos_product`
--

DROP TABLE IF EXISTS `pos_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_product` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `product_group_id` bigint(20) unsigned DEFAULT '0',
  `code` varchar(50) DEFAULT NULL,
  `short_cut` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `tax_id` int(11) DEFAULT NULL,
  `hide` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_product`
--

LOCK TABLES `pos_product` WRITE;
/*!40000 ALTER TABLE `pos_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `pos_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_product_group`
--

DROP TABLE IF EXISTS `pos_product_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_product_group` (
  `id` int(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `galileo_id` char(3) NOT NULL DEFAULT '',
  `default_tax_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `shortname` varchar(50) DEFAULT NULL,
  `name` varchar(100) DEFAULT '',
  `quantity_proposal` int(11) NOT NULL DEFAULT '0',
  `price_proposal` decimal(12,2) NOT NULL DEFAULT '0.00',
  `opt_code_proposal` char(1) DEFAULT '',
  `account` varchar(100) DEFAULT '',
  `modified` tinyint(1) NOT NULL DEFAULT '0',
  `type` int(4) NOT NULL DEFAULT '0',
  `export_id` varchar(10) NOT NULL DEFAULT '',
  `withdraw` tinyint(1) DEFAULT '0',
  `paid_invoice` tinyint(1) DEFAULT '0',
  `is_default` tinyint(1) DEFAULT '0',
  `foreign_currency_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `default_tax_id` (`default_tax_id`),
  KEY `galileo_id` (`galileo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_product_group`
--

LOCK TABLES `pos_product_group` WRITE;
/*!40000 ALTER TABLE `pos_product_group` DISABLE KEYS */;
INSERT INTO `pos_product_group` VALUES (1,'2005-02-24 01:45:35',0,'999',4,'Default','Default',1,'0.00','','',0,0,'999',0,0,1,22),(2,'2005-02-24 17:59:52',0,'1',4,'Belletristik TB','Belletristik TB',1,'0.00','','6001',0,0,'1',0,0,0,22),(3,'2005-02-24 17:59:52',0,'2',4,'Kinder/Jugend','Kinder/Jugend',1,'0.00','','6001',0,0,'2',0,0,0,22),(12,'2005-02-24 17:59:52',0,'10',4,'Sachbuch','Sachbuch',1,'0.00','','6001',0,0,'10',0,0,0,22),(15,'2005-02-24 17:59:52',0,'3',4,'Humor/Geschenk','Humor/Geschenk',1,'0.00','','6001',0,0,'3',0,0,0,22),(16,'2005-02-24 17:59:52',0,'4',4,'Comics','Comics',1,'0.00','','6001',0,0,'4',0,0,0,22),(17,'2005-02-24 17:59:52',0,'5',4,'Kunst/Musik','Kunst/Musik',1,'0.00','','6001',0,0,'5',0,0,0,22),(18,'2005-02-24 17:59:52',0,'6',4,'Reisen','Reisen',1,'0.00','','6001',0,0,'6',0,0,0,22),(19,'2005-02-24 17:59:52',0,'7',7,'Landkarten','Landkarten',1,'0.00','','6001',0,0,'7',0,0,0,22),(20,'2005-02-24 17:59:52',0,'8',4,'Hobby/Freizeit','Hobby/Freizeit',1,'0.00','','6001',0,0,'8',0,0,0,22),(21,'2005-02-24 17:59:52',0,'9',4,'Sport','Sport',1,'0.00','','6001',0,0,'9',0,0,0,22),(22,'2005-02-24 17:59:52',0,'11',4,'Naturwissenschaft','Naturwissenschaft',1,'0.00','','6001',0,0,'11',0,0,0,22),(23,'2005-02-24 17:59:52',0,'12',4,'Familie/Erziehung','Familie/Erziehung',1,'0.00','','6001',0,0,'12',0,0,0,22),(24,'2005-02-24 17:59:52',0,'13',4,'Lebenshilfe','Lebenshilfe',1,'0.00','','6001',0,0,'13',0,0,0,22),(25,'2005-02-24 17:59:52',0,'14',4,'Gesundheit','Gesundheit',1,'0.00','','6001',0,0,'14',0,0,0,22),(26,'2005-02-24 17:59:52',0,'15',4,'Tiere/Pflanzen','Tiere/Pflanzen',1,'0.00','','6001',0,0,'15',0,0,0,22),(27,'2005-02-24 17:59:52',0,'16',4,'Kochen','Kochen',1,'0.00','','6001',0,0,'16',0,0,0,22),(28,'2005-02-24 17:59:52',0,'17',4,'Wissen/Lernen','Wissen/Lernen',1,'0.00','','6001',0,0,'17',0,0,0,22),(29,'2005-02-24 17:59:52',0,'18',4,'Multimedia-Bücher','Multimedia-Bücher',1,'0.00','','6001',0,0,'18',0,0,0,22),(30,'2005-02-24 17:59:52',0,'19',4,'Fremdsprachig','Fremdsprachig',1,'0.00','','6001',0,0,'19',0,0,0,22),(31,'2005-02-24 17:59:52',0,'20',4,'A + A','A + A',1,'0.00','','6001',0,0,'20',0,0,0,22),(32,'2005-02-24 17:59:52',0,'21',7,'CD-ROM','CD-ROM',1,'0.00','','6001',0,0,'21',0,0,0,22),(33,'2005-02-24 17:59:52',0,'22',4,'Belletristk HC','Belletristk HC',1,'0.00','','6001',0,0,'22',0,0,0,22),(34,'2005-02-24 17:59:52',0,'23',4,'Aktivitäten','Aktivitäten',1,'0.00','','6001',0,0,'23',0,0,0,22),(35,'2005-02-24 17:59:52',0,'24',7,'AV-Medien','AV-Medien',1,'0.00','','6001',0,0,'24',0,0,0,22),(36,'2005-02-24 17:59:52',0,'25',7,'Glückwunschkarten','Glückwunschkarten',1,'0.00','','6001',0,0,'25',0,0,0,22),(37,'2005-02-24 17:59:53',0,'26',7,'Kalender','Kalender',1,'0.00','','6001',0,0,'26',0,0,0,22),(38,'2005-02-24 17:59:53',0,'400',1,'Bezahlte Rechnung','Bezahlte Rechnung',1,'0.00','','1201',0,1,'400',0,0,0,22),(39,'2005-02-24 17:59:53',1,'500',1,'Verrechnete Porti','Verrechnete Porti',1,'0.00','','30500-104',1,1,'500',0,0,0,22),(40,'2005-02-24 17:59:53',1,'600',1,'verkaufte Vulkan Bücherbons','verkaufte Vulkan Bücherbons',1,'0.00','','11400',1,1,'600',0,0,0,22),(41,'2005-02-24 17:59:53',0,'700',1,'Verkaufte SBB-Gutscheine','Verkaufte SBB-Gutscheine',1,'0.00','','11200',1,1,'700',0,0,0,22),(42,'2005-02-24 17:59:53',0,'900',8,'Ausgaben','Ausgaben',1,'0.00','','4761',0,2,'900',0,0,0,22),(43,'2005-02-24 17:59:53',0,'910',8,'Ausgaben  Reinigungsmaterial','Ausgaben  Reinigungsmaterial',1,'0.00','','4161',0,2,'910',0,0,0,22),(44,'2005-02-24 17:59:53',0,'920',8,'Ausgaben  Porti','Ausgaben  Porti',1,'0.00','','4161',0,2,'920',0,0,0,22),(45,'2005-02-24 17:59:53',0,'930',8,'Ausgaben  Dekomaterial','Ausgaben  Dekomaterial',1,'0.00','','4501',0,2,'930',0,0,0,22),(46,'2005-02-24 17:59:53',0,'940',8,'Ausgaben Reisespesen','Ausgaben Reisespesen',1,'0.00','','4761',0,2,'940',0,0,0,22),(47,'2005-02-24 17:59:53',0,'950',8,'Ausgaben Büromaterial','Ausgaben Büromaterial',1,'0.00','','4701',0,2,'950',0,0,0,22),(51,'2005-02-24 17:59:53',0,'800',1,'Verrechnete Porti','Verrechnete Porti',1,'0.00','','30500',0,1,'800',0,0,0,22),(52,'2005-02-24 17:59:53',0,'27',7,'Pap./Büromaterial','Pap./Büromaterial',1,'0.00','','6001',0,0,'27',0,0,0,22),(53,'2005-02-24 17:59:53',0,'28',7,'Pap./Schreibgeräte','Pap./Schreibgeräte',1,'0.00','','6001',0,0,'28',0,0,0,22),(54,'2005-02-24 17:59:53',0,'29',7,'Pap./Geschenkboutique','Pap./Geschenkboutique',1,'0.00','','6001',0,0,'29',0,0,0,22),(55,'2005-02-24 17:59:53',0,'30',7,'Pap./Agenden','Pap./Agenden',1,'0.00','','6001',0,0,'30',0,0,0,22),(57,'2005-02-24 17:59:53',0,'200',1,'Kassastand Ist','Kassastand Ist',1,'0.00','','11304',0,1,'200',0,0,0,22),(58,'2005-02-24 17:59:53',0,'300',2,'Einzahlung Bank','Einzahlung Bank',1,'0.00','','1100',0,2,'300',0,0,0,22),(59,'2005-02-24 17:59:53',0,'550',7,'Einnahmen Fotokopierer','Einnahmen Fotokopierer',1,'0.00','','6200',0,1,'550',0,0,0,22),(60,'2005-02-24 17:59:53',1,'650',1,'Verk. BB2000-Gutschein','Verk. BB2000-Gutschein',1,'0.00','','2100',0,1,'650',0,0,0,22),(61,'2005-02-24 17:59:53',0,'01',4,'Belletristik TB','Belletristik TB',1,'0.00','','6001',0,0,'',0,0,0,22),(62,'2005-02-24 17:59:53',0,'02',4,'Kinder/Jugend','Kinder/Jugend',1,'0.00','','6001',0,0,'',0,0,0,22),(63,'2005-02-24 17:59:53',0,'03',4,'Humor/Geschenk','Humor/Geschenk',1,'0.00','','6001',0,0,'',0,0,0,22),(64,'2005-02-24 17:59:53',0,'04',4,'Comics','Comics',1,'0.00','','6001',0,0,'',0,0,0,22),(65,'2005-02-24 17:59:53',0,'05',4,'Kunst/Musik','Kunst/Musik',1,'0.00','','6001',0,0,'',0,0,0,22),(66,'2005-02-24 17:59:53',0,'06',4,'Reisen','Reisen',1,'0.00','','6001',0,0,'',0,0,0,22),(67,'2005-02-24 17:59:53',0,'07',7,'Landkarten','Landkarten',1,'0.00','','6001',0,0,'',0,0,0,22),(68,'2005-02-24 17:59:53',0,'08',4,'Hobby/Freizeit','Hobby/Freizeit',1,'0.00','','6001',0,0,'',0,0,0,22),(69,'2005-02-24 17:59:53',0,'09',4,'Sport','Sport',1,'0.00','','6001',0,0,'',0,0,0,22),(70,'2005-02-24 17:59:53',0,'600',1,'verkaufte Vulkan Bücherbons','verkaufte Vulkan Bücherbons',1,'0.00','','11400',1,1,'600',0,0,0,22),(71,'1970-01-01 01:00:00',0,'40',7,'Fasnacht','Fasnacht',0,'0.00','','',0,0,'40',0,0,0,22),(91,'1970-01-01 01:00:00',0,'905',2,'Ausgaben Kundenkarten','Ausgaben Kundenkarten',1,'0.00','','35400',0,2,'905',0,0,0,22),(132,'1970-01-01 01:00:00',0,'',7,'Einnahmen Gebührenmarken','Einnahmen Gebührenmarken',1,'0.00','','',0,1,'560',0,0,0,22),(133,'1970-01-01 01:00:00',0,'',7,'Einnahmen Briefmarken','Einnahmen Briefmarken',1,'0.00','','',0,1,'570',0,0,0,22),(134,'1970-01-01 01:00:00',0,'',7,'Einnahmen DPD','Einnahmen DPD',1,'0.00','','',0,1,'580',0,0,0,22),(136,'1970-01-01 01:00:00',0,'32',7,'Pap./Artoz Papier','Pap./Artoz Papier',1,'0.00','','6001',0,0,'32',0,0,0,22),(137,'1970-01-01 01:00:00',0,'33',7,'Pap./Druckerpatronen','Pap./Druckerpatronen',1,'0.00','','6001',0,0,'33',0,0,0,22),(151,'1970-01-01 01:00:00',0,'34',7,'Pap./Alben','Pap./Alben',1,'0.00','','6001',0,0,'34',0,0,0,22),(171,'1970-01-01 01:00:00',0,'35',7,'Künstler','Künstler',1,'0.00','','6001',0,0,'35',0,0,0,22),(191,'1970-01-01 01:00:00',0,'990',4,'Eingelöste SBB-Gutscheine','Eingelöste SBB-Gutscheine',1,'0.00','L','20200',1,0,'',0,0,0,22),(211,'1970-01-01 01:00:00',0,'500',1,'Einzahlung  Diverses','Einzahlung Diverses',0,'0.00','','30000',0,0,'500',0,0,0,22),(231,'1970-01-01 01:00:00',0,'991',4,'Eingelöste BUSH-Gutscheine','Eingelöste BUSH-Gutscheine',1,'0.00','L','20200',1,0,'',0,0,0,22);
/*!40000 ALTER TABLE `pos_product_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_receipt`
--

DROP TABLE IF EXISTS `pos_receipt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_receipt` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `number` varchar(255) NOT NULL DEFAULT '',
  `transaction_id` bigint(20) DEFAULT '0',
  `booking_id` bigint(20) DEFAULT '0',
  `salespoint_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `user_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `foreign_currency_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `status` int(10) unsigned NOT NULL DEFAULT '0',
  `settlement` bigint(20) DEFAULT '0',
  `amount` double NOT NULL DEFAULT '0',
  `customer_id` varchar(13) NOT NULL DEFAULT '',
  `transferred` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user` (`user_id`),
  KEY `status` (`status`),
  KEY `settlement` (`settlement`),
  KEY `timestamp` (`timestamp`),
  KEY `salespoint` (`salespoint_id`),
  KEY `idx_number` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pos_salespoint`
--

DROP TABLE IF EXISTS `pos_salespoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_salespoint` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT '',
  `place` varchar(255) DEFAULT '',
  `stock` double unsigned NOT NULL DEFAULT '0',
  `current_receipt_id` bigint(20) unsigned NOT NULL DEFAULT '1',
  `current_settle_date` datetime DEFAULT '1900-00-00 00:00:00',
  `active` tinyint(1) NOT NULL DEFAULT '0',
  `export_id` varchar(10) NOT NULL DEFAULT '',
  `variable_stock` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_salespoint`
--

LOCK TABLES `pos_salespoint` WRITE;
/*!40000 ALTER TABLE `pos_salespoint` DISABLE KEYS */;
INSERT INTO `pos_salespoint` VALUES (1,'2005-02-24 17:59:51',0,'Kasse Baden','',1500,0,'2005-02-24 00:00:00',1,'1',1),(2,'2005-02-24 17:59:51',0,'Kasse Uster','',1500,0,'2005-02-24 00:00:00',1,'',1),(3,'2005-02-24 17:59:51',0,'Kasse Regensdorf','',0,0,'2005-02-24 00:00:00',1,'',1),(4,'2005-02-24 17:59:51',0,'Kasse Emmen','',0,0,'2005-02-24 00:00:00',1,'',1),(5,'2005-02-24 17:59:51',0,'Kasse Dübendorf','',0,0,'2005-02-24 00:00:00',1,'05',1),(6,'2005-02-24 17:59:51',0,'Kasse Bremgarten','',1500,0,'2005-02-24 00:00:00',1,'',1),(7,'2005-02-24 17:59:51',0,'Kasse Volkiland','',1500,0,'2005-02-24 00:00:00',1,'7',1),(9,'2005-02-24 17:59:51',0,'Kasse Aarburg','',1500,0,'2005-02-24 00:00:00',1,'09',1),(11,'2005-02-24 17:59:51',0,'Kasse 11 Baden Kredit','',0,0,'2005-02-24 00:00:00',1,'11',1),(20,'2005-02-24 17:59:51',0,'Kasse BB 2000','',0,0,'2005-02-24 00:00:00',1,'',1),(25,'2005-02-24 17:59:51',0,'Entwicklung','',1500,0,'2005-02-24 00:00:00',1,'',1),(30,'2005-02-24 17:59:51',0,'Kasse Vulkan','',0,0,'2005-02-24 00:00:00',1,'30',1),(35,'2005-02-24 17:59:51',0,'Kasse Vulkan AG Sous-Sol','',0,0,'2005-02-24 00:00:00',1,'',1),(40,'2005-02-24 17:59:51',0,'Kasse Limmattal','',0,0,'2005-02-24 00:00:00',1,'',1),(44,'2005-02-24 17:59:51',0,'Kasse 2 Emmen','',1500,0,'2005-02-24 00:00:00',1,'',1),(255,'2010-05-07 12:38:58',0,'Buchhaltung','',1500,7,'2010-05-07 00:00:00',1,'',1),(256,'1970-01-01 01:00:00',0,'Kasse Hägendorf','',1500,0,'2007-11-20 00:00:00',1,'99',1),(276,'1970-01-01 01:00:00',0,'Kasse Bener Buchhandlung','',1000,0,'2006-05-16 14:07:09',1,'50',1),(277,'1970-01-01 01:00:00',0,'Kasse Bener Papeterie','',1000,0,'2006-05-16 14:08:15',1,'51',1),(296,'1970-01-01 01:00:00',0,'Kasse Enderli Buchhandlung','Buchhandlung oben',0,0,'2008-01-23 07:56:54',1,'60',1),(316,'1970-01-01 01:00:00',0,'Kasse Enderli Papeterie 1','',0,0,'2008-03-20 10:21:34',1,'61',1),(318,'1970-01-01 01:00:00',0,'Kasse Enderli Papeterie 2','',0,0,'2008-03-20 10:23:05',1,'62',1),(336,'1970-01-01 01:00:00',0,'Kasse Bücherschoch 70','Parterre',0,0,'2008-06-27 10:01:15',1,'70',1),(337,'1970-01-01 01:00:00',0,'Kasse Bücherschoch 71','UG',0,0,'2008-06-27 10:02:03',1,'71',1),(356,'1970-01-01 01:00:00',0,'Kasse Bücherschoch 72','Kredit',0,0,'2008-12-02 08:11:05',1,'72',1);
/*!40000 ALTER TABLE `pos_salespoint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_setting`
--

DROP TABLE IF EXISTS `pos_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_setting` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `com_use` tinyint(1) unsigned DEFAULT '0',
  `com_classname` varchar(255) DEFAULT NULL,
  `com_hold` tinyint(1) unsigned DEFAULT '0',
  `com_update` int(10) unsigned DEFAULT '0',
  `com_path` varchar(255) DEFAULT NULL,
  `com_show_add_customer_message` tinyint(1) unsigned DEFAULT '0',
  `com_search_cd` tinyint(1) unsigned DEFAULT '0',
  `com_cd_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_setting`
--

LOCK TABLES `pos_setting` WRITE;
/*!40000 ALTER TABLE `pos_setting` DISABLE KEYS */;
INSERT INTO `pos_setting` VALUES (1,0,'ch.eugster.pos.product.GalileoServer',0,0,'//Linda/Comeliv/Galileo/Data/galidata.dbc',1,0,'C:/Programme/bibwin/bibwin.ini');
/*!40000 ALTER TABLE `pos_setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_settlement`
--

DROP TABLE IF EXISTS `pos_settlement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_settlement` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `salespoint_id` bigint(20) unsigned NOT NULL,
  `settlement` bigint(20) unsigned DEFAULT NULL,
  `line_number` int(11) DEFAULT '0',
  `type` int(11) DEFAULT '0',
  `subtype` int(11) DEFAULT '0',
  `cashtype` int(11) DEFAULT '0',
  `reference_class_name` varchar(255) DEFAULT NULL,
  `reference_object_id` bigint(20) DEFAULT NULL,
  `short_text` varchar(255) DEFAULT NULL,
  `long_text` varchar(255) DEFAULT NULL,
  `value` double DEFAULT '0',
  `quantity` int(11) DEFAULT '0',
  `amount1` double DEFAULT '0',
  `amount2` double DEFAULT '0',
  `code` varchar(20) DEFAULT NULL,
  `receipts` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_salespoint` (`salespoint_id`),
  KEY `index_settlement` (`settlement`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_settlement`
--

LOCK TABLES `pos_settlement` WRITE;
/*!40000 ALTER TABLE `pos_settlement` DISABLE KEYS */;
INSERT INTO `pos_settlement` VALUES (2,255,1273228737830,0,1,0,0,'ch.eugster.pos.db.ProductGroup',2,'Belletristik TB','Belletristik TB',0,6,23.5,-0.56,'1',7),(3,255,1273228737830,1,1,0,0,'ch.eugster.pos.db.ProductGroup',29,'Multimedia-Bücher','Multimedia-Bücher',0,1,5,-0.12,'18',7),(4,255,1273228737830,2,2,1,0,'ch.eugster.pos.db.PaymentType',1,'Barzahlung','Barzahlung',0,6,22.5,22.5,NULL,7),(5,255,1273228737830,3,2,2,0,'ch.eugster.pos.db.PaymentType',19,'EUR','Euro',0,1,3.87,6,NULL,7),(6,255,1273228737830,4,3,0,0,NULL,NULL,'Total Bewegungen','Total Bewegungen',0,7,28.5,0,NULL,7),(7,255,1273228737830,5,3,0,0,NULL,NULL,'Total Zahlungen','Total Zahlungen',0,7,28.5,0,NULL,7),(8,255,1273228737830,6,4,0,4,'ch.eugster.pos.db.CurrentTax',4,'Umsatzsteuer 2.4','Umsatzsteuer 2.4',0,7,28.5,-0.68,NULL,7),(9,255,1273228737830,7,9,0,1,NULL,NULL,'Anfangsbestand (var.)','Anfangsbestand (var.)',0,0,0,0,'CHF',7),(10,255,1273228737830,8,9,0,2,NULL,NULL,'Kassenbestand SOLL','Kassenbestand SOLL',0,0,22.5,0,'CHF',7),(11,255,1273228737830,9,9,0,3,NULL,NULL,'Kassenbestand IST','Kassenbestand IST',0,0,999900.05,0,'CHF',7),(12,255,1273228737830,10,9,0,4,NULL,NULL,'Bareinnahmen SOLL','Bareinnahmen SOLL',0,0,22.5,0,'CHF',7),(13,255,1273228737830,11,9,0,5,NULL,NULL,'Bareinnahmen IST','Bareinnahmen IST',0,0,999900.05,0,'CHF',7),(14,255,1273228737830,12,9,0,8,NULL,NULL,'Zuviel in Kasse','Zuviel in Kasse',0,0,999877.55,0,'CHF',7),(15,255,1273228737830,13,9,0,9,NULL,NULL,'CHF 100.00','CHF 100.00',100,9999,999900,0,'CHF',7),(16,255,1273228737830,14,9,0,9,NULL,NULL,'CHF 0.05','CHF 0.05',0.05,1,0.05,0,'CHF',7);
/*!40000 ALTER TABLE `pos_settlement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_stock`
--

DROP TABLE IF EXISTS `pos_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_stock` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `salespoint_id` bigint(20) unsigned NOT NULL,
  `foreign_currency_id` bigint(20) unsigned NOT NULL,
  `stock` double DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `index_salespoint` (`salespoint_id`),
  KEY `index_foreign_currency` (`foreign_currency_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_stock`
--

LOCK TABLES `pos_stock` WRITE;
/*!40000 ALTER TABLE `pos_stock` DISABLE KEYS */;
INSERT INTO `pos_stock` VALUES (2,1,22,0),(3,2,22,0),(4,3,22,0),(5,4,22,0),(6,5,22,0),(7,6,22,0),(8,7,22,0),(9,9,22,0),(10,11,22,0),(11,20,22,0),(12,25,22,0),(13,30,22,0),(14,35,22,0),(15,40,22,0),(16,44,22,0),(17,255,22,999900.05),(18,256,22,0),(19,276,22,0),(20,277,22,0),(21,296,22,0),(22,316,22,0),(23,318,22,0),(24,336,22,0),(25,337,22,0),(26,356,22,0),(27,1,37,0),(28,2,37,0),(29,3,37,0),(30,4,37,0),(31,5,37,0),(32,6,37,0),(33,7,37,0),(34,9,37,0),(35,11,37,0),(36,20,37,0),(37,25,37,0),(38,30,37,0),(39,35,37,0),(40,40,37,0),(41,44,37,0),(42,255,37,0),(43,256,37,0),(44,276,37,0),(45,277,37,0),(46,296,37,0),(47,316,37,0),(48,318,37,0),(49,336,37,0),(50,337,37,0),(51,356,37,0);
/*!40000 ALTER TABLE `pos_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_tab`
--

DROP TABLE IF EXISTS `pos_tab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_tab` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `block_id` int(10) unsigned NOT NULL DEFAULT '0',
  `default_tab_position` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `default_tab_payment` tinyint(4) NOT NULL DEFAULT '0',
  `sequence` int(10) unsigned NOT NULL DEFAULT '0',
  `rowz` int(10) unsigned NOT NULL DEFAULT '4',
  `columnz` int(10) unsigned NOT NULL DEFAULT '4',
  `visible` tinyint(4) NOT NULL DEFAULT '0',
  `font_size` double unsigned NOT NULL DEFAULT '0',
  `font_style` int(10) unsigned NOT NULL DEFAULT '0',
  `title` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_tab` (`block_id`,`title`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_tab`
--

LOCK TABLES `pos_tab` WRITE;
/*!40000 ALTER TABLE `pos_tab` DISABLE KEYS */;
INSERT INTO `pos_tab` VALUES (1,'1970-01-01 01:00:00',1,1,0,1,3,3,0,0,0,'WG 1'),(2,'1970-01-01 01:00:00',1,0,0,2,3,3,0,0,0,'WG 2'),(3,'1970-01-01 01:00:00',4,1,0,1,3,3,0,0,0,'Fun 1'),(4,'1970-01-01 01:00:00',4,0,0,2,3,3,0,0,0,'Fun 2'),(5,'2010-03-16 08:15:06',4,0,1,3,2,1,0,0,0,'Coupon'),(6,'1970-01-01 01:00:00',2,0,1,1,2,3,0,0,0,'Bar'),(21,'1970-01-01 01:00:00',1,0,0,3,3,3,0,0,0,'WG 3'),(22,'1970-01-01 01:00:00',1,0,0,4,3,3,0,0,0,'WG 4'),(23,'1970-01-01 01:00:00',1,0,0,5,3,3,0,0,0,'Ausgaben'),(24,'1970-01-01 01:00:00',2,0,0,2,2,3,0,0,0,'Kredit / Bon'),(81,'1970-01-01 01:00:00',2,0,0,3,2,3,0,0,0,'Euro');
/*!40000 ALTER TABLE `pos_tab` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_tax`
--

DROP TABLE IF EXISTS `pos_tax`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_tax` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `current_tax_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `tax_type_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `tax_rate_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `code` varchar(100) DEFAULT '',
  `galileo_id` char(3) NOT NULL DEFAULT '',
  `code128_id` char(3) NOT NULL DEFAULT '',
  `account` varchar(100) DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_type_rate` (`tax_type_id`,`tax_rate_id`),
  KEY `code128_id` (`code128_id`),
  KEY `tax_rate_id` (`tax_rate_id`),
  KEY `code` (`code`),
  KEY `tax_type_id` (`tax_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_tax`
--

LOCK TABLES `pos_tax` WRITE;
/*!40000 ALTER TABLE `pos_tax` DISABLE KEYS */;
INSERT INTO `pos_tax` VALUES (1,'2005-02-24 17:59:51',0,1,1,1,'UF','0','',''),(2,'2005-02-24 17:59:52',0,2,2,1,'MF','','',''),(3,'2005-02-24 17:59:52',0,3,3,1,'IF','','',''),(4,'2005-02-24 17:59:52',0,4,1,2,'UR','1','','2041'),(5,'2005-02-24 17:59:52',0,5,2,2,'MR','','','1271'),(6,'2005-02-24 17:59:52',0,6,3,2,'IR','','','1231'),(7,'2005-02-24 17:59:52',0,7,1,3,'UN','2','','2041'),(8,'2005-02-24 17:59:52',0,8,2,3,'MN','','','1271'),(9,'2005-02-24 17:59:52',0,9,3,3,'IN','','','1231');
/*!40000 ALTER TABLE `pos_tax` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_tax_rate`
--

DROP TABLE IF EXISTS `pos_tax_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_tax_rate` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `code` char(1) NOT NULL DEFAULT '',
  `name` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_code` (`code`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_tax_rate`
--

LOCK TABLES `pos_tax_rate` WRITE;
/*!40000 ALTER TABLE `pos_tax_rate` DISABLE KEYS */;
INSERT INTO `pos_tax_rate` VALUES (1,'2005-02-24 17:59:51',0,'F','Steuerfrei'),(2,'2005-02-24 17:59:51',0,'R','Reduzierter Steuersatz'),(3,'2005-02-24 17:59:51',0,'N','Normaler Steuersatz');
/*!40000 ALTER TABLE `pos_tax_rate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_tax_type`
--

DROP TABLE IF EXISTS `pos_tax_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_tax_type` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `code` char(1) NOT NULL DEFAULT '',
  `name` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_code` (`code`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_tax_type`
--

LOCK TABLES `pos_tax_type` WRITE;
/*!40000 ALTER TABLE `pos_tax_type` DISABLE KEYS */;
INSERT INTO `pos_tax_type` VALUES (1,'2005-02-24 17:59:51',0,'U','Umsatzsteuer'),(2,'2005-02-24 17:59:51',0,'M','Vorsteuer Material/Dienstleistungen'),(3,'2005-02-24 17:59:51',0,'I','Vorsteuer Investitionen/Betriebsaufwand');
/*!40000 ALTER TABLE `pos_tax_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_user`
--

DROP TABLE IF EXISTS `pos_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_user` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `username` varchar(100) NOT NULL DEFAULT '',
  `password` varchar(100) DEFAULT '',
  `pos_login` bigint(20) unsigned NOT NULL DEFAULT '0',
  `status` int(10) unsigned NOT NULL DEFAULT '0',
  `default_user` tinyint(4) DEFAULT '0',
  `reverse_receipts` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `pos_login` (`pos_login`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_user`
--

LOCK TABLES `pos_user` WRITE;
/*!40000 ALTER TABLE `pos_user` DISABLE KEYS */;
INSERT INTO `pos_user` VALUES (1,'2005-02-24 01:45:35',0,'Admin','1234',1234,0,1,1),(2,'2005-02-24 01:45:35',0,'Old','1234',1234,0,0,1),(3,'1970-01-01 01:00:00',0,'BUSH','123',123,1,0,1);
/*!40000 ALTER TABLE `pos_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_user_access`
--

DROP TABLE IF EXISTS `pos_user_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_user_access` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `timestamp` datetime DEFAULT '0000-00-00 00:00:00',
  `user_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `access_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `state` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_access_unique` (`user_id`,`access_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_user_access`
--

LOCK TABLES `pos_user_access` WRITE;
/*!40000 ALTER TABLE `pos_user_access` DISABLE KEYS */;
/*!40000 ALTER TABLE `pos_user_access` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pos_version`
--

DROP TABLE IF EXISTS `pos_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pos_version` (
  `data_version` int(10) unsigned NOT NULL DEFAULT '0',
  `transaction_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`data_version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pos_version`
--

LOCK TABLES `pos_version` WRITE;
/*!40000 ALTER TABLE `pos_version` DISABLE KEYS */;
INSERT INTO `pos_version` VALUES (35,0);
/*!40000 ALTER TABLE `pos_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequence`
--

DROP TABLE IF EXISTS `sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence` (
  `SEQ_NAME` varchar(50) NOT NULL,
  `SEQ_COUNT` decimal(38,0) DEFAULT NULL,
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence`
--

LOCK TABLES `sequence` WRITE;
/*!40000 ALTER TABLE `sequence` DISABLE KEYS */;
INSERT INTO `sequence` VALUES ('common_settings_id','0'),('configurable_id','0'),('currency_id','0'),('current_tax_code_mapping_id','0'),('current_tax_id','0'),('display_id','0'),('display_mode_id','0'),('external_product_group_id','0'),('money_id','0'),('payment_id','0'),('payment_type_id','0'),('periphery_id','0'),('periphery_property_id','0'),('position_id','0'),('printout_area_id','0'),('printout_id','0'),('product_group_id','0'),('product_group_mapping_id','0'),('product_id','0'),('profile_id','0'),('provider_property_id','0'),('receipt_id','0'),('role_id','0'),('role_property_id','0'),('salespoint_id','0'),('salespoint_periphery_id','0'),('settlement_id','0'),('stock_id','0'),('tax_code_mapping_id','0'),('tax_id','0'),('tax_rate_id','0'),('tax_type_id','0'),('user_id','0'),('version_id','0');
/*!40000 ALTER TABLE `sequence` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-09-02 16:43:23
