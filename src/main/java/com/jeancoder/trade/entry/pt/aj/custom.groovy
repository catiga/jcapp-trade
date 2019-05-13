package com.jeancoder.trade.entry.pt.aj

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.SysSupp
import com.jeancoder.trade.ready.ser.SuppService

def pt_id = JC.request.param('pt_id');
def pt_code = JC.request.param('pt_code');
def pt_name = JC.request.param('pt_name');

if(!pt_code||!pt_name) {
	return SimpleAjax.notAvailable('param_error,请设置代码和名称');
}

def pt_cat = '900';

SuppService ser = SuppService.INSTANCE();

List<SysSupp> all_sps = ser.findall('1000');

SysSupp select_ss = null;
def update = true;
if(pt_id!='0') {
	for(SysSupp ss : all_sps) {
		if(ss.id.toString()==pt_id&&ss.code_cat==pt_cat) {
			select_ss = ss;
			break;
		}
	}
} else {
	select_ss = new SysSupp();
	select_ss.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
	select_ss.c_time = select_ss.a_time;
	select_ss.flag = 0;
	select_ss.code_cat = pt_cat;
	select_ss.tyc = '1000';
	update = false;
}
if(select_ss==null) {
	return SimpleAjax.notAvailable('param_error,禁止编辑系统内置支付方式');
}
select_ss.code = pt_cat + '0' + pt_code;
select_ss.name = pt_name;
select_ss.rb = 1;		//类似现金，其实起到记账作用

if(update) {
	JcTemplate.INSTANCE().update(select_ss);
} else {
	JcTemplate.INSTANCE().save(select_ss);
}

return SimpleAjax.available();




