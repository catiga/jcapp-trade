package com.jeancoder.trade.ready.dto

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCID

class AuthRole {

	BigInteger id;
	
	String role_name;
	
	String role_type;
	
	Integer flag = 0;
	
	BigInteger pid;

}
