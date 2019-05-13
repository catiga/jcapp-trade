package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCID

@JCBean(tbname = "supp_conf_ts")
class SysSupp {

	@JCID
	BigInteger id;
	
	String tyc;
	
	String code_cat;
	
	String code;
	
	String name;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Integer flag = 0;
	
	//是否支持逆向操作，对于支付就是退款
	Integer rb = 0;
}
