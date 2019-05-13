package com.jeancoder.trade.entry.kitchen.aj
import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.ser.KitchenService
import com.jeancoder.trade.ready.util.RemoteUtil

KitchenService ser = KitchenService.INSTANCE;

def cash_do_log_id = JC.request.param('id')?.trim();
CashDoLog cash_do_log = ser.get_counter_log(cash_do_log_id);
if(cash_do_log==null) {
	return SimpleAjax.notAvailable('log_not_found');
}

SimpleAjax login_result = RemoteUtil.connect(SimpleAjax, 'project', '/incall/logout_account', [token:cash_do_log.utoken]);

if(!login_result.isAvailable()) {
	return SimpleAjax.notAvailable('logout_fail');
}

cash_do_log.end_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
cash_do_log.ss = '10';
JcTemplate.INSTANCE().update(cash_do_log);

CashCounter counter = ser.get_counter(cash_do_log.ccid);
if(counter!=null) {
	counter.inuse = 0;
	JcTemplate.INSTANCE().update(counter);
}

return SimpleAjax.available();


