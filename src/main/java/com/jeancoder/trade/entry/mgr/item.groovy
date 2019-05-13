package com.jeancoder.trade.entry.mgr

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.TradeTssUtil

TradeService t_ser = TradeService.INSTANCE();

def tid = JC.request.param('tid');
def tss = JC.request.param('tss');
TradeInfo trade = t_ser.get_trade(tid);
List<TradeOrder> result = t_ser.trade_items(trade);

Result view = new Result();
view.setView('mgr/item');
view.addObject('result', result);

view.addObject('trade', trade);
def all_tss = TradeTssUtil.all();
view.addObject('tss', tss);
view.addObject('all_tss', all_tss);
return view;