package com.jeancoder.trade.entry.outcall.ipaynow

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.MD5Util
import com.jeancoder.trade.ready.util.XMLUtil

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

println body.toString();

/**
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * 	<strout>
 * 	<sign>c22646b8ca1528e5b34f39eac5b5c112</sign>
 * 	<mid>713100089120003</mid>
 * <tid>A0000233</tid>
 * <order_id>901180816102536350001</order_id>
 * <order_type>Q</order_type>
 * </strout>
 */
XMLUtil xml_util = new XMLUtil();
def map = xml_util.to_map(body.toString());

//这两个参数针对现在支付暂时写死
def protocol_sign_key = '234357c4db6e03d38fc6e389e3f02d03';			
def mid = '713100089120003';

// 定义返回结果公共信息
def sort_key = [];
sort_key.add('order_type=Q');
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
	
	sort_key.add('order_id=' + order_num);
	
	TradeService trade_service = TradeService.INSTANCE();
	TradeInfo trade = trade_service.get_trade_by_num(order_num);
	if(trade==null) {
		sort_key.add('resp_code=14');
		sort_key.add('resp_msg=订单未找到');
		sort_key.add('order_amt=0');
		sort_key.add('order_desc=0');
		sort_key.add('tid=' + tid);
	} else if(!trade.tss.startsWith('0')) {
		sort_key.add('resp_code=15');
		sort_key.add('resp_msg=订单状态错误，请重新创建订单');
		sort_key.add('order_amt=0');
		sort_key.add('order_desc=0');
		sort_key.add('tid=' + tid);
	} else {
		sort_key.add('resp_code=00');
		sort_key.add('resp_msg=查询成功');
		sort_key.add('order_amt=' + trade.pay_amount.intValue());
		sort_key.add('order_desc=刷卡订单');
		sort_key.add('tid=' + tid);
	}
}

Collections.sort(sort_key);
StringBuffer buffer = new StringBuffer();
for(String s : sort_key) {
	buffer.append(s + "&");
}
String sign_original_str = buffer.substring(0, buffer.toString().length() - 1);
sign_original_str = sign_original_str + protocol_sign_key;
String sign = MD5Util.getMD5(sign_original_str).toUpperCase();

println sign_original_str;
sort_key.add('sign=' + sign);

def ret_xml = xml_util.to_xml(sort_key)

println ret_xml;

return ret_xml;





/*
<xml>
	<order_type>Q</order_type>
	<mid></mid>
	<tid></tid>
	<order_id></order_id>
	<resp_code></resp_code>
	<resp_msg></resp_msg>
	<order_amt></order_amt>
	<order_desc></order_desc>
	
	<!--
	<qr_info></qr_info>
	<amt_flag></amt_flag>
	<memo></memo>
	-->
	
	<sign></sign>
</xml> 


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



