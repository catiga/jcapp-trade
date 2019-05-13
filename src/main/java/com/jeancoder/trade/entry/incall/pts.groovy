package com.jeancoder.trade.entry.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.ser.SuppService

def tyc = JC.request.param('tyc');
SuppService ser = SuppService.INSTANCE();
def result = ser.findall(tyc);

return result;