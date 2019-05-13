import com.jeancoder.trade.ready.util.XMLUtil

import groovy.json.JsonSlurper

def xml = '''
<xml>
   <return_code><![CDATA[SUCCESS]]></return_code>
   <return_msg><![CDATA[OK]]></return_msg>
   <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
   <mch_id><![CDATA[10000100]]></mch_id>
   <device_info><![CDATA[1000]]></device_info>
   <nonce_str><![CDATA[GOp3TRyMXzbMlkun]]></nonce_str>
   <sign><![CDATA[D6C76CB785F07992CDE05494BB7DF7FD]]></sign>
   <result_code><![CDATA[SUCCESS]]></result_code>
   <openid><![CDATA[oUpF8uN95-Ptaags6E_roPHg7AG0]]></openid>
   <is_subscribe><![CDATA[Y]]></is_subscribe>
   <trade_type><![CDATA[MICROPAY]]></trade_type>
   <bank_type><![CDATA[CCB_DEBIT]]></bank_type>
   <total_fee>1</total_fee>
   <coupon_fee>0</coupon_fee>
   <fee_type><![CDATA[CNY]]></fee_type>
   <transaction_id><![CDATA[1008450740201411110005820873]]></transaction_id>
   <out_trade_no><![CDATA[1415757673]]></out_trade_no>
   <attach><![CDATA[订单额外描述]]></attach>
   <time_end><![CDATA[20141111170043]]></time_end>
</xml>
'''

XMLUtil xu = new XMLUtil();
def result = xu.to_map(xml);
//println result;



def json = '''
{"alipay_trade_pay_response":{"code":"10000","msg":"Success","buyer_logon_id":"jac***@yahoo.com.cn","buyer_pay_amount":"0.01","buyer_user_id":"2088002474753815","fund_bill_list":[{"amount":"0.01","fund_channel":"ALIPAYACCOUNT"}],"gmt_payment":"2018-08-06 14:22:15","invoice_amount":"0.01","out_trade_no":"1533454781590","point_amount":"0.00","receipt_amount":"0.01","total_amount":"0.01","trade_no":"2018080621001004810536770846"},"sign":"PmxWKmqhcXqvoP4s+934LH8MX+wGSsAzETke/VeG1Tmn2m99iUb4Y5VOmW5hdxsWnn9+5L0+Kt00XgK1cKhILZME9FFOGCIOHz9Z5camxDMbZDUMalAUdfkOia8f0l6Yl3HumkXV+7vX+f9+ix1m+RBPu0McwG8/h2x+SED/+2WinK6mp7Tu45Ztzhvi32cbPwrz3W173lJ0ZVjnlWKqfvZtkLM/biOsT28SuPeiWyQPkNTgPjrw466pDKB9y9GsEj5hknA7ybmTSzf12V408op7GqaG07wuEuIx5Yx+ToQrYQrkwmrKpVbNsUu4vTfAsp2QJs/RJ+zyU0uRiqkmpA=="}
''';

def jsonSlurper = new JsonSlurper()
def map = jsonSlurper.parseText(json)

println 'code=' + map['alipay_trade_pay_response']['code'];
println 'alipay_account_name=' + map['alipay_trade_pay_response']['buyer_logon_id'];
println 'pay_amount=' + map['alipay_trade_pay_response']['buyer_pay_amount'];

println 'buyer_user_id=' + map['alipay_trade_pay_response']['buyer_user_id'];

println 'fund_bill_list=' + map['alipay_trade_pay_response']['fund_bill_list'];

println 'pay_time=' + map['alipay_trade_pay_response']['gmt_payment'];

println 'invoice_amount=' + map['alipay_trade_pay_response']['invoice_amount'];

println 'get_amount=' + map['alipay_trade_pay_response']['receipt_amount'];

println 'total_amount=' + map['alipay_trade_pay_response']['total_amount'];

println 'trans_id=' + map['alipay_trade_pay_response']['trade_no'];

println 'sign=' + map['sign'];




println(map)



