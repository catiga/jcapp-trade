/*结算状态*/
ALTER TABLE `cashier_doer_log` ADD COLUMN `uid`  bigint(11) DEFAULT NULL;
ALTER TABLE `cashier_doer_log` ADD COLUMN `settle`  VARCHAR(4) DEFAULT NULL;
ALTER TABLE `trade_info` ADD COLUMN `log_id`  bigint(11) DEFAULT NULL;
update cashier_doer_log set settle='00';
/*支付记录id*/
ALTER TABLE `trade_pay_info` ADD COLUMN `log_id`  bigint(11) DEFAULT NULL;
