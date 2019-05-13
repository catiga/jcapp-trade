package com.jeancoder.trade.entry.outcall.wxpay

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.log.JCLoggerFactory
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.notify.NotifyService
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.JackSonBeanMapper
import com.jeancoder.trade.ready.util.XMLUtil

/**
 * ct:201       微信支付
 * ct:201001    微信支付被扫（商户扫码用户）
 * ct:201002    微信支付主扫（用户扫码商户）
 */

JCLogger LOGGER = JCLoggerFactory.getLogger('pay_notify:wxpay');

/*
def domain = JC.request.get().getServerName();
SimpleAjax now_project = JC.internal.call(SimpleAjax, 'project', '/incall/project_stand', [domain:domain]);
if(!now_project.available) {
	LOGGER.error('domain=' + domain + ', project empty');
	return null;
}

def pid = now_project.data['id'];
*/

String body = '';

try {
	StringBuffer buffer = new StringBuffer();
	BufferedReader reader = new BufferedReader(new InputStreamReader(JC.request.get().getInputStream()));
	String line = null;
	int counter = 0;
	while ((line = reader.readLine()) != null) {
		buffer.append(line);
	}
	reader.close();
	body = buffer.toString();
}catch(any) {
}

/*
def pay_code = '201101';		//统一接收微信支付通知的代码
//获取项目配置信息
ProjectGeneralConfig supp_config = JC.internal.call(ProjectGeneralConfig, 'project', '/incall/project_single_pt', [pid:pid, sc_type:'1000', sc_code:pay_code]);
if(supp_config==null||supp_config.partner==null) {
	LOGGER.error('wxpay_config_empty');
	//这里应该需要校验安全签名
	//return null;
}
*/


//打印日志
LOGGER.info("wx_pay_notify_xml:" + body);

XMLUtil xml_util = new XMLUtil();
def ret_map = xml_util.to_map(body);

def return_code = ret_map.get('return_code');
def return_msg = ret_map.get('return_msg');
def result_code = ret_map.get('result_code');

def return_to_weixin_code = "FAIL";	//SUCCESS
def return_to_weixin_msg = "FAIL";	//OK

if(return_code=='SUCCESS'&&result_code=='SUCCESS') {
	def appid = ret_map.get('appid');
	def mch_id = ret_map.get('mch_id');
	
	def bank_type = ret_map.get("bank_type");
	def total_fee = ret_map.get("total_fee");
	def out_trade_no = ret_map.get("out_trade_no");
	def transaction_id = ret_map.get("transaction_id");
	def sign = ret_map.get('sign');						//需要校验签名
	
	TradeInfo trade = TradeService.INSTANCE().get_trade_by_num(out_trade_no);
	if(trade==null) {
		//说明没有找到交易
		LOGGER.error("wx_pay_notify: could not find trade by num===" + out_trade_no);
	}
	def pid = trade.pid;
	def pay_code = '201';	//模糊
	if(trade.pay_type==null) {
		SimpleAjax pay_wrapper = JC.internal.call(SimpleAjax, 'project', '/incall/project_pts', [pid:pid,tyc:'1000']);
		if(pay_wrapper.available) {
			for(x in pay_wrapper.data) {
				if(x['partner']==appid) {
					pay_code = x['sc_code'];	//找到准确的支付方式
					break;
				}
			}
		}
	} else {
		pay_code = trade.pay_type
	}
	
	def pay_data_map = [:];
	pay_data_map['trans_get_account'] = ret_map.get('mch_id');
	pay_data_map['bank_type'] = ret_map.get('bank_type');
	pay_data_map['trans_type'] = ret_map.get('trade_type');
	pay_data_map['trans_pay_amount'] = new BigDecimal(ret_map.get('cash_fee'));
	pay_data_map['trans_total_amount'] = new BigDecimal(ret_map.get('total_fee'));
	pay_data_map['trans_user_id'] = ret_map.get('openid');
	pay_data_map['trans_id'] = ret_map.get('transaction_id');
	
	PayResult pay_result = new PayResult();
	pay_result.code = '0';
	pay_result.text = '交易成功';
	pay_result.paycode = pay_code;
	
	TradeService.INSTANCE().save_trade_pay_data(trade, [pay_result, pay_data_map]);
	
	return_to_weixin_code = 'SUCCESS';
	return_to_weixin_msg = 'OK';
	
	//def op_errors = NotifyService.pub(trade);
//		def op_errors = NotifyService.incallPub(trade);
//		if(!op_errors.empty) {
//			println JackSonBeanMapper.listToJson(op_errors);
//		}
	
	//改为异步方法
	JC.thread.run(60*1000, {
		def op_errors = NotifyService.incallPub(trade);
		//if(op_errors && !op_errors.empty) {
		if(op_errors) {
			LOGGER.error(out_trade_no + ':::wx_pay_op_process_pub_ticket:::' + JackSonBeanMapper.toJson(op_errors));
		}
	}, {
		e->
		LOGGER.info('wx_pay_op_pub_result:' + JackSonBeanMapper.toJson(e));
	})
}

return build_return_xml(return_to_weixin_code, return_to_weixin_msg);


def build_return_xml(String code, String msg) {
	String return_xml = "<xml><return_code><![CDATA[" + code + "]]></return_code><return_msg><![CDATA[" + msg + "]]></return_msg></xml>";
	return return_xml;
}

