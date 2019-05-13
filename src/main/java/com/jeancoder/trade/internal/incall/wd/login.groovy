package com.jeancoder.trade.internal.incall.wd

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.ResponseSource
import com.jeancoder.core.http.JCCookie
import com.jeancoder.core.http.JCResponse
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.ser.KitchenService
import com.jeancoder.trade.ready.util.JsonUtil
import com.jeancoder.trade.ready.util.RemoteUtil

def jc_name = JC.internal.param('jc_name');
def jc_pwd = JC.internal.param('jc_pwd');
def jc_counter = JC.internal.param('jc_counter');
def jc_pid = JC.internal.param('jc_pid');

KitchenService ser = KitchenService.INSTANCE;
CashCounter counter = ser.get_counter(jc_counter);
if(counter==null||counter.inuse==1) {
	//使用中，不可登入
	return SimpleAjax.notAvailable('counter_in_using');
}

//执行登录操作
def login_result = JC.internal.call('project', '/incall/login', [jc_name:jc_name,jc_pwd:jc_pwd,jc_pid:jc_pid]);

SimpleAjax ret_obj = JsonUtil.convertObj(SimpleAjax, login_result);
if(!ret_obj.available) {
	return ret_obj;
}
def login_token = ret_obj.data[0]['token'];
//修改收银台状态
def login_success = ser.counter_log_in(new BigInteger(jc_pid.toString()), counter, jc_name, login_token);

return SimpleAjax.available('', login_token);





