package com.jeancoder.trade.internal.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.ser.SuppService

def tyc = JC.internal.param('tyc');
SuppService ser = SuppService.INSTANCE();
def result = ser.findall_availables(tyc);

return SimpleAjax.available('', result);