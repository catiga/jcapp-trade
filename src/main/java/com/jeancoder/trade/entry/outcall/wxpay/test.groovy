package com.jeancoder.trade.entry.outcall.wxpay

import java.io.ByteArrayOutputStream

import javax.servlet.ServletInputStream

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.RemoteUtil
import com.jeancoder.trade.ready.util.XMLUtil

def xml = null;

println new BigDecimal("1.00").setScale(0);

try {
//微信支付结果通知	
xml = '''
<xml>
	<appid><![CDATA[wxb8fd6ebb082e8b24]]></appid>
	<attach><![CDATA[好时光消费]]></attach>
	<bank_type><![CDATA[CFT]]></bank_type>
	<cash_fee><![CDATA[1]]></cash_fee>
	<device_info><![CDATA[1]]></device_info>
	<fee_type><![CDATA[CNY]]></fee_type>
	<is_subscribe><![CDATA[N]]></is_subscribe>
	<mch_id><![CDATA[1508910931]]></mch_id>
	<nonce_str><![CDATA[9ab2a43fdd821a77eaffeeaebb2e4254]]></nonce_str>
	<openid><![CDATA[ooKYc1Pb8jfGufOaWo-yyeBu54lo]]></openid>
	<out_trade_no><![CDATA[901180913175329120001]]></out_trade_no>
	<result_code><![CDATA[SUCCESS]]></result_code>
	<return_code><![CDATA[SUCCESS]]></return_code>
	<sign><![CDATA[5C5415F92876764383904942171DC668]]></sign>
	<time_end><![CDATA[20180913164719]]></time_end>
	<total_fee>1</total_fee>
	<trade_type><![CDATA[JSAPI]]></trade_type>
	<transaction_id><![CDATA[4200000185201809131104413182]]></transaction_id>
</xml>
''';

JC.remote.http_call('http://192.168.1.2:8092/trade/outcall/wxpay/trade_notify', xml);

return;

//扫码支付等待密码通知
xml = '''
<xml>
  <return_code><![CDATA[SUCCESS]]></return_code>
  <return_msg><![CDATA[OK]]></return_msg>
  <appid><![CDATA[wxd0569da27f196643]]></appid>
  <mch_id><![CDATA[10111422]]></mch_id>
  <device_info><![CDATA[1]]></device_info>
  <nonce_str><![CDATA[p6Db5RWLdkryIzjz]]></nonce_str>
  <sign><![CDATA[FF11F9FFD96A989C6E8C21DF0F88AC24]]></sign>
  <result_code><![CDATA[FAIL]]></result_code>
  <err_code><![CDATA[USERPAYING]]></err_code>
  <err_code_des><![CDATA[需要用户输入支付密码]]></err_code_des>
</xml>
'''
} catch(any) {}

XMLUtil xu = new XMLUtil();
def ret_map = xu.to_map(xml);
println ret_map;

String return_code = ret_map.get("return_code");
String result_code = ret_map.get("result_code");
String bank_type = ret_map.get("bank_type");
String total_fee = ret_map.get("total_fee");
String out_trade_no = ret_map.get("out_trade_no");
String transaction_id = ret_map.get("transaction_id");



println return_code;


