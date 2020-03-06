package com.jeancoder.trade.ready.dto

import java.sql.Timestamp

class ActOrder {

	BigInteger id;
	
	String order_no;
	
	BigInteger pid;
	
	Integer flag = 0;
	
	Date a_time;
	
	Timestamp c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
	
	BigInteger act_id;
	
	BigDecimal total_amount;
	
	BigDecimal pay_amount;
	
	String pay_type;	//主支付方式
	
	Date pay_time;
	
	String order_status = '0000';
	
	BigInteger basic_id;
	
	BigInteger ap_id;
	
	String contact_person;
	
	String contact_mobile;
	
	Integer seat_amount;
}
