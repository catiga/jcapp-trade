package com.jeancoder.trade.ready.ser

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.entity.CounterOc
import com.jeancoder.trade.ready.util.GlobalHolder

class KitchenService {

	final static KitchenService INSTANCE = new KitchenService();
	
	def ctcode = '2000'; //后厨
	
	JcTemplate jc_template = JcTemplate.INSTANCE;
	
	def save_counter(CashCounter e) {
		e.ctcode = ctcode;
		e.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		e.pid = GlobalHolder.proj.id;
		BigInteger id = jc_template.save(e);
		//同时设置可接受的订单类型
		CounterOc oc = new CounterOc();
		oc.cc_id = id;
		oc.oc = '1000';
		jc_template.save(oc);
		return id;
	}
	
	def update_counter(CashCounter e) {
		e.ctcode = ctcode;
		e.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return jc_template.update(e);
	}
	
	def counter_log_in(def pid = GlobalHolder.proj?.id, CashCounter counter, def uname, def token, def remote_addr = JC.request.get()?.getRemoteHost()) {
		counter.inuse = 1;
		jc_template.update(counter);
		CashDoLog cdl = new CashDoLog();
		cdl.start_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		cdl.ccid = counter.id;
		cdl.ccname = counter.name;
		cdl.ccsn = counter.sn;
		cdl.do_ip = remote_addr;
		cdl.flag = 0;
		cdl.pid = pid;
		cdl.ss = '00';
		cdl.uname = uname;
		cdl.utoken = token;
		jc_template.save(cdl);
		
		return true;
	}
	
	def get_counter_by_token(def token) {
		String sql = 'select * from CashDoLog where flag!=? and utoken=? and ss=?';
		def result_list = jc_template.find(CashDoLog, sql, -1, token, '00');
		return result_list;
	}
	
	def counter_log_out(def token) {
		String sql = 'select * from CashDoLog where flag!=? and utoken=?';
		def result_list = jc_template.find(CashDoLog, sql, -1, token);
		def ts = new Timestamp(Calendar.getInstance().getTimeInMillis());
		def counter_id = null;
		for(x in result_list) {
			x.ss = '10';
			x.end_time = ts;
			jc_template.update(x);
			counter_id = x.ccid;
		}
		if(counter_id) {
			jc_template.batchExecute('update CashCounter set inuse = 0 where id=?', counter_id);
		}
	}
	
	def find_counter_log(def page, def ccid) {
		String sql = 'select * from CashDoLog where flag!=?';
		def param = []; param.add(-1);
		if(GlobalHolder.proj.root!=1) {
			sql += ' and pid=?';
			param.add(GlobalHolder.proj.id);
		}
		if(ccid) {
			sql += ' and ccid=?';
			param.add(ccid);
		}
		sql += ' order by ss asc, start_time';
		return jc_template.find(CashDoLog, page, sql, param.toArray());
	}
	
	def get_counter_log(def id) {
		String sql = 'select * from CashDoLog where flag!=?';
		def param = []; param.add(-1);
		sql += ' and id=?'; param.add(id);
		return jc_template.get(CashDoLog, sql, param.toArray());
	}
	
	def find_now_counters(def pid) {
		String sql = 'select * from CashCounter where flag!=? and inuse=? and ctcode=?';
		def param = [];
		param.add(-1); param.add(0); param.add(ctcode);
		//if(GlobalHolder.proj.root!=1) {
		//每个项目只能获得自己的收银台，所以必须加pid
			sql += ' and pid=?';
			//param.add(GlobalHolder.proj.id);
			param.add(pid);
		//}
		
		def result = jc_template.find(CashCounter, sql, param.toArray());
		result.each{it.ocs = jc_template.find(CounterOc, 'select * from CounterOc where cc_id=?', it.id);}
		return result;
	}
	
	def get_counter(def id) {
		if(!id) {
			return null;
		}
		def param = [];
		String sql = 'select * from CashCounter where id=? and ctcode=?';
		param.add(id); param.add(ctcode);
		return jc_template.get(CashCounter, sql, param.toArray());
	}
	
	def find_counter(def store_id, def cn) {
		String sql = 'select * from CashCounter where flag!=? and ctcode=?';
		def param = [];
		param.add(-1); param.add(ctcode);
		if(store_id) {
			sql += ' and sid=?';
			param.add(store_id);
		}
		if(cn) {
			sql += ' and sn=?';
			param.add(cn);
		}
		def result = jc_template.find(CashCounter, sql, param.toArray());
		result.each{it.ocs = jc_template.find(CounterOc, 'select * from CounterOc where cc_id=?', it.id);}
		return result;
	}
}
