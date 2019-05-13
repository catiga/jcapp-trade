package com.jeancoder.trade.internal.incall.wd

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.AuthToken
import com.jeancoder.trade.ready.token.KitchenTokenService

def token = JC.internal.param('token');

JCLogger  Logger  = LoggerSource.getLogger(this.getClass().getName());

SimpleAjax counter = KitchenTokenService.INSTANCE.validate(token);
if(!counter.available) {
	return counter;
}
AuthToken authToken = null;
try {
	//去中心进行token 校验
	authToken = JC.internal.call(AuthToken, 'project', '/incall/token', ["pid":counter.data.pid,"token":token]);
	if (token == null) {
		KitchenTokenService.INSTANCE.invalid(token);
		return SimpleAjax.notAvailable('need_login,重新登录');
	}
} catch(Exception e) {
	Logger.error("token 校验失败",e);
	KitchenTokenService.INSTANCE.invalid(token);
	return SimpleAjax.notAvailable('need_login,重新登录');
}

return SimpleAjax.available('', [counter.data, authToken]);