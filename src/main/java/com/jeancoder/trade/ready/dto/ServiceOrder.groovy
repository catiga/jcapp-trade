package com.jeancoder.trade.ready.dto

import java.sql.Timestamp

class ServiceOrder {

	BigInteger id;
	
	BigInteger pid;
	
	Integer flag = 0;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	BigInteger sp_id;
	
	String book_id;
	
	BigInteger basic_id;
	
	BigInteger ap_id;
	
	String mobile;
	
	String order_no;
	
	String order_status;
	
	BigDecimal total_amount;
	
	BigDecimal pay_amount;
	
	Timestamp pay_time;
	
	String pay_type;
	
	BigInteger store_id;
	
	String storename;
	
	String start_date;
	
	String start_time;
	
	String end_date;
	
	String end_time;
	
	BigInteger res_id;
	
	String resname;
	
	String abjs;
	
	BigInteger acmid;
}
