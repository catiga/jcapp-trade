package com.jeancoder.trade.entry.mgr

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.entity.TradePreferential
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.SSUtil

// 判断交易是否可以开发票
TradeService t_ser = TradeService.INSTANCE();

def tid = JC.request.param('tid')?.trim();

TradeInfo trade = t_ser.get_trade(tid);


List<TradePayInfo> trade_pays = t_ser.get_trade_pay(trade);
Result view = new Result();
view.setView('mgr/pay_info');
view.addObject('trade', trade);
view.addObject('result', trade_pays);

def all_types = SSUtil.findall();
view.addObject('all_pay_type', all_types);

List<TradePreferential> trade_prefs = t_ser.get_trade_pre(trade);
view.addObject('trade_prefs', trade_prefs);

return view;
