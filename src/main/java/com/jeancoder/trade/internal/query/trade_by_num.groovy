package com.jeancoder.trade.internal.query

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder

def tnum = JC.internal.param('tnum');
def pid = JC.internal.param('pid');

def sql = 'select * from TradeInfo where tnum=? and pid=? and flag!=?';

TradeInfo trade = JcTemplate.INSTANCE().get(TradeInfo, sql, tnum, pid, -1);
List<TradeOrder> trade_orders = null;

if(trade) {
	sql = 'select * from TradeOrder where t_id=?';
	trade_orders = JcTemplate.INSTANCE().find(TradeOrder, sql, trade.id);
	
	for(x in trade_orders) {
		if(x.oc=='2000') {
			//影票订单获取详情
			SimpleAjax ret_tickets = JC.internal.call(SimpleAjax, 'ticketingsys', '/ticketing/ticket_order_detail', [order_id:x.order_id]);
			if(ret_tickets && ret_tickets.available) {
				def tcss_seats = ret_tickets.data[1];
				x.order_detail = tcss_seats;
			}
		}
	}
	
	return SimpleAjax.available('', [trade, trade_orders]);
}

return SimpleAjax.notAvailable('obj_not_found,交易信息不存在');
