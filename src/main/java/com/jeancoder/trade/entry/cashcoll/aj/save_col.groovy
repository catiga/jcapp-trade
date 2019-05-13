package com.jeancoder.trade.entry.cashcoll.aj

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CollectSetting

def id = JC.request.param('id')?.trim();
def v = JC.request.param('v')?.trim();

BigDecimal bd_v = -1;
try {
	bd_v = new BigDecimal(v.toString());
	id = new BigInteger(id.toString());
} catch(any) {
}


if(bd_v.compareTo(new BigDecimal(0))==-1||bd_v.compareTo(new BigDecimal(0))==0) {
	return SimpleAjax.notAvailable('param_error,折扣设置需0到1的数字');
}
if(bd_v.compareTo(new BigDecimal(1))==1) {
	return SimpleAjax.notAvailable('param_error,折扣设置需0到1的数字');
}

bd_v.setScale(2);

CollectSetting data = JcTemplate.INSTANCE().get(CollectSetting, 'select * from CollectSetting where role_id=? and vv=? and flag!=?', id, bd_v, -1);
if(data!=null) {
	return SimpleAjax.notAvailable('param_error,折扣已经存在');
}

data = new CollectSetting(role_id:id, vv:bd_v, cscode:'1000');
data.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
data.c_time = data.a_time;
data.flag = 0;
data.id = JcTemplate.INSTANCE().save(data);

return SimpleAjax.available('', data);