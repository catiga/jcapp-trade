package com.jeancoder.trade.entry.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.ser.TriggerService

def tnum = JC.request.param('tnum');

TradeService trade_service = TradeService.INSTANCE();
TriggerService trigger_service = TriggerService.INSTANCE();

TradeInfo trade = trade_service.get_trade_by_num(tnum);

if(trade==null) {
	return SimpleAjax.notAvailable('trade_not_found,交易未找到');
}
if(trade.tss.startsWith('0')) {
	return SimpleAjax.notAvailable('no_pay_info,该交易没有付款信息');
}

//获取trade的支付信息
List<TradePayInfo> trade_pays = trade_service.get_trade_pay(trade);
return SimpleAjax.available('', [trade, trade_pays]);

