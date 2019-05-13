package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID
import com.jeancoder.jdbc.bean.JCNotColumn

@JCBean(tbname = 'counter_conf')
class CounterConf {

	@JCID
	BigInteger id;
	
	@JCForeign
	BigInteger cc_id;
	
	String cft;
	
	String code;
	
	String name;
	
	String info;
	
	String param;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Integer flag = 0;
	
	String html;
	
	@JCNotColumn
	def paraminfo;
	
}
