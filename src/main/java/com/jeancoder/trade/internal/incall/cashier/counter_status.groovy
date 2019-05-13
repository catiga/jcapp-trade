package com.jeancoder.trade.internal.incall.cashier

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.AuthToken
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.incall.CashierService
import com.jeancoder.trade.ready.token.TokenService

def start = System.currentTimeMillis();
def token = JC.internal.param('token');

JCLogger  Logger  = LoggerSource.getLogger(this.getClass().getName());
CashierService cashier_service = CashierService.INSTANCE;

//本地token校验
SimpleAjax counter = TokenService.INSTANCE.validate(token);
if(!counter.available) {
	return counter;
}

return counter;

