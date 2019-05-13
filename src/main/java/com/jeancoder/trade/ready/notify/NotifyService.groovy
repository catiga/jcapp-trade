package com.jeancoder.trade.ready.notify

import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.util.MD5Util
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.entity.TriggerRog
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.ser.TriggerService
import com.jeancoder.trade.ready.util.NativeUtil
import com.jeancoder.trade.ready.util.RemoteUtil

import groovy.json.JsonSlurper

class NotifyService {
	static final TradeService trade_service = TradeService.INSTANCE();
	
	static final TriggerService trigger_service = TriggerService.INSTANCE();
	
	static final JCLogger  Logger  = LoggerSource.getLogger();
	/**
	 * @param trade
	 * @return
	 */
	static def pub(TradeInfo trade, def op = null) {
		def store_id = trade.storeid;
		def store_name = '';
		if(trade.storename!=null) {
			store_name = URLEncoder.encode(URLEncoder.encode(trade.storename, "UTF-8"), "UTF-8");
		}
		def pid = trade.pid;
		def op_errors = [];
		op_errors.add("系统错误")
		def zero_amount = new BigDecimal(0);
		try{
			//向相关模块进行支付结果通知
			def trade_items = trade_service.trade_items(trade);
			if(trade_items) {
				trade_items.each{item->
					def oc = item.oc;
					def on = item.order_num;
					def trade_order_pay_amount = item.pay_amount;
					if(item.tp_amount!=null&&item.tp_amount.compareTo(zero_amount)>=0) {
						trade_order_pay_amount = item.tp_amount;
					} else if(item.tp_amount!=null&&item.tp_amount.compareTo(zero_amount)<0) {
						trade_order_pay_amount = new BigDecimal(0);
					}
					//同步订单和交易状态
					//item.oss = trade.tss;
					//JcTemplate.INSTANCE().update(item);
					
					TriggerRog rog = trigger_service.get_by_order_type(oc);
					if(rog) {
						def url = '/' + rog.appcode + '/' + rog.callback;
						def orig_str = 'oc=' + oc + '&on=' + on;
						def sign = MD5Util.getStringMD5(orig_str + rog.token);
						orig_str += '&sign=' + sign + '&pt=' + trade.pay_type;
						url += ('?'+ orig_str);

						def request_param = [oc:oc,on:on,pid:pid,sid:store_id,sname:store_name,sign:sign,pt:trade.pay_type,pay_amount:trade_order_pay_amount];
						if(op!=null) {
							request_param.put('op', op);
						}
						def ret_str = RemoteUtil.connect(rog.appcode, rog.callback, request_param);
						Logger.info("NotifyService op="+op+"  notify rules:" + ret_str)
						if(ret_str!=null) {
							try {
								def jsonSlurper = new JsonSlurper()
								//获取到的是Map对象
								def map = jsonSlurper.parseText(ret_str)
								if(map['code']!='0') {
									//失败情况
									op_errors = [];
									op_errors.add(map);
								} else {
									op_errors = [];
								}
							}catch(any_e) {
							}
						}
					}
				}
			}
		}catch(any) {
			Logger.error('wx_pay_notify_exception:', any);
		}
		return op_errors;
	}
	
	
	
	static def incallPub(TradeInfo trade, def op = null) {
		def store_id = trade.storeid;
		def store_name = '';
		if(trade.storename!=null) {
			store_name = URLEncoder.encode(URLEncoder.encode(trade.storename, "UTF-8"), "UTF-8");
		}
		def pid = trade.pid;
		def op_errors = [];
		op_errors.add("系统错误")
		def zero_amount = new BigDecimal(0);
		try{
			//向相关模块进行支付结果通知
			def trade_items = trade_service.trade_items(trade);
			if(trade_items) {
				//trade_items.each{item->
				for(item in trade_items) {
					def oc = item.oc;
					def on = item.order_num;
					def trade_order_pay_amount = item.pay_amount;
					if(item.tp_amount!=null&&item.tp_amount.compareTo(zero_amount)>=0) {
						trade_order_pay_amount = item.tp_amount;
					} else if(item.tp_amount!=null&&item.tp_amount.compareTo(zero_amount)<0) {
						trade_order_pay_amount = new BigDecimal(0);
					}
					//同步订单和交易状态
					//item.oss = trade.tss;
					//JcTemplate.INSTANCE().update(item);
					
					TriggerRog rog = trigger_service.get_by_order_type(oc);
					if(rog) {
						def url = '/' + rog.appcode + '/' + rog.callback;
						def orig_str = 'oc=' + oc + '&on=' + on;
						def sign = MD5Util.getStringMD5(orig_str + rog.token);
						orig_str += '&sign=' + sign + '&pt=' + trade.pay_type;
						url += ('?'+ orig_str);
						Logger.info(url);
						Logger.info('OP=' + op);
						
						def request_param = [oc:oc,on:on,pid:pid,sid:store_id,sname:store_name,sign:sign,pt:trade.pay_type,pay_amount:trade_order_pay_amount];
						if(op!=null) {
							Logger.info('ADD OP ACTION FLAG:::' + op);
							request_param.put('op', op);
						}
						def ret_str  = NativeUtil.connectStr(rog.appcode, rog.callback, request_param)
						if(ret_str!=null) {
							try {
								def jsonSlurper = new JsonSlurper()
								//获取到的是Map对象
								def map = jsonSlurper.parseText(ret_str)
								op_errors = [];
								if(map['code']!='0') {
									//失败情况
									op_errors = [];
									op_errors.add(map);
								} else {
									op_errors = [];
								}
							}catch(any_e) {
								Logger.error("wx_pay_notify_inner_notify_fail:", any_e);
							}
						}
					}
				}
			}
		}catch(any) {
			Logger.error('wx_pay_notify_exception:', any);
		}
		return op_errors;
	}
}
