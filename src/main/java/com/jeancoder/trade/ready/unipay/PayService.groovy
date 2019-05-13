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
import com.jeancoder.trade.ready.util.AlipayResponse
import com.jeancoder.trade.ready.util.AlipayUtil
import com.jeancoder.trade.ready.util.DateUtil
import com.jeancoder.trade.ready.util.MD5Util
import com.jeancoder.trade.ready.util.NativeUtil
import com.jeancoder.trade.ready.util.XMLUtil


class PayService {

	private static final PayService _instance_ = new PayService();
	
	JCLogger LOGGER = JCLoggerFactory.getLogger(PayService.class.getName());
	
	TradeService trade_service = TradeService.INSTANCE();
	
	private PayService() {}
	
	public static PayService INSTANCE() {
		return _instance_;
	}
	
	
	def scan_pay_router(ProjectGeneralConfig config, def ct, def unicode, TradeInfo trade, Map<String, String> other_param) {
		PayResult pay_result = new PayResult();
		pay_result.code = "-1";
		pay_result.text = "不支持的支付方式";
		pay_result.err_code = 'unsupport_pay_code';
		pay_result.paycode = ct;
		
		if(ct=='201001') {
			//微信支付被扫
			pay_result = scan_wxpay(config, ct, unicode, trade);
		} else if(ct=='202001') {
			//支付宝被扫
			pay_result = scan_alipay(config, ct, unicode, trade);
		} else if(ct=='101001') {
			//会员卡被扫
			pay_result = scan_mc(config, ct, unicode, trade, other_param);
		} else if(ct=='001001') {
			//现金支付
			pay_result = scan_cash(config, ct, unicode, trade,other_param);
		} else if(ct.toString().startsWith('900')) {
			//记账支付方式
			pay_result = scan_record(config, ct, unicode, trade, other_param);
		}
		return pay_result;
	}
	
	def scan_cash(ProjectGeneralConfig config, def ct, def unicode, TradeInfo trade, Map<String, String> other_param) {
		PayResult pay_result = new PayResult();
		pay_result.paycode = '001001';
		pay_result.code = '0';
		pay_result.text = '交易成功';
		def cash_col = other_param['cash_col'];
		//现金则直接为扣款成功
		def pay_data_map = [:];
		pay_data_map['trans_get_account'] = config.partner;			//现金收款账户直接写死为钱箱
		pay_data_map['bank_type'] = 'CASH';
		pay_data_map['trans_type'] = 'CASH';
		pay_data_map['trans_pay_amount'] = trade.pay_amount;
		pay_data_map['trans_total_amount'] = trade.total_amount;
		pay_data_map['trans_user_id'] = null;
		pay_data_map['trans_id'] = trade.tnum;
		pay_data_map['cash_col'] = cash_col;
		trade_service.save_trade_pay_data(trade, [pay_result, pay_data_map]);
		return pay_result;
	}
	
	def scan_mc(ProjectGeneralConfig config, def ct, def unicode, TradeInfo trade, Map<String, String> other_param) {
		PayResult pay_result = new PayResult();
		pay_result.paycode = '101001';
		pay_result.code = "-1";
		pay_result.text = "交易失败";
		
		def pwd = other_param==null?null:other_param['pwd'];
		
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
			order_items.add([order_no:toi.order_num,amount:real_pay_amount.toString(),oc:toi.oc]);
		}
		def json_order_items = URLEncoder.encode(JackSonBeanMapper.listToJson(order_items), "UTF-8");
		def param_dic = [card_code:unicode,pwd:pwd,os:json_order_items];
		//TradeResult param = RemoteUtil.connect(TradeResult.class, 'crm', '/api/order/deduction', param_dic);
		
		param_dic['pid'] = other_param['pid']
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
	
	def scan_alipay(ProjectGeneralConfig config, def ct, def unicode, TradeInfo trade) {
		//def app_id = AlipayUtil.app_id;
		//def seller_id = AlipayUtil.seller_id;
		def app_id = config.partner;
		def seller_id = config.mchid;
		
		//public param begin
		def head = [:]; def body = [:];
		head['app_id'] = app_id;
		head['method'] = 'alipay.trade.pay';
		head['format'] = 'JSON';
		head['charset'] = 'utf-8';
		head['sign_type'] = 'RSA2';
		//head.add(['sign', sign]);
		head['timestamp'] = DateUtil.format(Calendar.getInstance().getTime(), 'yyyy-MM-dd HH:mm:ss');
		head['version'] = '1.0';


		body['out_trade_no'] = trade.tnum;
		body['scene'] = 'bar_code';
		body['auth_code'] = unicode;
		body['subject'] = '好时光消费';
		body['seller_id'] = seller_id;
		body['total_amount'] = trade.pay_amount.divide(new BigDecimal('100')).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		
		AlipayUtil au = new AlipayUtil(app_id:app_id,seller_id:seller_id,alipay_pub_key:config.pub_key,app_private_key:config.pri_key);
		au.head = head; au.body = body;
		
		def url = au.request();
		def ret_data = JC.remote.http_call(url, null);
		LOGGER.info("ret_data_"+ret_data)
		AlipayResponse ar = new AlipayResponse(ret_data);
		Map<String, String> ret_map = ar.get_result();
		String return_code = ret_map['alipay_trade_pay_response']['code'];
		String return_msg = ret_map['alipay_trade_pay_response']['msg'];
		
		PayResult pay_result = new PayResult();
		pay_result.paycode = '202001';
		pay_result.code = "-1";
		pay_result.err_code = return_code;
		pay_result.err_code_des = return_msg;
		pay_result.text = return_msg;
		
		if(return_code=='10000') {
			pay_result.code = '0';
			pay_result.text = '交易成功';
			
			def code = ret_map['alipay_trade_pay_response']['code'];
			def alipay_account_name = ret_map['alipay_trade_pay_response']['buyer_logon_id'];
			def pay_amount = ret_map['alipay_trade_pay_response']['buyer_pay_amount'];
			def buyer_user_id = ret_map['alipay_trade_pay_response']['buyer_user_id'];
			def fund_bill_list = ret_map['alipay_trade_pay_response']['fund_bill_list'];
			def pay_time = ret_map['alipay_trade_pay_response']['gmt_payment'];
			def invoice_amount = ret_map['alipay_trade_pay_response']['invoice_amount'];
			def get_amount = ret_map['alipay_trade_pay_response']['receipt_amount'];
			def total_amount = ret_map['alipay_trade_pay_response']['total_amount'];
			def trans_id = ret_map['alipay_trade_pay_response']['trade_no'];
			def sign = ret_map['sign'];
			
			def pay_data_map = [:];
			pay_data_map['trans_get_account'] = seller_id;
			pay_data_map['bank_type'] = 'ALIPAYACCOUNT';
			pay_data_map['trans_type'] = 'ALIPAYACCOUNT';
			pay_data_map['trans_pay_amount'] = new BigDecimal(invoice_amount).multiply(new BigDecimal(100));				//现金部分为发票金额
			pay_data_map['trans_total_amount'] = new BigDecimal(get_amount).multiply(new BigDecimal(100));
			pay_data_map['trans_user_id'] = buyer_user_id;
			pay_data_map['trans_id'] = trans_id;
			
			trade_service.save_trade_pay_data(trade, [pay_result, pay_data_map]);
			return pay_result;
		}
		
		return pay_result;
	}
	
