package com.jeancoder.trade.ready.ser

import java.sql.Timestamp

import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.entity.SysSupp
import com.jeancoder.trade.ready.util.SSUtil

class SuppService {

	private static final SuppService __instance__ = new SuppService();
	
	JcTemplate jc_template = JcTemplate.INSTANCE();
	
	public static SuppService INSTANCE() {
		return __instance__;
	}
	
	List<SysSupp> findall(tyc=null) {
		def sql = 'select * from SysSupp';
		def param = [];
		if(tyc) {
			sql += ' where tyc=?';
			param.add(tyc);
		}
		sql += ' order by tyc, code';
		return jc_template.find(SysSupp, sql, param.toArray());
	}
	
	List<SysSupp> findall_availables(tyc=null) {
		def sql = 'select * from SysSupp where flag!=?';
		def param = [];
		param.add(-1);
		if(tyc) {
			sql += ' and tyc=?';
			param.add(tyc);
		}
		sql += ' order by tyc, code';
		return jc_template.find(SysSupp, sql, param.toArray());
	}
	
	def do_check(def id, def tyc, def code) {
		String sql = 'select * from SysSupp where tyc=? and code=?';
		SysSupp sys_supp = jc_template.get(SysSupp, sql, tyc, code);
		if(sys_supp!=null) {
			//sql = 'delete from SysSupp where id=?';
			if(sys_supp.flag==0) {
				sql = 'update SysSupp set flag=-1 where id=?';
				jc_template.batchExecute(sql, sys_supp.id);
			} else {
				sql = 'update SysSupp set flag=0 where id=?';
				jc_template.batchExecute(sql, sys_supp.id);
			}
		} else {
			SysSupp ss = SSUtil.get(tyc, code);
			if(ss==null) {
				return;
			}
			ss.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
			ss.c_time = ss.a_time;
			ss.flag = 0;
			
			jc_template.save(ss);
		}
	}
}
