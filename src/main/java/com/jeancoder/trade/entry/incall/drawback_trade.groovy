package com.jeancoder.trade.entry.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.AuthToken
import com.jeancoder.trade.ready.dto.AuthUser
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.token.TokenService
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.RemoteUtil

def onum = JC.request.param('onum');
def oid = JC.request.param('oid');
def oc = JC.request.param('oc');

//首先校验当前用户
AuthToken token = GlobalHolder.token;
AuthUser user = token?.user;

if(user==null) {
	//检查是否传入
	def token_key = JC.request.param('token');
	SimpleAjax ret = TokenService.INSTANCE.validate(token_key);
	if(!ret.available) {
		return ret;
	}
}

TradeService trade_service = TradeService.INSTANCE();
List<TradeInfo> trades = trade_service.get_trade_by_order(oid, oc);

if(trades==null||trades.isEmpty()) {
	return SimpleAjax.notAvailable('trade_not_found,交易未找到');
}

if(trades.size()>1) {
	return SimpleAjax.notAvailable('trade_repeat,交易重复，请联系管理员');
}

TradeInfo trade = trades.get(0);
if(!trade.tss.startsWith('1')&&!trade.tss.startsWith('2')&&!trade.tss.startsWith('3')) {
	return SimpleAjax.notAvailable('trade_status_invalid,交易状态不支持退单操作');
}

// TODO 开始执行退货操作

return SimpleAjax.available();;

