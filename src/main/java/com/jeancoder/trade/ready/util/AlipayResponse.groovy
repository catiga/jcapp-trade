package com.jeancoder.trade.ready.util

import java.util.Map

import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger

import groovy.json.JsonSlurper

class AlipayResponse {
	
	public static JCLogger logger = LoggerSource.getLogger(AlipayResponse.class);

	def respone;
	
	AlipayResponse(response) {
		this.respone = response;
	}

	Map<String, String> get_result() {
		def jsonSlurper = new JsonSlurper();
		def map = jsonSlurper.parseText(respone);
		
		logger.info("get_result_"+JackSonBeanMapper.toJson(map));
		
		def code = map['alipay_trade_pay_response']['code'];
		def alipay_account_name = map['alipay_trade_pay_response']['buyer_logon_id'];
		def pay_amount = map['alipay_trade_pay_response']['buyer_pay_amount'];

		def buyer_user_id = map['alipay_trade_pay_response']['buyer_user_id'];
		def fund_bill_list = map['alipay_trade_pay_response']['fund_bill_list'];
		def pay_time = map['alipay_trade_pay_response']['gmt_payment'];
		def invoice_amount = map['alipay_trade_pay_response']['invoice_amount'];
		def get_amount = map['alipay_trade_pay_response']['receipt_amount'];
		def total_amount = map['alipay_trade_pay_response']['total_amount'];
		def trans_id = map['alipay_trade_pay_response']['trade_no'];
		def sign = map['sign'];
		
		return map;
	}
	
	
	Map<String, String> get_alipay_query_result() {
		def jsonSlurper = new JsonSlurper();
		def map = jsonSlurper.parseText(respone);
		
		def code = map['alipay_trade_query_response']['code'];
		def alipay_account_name = map['alipay_trade_query_response']['buyer_logon_id'];
		def pay_amount = map['alipay_trade_query_response']['buyer_pay_amount'];

		def buyer_user_id = map['alipay_trade_query_response']['buyer_user_id'];
		def fund_bill_list = map['alipay_trade_query_response']['fund_bill_list'];
		def pay_time = map['alipay_trade_query_response']['gmt_payment'];
		def invoice_amount = map['alipay_trade_query_response']['invoice_amount'];
		def get_amount = map['alipay_trade_query_response']['receipt_amount'];
		def total_amount = map['alipay_trade_query_response']['total_amount'];
		def trans_id = map['alipay_trade_query_response']['trade_no'];
		def sign = map['sign'];
		
		return map;
	}
}
