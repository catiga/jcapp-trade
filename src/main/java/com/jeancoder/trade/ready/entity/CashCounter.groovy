package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID
import com.jeancoder.jdbc.bean.JCNotColumn

@JCBean(tbname = "trade_cashier_counter")
class CashCounter {

	@JCID
	BigInteger id;
	
	String sn;
	
	String name;
	
	String info;
	
	@JCForeign
	BigInteger pid;
	
	BigInteger sid;
	
	String sname;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Integer flag = 0;
	
	@JCNotColumn
	def ocs;
	
	//默认可以用
	/**
	 * 0:待使用
	 * 1:使用中
	 * 2:锁定中
	 */
	Integer inuse = 0;
	
	//工作台类型
	/**
	 * 10**：收银台预留
	 * 20**：后厨工作台预留
	 */
	String ctcode = '1000'
}
