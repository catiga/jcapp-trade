package com.jeancoder.trade.internal.incall.wd

import com.jeancoder.app.sdk.source.CommunicationSource
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.ser.KitchenService
import com.jeancoder.trade.ready.util.NativeUtil
import com.jeancoder.trade.ready.util.RemoteUtil

KitchenService cc_ser = KitchenService.INSTANCE;


def token = CommunicationSource.getParameter("token");
def pid = CommunicationSource.getParameter("pid");
SimpleAjax login_result = NativeUtil.connect(SimpleAjax, 'project', '/incall/logout', [token:token,pid:pid]);
if(login_result.isAvailable()) {
	cc_ser.counter_log_out(login_result.data);
}
return SimpleAjax.available();