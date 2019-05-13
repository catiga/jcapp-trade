package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID

@JCBean(tbname = 'trade_invoice')
class TradeInvoice {

	@JCID
	BigInteger id;
	
	@JCForeign
	BigInteger t_id;
	
	@JCForeign
	BigInteger  invo_set_id;
	
	String is_unit_name;
	
	String is_unit_tax;
	
	BigDecimal is_tax_fee;
	
	String pay_unit_name;
	
	String pay_unit_tax;
	
	String pay_bank;
	
	String pay_account;
	
	String pay_addr;
	
	String pay_phone;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Integer flag = 0;
	
	BigDecimal invoice_amount;
}
