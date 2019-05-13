package com.jeancoder.trade.entry.mgr

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.entity.InvoiceSet
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeInvoice
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.ser.InvoiceService
import com.jeancoder.trade.ready.ser.TradeService

def id = JC.request.param('id');
def t_id = JC.request.param('t_id');

Result view = new Result();
view.setView('mgr/invoice_detail');

TradeInfo trade = TradeService.INSTANCE().get_trade(t_id);
view.addObject('trade', trade);

def sql = 'select * from TradeInvoice where flag!=? and t_id=? and id=? order by a_time desc';
TradeInvoice invoice = JcTemplate.INSTANCE().get(TradeInvoice, sql, -1, t_id, id);

//获取发票设置
def invoice_sets = InvoiceService.INSTANCE().find_invoice_sets();
view.addObject('invoice_sets', invoice_sets);

if(invoice) {
	view.addObject('invoice', invoice);
	InvoiceSet select_invoice_set = null;
	for(x in invoice_sets) {
		if(x.id==invoice.invo_set_id) {
			select_invoice_set = x;
			break;
		}
	}
	
	view.addObject('select_invoice_set', select_invoice_set);
	
	List<TradePayInfo> trade_pays = TradeService.INSTANCE().get_trade_pay(trade);
	view.addObject('all_trade_pays', trade_pays);
	
	//获取交易下的订单列表
	List<TradeOrder> trade_orders = TradeService.INSTANCE().trade_items(trade);
	view.addObject('trade_orders', trade_orders);
	
	//计算总价
	BigDecimal total_amount = new BigDecimal('0');
	trade_orders.each({total_amount += it.pay_amount});
	view.addObject('total_amount', total_amount);
	//税额
	def tax_amount = total_amount.multiply(new BigDecimal(select_invoice_set.tax_fee)).divide(new BigDecimal(100));
	view.addObject('tax_amount', tax_amount);
	//不含税总价
	view.addObject('none_tax_amount', total_amount.subtract(tax_amount));
}


return view;




