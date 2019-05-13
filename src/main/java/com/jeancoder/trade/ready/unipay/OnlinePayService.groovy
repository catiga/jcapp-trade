package com.jeancoder.trade.ready.unipay

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.log.JCLoggerFactory
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.dto.TradeResult
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.DateUtil
import com.jeancoder.trade.ready.util.MD5Util
import com.jeancoder.trade.ready.util.NativeUtil
import com.jeancoder.trade.ready.util.XMLUtil


class OnlinePayService {

	private static final OnlinePayService _instance_ = new OnlinePayService();
	
	JCLogger LOGGER = JCLoggerFactory.getLogger(OnlinePayService.class.getName());
	
	TradeService trade_service = TradeService.INSTANCE();
	
	private PayService() {}
	
	public static OnlinePayService INSTANCE() {
		return _instance_;
	}
	
	
	def pay_router(ProjectGeneralConfig config, def ct, def unicode, TradeInfo trade, Map<String, String> other_param) {
		PayResult pay_result = new PayResult();
		pay_result.code = "-1";
		pay_result.text = "不支持的支付方式";
		pay_result.err_code = 'unsupport_pay_code';
		
		if(ct=='201101') {
			//微信公众号
			pay_result = wxpay_inner(config, unicode, trade, other_param);
		} else if(ct=='201102') {
			//微信小程序
			pay_result = wxpay_inner(config, unicode, trade, other_param);
		} else if(ct=='101001') {
			//会员卡被扫
			pay_result = scan_mc(config, unicode, trade, other_param);
		} else if(ct=='001001') {
			//现金支付
			pay_result = scan_cash(config, unicode, trade, other_param);
		}
		pay_result.paycode = ct;
		return pay_result;
	}
	
	def scan_cash(ProjectGeneralConfig config, def unicode, TradeInfo trade, def other_param) {
		PayResult pay_result = new PayResult();
		pay_result.paycode = '001001';
		pay_result.code = '0';
		pay_result.text = '交易成功';
		
		def real_pay_amount = trade.pay_amount;
		if(trade.handle_fee) {
			real_pay_amount = real_pay_amount.add(trade.handle_fee);
		}
		if(trade.service_fee) {
			real_pay_amount = real_pay_amount.add(trade.service_fee);
		}
		def total_pay_amount = trade.total_amount;
		if(trade.handle_fee) {
			total_pay_amount = total_pay_amount.add(trade.handle_fee);
		}
		if(trade.service_fee) {
			total_pay_amount = total_pay_amount.add(trade.service_fee);
		}
		//现金则直接为扣款成功
		def pay_data_map = [:];
		pay_data_map['trans_get_account'] = config.partner;			//现金收款账户直接写死为钱箱
		pay_data_map['bank_type'] = 'CASH';
		pay_data_map['trans_type'] = 'CASH';
		pay_data_map['trans_pay_amount'] = real_pay_amount;
		pay_data_map['trans_total_amount'] = total_pay_amount;
		pay_data_map['trans_user_id'] = null;
		pay_data_map['trans_id'] = trade.tnum;
		
		trade_service.save_trade_pay_data(trade, [pay_result, pay_data_map]);
		return pay_result;
	}
	
