package com.jeancoder.trade.internal.incall.cashier

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.util.RemoteUtil

def pid = JC.internal.param('pid');
def token = JC.internal.param('token');

CashierService cc_ser = CashierService.INSTANCE;

//SimpleAjax login_result = RemoteUtil.connect(SimpleAjax, 'project', '/incall/logout', null);
SimpleAjax login_result = JC.internal.call(SimpleAjax, 'project', '/incall/logout', [pid:pid,token:token]);

if(login_result.isAvailable()) {
	cc_ser.counter_log_out(login_result.data);
}

return SimpleAjax.available();