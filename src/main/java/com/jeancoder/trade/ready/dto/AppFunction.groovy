package com.jeancoder.trade.ready.dto

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCID
import com.jeancoder.jdbc.bean.JCNotColumn

class AppFunction {

	BigInteger id;
	
	String appcode;
	
	String unicode;
	
	String func_name;
	
	String func_ss;
	
	String func_type;
	
	String func_info;
	
	BigInteger parent_id;
	
	String parent_unicode;
	
	Integer flag = 0;
	
	Integer level;
	
	String click_url;
	
	Boolean whole;
	
	Boolean hasson;
	
	Integer sort;
	
	String limpro;
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AppFunction)) {
			return false;
		}
		AppFunction aim = (AppFunction)obj;
		if(aim.unicode.equals(this.unicode)) {
			return true;
		}
		return false;
	}
}
