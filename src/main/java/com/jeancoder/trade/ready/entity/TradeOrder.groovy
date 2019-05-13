package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID
import com.jeancoder.jdbc.bean.JCNotColumn
import com.jeancoder.trade.ready.dto.OrderInfo

@JCBean(tbname = 'trade_order')
class TradeOrder {

	@JCID
	BigInteger id;
	
	@JCForeign
	BigInteger t_id;
	
	BigInteger order_id;
	
	String order_num;
	
	String order_data;
	
	String oss;
	
	String oc;
	
	BigDecimal total_amount;
	
	BigDecimal pay_amount;
	
	BigDecimal tp_amount;
	
	BigDecimal handle_fee = new BigDecimal(0);
	
	BigDecimal service_fee = new BigDecimal(0);
	
	BigInteger pid;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Integer flag = 0;
	
	BigInteger storeid;
	
	String storename;
	
	BigInteger buyerid;
	
	String buyerphone;
	
	String buyername;
	
	BigInteger ouid;
	
	String ouname;
	
	@JCNotColumn
	def order_detail;
	
//	public def getRealData() {
//		if(this.order_data) {
//			return URLDecoder.decode(this.order_data, 'UTF-8');
//		}
//		return order_data;
//	}
//
//	public void setOrder_data(String order_data) {
//		if(order_data) {
//			order_data = URLEncoder.encode(order_data, 'UTF-8');
//		}
//		this.order_data = order_data;
//	}
	
	public static void main(def argc) {
		def test = '{"id":412,"order_no":"101181019203030100001","a_time":1539952230386,"c_time":1539952230386,"pay_time":null,"flag":0,"total_amount":1.00,"pay_amount":1.00,"oss":"0000","dss":"100","buyerid":null,"buyername":null,"buyerphone":null,"buyerprovincecode":null,"buyerprovincename":null,"buyercitycode":null,"buyercityname":null,"buyerzonecode":null,"buyerzonename":null,"buyeraddr":null,"buyerpoint":null,"pid":1,"store_id":1,"store_name":"上梅林腾讯视频好时光","ouid":6,"ouname":"naya","pay_type":null,"items":[{"id":617,"order_id":412,"pic_url":"12_17f5805e85f4734be5cb2a284d300d17","goods_id":12,"goods_no":"10029154924100001","goods_name":"男装01","goods_sku_id":2,"goods_sku_no":"9586","goods_sku_name":"{\\"尺码\\":\\"S\\",\\"颜色\\":\\"白色\\"}","tycode":"100","buy_num":1,"unit_amount":1.00,"pay_amount":1.00,"flag":0,"fss":null,"remark":null}]}';
		OrderInfo o = JackSonBeanMapper.fromJson(test, OrderInfo);
		
		for(x in o.items) {
			def sku = x.goods_sku_name;
			println sku;
			
			JackSonBeanMapper.jsonToMap(sku);
		}
		
	}
}
