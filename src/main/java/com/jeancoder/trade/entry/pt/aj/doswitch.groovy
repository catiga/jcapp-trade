package com.jeancoder.trade.entry.pt.aj

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.ser.SuppService
import com.jeancoder.trade.ready.util.SSUtil

def supp_dics = SSUtil.findall();

SuppService ser = SuppService.INSTANCE();

def id = JC.request.param('id');
def tyc = JC.request.param('tyc');
def code = JC.request.param('code');


ser.do_check(id, tyc, code);

return SimpleAjax.available();



