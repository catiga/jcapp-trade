package com.jeancoder.trade.ready.dto

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCID

class SysFunction {

	BigInteger id;
	
	String func_name;
	
	String func_ss;
	
	String func_type;
	
	String func_info;
	
	BigInteger parent_id;
	
	Timestamp c_time;
	
	Timestamp a_time;
	
	Integer flag = 0;
	
	Integer level;
	
	String click_url;
	
	Boolean whole;
	
	Boolean hasson;
	
	Integer sort;
	
	String limpro;

	@Override
	public int hashCode() {
		return this.id.intValue();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof SysFunction)) {
			return false;
		}
		SysFunction aim = (SysFunction)obj;
		if(aim.getId().equals(this.id)) {
			return true;
		}
		return false;
	}
}
