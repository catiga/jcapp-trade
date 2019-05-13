package com.jeancoder.trade.ready.incall

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.entity.CounterOc
import com.jeancoder.trade.ready.util.GlobalHolder

class CashierService {

	final static CashierService INSTANCE = new CashierService();
	
	JcTemplate jc_template = JcTemplate.INSTANCE;
	
	def get_counter_by_token(def token) {
		String sql = 'select * from CashDoLog where flag!=? and utoken=? and ss=?';
		def result_list = jc_template.find(CashDoLog, sql, -1, token, '00');
		return result_list;
	}
	
	
	def get_counter(def id,def pid) {
		if(!id) {
			return null;
		}
		def param = [];
		String sql = 'select * from CashCounter where id=?';
		param.add(id);
		if(!pid.toString().equals("1")) {
			sql += ' and pid=?';
			param.add(pid);
		}
		return jc_template.get(CashCounter, sql, param.toArray());
	}
	
}
