package com.jeancoder.trade.ready.dto;

import java.sql.Timestamp;

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCID

class AuthUser {

	BigInteger id;
	
	String name;
	
	String password;
	
	String mobile;
	
	String status;
	
	String username;
	
	Boolean mv = false;
	
	Integer flag = 0;

}
