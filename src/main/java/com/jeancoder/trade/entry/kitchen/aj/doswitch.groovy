package com.jeancoder.trade.entry.kitchen.aj
import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.ser.KitchenService

KitchenService ser = KitchenService.INSTANCE;

CashCounter counter = ser.get_counter(JC.request.param('id'));

if(counter!=null) {
	if(counter.flag==0) {
		counter.flag = 1;
	} else if(counter.flag==1) {
		counter.flag = 0;
	}
	counter.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
	JcTemplate.INSTANCE.update(counter);
}

return SimpleAjax.available('', counter);


