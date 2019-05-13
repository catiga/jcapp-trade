package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCID

@JCBean(tbname = 'trade_trigger_rog')
class TriggerRog {

	@JCID
	BigInteger id;
	
	String appcode;
	
	String callback;
	
	String token;
	
	String dataformat;
	
	Integer flag = 0;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Long rushed;
	
	String order_type;
}
