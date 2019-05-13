package com.jeancoder.trade.entry.incall.reg

import com.jeancoder.core.util.MD5Util
import com.jeancoder.trade.ready.entity.TriggerRog
import com.jeancoder.trade.ready.ser.TriggerService
import com.jeancoder.trade.ready.util.RemoteUtil

def oc = '8001';
def on = 'gmc1808232024563500010';

TriggerService tri = TriggerService.INSTANCE();

TriggerRog rog = tri.get_by_order_type(oc);
if(rog==null) {
	println 'fk oc=' + oc + ' is not registered, exception and error';
	return;
}

def string = 'oc=' + oc + '&on=' + on;

def sign = MD5Util.getStringMD5(string + rog.token);


def ret_str = RemoteUtil.connect(rog.appcode, rog.callback, [oc:oc,on:on,sign:sign]);
println ret_str;

def url = 'http://192.168.1.10:8080/' + rog.appcode + rog.callback + '?' + string + '&sign=' + sign;

println url;


