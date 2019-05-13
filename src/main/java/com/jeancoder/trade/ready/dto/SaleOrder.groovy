package com.jeancoder.trade.ready.dto

import java.sql.Timestamp

class SaleOrder {

	BigInteger id;
	
	String order_no;
	
	String order_status;
	
	String total_amount;
	
	String pay_amount;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Integer ticket_sum;
	
	BigInteger store_id;
	
	String store_name;
	
	String hall_id;
	
	String hall_name;
	
	String plan_id;
	
	String plan_date;
	
	String plan_time;
	
	String film_name;
	
	String o_c;
	
	BigInteger proj_id;
	
	BigInteger user_id;	//ap_id
	
	BigInteger store_basic;
	
	BigDecimal handle_fee = new BigDecimal(0);
	
	BigDecimal service_fee = new BigDecimal(0);
	
	String film_dimensional;
	
	String film_no;
	
	String pay_type;
}
