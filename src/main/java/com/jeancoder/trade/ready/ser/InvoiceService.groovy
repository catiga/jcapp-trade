package com.jeancoder.trade.ready.ser

import java.sql.Timestamp

import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.entity.InvoiceSet
import com.jeancoder.trade.ready.entity.SysSupp
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.SSUtil

class InvoiceService {

	private static final InvoiceService __instance__ = new InvoiceService();
	
	JcTemplate jc_template = JcTemplate.INSTANCE();
	
	public static InvoiceService INSTANCE() {
		return __instance__;
	}
	
	def find_invoice_sets() {
		String sql = 'select * from InvoiceSet where flag!=?';
		def params = []; params.add(-1);
		sql += ' and (pid=? or pid=1)'; params.add(GlobalHolder.proj.id);
		sql += ' order by pid asc';
		return jc_template.find(InvoiceSet, sql, params.toArray());
	}
	
	def get_invoice_set(def id) {
		String sql = 'select * from InvoiceSet where flag!=?';
		def params = []; params.add(-1);
//		if(GlobalHolder.proj.root!=1) {
//			sql += ' and pid=?'; params.add(GlobalHolder.proj.id);
//		}
		sql += ' and (pid=? or pid=1)'; params.add(GlobalHolder.proj.id);
		sql += ' and id=?'; params.add(id);
		return jc_template.get(InvoiceSet, sql, params.toArray());
	}
	
	def save_invoice_set(InvoiceSet e) {
		return jc_template.save(e);
	}
	
	def update_invoice_set(InvoiceSet e) {
		return jc_template.update(e);
	}
}
