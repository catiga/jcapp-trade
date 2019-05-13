package com.jeancoder.trade.entry.mgr.aj

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.jdbc.JcPage
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.StoreInfo
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.RemoteUtil
import com.jeancoder.trade.ready.util.SSUtil
import com.jeancoder.trade.ready.util.TradeTssUtil

TradeService t_ser = TradeService.INSTANCE();

def tid = JC.request.param('tid')?.trim();

def trade = t_ser.get_trade(tid);
if(trade==null) return SimpleAjax.notAvailable('trade_not_found');



return SimpleAjax.available('', trade);

