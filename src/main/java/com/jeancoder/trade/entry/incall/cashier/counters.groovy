package com.jeancoder.trade.entry.incall.cashier

import com.jeancoder.trade.ready.dto.AuthToken
import com.jeancoder.trade.ready.dto.AuthUser
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.util.GlobalHolder


CashierService ser = CashierService.INSTANCE;

AuthUser user = GlobalHolder.token?.user;

def result = ser.find_now_counters();

return result;


