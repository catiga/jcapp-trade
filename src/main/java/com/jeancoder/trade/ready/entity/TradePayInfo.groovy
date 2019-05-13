package com.jeancoder.trade.ready.entity

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID

@JCBean(tbname = 'trade_pay_info')
class TradePayInfo {

	@JCID
	BigInteger id;
	
	@JCForeign
	BigInteger t_id;
	
	BigInteger log_id;
	
	String t_num;
	
	String pay_id;
	
	String pay_code;
	
	String pay_name;
	
	Timestamp pay_time;
	
	Timestamp c_time;
	
	Integer flag = 0;
	
	String trans_id;
	
	String trans_user_id;
	
	BigDecimal trans_total_amount;
	
	BigDecimal trans_pay_amount;
	
	BigDecimal trans_cash_col;
	
	String trans_type;
	
	String bank_type;
	
	String trans_get_account;
	
	/**
	 * 10:扣款交易
	 * 60:退款交易
	 */
	String pay_type = '10';
}
