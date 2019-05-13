package com.jeancoder.trade.internal.incall.cashier

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.AuthToken
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.incall.CashierService
import com.jeancoder.trade.ready.token.TokenService

def token = JC.internal.param('token');

JCLogger  Logger  = LoggerSource.getLogger(this.getClass().getName());

SimpleAjax counter = TokenService.INSTANCE.validate(token);
if(!counter.available) {
	return counter;
}
List<CashDoLog> result_list = CashierService.INSTANCE.get_counter_by_token(token);
CashDoLog do_log = result_list.get(0);

AuthToken authToken = null;
try {
	//去中心进行token 校验
	authToken = JC.internal.call(AuthToken, 'project', '/incall/token', ["pid":counter.data.pid,"token":token]);
	if (token == null) {
		TokenService.INSTANCE.invalid(token);
		return SimpleAjax.notAvailable('need_login,重新登录');
	}
} catch(Exception e) {
	Logger.error("token 校验失败",e);
	TokenService.INSTANCE.invalid(token);
	return SimpleAjax.notAvailable('need_login,重新登录');
}

//return counter;

return SimpleAjax.available('', [counter.data, authToken,do_log]);

