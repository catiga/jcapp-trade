package com.jeancoder.trade.entry.pt.aj

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.SysSupp
import com.jeancoder.trade.ready.ser.SuppService

def pt_id = JC.request.param('id');

def pt_cat = '900';

SuppService ser = SuppService.INSTANCE();

List<SysSupp> all_sps = ser.findall('1000');

SysSupp select_ss = null;
def update = true;
if(pt_id) {
	for(SysSupp ss : all_sps) {
		if(ss.id.toString()==pt_id&&ss.code_cat==pt_cat) {
			select_ss = ss;
			break;
		}
	}
}
if(select_ss==null) {
	return SimpleAjax.notAvailable('param_error,禁止编辑系统内置支付方式');
}
return SimpleAjax.available('', select_ss);