	def wxpay_inner(ProjectGeneralConfig config, def unicode, TradeInfo trade, def other_param) {
		def url = 'https://api.mch.weixin.qq.com/pay/unifiedorder';
		def sign_type = 'MD5';
		def fee_type = 'CNY';
		def spbill_create_ip = '166.111.4.100';
		
		def appid = config.partner;
		def mch_id = config.mchid;
		def wx_pay_key = config.pri_key;
		
		def device_info = trade.pid;
		def nonce_str = MD5Util.getMD5(System.currentTimeMillis() + '');
		def body = '微信支付';
		def attach = '好时光消费';
		def out_trade_no = trade.tnum + '';
		//def total_fee = trade.total_amount.intValue() + '';
		def total_fee = trade.pay_amount;
		if(trade.handle_fee) {
			total_fee = total_fee.add(trade.handle_fee);
		}
		if(trade.service_fee) {
			total_fee = total_fee.add(trade.service_fee);
		}
		total_fee = total_fee.intValue() + '';
		def time_start = DateUtil.format(trade.a_time);
		Calendar c = Calendar.getInstance();
		c.setTime(trade.a_time);
		c.add(Calendar.DAY_OF_MONTH, 15);
		def time_expire = DateUtil.format(c.getTime());
		def trade_type = 'JSAPI';
		def notify_url = 'http://'+other_param['domain']+'/trade/outcall/wxpay/trade_notify';
		
		def sort_key = [];
		sort_key.add("appid=" + appid);
		sort_key.add("mch_id=" + mch_id);
		sort_key.add("device_info=" + device_info);
		sort_key.add("nonce_str=" + nonce_str);
		sort_key.add("sign_type=" + sign_type);
		sort_key.add("body=" + body);
		sort_key.add('openid=' + unicode);
		//sort_key.add("detail=" + detail);
		sort_key.add("attach=" + attach);
		sort_key.add("out_trade_no=" + out_trade_no);
		sort_key.add('trade_type=' + trade_type);
		sort_key.add("total_fee=" + total_fee);
		sort_key.add("fee_type=" + fee_type);
		sort_key.add("spbill_create_ip=" + spbill_create_ip);
		sort_key.add("time_start=" + time_start);		//订单开始时间  YYYYMMDDHHMMSS
		sort_key.add("time_expire=" + time_expire);		//订单失效时间  YYYYMMDDHHMMSS
		sort_key.add('notify_url=' + notify_url);
		
		Collections.sort(sort_key);
		StringBuffer buffer = new StringBuffer();
		for(String s : sort_key) {
			buffer.append(s + "&");
		}
		String sign_original_str = buffer.substring(0, buffer.toString().length() - 1);
		sign_original_str = sign_original_str + "&key=" + wx_pay_key;
		String sign = MD5Util.getMD5(sign_original_str).toUpperCase();
		sort_key.add('sign=' + sign);
		
		XMLUtil xml_util = new XMLUtil();
		def request_xml = xml_util.to_xml(sort_key);
		
		LOGGER.info(request_xml);
		
		PayResult pay_result = new PayResult();
		pay_result.paycode = config.sc_code;
		pay_result.code = "-1";
		pay_result.text = "交易失败";
		
		def ret_data = JC.remote.http_call(url, request_xml);
		LOGGER.info(ret_data);
		try {
			Map<String, String> ret_map = xml_util.to_map(ret_data);
			String return_code = ret_map.get('return_code');
			String return_msg = ret_map.get('return_msg');
			String result_code = ret_map.get('result_code');
			
			if(return_code=='SUCCESS'&&result_code=='SUCCESS') {
				//开始构建并记录交易支付数据
				String signType = "MD5";
				nonce_str = ret_map.get('nonce_str');
				def prepay_id = ret_map.get('prepay_id');
				String timestamp = System.currentTimeSeconds() + "";
				String jsapi_sign_str = "appId=" + appid + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id;
				jsapi_sign_str = jsapi_sign_str + "&signType=" + signType + "&timeStamp=" + timestamp + "&key=" + wx_pay_key;
				LOGGER.info("jsapi_sign_str==========================" + jsapi_sign_str);
				String jsapi_sign = MD5Util.getMD5(jsapi_sign_str).toUpperCase();
				
				def pay_data_map = [:];
				pay_data_map['appId'] = appid;
				pay_data_map['timeStamp'] = timestamp;
				pay_data_map['nonceStr'] = nonce_str;
				pay_data_map['signType'] = signType;
				pay_data_map['paySign'] = jsapi_sign;
				pay_data_map['package'] = 'prepay_id=' + prepay_id;
				
				pay_result.code = '0';
				pay_result.text = '交易成功';
				pay_result.other = pay_data_map;
				//trade_service.save_trade_pay_data(trade, [pay_result, pay_data_map]);
				return pay_result;
			}
			
			String err_code = ret_map.get('err_code');
			String err_code_desc = ret_map.get('err_code_des');
			if(err_code=='USERPAYING') {
				pay_result.code = '1';
				pay_result.text = err_code_desc;
			}
			pay_result.text = return_msg;
			pay_result.err_code = err_code;
			pay_result.err_code_des = err_code_desc;
		}	catch(Exception e) {
			//任意情况返回失败
			LOGGER.error('', e);
			pay_result.code = '-99';
			pay_result.text = '系统错误';
			pay_result.err_code = 'unknown';
			pay_result.err_code_des = e.message;
		}
		return pay_result;
	}
	
	def scan_mc(ProjectGeneralConfig config, def unicode, TradeInfo trade, Map<String, String> other_param) {
		PayResult pay_result = new PayResult();
		pay_result.paycode = '101001';
		pay_result.code = "-1";
		pay_result.text = "交易失败";
		
		def pwd = other_param==null?null:other_param['pwd'];
		def pid = other_param==null?null:other_param['pid'];
		List<TradeOrder> trade_orders = trade_service.trade_items(trade);
		def order_items = [];
		if(trade_orders==null||trade_orders.empty) {
			pay_result.text = '订单信息为空';
			return pay_result;
		}
		
		for(TradeOrder toi : trade_orders) {
			def real_pay_amount = toi.pay_amount;
			if(toi.tp_amount!=null) {
				real_pay_amount = toi.tp_amount;
			}
			if(toi.handle_fee) {
				real_pay_amount = real_pay_amount.add(toi.handle_fee);
			}
			if(toi.service_fee) {
				real_pay_amount = real_pay_amount.add(toi.service_fee);
			}
			order_items.add([order_no:toi.order_num,amount:real_pay_amount.toString(),oc:toi.oc]);
		}
		def json_order_items = URLEncoder.encode(JackSonBeanMapper.listToJson(order_items), "UTF-8");
		def param_dic = [card_code:unicode,pwd:pwd,os:json_order_items,pid:pid];
		TradeResult param = NativeUtil.connect(TradeResult.class, 'crm', '/api/order/deduction', param_dic);
		if(param.code!='0') {
			//交易失败
			pay_result.err_code = param.err_code;
			pay_result.err_code_des = param.err_code_des;
			return pay_result;
		}
		//扣款成功
		def pay_data_map = [:];
		pay_data_map['trans_get_account'] = param.trans_get_account;
		pay_data_map['bank_type'] = param.bank_type;
		pay_data_map['trans_type'] = param.trans_type;
		pay_data_map['trans_pay_amount'] = new BigDecimal(param.trans_pay_amount);
		pay_data_map['trans_total_amount'] = new BigDecimal(param.trans_total_amount);
		pay_data_map['trans_user_id'] = param.trans_user_id;
		pay_data_map['trans_id'] = param.trans_id;
		
		pay_result.code = '0';
		pay_result.text = '交易成功';
		
		trade_service.save_trade_pay_data(trade, [pay_result, pay_data_map]);
		return pay_result;
	}
}


