package com.jeancoder.trade.entry.incall.cashier

import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.util.RemoteUtil

CashierService cc_ser = CashierService.INSTANCE;

SimpleAjax login_result = RemoteUtil.connect(SimpleAjax, 'project', '/incall/logout', null);
if(login_result.isAvailable()) {
	cc_ser.counter_log_out(login_result.data);
}

return SimpleAjax.available();