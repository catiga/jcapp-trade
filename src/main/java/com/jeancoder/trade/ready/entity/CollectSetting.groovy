package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID

@JCBean(tbname = 'cash_collect_setting')
class CollectSetting {

	@JCID
	BigInteger id;
	
	@JCForeign
	BigInteger role_id;

	BigDecimal vv;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Integer flag = 0;
	
	String cscode = '1000';
	
	String tip;
}
