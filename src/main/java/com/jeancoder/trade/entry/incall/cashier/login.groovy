package com.jeancoder.trade.entry.incall.cashier

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.util.RemoteUtil

def jc_name = JC.request.param('jc_name');
def jc_pwd = JC.request.param('jc_pwd');
def jc_counter = JC.request.param('jc_counter');

CashierService ser = CashierService.INSTANCE;
CashCounter counter = ser.get_counter(jc_counter);
if(counter==null||counter.inuse==1) {
	//使用中，不可登入
	return SimpleAjax.notAvailable('counter_in_using');
}

//执行登录操作
def login_result = RemoteUtil.connect('project', '/incall/login', [jc_name:jc_name,jc_pwd:jc_pwd]);
println login_result;
if(login_result==null||login_result=='') {
	return SimpleAjax.notAvailable('uname_pwd_error');
}
//修改收银台状态
def login_success = ser.counter_log_in(counter, jc_name, login_result);

//JCResponse response = ResponseSource.getResponse();
//JCCookie cookie = new JCCookie('_c_u_k_adm_', login_result);
//cookie.setPath('/');
//response.addCookie(cookie);
return SimpleAjax.available('', login_result);





