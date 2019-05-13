package com.jeancoder.trade.ready.dto

import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID

class OrderItemPack {
	BigInteger id;
	
	BigInteger order_id;
	
	BigInteger order_item_id;
	
	String pic_url;
	
	BigInteger goods_id;
	
	String goods_no;
	
	String goods_name;
	
	BigInteger goods_sku_id;
	
	String goods_sku_no;
	
	String goods_sku_name;
	
	/**
	 * 100:商品
	 * 300:合成品
	 */
	String tycode;
	
	BigDecimal buy_num;
	
	Integer flag = 0;
	
	String remark;
}
