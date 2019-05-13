package com.jeancoder.trade.ready.ser

import java.sql.Timestamp

import com.jeancoder.core.util.MD5Util
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.entity.TriggerRog

class TriggerService {
	
	def _default_token_valid_ = 24*60*60;

	static final TriggerService _instance_ = new TriggerService();
	
	static INSTANCE() {
		return _instance_;
	}
	
	def get_by_order_type(String order_type) {
		String sql = 'select * from TriggerRog where flag!=? and order_type=?';
		
		TriggerRog rog = JcTemplate.INSTANCE().get(TriggerRog.class, sql, -1, order_type);
		return rog;
	}
	
	def get_by_appcode(String app_code, String callback, String dataformat, def allow_order_types) {
		String sql = 'select * from TriggerRog where flag!=? and appcode=? and order_type=?';
		
		TriggerRog rog = JcTemplate.INSTANCE().get(TriggerRog.class, sql, -1, app_code, allow_order_types);
		if(rog==null) {
			synchronized (this) {
				rog = JcTemplate.INSTANCE().get(TriggerRog.class, sql, -1, app_code, allow_order_types);
				if(rog==null) {
					rog = new TriggerRog();
					rog.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
					rog.c_time = rog.a_time;
					rog.appcode = app_code;
					rog.callback = callback;
					rog.dataformat = dataformat;
					rog.flag = 0;
					rog.order_type = allow_order_types;
					//生成默认过期时间
					def days = nextInt(100, 300);
					rog.rushed = days*_default_token_valid_;
					
					//生成可用token
					//token 规则  app_code+3位随机数 + 有效期
					rog.token = MD5Util.getStringMD5(app_code + nextInt(100, 999)) + MD5Util.getStringMD5(rog.rushed+'');
					rog.id = JcTemplate.INSTANCE().save(rog);
				}
			}
		} else {
			//执行部分更新操作
			//判断是否要进行更新
			if(rog.callback!=callback) {
				rog.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				rog.callback = callback;
				rog.dataformat = dataformat;
				rog.token = MD5Util.getStringMD5(app_code + nextInt(100, 999)) + MD5Util.getStringMD5(rog.rushed+'');
				JcTemplate.INSTANCE().update(rog);
			}
		}
		return rog;
	}
	
	
	def nextInt(final int min, final int max){
		Random rand= new Random();
		int tmp = Math.abs(rand.nextInt());
		return tmp % (max - min + 1) + min;
	}
}
