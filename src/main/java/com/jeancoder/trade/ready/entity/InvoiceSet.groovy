package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID

@JCBean(tbname = 'sp_invoice_info')
class InvoiceSet {

	@JCID
	BigInteger id;
	
	@JCForeign
	BigInteger pid;
	
	String unit_name;
	
	String unit_tax;
	
	String unit_account;
	
	String unit_addr;
	
	String unit_bank;
	
	String unit_phone;
	
	BigDecimal tax_fee;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Integer flag = 0;
}
