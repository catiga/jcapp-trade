package com.jeancoder.trade.entry.cashcoll.aj

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.counterperm.AllowCusPrice
import com.jeancoder.trade.ready.entity.CollectSetting

def id = JC.request.param('id')?.trim();
def v = JC.request.param('v')?.trim();

id = new BigInteger(id.toString());

if(v!='1100.00'&&v!='1101.00'&&v!='1102.00') {
	return SimpleAjax.notAvailable('param_error,不支持的改价类型');
}

BigDecimal bd_v = new BigDecimal(v).setScale(2);

def value = AllowCusPrice.compare(bd_v);
if(value==null) {
	return SimpleAjax.notAvailable('param_error,不支持的改价类型');
}

CollectSetting data = JcTemplate.INSTANCE().get(CollectSetting, 'select * from CollectSetting where role_id=? and vv=? and flag!=?', id, bd_v, -1);
if(data!=null) {
	return SimpleAjax.notAvailable('param_error,改价设置已经存在');
}

data = new CollectSetting(role_id:id, vv:bd_v, cscode:'2000');
data.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
data.c_time = data.a_time;
data.flag = 0;
data.tip = value;
data.id = JcTemplate.INSTANCE().save(data);

return SimpleAjax.available('', data);