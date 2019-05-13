package com.jeancoder.trade.entry.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.AuthToken
import com.jeancoder.trade.ready.dto.SysProjectInfo
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.incall.CashierService
import com.jeancoder.trade.ready.token.TokenService
import com.jeancoder.trade.ready.util.NativeUtil


def start = System.currentTimeMillis();
def token = JC.request.param('token');
def domain = JC.request.param('domain');

JCLogger  Logger  = LoggerSource.getLogger(this.getClass().getName());
CashierService cashier_service = CashierService.INSTANCE;

//本地token校验
SimpleAjax counter = TokenService.INSTANCE.validate(token);
if(!counter.available) {
	return counter;
}
try {
	//去中心进行token 校验
	//SysProjectInfo project  = NativeUtil.connect(SysProjectInfo.class, 'project', '/incall/project', ["domain":domain]);
	
//	SysProjectInfo project = JC.internal.call(SysProjectInfo, 'project', '/incall/project', ["domain":domain])
//	println "domain" + domain + " pid___" + project.id;
	
	//AuthToken authToken = NativeUtil.connect(AuthToken.class, 'project', '/incall/token', ["pid":project.id,"token":token]);
	AuthToken authToken = JC.internal.call(AuthToken, 'project', '/incall/token', ["pid":counter.data.pid,"token":token]);
	if (token == null) {
		TokenService.INSTANCE.invalid(token);
		return SimpleAjax.notAvailable('need_login,重新登录');
	}
} catch(Exception e) {
	Logger.error("token 校验失败",e);
	TokenService.INSTANCE.invalid(token);
	return SimpleAjax.notAvailable('need_login,重新登录');
}

return counter;

