package com.jeancoder.trade.internal.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.log.JCLoggerFactory
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.notify.NotifyService
import com.jeancoder.trade.ready.printer.FeedPrinter
import com.jeancoder.trade.ready.printer.PrinterUtil
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.ser.TriggerService
import com.jeancoder.trade.ready.util.AlipayResponse
import com.jeancoder.trade.ready.util.AlipayUtil
import com.jeancoder.trade.ready.util.DateUtil
import com.jeancoder.trade.ready.util.MD5Util
import com.jeancoder.trade.ready.util.XMLUtil

JCLogger logger = JCLoggerFactory.getLogger('pay_result_query');

def tnum = JC.internal.param('tnum');
def token = JC.internal.param('token');
def pid = JC.internal.param('pid');

TradeService trade_service = TradeService.INSTANCE();
TriggerService trigger_service = TriggerService.INSTANCE();

TradeInfo trade = trade_service.get_trade_by_num(tnum);

if(trade==null) {
	return SimpleAjax.notAvailable('trade_not_found,交易未找到');
}

List<TradeOrder> trade_orders = TradeService.INSTANCE().trade_items(trade);
//获取trade的支付信息
List<TradePayInfo> trade_pays = trade_service.get_trade_pay(trade);
if(trade_pays != null && !trade_pays.isEmpty()) {
	TradePayInfo info = trade_pays.get(0);
	PayResult pay_result = new PayResult();
	pay_result.paycode =  info.pay_code;
	pay_result.code = '0';
	pay_result.text = '交易成功';
	//把交易信息返回到前台
	pay_result.bzdata = info;
	pay_result.user_code = info.trans_user_id;
	
	def ccid = null;
	if (trade.log_id != null)  {
		CashDoLog log = CashierService.INSTANCE.get_counter_by_token(trade.log_id);
		ccid = log.ccid;
	}
	//获取打印模板
	def smarttemplate = "<html></html>";
	for(x in trade_orders) {
		if(x.oc=='1000') {
			x = TradeService.INSTANCE().getTradeById(x.id)
			smarttemplate = new FeedPrinter().get_printer_content(trade, x).toString();
			smarttemplate = URLEncoder.encode(smarttemplate, 'UTF-8');
			smarttemplate = URLEncoder.encode(smarttemplate, 'UTF-8');
			smarttemplate = PrinterUtil.getPrinterDtoLsit(pid,ccid, x.oc, smarttemplate);
		}
	}
	pay_result.smarttemplet = smarttemplate;
	//该交易有付款信息
	return SimpleAjax.available('', [trade, pay_result]);
}

if(!trade.tss.startsWith('0')) {
	return SimpleAjax.notAvailable('status_invalid,交易状态非法');
}



//查询支付宝支付或微信支付接口的订单付款信息
//查询付款配置信息
def ct_wxpay = '201001';  def ct_alipay = '202001';
def aim_pid = trade.pid;
//首先查询微信支付
ProjectGeneralConfig supp_config = JC.internal.call(ProjectGeneralConfig, 'project', '/incall/project_single_pt', [sc_type:'1000',sc_code:ct_wxpay,pid:pid]);
if(supp_config==null||supp_config.partner==null) {
	return SimpleAjax.notAvailable('pay_config_error,项目支付方式配置错误');
}

PayResult ret_data = query_wxpay_result(supp_config, trade);
logger.info("pay_result_query_"+JackSonBeanMapper.toJson(ret_data))
if(ret_data==null  || ret_data.code!='0') {
	//这时查询支付宝交易
	supp_config = JC.internal.call(ProjectGeneralConfig, 'project', '/incall/project_single_pt', [sc_type:'1000',sc_code:ct_alipay,pid:pid]);
	if(supp_config==null||supp_config.partner==null) {
		return SimpleAjax.notAvailable('pay_config_error,项目支付方式配置错误');
	}
	ret_data = query_alipay_result(supp_config, trade,logger);
}

if(ret_data == null || ret_data.code!='0') {
	return SimpleAjax.notAvailable('no_pay_info,该交易没有付款信息');
}

def ccid = null;
if (trade.log_id != null)  {
	CashDoLog log = CashierService.INSTANCE.get_counter_by_token(trade.log_id);
	ccid = log.ccid;
}
//获取打印模板
def smarttemplate = "<html></html>";
for(x in trade_orders) {
	if(x.oc=='1000') {
		x = TradeService.INSTANCE().getTradeById(x.id)
		smarttemplate = new FeedPrinter().get_printer_content(trade, x).toString();
		smarttemplate = URLEncoder.encode(smarttemplate, 'UTF-8');
		smarttemplate = URLEncoder.encode(smarttemplate, 'UTF-8');
		smarttemplate = PrinterUtil.getPrinterDtoLsit(pid,ccid, x.oc, smarttemplate);

	}
}
ret_data.smarttemplet = smarttemplate;

def op_errors = NotifyService.incallPub(trade);
if(!op_errors.empty) {
	ret_data.other = op_errors;
	ret_data.text = ret_data.text + '.库存或出票信息操作失败，请检查订单详情';
}
//return ret_data;
return SimpleAjax.available('', [trade, ret_data]);