	def scan_wxpay(ProjectGeneralConfig config, def ct, def unicode, TradeInfo trade) {
		def url = 'https://api.mch.weixin.qq.com/pay/micropay';
		def sign_type = 'MD5';
		def fee_type = 'CNY';
		def spbill_create_ip = '166.111.4.100';
		def auth_code = unicode;
		
		def appid = config.partner;
		def mch_id = config.mchid;
		def wx_pay_key = config.pri_key;
		
		def device_info = trade.pid;
		def nonce_str = MD5Util.getMD5(System.currentTimeMillis() + '');
		def body = '微信支付';
		def attach = '好时光消费';
		def out_trade_no = trade.tnum + '';
		def total_fee = trade.pay_amount.intValue() + '';
		def time_start = DateUtil.format(trade.a_time);
		Calendar c = Calendar.getInstance();
		c.setTime(trade.a_time);
		c.add(Calendar.DAY_OF_MONTH, 15);
		def time_expire = DateUtil.format(c.getTime());
		
		def sort_key = [];
		sort_key.add("appid=" + appid);
		sort_key.add("mch_id=" + mch_id);
		sort_key.add("device_info=" + device_info);
		sort_key.add("nonce_str=" + nonce_str);
		sort_key.add("sign_type=" + sign_type);
		sort_key.add("body=" + body);
		//sort_key.add("detail=" + detail);
		sort_key.add("attach=" + attach);
		sort_key.add("out_trade_no=" + out_trade_no);
		
		sort_key.add("total_fee=" + total_fee);
		sort_key.add("fee_type=" + fee_type);
		sort_key.add("spbill_create_ip=" + spbill_create_ip);
		//sort_key.add("goods_tag=" + goods_tag);
		//sort_key.add("limit_pay=" + limit_pay);
		sort_key.add("time_start=" + time_start);		//订单开始时间  YYYYMMDDHHMMSS
		sort_key.add("time_expire=" + time_expire);		//订单失效时间  YYYYMMDDHHMMSS
		sort_key.add("auth_code=" + auth_code);
		
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
		pay_result.paycode = '201001';
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
				def pay_data_map = [:];
				pay_data_map['trans_get_account'] = mch_id;
				pay_data_map['bank_type'] = ret_map.get('bank_type');
				pay_data_map['trans_type'] = ret_map.get('trade_type');
				pay_data_map['trans_pay_amount'] = new BigDecimal(ret_map.get('cash_fee'));
				pay_data_map['trans_total_amount'] = new BigDecimal(ret_map.get('total_fee'));
				pay_data_map['trans_user_id'] = ret_map.get('openid');
				pay_data_map['trans_id'] = ret_map.get('transaction_id');
				
				pay_result.code = '0';
				pay_result.text = '交易成功';
				
				def return_trade_info = trade_service.save_trade_pay_data(trade, [pay_result, pay_data_map]);
				//把交易信息返回到前台
				pay_result.bzdata = return_trade_info;
				pay_result.user_code = ret_map.get('openid');
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
	
	
	def scan_record(ProjectGeneralConfig config, def ct, def unicode, TradeInfo trade, Map<String, String> other_param) {
		PayResult pay_result = new PayResult();
		pay_result.paycode = ct;
		pay_result.code = '0';
		pay_result.text = '交易成功';
		
		//自定义支付方式则直接为扣款成功
		def pay_data_map = [:];
		pay_data_map['trans_get_account'] = config.partner;			//配置的收款信息
		pay_data_map['bank_type'] = '900';
		pay_data_map['trans_type'] = '900';
		pay_data_map['trans_pay_amount'] = trade.pay_amount;
		pay_data_map['trans_total_amount'] = trade.total_amount;
		pay_data_map['trans_user_id'] = unicode;
		pay_data_map['trans_id'] = trade.tnum;
		
		trade_service.save_trade_pay_data(trade, [pay_result, pay_data_map]);
		return pay_result;
	}
	
}


