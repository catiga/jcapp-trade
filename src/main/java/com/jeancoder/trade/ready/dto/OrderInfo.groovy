package com.jeancoder.trade.ready.dto

import java.sql.Timestamp

class OrderInfo {

	BigInteger id;
	
	String order_no;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Integer flag = 0;
	
	BigDecimal total_amount;
	
	BigDecimal pay_amount;
	
	String oss;
	
	/**
	 * 100:到店订单
	 */
	String dss;
	
	BigInteger buyerid;
	
	String buyername;
	
	String buyerphone;
	
	String buyerprovincecode;
	
	String buyerprovincename;
	
	String buyercitycode;
	
	String buyercityname;
	
	String buyerzonecode;
	
	String buyerzonename;
	
	String buyeraddr;
	
	BigInteger pid;
	
	BigInteger store_id;
	
	String store_name;
	
	BigInteger ouid;
	
	String ouname;
	
	List<OrderItem> items;
	
}