def query_wxpay_result(ProjectGeneralConfig config, TradeInfo trade) {
	def appid = config.partner;
	def mch_id = config.mchid;
	def out_trade_no = trade.tnum;
	def nonce_str = MD5Util.getMD5(System.currentTimeMillis() + '');
	def sign_type = 'MD5';
	
	def wx_pay_key = config.pri_key;
	
	def sort_key = [];
	sort_key.add("appid=" + appid);
	sort_key.add("mch_id=" + mch_id);
	sort_key.add("nonce_str=" + nonce_str);
	sort_key.add("sign_type=" + sign_type);
	sort_key.add("out_trade_no=" + out_trade_no);
	
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
	
	def ret_data = JC.remote.http_call('https://api.mch.weixin.qq.com/pay/orderquery', request_xml);
	
	Map<String, String> ret_map = xml_util.to_map(ret_data);
	String return_code = ret_map.get('return_code');
	String trade_state = ret_map.get('trade_state');
	String result_code = ret_map.get('result_code');
	
	if(return_code=='SUCCESS'&&result_code=='SUCCESS'&&trade_state=='SUCCESS') {
		PayResult pay_result = new PayResult();
		pay_result.paycode = '201001';
		pay_result.code = '0';
		pay_result.text = '交易成功';
		
		//交易成功
		def pay_data_map = [:];
		pay_data_map['trans_get_account'] = mch_id;
		pay_data_map['bank_type'] = ret_map.get('bank_type');
		pay_data_map['trans_type'] = ret_map.get('trade_type');
		pay_data_map['trans_pay_amount'] = new BigDecimal(ret_map.get('cash_fee'));
		pay_data_map['trans_total_amount'] = new BigDecimal(ret_map.get('total_fee'));
		pay_data_map['trans_user_id'] = ret_map.get('openid');
		pay_data_map['trans_id'] = ret_map.get('transaction_id');
		
		def return_trade_info = TradeService.INSTANCE().save_trade_pay_data(trade, [pay_result, pay_data_map]);
		
		//把交易信息返回到前台
		pay_result.bzdata = return_trade_info;
		pay_result.user_code = ret_map.get('openid');
		return pay_result;
	}
	return null;
}

def query_alipay_result(ProjectGeneralConfig config, TradeInfo trade,def logger) {
	def app_id = config.partner;
	def seller_id = config.mchid;
	
	//public param begin
	def head = [:]; def body = [:];
	head['app_id'] = app_id;
	head['method'] = 'alipay.trade.query';
	head['format'] = 'JSON';
	head['charset'] = 'utf-8';
	head['sign_type'] = 'RSA2';
	//head.add(['sign', sign]);
	head['timestamp'] = DateUtil.format(Calendar.getInstance().getTime(), 'yyyy-MM-dd HH:mm:ss');
	head['version'] = '1.0';


	body['out_trade_no'] = trade.tnum;
	
	AlipayUtil au = new AlipayUtil(app_id:app_id,seller_id:seller_id,alipay_pub_key:config.pub_key,app_private_key:config.pri_key);
	au.head = head; au.body = body;
	
	def url = au.request();
	def ret_data = JC.remote.http_call(url, null);
	AlipayResponse ar = new AlipayResponse(ret_data);
	Map<String, String> ret_map = ar.get_alipay_query_result();
	String return_code = ret_map['alipay_trade_query_response']['code'];
	String return_msg = ret_map['alipay_trade_query_response']['msg'];
	String trade_status = ret_map['alipay_trade_query_response']['trade_status'];
	if(return_code=='10000'&&trade_status=='TRADE_SUCCESS') {
		PayResult pay_result = new PayResult();
		pay_result.paycode = '202001';
		pay_result.code = '0';
		pay_result.text = '交易成功';
		
		def alipay_account_name = ret_map['alipay_trade_query_response']['buyer_logon_id'];
		def pay_amount = ret_map['alipay_trade_query_response']['buyer_pay_amount'];
		def buyer_user_id = ret_map['alipay_trade_query_response']['buyer_user_id'];
		def fund_bill_list = ret_map['alipay_trade_query_response']['fund_bill_list'];
		def invoice_amount = ret_map['alipay_trade_query_response']['invoice_amount'];
		def get_amount = ret_map['alipay_trade_query_response']['receipt_amount'];
		def total_amount = ret_map['alipay_trade_query_response']['total_amount'];
		def trans_id = ret_map['alipay_trade_query_response']['trade_no'];
		def sign = ret_map['sign'];
		
		def pay_data_map = [:];
		pay_data_map['trans_get_account'] = seller_id;
		pay_data_map['bank_type'] = 'ALIPAYACCOUNT';
		pay_data_map['trans_type'] = 'ALIPAYACCOUNT';
		pay_data_map['trans_pay_amount'] = new BigDecimal(invoice_amount).multiply(new BigDecimal(100));				//现金部分为发票金额
		pay_data_map['trans_total_amount'] = new BigDecimal(get_amount).multiply(new BigDecimal(100));
		pay_data_map['trans_user_id'] = buyer_user_id;
		pay_data_map['trans_id'] = trans_id;
		
		TradeService.INSTANCE().save_trade_pay_data(trade, [pay_result, pay_data_map]);
		return pay_result;
	}
	return null;
}

