package com.jeancoder.trade.internal.incall.wd

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.dto.AuthToken
import com.jeancoder.trade.ready.dto.AuthUser
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.ser.KitchenService
import com.jeancoder.trade.ready.util.GlobalHolder

def pid = JC.internal.param('pid');

KitchenService ser = KitchenService.INSTANCE;

def result = ser.find_now_counters(pid);

return result;


