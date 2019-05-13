package com.jeancoder.trade.entry.outcall.ipaynow

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.core.util.MD5Util
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TriggerRog
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.ser.TriggerService
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.RemoteUtil
import com.jeancoder.trade.ready.util.XMLUtil

import groovy.json.JsonSlurper

def pay_code = '301001';
//获取项目配置信息
ProjectGeneralConfig supp_config = RemoteUtil.connect(ProjectGeneralConfig, 'project', '/incall/project_single_pt', [sc_type:'1000', sc_code:pay_code]);
if(supp_config==null||supp_config.partner==null) {
	return null;
}

StringBuffer body = new StringBuffer();

try {
	//需要从request输入流中获取
	BufferedReader reader = new BufferedReader(new InputStreamReader(JC.request.get().getInputStream()));
	String line = null;
	int counter = 0;
	while ((line = reader.readLine()) != null) {
		if (counter > 0) {
			body.append("rn");
		}
		body.append(line);
		counter++;
	}
	reader.close();
}catch(any) {
	println any;
}

XMLUtil xml_util = new XMLUtil();
def map = xml_util.to_map(body.toString());

def protocol_sign_key = supp_config.pri_key;			//这里写死即可
def mid = supp_config.mchid;

// 定义返回结果公共信息
def sort_key = [];
sort_key.add('order_type=S');
sort_key.add('mid=' + mid);

if(map.empty) {
	sort_key.add('order_id=0');
	sort_key.add('resp_code=10');
	sort_key.add('resp_msg=数据解析错误');
	sort_key.add('order_amt=0');
	sort_key.add('order_desc=0');
	sort_key.add('tid=0');
} else {
	def order_num = map.get('order_id');//订单编号，实际上对应的应该为我们的交易编号
	def mch_id = map.get('mid');		//商户号
	def tid = map.get('tid');			//终端编号
	def systrace = map.get('systrace');	//终端流水号
	def trade_card_num = map.get('pan');//交易卡号，隐藏
	def trans_id = map.get('trans_id');	//交易流水号
	def order_amt = map.get('order_amt');//订单交易金额
	def rrn = map.get('rrn');			//交易参考号
	def con_model = map.get('con_model');//消费模式
	def order_date = map.get('order_date');//交易日期
	def order_time = map.get('order_time');//交易时间
	def trans_type = map.get('trans_type');//交易类型    01消费；02撤销
	def sign = map.get('sign');			
	
	
	sort_key.add('order_id=' + order_num);
	
	TradeService trade_service = TradeService.INSTANCE();
	TradeInfo trade = trade_service.get_trade_by_num(order_num);
	if(trade==null) {
		sort_key.add('resp_code=14');
		sort_key.add('resp_msg=订单未找到');
		sort_key.add('tid=' + tid);
	} else if(trans_type=='01'&&!trade.tss.startsWith('0')) {
		sort_key.add('resp_code=15');
		sort_key.add('resp_msg=订单状态错误');				//说明已支付订单
		sort_key.add('tid=' + tid);
	} else if(trans_type=='02'&&!trade.tss.startsWith('1')) {
		sort_key.add('resp_code=15');
		sort_key.add('resp_msg=订单状态错误');				//说明处于不可撤销状态，只有支付成功（1开头状态）可以被撤销
		sort_key.add('tid=' + tid);
	} else {
		if(trans_type=='01') {
			//开始处理支付成功
			//开始构建并记录交易支付数据
			def pay_data_map = [:];
			pay_data_map['trans_get_account'] = mid;
			pay_data_map['bank_type'] = con_model;
			pay_data_map['trans_type'] = con_model;
			pay_data_map['trans_pay_amount'] = new BigDecimal(order_amt);
			pay_data_map['trans_total_amount'] = new BigDecimal(order_amt);
			pay_data_map['trans_user_id'] = trade_card_num;
			pay_data_map['trans_id'] = trans_id;
			pay_data_map['pay_id'] = tid;
			
			PayResult pay_result = new PayResult();
			pay_result.paycode = pay_code;
			pay_result.code = '0';
			pay_result.text = '交易成功';
			
			trade_service.save_trade_pay_data(trade, [pay_result, pay_data_map]);
			
			
			// 向各模块通知交易成功
			def op_errors = [];
			try{
				//向相关模块进行支付结果通知
				def trade_items = TradeService.INSTANCE().trade_items(trade);
				if(trade_items) {
					trade_items.each{item->
						def url = 'http://' + GlobalHolder.proj.domain;
						def oc = item.oc;
						def on = item.order_num;
						TriggerService trigger_service = TriggerService.INSTANCE();
						TriggerRog rog = trigger_service.get_by_order_type(oc);
						if(rog) {
							url += '/' + rog.appcode + '/' + rog.callback;
							def orig_str = 'oc=' + oc + '&on=' + on;
							def sign_str = MD5Util.getStringMD5(orig_str + rog.token);
							orig_str += '&sign=' + sign_str;
							url += ('?'+ orig_str);
							println url;
							
							ret_str = RemoteUtil.connect(rog.appcode, rog.callback, [oc:oc,on:on,sign:sign]);
							println ret_str;
							
							if(ret_str!=null) {
								try {
									def jsonSlurper = new JsonSlurper()
									//获取到的是Map对象
									def nmap = jsonSlurper.parseText(ret_str)
									if(nmap['code']!='0') {
										//失败情况
										op_errors.add(nmap);
									}
								}catch(any_e) {
								}
							}
						}
					}
				}
			}catch(any) {
				println any;
			}
			if(!op_errors.empty) {
				println JackSonBeanMapper.listToJson(op_errors);
			}
			
		} else if(trans_type=='02') {
			// TODO 暂不处理退货交易
			
		}
		sort_key.add('resp_code=00');
		sort_key.add('resp_msg=交易成功');
		sort_key.add('tid=' + tid);
	}
}

Collections.sort(sort_key);
StringBuffer buffer = new StringBuffer();
for(String s : sort_key) {
	buffer.append(s + "&");
}
String sign_original_str = buffer.substring(0, buffer.toString().length() - 1);
sign_original_str = sign_original_str.toLowerCase() + protocol_sign_key;
String sign = MD5Util.getStringMD5(sign_original_str).toUpperCase();
sort_key.add('sign=' + sign);

return xml_util.to_xml(sort_key)




/*
<?xml version="1.0" encoding="UTF-8"?>
<strin>
<order_type >S</order_type>
<mid>123456789012345</mid>
 <tid>12345678</tid>
 <order_id >1111111111</order_id>
 <resp_code >00</resp_code >
 <resp_msg >交易成功</resp_msg >
 <sign>218c1dfdfe51a9371f1da1e7576d2d88</sign >
</strin>


00	成功
10	输入数据为空
11	输入数据无法解析
12	报文格式错误
13	验证签名失败
14	订单无法找到
15	订单的状态或支付方式非法
16	订单类型非法
17	查询异常
18	支付金额与订单金额不匹配
19	订单已经支付
20	订单不能支付
21	支付报文类型非法
22	支付异常
EE	异常
EF	无原始交易或者调用方法错误
*/



