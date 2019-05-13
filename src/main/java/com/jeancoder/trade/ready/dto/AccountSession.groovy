package com.jeancoder.trade.ready.dto

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCID

class AccountSession {

	BigInteger id;
	
	BigInteger proj_id;
	
	BigInteger basic_id;
	
	Long rushed;
	
	String token;
	
	Integer flag;
	
	Integer lograns;
	
	String logtype;

}
