package com.jeancoder.trade.entry.cashier.aj
import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.ser.KitchenService
import com.jeancoder.trade.ready.util.RemoteUtil

CashierService ser = CashierService.INSTANCE;


def id = JC.request.param('id')?.trim();

CashCounter counter = ser.get_counter(id);
if(counter==null) {
	return SimpleAjax.notAvailable('obj_not_found,工作台未找到');
}

return SimpleAjax.available('', counter);
