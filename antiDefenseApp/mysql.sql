-- MySQL dump 10.13  Distrib 5.6.23, for Win32 (x86)
--
-- Host: localhost    Database: adconfig
-- ------------------------------------------------------
-- Server version	5.6.23-log

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

GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'cetc' WITH GRANT OPTION;
FLUSH PRIVILEGES;


--
-- Table structure for table `attack_detail`
--

DROP TABLE IF EXISTS `attack_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attack_detail` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `attackip` varchar(20) NOT NULL,
  `attack_type` int(4) NOT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `duration` bigint(10) unsigned DEFAULT '0' COMMENT '持续时间,单位秒',
  `status` int(4) DEFAULT '0',
  `total_pkts` bigint(10) unsigned DEFAULT '0' COMMENT '报文数',
  `total_bytes` bigint(10) unsigned DEFAULT '0' COMMENT '千字节数',
  `peak` bigint(10) unsigned DEFAULT '0',
  `po_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `po_id` (`po_id`),
  CONSTRAINT `attack_detail_ibfk_2` FOREIGN KEY (`po_id`) REFERENCES `protectobject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attack_detail`
--

LOCK TABLES `attack_detail` WRITE;
/*!40000 ALTER TABLE `attack_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `attack_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attack_ip`
--

DROP TABLE IF EXISTS `attack_ip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attack_ip` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(20) NOT NULL,
  `handled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attack_ip`
--

