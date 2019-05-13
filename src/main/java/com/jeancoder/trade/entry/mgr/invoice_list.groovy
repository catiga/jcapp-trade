package com.jeancoder.trade.entry.mgr

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeInvoice
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.ser.InvoiceService
import com.jeancoder.trade.ready.ser.TradeService

Result view = new Result();
view.setView('mgr/invoice_list');

// 判断交易是否可以开发票
TradeService t_ser = TradeService.INSTANCE();

def tid = JC.request.param('tid')?.trim();

TradeInfo trade = t_ser.get_trade(tid);
if(trade!=null) {
	view.addObject('trade', trade);
	
	def sql = 'select * from TradeInvoice where flag!=? and t_id=?';
	def trade_invoices = JcTemplate.INSTANCE().find(TradeInvoice, sql, -1, trade.id);
	view.addObject('trade_invoices', trade_invoices);
}

return view;




