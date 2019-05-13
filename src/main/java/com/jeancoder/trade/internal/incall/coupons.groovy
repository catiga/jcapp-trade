package com.jeancoder.trade.internal.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.GlobalHolder

def tnum = JC.internal.param('tnum');
def phone = JC.internal.param('phone');
def pid = JC.internal.param('pid');

TradeInfo trade = TradeService.INSTANCE().get_trade_by_num(tnum);
if(trade==null) {
	return SimpleAjax.notAvailable('coupon_empty,交易信息为空');
}

pid = trade.pid;

if(!phone) {
	return SimpleAjax.available();
}
def param = [mobile:phone,pid:pid, sid:trade.storeid];

def coupon_list = JC.internal.call('market', '/coupon/coupon_list', param);
def coupon_result = JackSonBeanMapper.jsonToMap(coupon_list);
if(coupon_result['code']!='0') {
	return SimpleAjax.notAvailable('coupon_empty,券服务通讯异常' + coupon_result['code']);
}

return SimpleAjax.available('', coupon_result['list']);
