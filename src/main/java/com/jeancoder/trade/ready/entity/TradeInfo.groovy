package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID

@JCBean(tbname = 'trade_info')
class TradeInfo {

	@JCID
	BigInteger id;
	
	@JCForeign
	BigInteger pid;
	
	BigInteger log_id;
	
	String tnum;
	
	String tss;
	
	Timestamp a_time;
	
	Timestamp c_time;
	
	Timestamp pay_time;
	
	//这里映射为主支付方式
	String pay_type;
	
	Integer flag = 0;
	
	BigDecimal total_amount;
	
	BigDecimal pay_amount;
	
	BigDecimal handle_fee = new BigDecimal(0);
	
	BigDecimal service_fee = new BigDecimal(0);
	
	String tname;
	
	String tbody;
	
	@JCForeign
	BigInteger storeid;
	
	String storename;
	
	@JCForeign
	BigInteger buyerid;
	
	String buyerphone;
	
	String buyername;

//	public BigDecimal getPay_amount() {
//		if(handle_fee) {
//			return pay_amount.add(handle_fee);
//		}
//		return pay_amount;
//	}
	
}
