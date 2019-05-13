package com.jeancoder.trade.entry.incall.cashier

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.token.TokenService

def token = JC.request.param('token');

return TokenService.INSTANCE.validate(token);