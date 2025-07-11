package com.jeancoder.trade.ready.unipay

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.remote.RequestCert
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.log.JCLoggerFactory
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.dto.TradeResult
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.AlipayUtil
import com.jeancoder.trade.ready.util.DateUtil
import com.jeancoder.trade.ready.util.JackSonBeanMapper
import com.jeancoder.trade.ready.util.MD5Util
import com.jeancoder.trade.ready.util.XMLUtil

class RefundService {

	JCLogger LOGGER = JCLoggerFactory.getLogger(RefundService.class.getName());
	
	static final RefundService _instance_ = new RefundService();
	
	TradeService trade_service = TradeService.INSTANCE();
	
	static RefundService INSTANCE() {
		return _instance_;
	}
	
	def refund_pay_router(ProjectGeneralConfig config, def ct, TradeInfo trade, TradePayInfo pay_info, Map<String, String> other_param) {
		PayResult pay_result = new PayResult();
		pay_result.code = "-1";
		pay_result.text = "不支持该支付方式退款操作";
		pay_result.err_code = 'unsupport_pay_code';
		pay_result.paycode = ct;
		
		if(ct=='201101') {
			//微信支付公众号
			other_param.put("ctcode", "201101");
			return refund_wx(config, trade, pay_info, other_param);
		} else if(ct=='201001') {
			other_param.put("ctcode", "201001");
			return refund_wx(config, trade, pay_info, other_param);
		} else if(ct=='201002') {
			other_param.put("ctcode", "201002");
			return refund_wx(config, trade, pay_info, other_param);
		} else if(ct=='201102') {
			//微信支付小程序
			other_param.put("ctcode", "201102");
			return refund_wx(config, trade, pay_info, other_param);
		} else if(ct=='101001') {
			other_param.put("ctcode", "101001");
			//会员卡被扫
			return refund_mc(config, trade, pay_info, other_param);
		} else if(ct=='001001') {
			other_param.put("ctcode", "001001");
			//现金支付
			pay_result = refund_cash(config, trade, pay_info, other_param);
		} else if(ct=='202001') {
			//支付宝被扫退款
			other_param.put('ctcode', '202001');
			pay_result = refund_alipay(config, trade, pay_info, other_param);
		} else if(ct.toString().startsWith('900')){
			//按照自定义支付方式退款，先进退款
			other_param.put("ctcode", ct);
			//现金支付
			pay_result = refund_cash(config, trade, pay_info, other_param);
		}
		return pay_result;
	}
	
