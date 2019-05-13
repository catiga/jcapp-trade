package com.jeancoder.trade.entry.mgr

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.ser.InvoiceService
import com.jeancoder.trade.ready.ser.TradeService

Result view = new Result();
view.setView('mgr/invoice');

// 判断交易是否可以开发票
TradeService t_ser = TradeService.INSTANCE();

def tid = JC.request.param('tid')?.trim();
def gs = JC.request.param('gs')?.trim()?.split(",");

TradeInfo trade = t_ser.get_trade(tid);
if(trade!=null) {
	view.addObject('trade', trade);
	List<TradePayInfo> trade_pays = t_ser.get_trade_pay(trade);
	view.addObject('all_trade_pays', trade_pays);
	
	def trade_pay_invoice = [];
	if(trade_pays) {
		for(x in trade_pays) {
			TradePayInfo select_pay = null;
			for(y in gs) {
				if(y.toString()==x.id.toString()) {
					select_pay = x;
					break;
				}
			}
			if(select_pay!=null&&select_pay.pay_type=='10') {
				trade_pay_invoice.add(select_pay);
			}
		}
	}
	view.addObject('trade_pay_invoice', trade_pay_invoice);
	
	//获取交易下的订单列表
	List<TradeOrder> trade_orders = t_ser.trade_items(trade);
	view.addObject('trade_orders', trade_orders);
	
	//获取发票设置
	def invoice_sets = InvoiceService.INSTANCE().find_invoice_sets();
	view.addObject('invoice_sets', invoice_sets);
	
	//计算总价
	BigDecimal total_amount = new BigDecimal('0');
	trade_orders.each({total_amount += it.pay_amount});
	view.addObject('total_amount', total_amount);
}

return view;