LOCK TABLES `attack_ip` WRITE;
/*!40000 ALTER TABLE `attack_ip` DISABLE KEYS */;
/*!40000 ALTER TABLE `attack_ip` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attack_statistics`
--

DROP TABLE IF EXISTS `attack_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attack_statistics` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `max_bps` bigint(10) unsigned NOT NULL DEFAULT '0',
  `defense_count` bigint(10) unsigned NOT NULL DEFAULT '0',
  `save_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attack_statistics`
--

LOCK TABLES `attack_statistics` WRITE;
/*!40000 ALTER TABLE `attack_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `attack_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `autolearnbasevalue`
--

DROP TABLE IF EXISTS `autolearnbasevalue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `autolearnbasevalue` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `PoName` varchar(256) DEFAULT NULL,
  `protocal` int(3) unsigned DEFAULT NULL,
  `flowid` int(11) unsigned DEFAULT NULL,
  `week` int(8) DEFAULT NULL,
  `hour` int(8) DEFAULT NULL,
  `pps` varchar(256) DEFAULT '0',
  `bps` varchar(256) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `flowid` (`flowid`),
  CONSTRAINT `autolearnbasevalue_ibfk_1` FOREIGN KEY (`flowid`) REFERENCES `flow` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `autolearnbasevalue`
--

LOCK TABLES `autolearnbasevalue` WRITE;
/*!40000 ALTER TABLE `autolearnbasevalue` DISABLE KEYS */;
/*!40000 ALTER TABLE `autolearnbasevalue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cleandev`
--

DROP TABLE IF EXISTS `cleandev`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cleandev` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `direct` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `check_interval` int(10) unsigned NOT NULL,
  `tcp` int(10) unsigned DEFAULT '0',
  `tcp_abnormal` int(10) unsigned DEFAULT '0',
  `tcp_first` tinyint(1) unsigned DEFAULT '0',
  `udp` int(10) unsigned DEFAULT '0',
  `icmp` int(10) unsigned DEFAULT '0',
  `http` int(10) unsigned DEFAULT '0',
  `http_port` int(10) unsigned DEFAULT '80',
  `http_header` int(10) unsigned DEFAULT '0',
  `http_post` int(10) unsigned DEFAULT '0',
  `https` int(10) unsigned DEFAULT '0',
  `https_port` int(10) unsigned DEFAULT '443',
  `https_thc` int(10) unsigned DEFAULT '0',
  `dns_request` int(10) unsigned DEFAULT '0',
  `dns_reply` int(10) unsigned DEFAULT '0',
  `dns_abnormal` int(10) unsigned DEFAULT '0',
  `dns_port` int(10) unsigned DEFAULT '53',
  `ip` varchar(32) DEFAULT NULL,
  `user` varchar(64) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `snmp` int(10) DEFAULT NULL,
  `snmp_port` int(10) DEFAULT NULL,
  `ntp` int(10) DEFAULT NULL,
  `ntp_port` int(11) DEFAULT NULL,
  `flow_timeout` int(10) unsigned DEFAULT '60',
  `flag` tinyint(4) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cleandev`
--

LOCK TABLES `cleandev` WRITE;
/*!40000 ALTER TABLE `cleandev` DISABLE KEYS */;
/*!40000 ALTER TABLE `cleandev` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cleandev_flowstatistics`
--

DROP TABLE IF EXISTS `cleandev_flowstatistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cleandev_flowstatistics` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `attack_count` bigint(10) unsigned DEFAULT '0',
  `defense_count` bigint(10) unsigned DEFAULT '0',
  `clean_traffic` bigint(20) DEFAULT NULL,
  `attack_src_num` bigint(20) DEFAULT NULL,
  `syn_flood` bigint(10) unsigned DEFAULT '0',
  `ack_flood` bigint(10) unsigned DEFAULT '0',
  `syn_ack_flood` bigint(10) unsigned DEFAULT '0',
  `fin_rst_flood` bigint(10) DEFAULT NULL,
  `udp_flood` bigint(10) unsigned DEFAULT '0',
  `icmp_flood` bigint(10) unsigned DEFAULT '0',
  `dns_flood` bigint(10) unsigned DEFAULT '0',
  `cc_flood` bigint(10) unsigned DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cleandev_flowstatistics`
--

LOCK TABLES `cleandev_flowstatistics` WRITE;
/*!40000 ALTER TABLE `cleandev_flowstatistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `cleandev_flowstatistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `controller`
--

DROP TABLE IF EXISTS `controller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `controller` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(255) NOT NULL,
  `port` smallint(5) unsigned NOT NULL,
  `user` varchar(64) NOT NULL,
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `type` tinyint(3) unsigned NOT NULL,
  `hardware_type` tinyint(3) DEFAULT '0',
  `status` tinyint(3) unsigned NOT NULL,
  `flag` tinyint(3) unsigned zerofill NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ip_2` (`ip`,`port`) USING BTREE,
  KEY `ip` (`ip`),
  KEY `flag` (`flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `controller`
--

LOCK TABLES `controller` WRITE;
/*!40000 ALTER TABLE `controller` DISABLE KEYS */;
/*!40000 ALTER TABLE `controller` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `controller_iface`
--

DROP TABLE IF EXISTS `controller_iface`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `controller_iface` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inport` varchar(64) NOT NULL,
  `outport` varchar(64) NOT NULL,
  `controller_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `controller_id` (`controller_id`),
  CONSTRAINT `controller_iface_ibfk_1` FOREIGN KEY (`controller_id`) REFERENCES `controller` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `controller_iface`
--

LOCK TABLES `controller_iface` WRITE;
/*!40000 ALTER TABLE `controller_iface` DISABLE KEYS */;
/*!40000 ALTER TABLE `controller_iface` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ddosparam`
--

DROP TABLE IF EXISTS `ddosparam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ddosparam` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `detection_interval` int(11) NOT NULL DEFAULT '60',
  `deviation_percentage` smallint(5) unsigned DEFAULT '50',
  `suspicions_threshold` tinyint(3) unsigned DEFAULT '3',
  `recover_threshold` tinyint(3) unsigned DEFAULT '3',
  `controller_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `controller_id` (`controller_id`) USING BTREE,
  CONSTRAINT `ddosparam_ibfk_1` FOREIGN KEY (`controller_id`) REFERENCES `controller` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ddosparam`
--

LOCK TABLES `ddosparam` WRITE;
/*!40000 ALTER TABLE `ddosparam` DISABLE KEYS */;
/*!40000 ALTER TABLE `ddosparam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `attack_speed` double unsigned NOT NULL DEFAULT '0',
  `attack_pps` bigint(20) unsigned NOT NULL DEFAULT '0',
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `flow_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `flow_id` (`flow_id`) USING BTREE,
  CONSTRAINT `event_ibfk_1` FOREIGN KEY (`flow_id`) REFERENCES `flow` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flow`
--

DROP TABLE IF EXISTS `flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `flow` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `priority` int(10) unsigned DEFAULT NULL,
  `ethernetType` smallint(4) DEFAULT NULL,
  `protocol` tinyint(3) unsigned zerofill NOT NULL,
  `l4` mediumint(9) NOT NULL DEFAULT '0',
  `threshold_type` tinyint(4) NOT NULL DEFAULT '0',
  `threshold_kbps` bigint(40) unsigned NOT NULL DEFAULT '0',
  `threshold_pps` bigint(40) unsigned NOT NULL DEFAULT '0',
  `po_id` int(10) unsigned NOT NULL,
  `flag` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `po_id` (`po_id`),
  KEY `flag` (`flag`),
  CONSTRAINT `flow_ibfk_1` FOREIGN KEY (`po_id`) REFERENCES `protectobject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flow`
--

LOCK TABLES `flow` WRITE;
/*!40000 ALTER TABLE `flow` DISABLE KEYS */;
/*!40000 ALTER TABLE `flow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flowstatistics`
--

DROP TABLE IF EXISTS `flowstatistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `flowstatistics` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `normal_speed_count` bigint(20) unsigned DEFAULT '0',
  `exception_speed_count` bigint(20) unsigned DEFAULT '0',
  `attack_count` bigint(20) unsigned DEFAULT '0',
  `defense_count` bigint(20) unsigned DEFAULT '0',
  `recover_count` bigint(20) unsigned DEFAULT NULL,
  `flow_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `flow_id` (`flow_id`) USING BTREE,
  CONSTRAINT `flowstatistics_ibfk_1` FOREIGN KEY (`flow_id`) REFERENCES `flow` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flowstatistics`
--

LOCK TABLES `flowstatistics` WRITE;
/*!40000 ALTER TABLE `flowstatistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `flowstatistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `input_trafficinfo`
--

DROP TABLE IF EXISTS `input_trafficinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `input_trafficinfo` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `protocol` int(11) NOT NULL,
  `rate_pps` bigint(11) NOT NULL,
  `rate_bps` bigint(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `input_trafficinfo`
--

LOCK TABLES `input_trafficinfo` WRITE;
/*!40000 ALTER TABLE `input_trafficinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `input_trafficinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ip_trafficinfo`
--

DROP TABLE IF EXISTS `ip_trafficinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_trafficinfo` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(255) NOT NULL,
  `flowrate_pps` bigint(20) NOT NULL,
  `flowrate_bps` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ip_trafficinfo`
--

LOCK TABLES `ip_trafficinfo` WRITE;
/*!40000 ALTER TABLE `ip_trafficinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `ip_trafficinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ipcity`
--

DROP TABLE IF EXISTS `ipcity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ipcity` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `city` varchar(10) DEFAULT NULL,
  `lng` varchar(20) DEFAULT NULL,
  `lat` varchar(20) DEFAULT NULL,
  `count` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ipcity`
--

LOCK TABLES `ipcity` WRITE;
/*!40000 ALTER TABLE `ipcity` DISABLE KEYS */;
/*!40000 ALTER TABLE `ipcity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `netnode`
--

DROP TABLE IF EXISTS `netnode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `netnode` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `switch_id` varchar(64) NOT NULL,
  `type` tinyint(3) unsigned zerofill NOT NULL DEFAULT '000',
  `controller_id` int(10) unsigned NOT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `flag` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `name` (`name`),
  KEY `switch_id` (`switch_id`),
  KEY `flag` (`flag`),
  KEY `controller_id` (`controller_id`,`flag`) USING BTREE,
  CONSTRAINT `netnode_ibfk_1` FOREIGN KEY (`controller_id`) REFERENCES `controller` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `netnode`
--

LOCK TABLES `netnode` WRITE;
/*!40000 ALTER TABLE `netnode` DISABLE KEYS */;
/*!40000 ALTER TABLE `netnode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `po_trafficinfo`
--

DROP TABLE IF EXISTS `po_trafficinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `po_trafficinfo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `flowrate_pps` bigint(20) NOT NULL,
  `flowrate_bps` bigint(20) NOT NULL,
  `po_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `po_id` (`po_id`),
  CONSTRAINT `po_trafficinfo_ibfk_1` FOREIGN KEY (`po_id`) REFERENCES `protectobject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `po_trafficinfo`
--

LOCK TABLES `po_trafficinfo` WRITE;
/*!40000 ALTER TABLE `po_trafficinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `po_trafficinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `protectobject`
--

DROP TABLE IF EXISTS `protectobject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protectobject` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `iptype` tinyint(4) NOT NULL,
  `network` varchar(64) NOT NULL,
  `clean_inport` varchar(64) NOT NULL DEFAULT '0',
  `clean_outport` varchar(64) NOT NULL DEFAULT '0',
  `inport` varchar(64) DEFAULT NULL,
  `outport` varchar(64) DEFAULT NULL,
  `defensetype` tinyint(4) NOT NULL DEFAULT '0',
  `guideport` varchar(4) DEFAULT NULL,
  `reinjection_port` varchar(64) DEFAULT NULL,
  `learn_status` tinyint(3) unsigned NOT NULL,
  `controller_id` int(10) unsigned DEFAULT '0',
  `cleandev_id` int(10) unsigned NOT NULL,
  `check_interval` int(11) NOT NULL DEFAULT '2',
  `tcp_syn` int(10) unsigned DEFAULT '0',
  `tcp_synack` int(10) unsigned DEFAULT '0',
  `udp` int(10) unsigned DEFAULT '0',
  `icmp` int(10) unsigned DEFAULT '0',
  `icmp_redirect` tinyint(1) DEFAULT '0',
  `http` int(10) unsigned DEFAULT '0',
  `http_port` int(10) unsigned DEFAULT '80',
  `http_post` int(10) unsigned DEFAULT '0',
  `http_src_auth` tinyint(1) unsigned DEFAULT '0',
  `https` int(10) unsigned DEFAULT '0',
  `https_port` int(10) unsigned DEFAULT '443',
  `https_thc` int(10) unsigned DEFAULT '0',
  `dns_request` int(10) unsigned DEFAULT '0',
  `dns_reply` int(10) unsigned DEFAULT '0',
  `dns_port` int(10) unsigned DEFAULT '53',
  `snmp` int(10) unsigned DEFAULT NULL,
  `snmp_port` int(10) DEFAULT NULL,
  `ntp` int(10) unsigned DEFAULT NULL,
  `ntp_port` int(10) DEFAULT NULL,
  `ip_option` int(10) unsigned DEFAULT '0',
  `flag` tinyint(3) unsigned zerofill NOT NULL,
  PRIMARY KEY (`id`),
  KEY `name` (`name`),
  KEY `flag` (`flag`),
  KEY `network` (`network`),
  KEY `netnode_id` (`controller_id`),
  KEY `cleandev_id` (`cleandev_id`),
  CONSTRAINT `protectobject_ibfk_1` FOREIGN KEY (`cleandev_id`) REFERENCES `cleandev` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `protectobject`
--

LOCK TABLES `protectobject` WRITE;
/*!40000 ALTER TABLE `protectobject` DISABLE KEYS */;
/*!40000 ALTER TABLE `protectobject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `traffic_info`
--

DROP TABLE IF EXISTS `traffic_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `traffic_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` int(11) DEFAULT NULL,
  `time` bigint(20) DEFAULT NULL,
  `pps_tcp` bigint(20) DEFAULT NULL,
  `pps_udp` bigint(20) DEFAULT NULL,
  `pps_icmp` bigint(20) DEFAULT NULL,
  `pps_other` bigint(20) DEFAULT NULL,
  `pps_all` bigint(20) DEFAULT NULL,
  `bps_tcp` bigint(20) DEFAULT NULL,
  `bps_udp` bigint(20) DEFAULT NULL,
  `bps_icmp` bigint(20) DEFAULT NULL,
  `bps_other` bigint(20) DEFAULT NULL,
  `bps_all` bigint(20) DEFAULT NULL,
  `output_pps` bigint(20) DEFAULT NULL,
  `output_bps` bigint(20) DEFAULT NULL,
  `po_id` int(10) unsigned DEFAULT NULL,
  `attack_bps` bigint(20) DEFAULT NULL,
  `attack_pps` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `po_id` (`po_id`),
  CONSTRAINT `traffic_info_ibfk_1` FOREIGN KEY (`po_id`) REFERENCES `protectobject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traffic_info`
--

LOCK TABLES `traffic_info` WRITE;
/*!40000 ALTER TABLE `traffic_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `traffic_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `traffic_statistics`
--

DROP TABLE IF EXISTS `traffic_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `traffic_statistics` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `input_traffic_pps` bigint(11) DEFAULT NULL COMMENT '流量类型：入流量、出流量、攻击流量',
  `input_traffic_bps` bigint(11) DEFAULT NULL COMMENT '流量类型：入流量、出流量、攻击流量',
  `output_traffic_pps` bigint(20) DEFAULT NULL,
  `output_traffic_bps` bigint(20) DEFAULT NULL,
  `attack_traffic_pps` bigint(20) DEFAULT NULL,
  `attack_traffic_bps` bigint(20) DEFAULT NULL,
  `icmp_pps` bigint(20) DEFAULT NULL,
  `icmp_bps` bigint(20) DEFAULT NULL,
  `udp_pps` bigint(20) DEFAULT NULL,
  `udp_bps` bigint(20) DEFAULT NULL,
  `tcp_pps` bigint(20) DEFAULT NULL,
  `tcp_bps` bigint(20) DEFAULT NULL,
  `other_pps` bigint(20) DEFAULT NULL,
  `other_bps` bigint(20) DEFAULT NULL,
  `save_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traffic_statistics`
--

LOCK TABLES `traffic_statistics` WRITE;
/*!40000 ALTER TABLE `traffic_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `traffic_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trafficbase`
--

DROP TABLE IF EXISTS `trafficbase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trafficbase` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `weekday` tinyint(4) DEFAULT NULL,
  `hour` tinyint(4) DEFAULT NULL,
  `base_value` int(10) unsigned DEFAULT NULL,
  `flow_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `flow_id` (`flow_id`),
  CONSTRAINT `trafficbase_ibfk_1` FOREIGN KEY (`flow_id`) REFERENCES `flow` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trafficbase`
--

LOCK TABLES `trafficbase` WRITE;
/*!40000 ALTER TABLE `trafficbase` DISABLE KEYS */;
/*!40000 ALTER TABLE `trafficbase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `USERNAME` varchar(50) NOT NULL,
  `PASSWORD` varchar(250) DEFAULT NULL,
  `CREATETIME` timestamp NULL DEFAULT NULL,
  `NICKNAME` varchar(50) DEFAULT NULL,
  `ROLE` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `user_name` (`USERNAME`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','38d180985d1b2e7a6014190e2cbd3c967408837188354ec93d27bfd86d09a017','2015-09-14 07:59:46','admin','ADMIN'),(2,'user','38d180985d1b2e7a6014190e2cbd3c967408837188354ec93d27bfd86d09a017','2016-06-29 01:43:37','user','TENANT');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_po`
--

DROP TABLE IF EXISTS `user_po`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_po` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `po_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `po_id` (`po_id`),
  CONSTRAINT `user_po_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_po_ibfk_2` FOREIGN KEY (`po_id`) REFERENCES `protectobject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_po`
--

LOCK TABLES `user_po` WRITE;
/*!40000 ALTER TABLE `user_po` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_po` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-09-19 10:33:17
