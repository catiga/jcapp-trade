package com.jeancoder.trade.ready.entity

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID

@JCBean(tbname = 'cashier_doer_log')
class CashDoLog {

	@JCID
	BigInteger id;
	
	@JCForeign
	BigInteger ccid;
	
	String ccsn;
	
	String ccname;
	
	@JCForeign
	BigInteger pid;
	
	String do_ip;
	
	Date start_time;
	
	Date end_time;
	
	String utoken;
	
	String uname;
	
	BigInteger uid;
	
	/**
	 * 00:登出
	 * 10:登入
	 */
	String ss = '00';
	
	Integer flag = 0;
	
	/**
	 * 10未结算
	 * 00 结算
	 */
	String settle = '10';
}
