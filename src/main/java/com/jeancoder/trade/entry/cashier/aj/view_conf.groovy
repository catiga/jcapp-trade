package com.jeancoder.trade.entry.cashier.aj
import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CounterConf
import com.jeancoder.trade.ready.ser.CashierService

CashierService ser = CashierService.INSTANCE;

def id = JC.request.param('id')?.trim();
CounterConf conf = ser.get_cft(id);
if(conf==null) {
	return SimpleAjax.notAvailable('obj_not_found,配置未找到');
}

return SimpleAjax.available('', conf);





