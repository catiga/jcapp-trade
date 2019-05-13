package com.jeancoder.trade.internal.incall.cashier

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.token.TokenService

def token = JC.internal.param('token');
def lock_flag = JC.internal.param('lock_flag');

JCLogger  Logger  = LoggerSource.getLogger(this.getClass().getName());

CashierService service = CashierService.INSTANCE;

//本地token校验
SimpleAjax counter = TokenService.INSTANCE.validate(token);
if(!counter.available) {
	return counter;
}
CashCounter real_counter = counter.data;
if(lock_flag=='0') {
	//解锁
	service.counter_unlock(real_counter);
} else {
	//锁定
	service.counter_lock(real_counter);
}

return SimpleAjax.available('', com.jeancoder.trade.ready.incall.CashierService.INSTANCE.get_counter_by_token(token));

