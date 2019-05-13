package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID

@JCBean(tbname = 'trade_preferential')
class TradePreferential {

	@JCID
	BigInteger id;
	
	@JCForeign
	BigInteger t_id;
	
	String t_num;
	
	BigInteger order_id;
	
	String order_num;
	
	String oc;
	
	String pref_id;
	
	String pref_code;
	
	String pref_name;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Integer flag = 0;
	
	String pref_type;
	
	BigDecimal max_deduct;
	
	BigDecimal real_deduct;
}
