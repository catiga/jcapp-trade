package com.jeancoder.trade.entry.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.GlobalHolder

def tnum = JC.request.param('tnum')?.trim();
def phone = JC.request.param('phone')?.trim();

TradeInfo trade = TradeService.INSTANCE().get_trade_by_num(tnum);
if(trade==null) {
	return SimpleAjax.notAvailable('coupon_empty,交易信息为空');
}

if(!phone) {
	return SimpleAjax.available();
}
def param = [mobile:phone,pid:GlobalHolder.proj.id, sid:trade.storeid];

def coupon_list = JC.internal.call('market', '/coupon/coupon_list', param);
def coupon_result = JackSonBeanMapper.jsonToMap(coupon_list);
if(coupon_result['code']!='0') {
	return SimpleAjax.notAvailable('coupon_empty,券服务通讯异常' + coupon_result['code']);
}

return SimpleAjax.available('', coupon_result['list']);
