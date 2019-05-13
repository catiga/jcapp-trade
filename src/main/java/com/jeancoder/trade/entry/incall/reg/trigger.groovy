package com.jeancoder.trade.entry.incall.reg

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.DataTrans
import com.jeancoder.trade.ready.entity.TriggerRog
import com.jeancoder.trade.ready.ser.TriggerService
import com.jeancoder.trade.ready.util.TradeTssUtil

/**
 * 输入参数
 * @param app_code
 * @param order_type
 * @param callback
 * @param dataformat  null?'订单编号':对象json
 */


/**
 * 返回参数
 * @value token
 * @value valid   过期时间时间戳
 */
TriggerService tri = TriggerService.INSTANCE();

def app_code = JC.request.param('app_code');
def callback = JC.request.param('callback');
def dataformat = JC.request.param('dataformat');
def order_type = JC.request.param('order_type');

def arr_order_ts = order_type.split(",");

DataTrans dt = new DataTrans();

if(!app_code) {
	dt.code = '-1';
	dt.msg = 'app_code_empty';
	return dt;
}
def ret_rogs = [];
for(x in arr_order_ts) {
	TriggerRog rog = tri.get_by_appcode(app_code, callback, dataformat, x);
	ret_rogs.add(rog);
}

dt.code = '0';
dt.msg = 'success';
dt.data = ret_rogs;
dt.oss = TradeTssUtil.all();

return dt;


