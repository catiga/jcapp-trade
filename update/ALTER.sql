/*20180825*/
ALTER TABLE `trade_cashier_counter` ADD COLUMN `ctcode` CHAR(4) NOT NULL DEFAULT '1000' AFTER `inuse`;

/*20180907*/
CREATE TABLE `trade_preferential` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `t_id` bigint(20) DEFAULT NULL,
  `t_num` varchar(255) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `order_num` varchar(255) DEFAULT NULL,
  `oc` char(4) DEFAULT NULL,
  `pref_id` varchar(255) DEFAULT NULL,
  `pref_code` varchar(255) DEFAULT NULL,
  `pref_name` varchar(255) DEFAULT NULL,
  `pref_type` char(4) DEFAULT '10',
  `a_time` datetime DEFAULT NULL,
  `c_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `flag` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

/*20180912*/
CREATE TABLE `cash_collect_setting` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL,
  `vv` decimal(10,2) DEFAULT NULL,
  `a_time` datetime DEFAULT NULL,
  `c_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `flag` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;


/*20180913*/
ALTER TABLE `trade_preferential` ADD COLUMN `max_deduct` DECIMAL(10,2) AFTER `flag`;
ALTER TABLE `trade_preferential` ADD COLUMN `real_deduct` DECIMAL(10,2) AFTER `max_deduct`;


/*20180914*/
ALTER TABLE `cash_collect_setting` ADD COLUMN `cscode` CHAR(4) NOT NULL DEFAULT '1000' AFTER `flag`;
ALTER TABLE `cash_collect_setting` ADD COLUMN `tip` VARCHAR(16) AFTER `flag`;

/*20181005*/
ALTER TABLE `supp_conf_ts` ADD COLUMN `code_cat` VARCHAR(16) AFTER `code`;


/*2018-12-13 ***/
ALTER TABLE `cashier_doer_log` ADD  COLUMN  uid bigint(11) DEFAULT NULL; 