	def refund_wx(ProjectGeneralConfig config, TradeInfo trade, TradePayInfo pay_info, Map<String, String> other_param) {
		PayResult pay_result = new PayResult();
		pay_result.paycode = other_param.get('ctcode');
		pay_result.code = '0';
		pay_result.text = '退款成功';
		
		def wx_refund_url = 'https://api.mch.weixin.qq.com/secapi/pay/refund';
		def refund_notify_url = 'https://' + other_param.get('domain')+'/trade/outcall/wxpay/refund_notify';
		
		def out_trade_refund_no = '950' + trade.tnum.substring(2);
		def data = [:];
		data.put("app_id", config.partner);
		data.put('mch_id', config.mchid);
		data.put('nonce_str', MD5Util.getMD5(new Random().nextFloat() + ''));
		data.put('transaction_id', pay_info.trans_id)
		data.put('out_refund_no', out_trade_refund_no)
		data.put('total_fee', pay_info.trans_total_amount.setScale(0).toString());
		data.put('refund_fee', pay_info.trans_pay_amount.setScale(0).toString());
		data.put('notify_url', refund_notify_url);
		
		String cert_file = config.rb_file;
		String cert_pass = config.rb_kp;
		String cert_type = config.rb_key_format;
		LOGGER.info("cert type: ", cert_type, " cert_passwd: ", cert_pass, " cert file path: ", cert_file)
		RequestCert cert_obj = new RequestCert(cert_type:cert_type,cert_passwd:cert_pass,cert_file_path:cert_file);
		
		String app_id = data.get("app_id");
		String mch_id = data.get("mch_id");
		String nonce_str = data.get('nonce_str');
		String transaction_id = data.get('transaction_id');
		String out_refund_no = data.get("out_refund_no");
		String total_fee = data.get("total_fee");
		String refund_fee = data.get("refund_fee");
		String notify_url = data.get('notify_url');
		
		String wx_pay_key = config.pri_key;
		
		def param = [];
		param.add("appid=" + app_id);
		param.add("mch_id=" + mch_id);
		param.add("nonce_str=" + nonce_str);
		param.add("transaction_id=" + transaction_id);
		param.add("out_refund_no=" + out_refund_no);
		param.add("total_fee=" + total_fee);
		param.add("refund_fee=" + refund_fee);
		param.add('notify_url=' + notify_url);
		
		Collections.sort(param);
		StringBuffer buffer = new StringBuffer();
		for(String s : param) {
			buffer.append(s + "&");
		}
		buffer.append("key=" + wx_pay_key);
		String sign = MD5Util.getMD5(buffer.toString()).toUpperCase();
		param.add('sign=' + sign);
		
		XMLUtil xml_util = new XMLUtil();
		def request_xml = xml_util.to_xml(param);
		
		LOGGER.info('wx_pay_refund request_xml: {}' + request_xml);

		LOGGER.info("wx_refund_url: ", wx_refund_url)
		def ret_data = JC.remote.http_call(wx_refund_url, request_xml, cert_obj);
		LOGGER.info('退款返回提示：{}', ret_data);
			
		try {
			Map<String, String> ret_map = xml_util.to_map(ret_data);
			String return_code = ret_map.get('return_code');
			String return_msg = ret_map.get('return_msg');
			String result_code = ret_map.get('result_code');
			
			if(return_code=='SUCCESS'&&result_code=='SUCCESS') {
				//退款成功
				def pay_data_map = [:];
				pay_data_map['trans_get_account'] = pay_info.trans_get_account;
				pay_data_map['bank_type'] = pay_info.bank_type;
				pay_data_map['trans_type'] = pay_info.trans_type;
				pay_data_map['trans_pay_amount'] = new BigDecimal(ret_map['refund_fee']);
				pay_data_map['trans_total_amount'] = pay_info.trans_total_amount;
				pay_data_map['trans_user_id'] = pay_info.trans_user_id;
				pay_data_map['trans_id'] = ret_map['refund_id'];					//退款流水号
				
				pay_result.code = '0';
				pay_result.text = '交易成功';
				pay_result.other = pay_data_map;
				trade_service.save_trade_refund_data(trade, [pay_result, pay_data_map]);
				return pay_result;
			}
			
			String err_code = ret_map.get('err_code');
			String err_code_desc = ret_map.get('err_code_des');
			
			pay_result.text = return_msg + return_code;
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
	
	def refund_cash(ProjectGeneralConfig config, TradeInfo trade, TradePayInfo pay_info, Map<String, String> other_param) {
		PayResult pay_result = new PayResult();
		//pay_result.paycode = '001001';
		pay_result.paycode = other_param.get('ctcode');
		pay_result.code = '0';
		pay_result.text = '退款成功';
		
		//现金则直接为扣款成功
		def pay_data_map = [:];
		pay_data_map['trans_get_account'] = config.partner;			//现金收款账户直接写死为钱箱
		pay_data_map['bank_type'] = 'CASH';
		pay_data_map['trans_type'] = 'CASH';
		pay_data_map['trans_pay_amount'] = trade.pay_amount;
		pay_data_map['trans_total_amount'] = trade.total_amount;
		pay_data_map['trans_user_id'] = null;
		pay_data_map['trans_id'] = trade.tnum;
		
		trade_service.save_trade_refund_data(trade, [pay_result, pay_data_map]);
		return pay_result;
	}
	
	def refund_mc(ProjectGeneralConfig config, TradeInfo trade, TradePayInfo pay_info, Map<String, String> other_param) {
		PayResult pay_result = new PayResult();
		//pay_result.paycode = '101001';
		pay_result.paycode = other_param.get('ctcode');
		pay_result.code = "-1";
		pay_result.text = "交易失败";
		
		if(!trade.tss.startsWith("1")) {
			pay_result.text = "交易状态错误";
			pay_result.err_code = "trade_status_invalid";
			return pay_result;
		}
		
		if(pay_info==null) {
			pay_result.text = "交易付款信息为空";
			pay_result.err_code = "pay_info_empty";
			return pay_result;
		}
		
		def pid = other_param['pid'];
		def num = other_param['trans_id'];
		def param_dic = [pid:pid,num:num];
		
		TradeResult param = JC.internal.call(TradeResult.class, 'crm', '/api/order/refund', param_dic);
		
		if(param.code!='0') {
			//交易失败
			pay_result.err_code = param.err_code;
			pay_result.err_code_des = param.err_code_des;
			return pay_result;
		}
		//退款成功
		def pay_data_map = [:];
		pay_data_map['trans_get_account'] = pay_info.trans_get_account;
		pay_data_map['bank_type'] = pay_info.bank_type;
		pay_data_map['trans_type'] = pay_info.trans_type;
		pay_data_map['trans_pay_amount'] = pay_info.trans_pay_amount;
		pay_data_map['trans_total_amount'] = pay_info.trans_total_amount;
		pay_data_map['trans_user_id'] = pay_info.trans_user_id;
		pay_data_map['trans_id'] = param.trans_id;											//退款流水号
		
		pay_result.code = '0';
		pay_result.text = '交易成功';
		
		trade_service.save_trade_refund_data(trade, [pay_result, pay_data_map]);
		return pay_result;
	}
	
	def refund_alipay(ProjectGeneralConfig config, TradeInfo trade, TradePayInfo pay_info, Map<String, String> other_param) {
		def app_id = config.partner;
		def seller_id = config.mchid;
		
		//public param begin
		def head = [:]; def body = [:];
		head['app_id'] = app_id;
		head['method'] = 'alipay.trade.refund';		//退款交易接口
		head['format'] = 'JSON';
		head['charset'] = 'utf-8';
		head['sign_type'] = 'RSA2';
		//head.add(['sign', sign]);
		head['timestamp'] = DateUtil.format(Calendar.getInstance().getTime(), 'yyyy-MM-dd HH:mm:ss');
		head['version'] = '1.0';


		body['out_trade_no'] = trade.tnum;
		body['refund_amount'] = trade.pay_amount.divide(new BigDecimal('100')).setScale(2, BigDecimal.ROUND_HALF_UP).toString();	//整单退款金额
		
		AlipayUtil au = new AlipayUtil(app_id:app_id,seller_id:seller_id,alipay_pub_key:config.pub_key,app_private_key:config.pri_key);
		au.head = head; au.body = body;
		
		def url = au.request();
		def ret_data = JC.remote.http_call(url, null);
		LOGGER.info('alipay_refund_data:' + ret_data);
		
		//AlipayResponse ar = new AlipayResponse(ret_data);
		//Map<String, String> ret_map = ar.get_result();
		
		Map<String, String> ret_map = JackSonBeanMapper.jsonToMap(ret_data);
		
		String return_code = ret_map['alipay_trade_refund_response']['code'];
		String return_msg = ret_map['alipay_trade_refund_response']['msg'];
		
		PayResult pay_result = new PayResult();
		pay_result.paycode = '202001';
		pay_result.code = "-1";
		pay_result.err_code = return_code;
		pay_result.err_code_des = return_msg;
		pay_result.text = return_msg;
		
		if(return_code=='10000') {
			pay_result.code = '0';
			pay_result.text = '交易成功';
			
			def code = ret_map['alipay_trade_refund_response']['code'];
			def refund_fee = ret_map['alipay_trade_refund_response']['refund_fee'];
			def buyer_logon_id = ret_map['alipay_trade_refund_response']['buyer_logon_id'];
			def trans_id = ret_map['alipay_trade_refund_response']['trade_no'];
			def buyer_user_id = ret_map['alipay_trade_refund_response']['buyer_user_id'];
			def refund_settlement_id = ret_map['alipay_trade_refund_response']['refund_settlement_id'];
			
			def sign = ret_map['sign'];
			
			def pay_data_map = [:];
			pay_data_map['trans_get_account'] = pay_info.trans_get_account;
			pay_data_map['bank_type'] = pay_info.bank_type;
			pay_data_map['trans_type'] = pay_info.trans_type;
			pay_data_map['trans_pay_amount'] = new BigDecimal(refund_fee).multiply(new BigDecimal(100));				//现金部分为发票金额
			pay_data_map['trans_total_amount'] = new BigDecimal(refund_fee).multiply(new BigDecimal(100));
			pay_data_map['trans_user_id'] = buyer_user_id;
			pay_data_map['trans_id'] = trans_id;
			
			trade_service.save_trade_refund_data(trade, [pay_result, pay_data_map]);
			return pay_result;
		}
		
		return pay_result;
	}
	
}
