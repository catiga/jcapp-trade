package com.jeancoder.trade.internal.incall

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.uncertain.KeepClose

def tnum = JC.internal.param('tnum');

TradeInfo info = TradeService.INSTANCE().get_trade_by_num(tnum);

if(info.tss!='0000') {
	return SimpleAjax.notAvailable('status_invalid,交易状态不支持取消操作');
}

def orderss = '';
List<TradeOrder> trade_orders = TradeService.INSTANCE().trade_items(info);
for (TradeOrder order : trade_orders) {
	def close = KeepClose.CloseURL[order.oc];
	def orders = order.order_num + ","+order.oc;
	JC.internal.call(SimpleAjax, close.name, close.url,[orders:orders,pid:info.pid.toString()]);
	orderss += orders + ";"
}
JC.thread.run(5000, {
	SimpleAjax relse = JC.internal.call(SimpleAjax, 'market', '/coupon/clear_coupon_info',[orders:orderss,pid:info.pid.toString()]);
	if (relse == null || !relse.available) {
		return;
	}
	return info;
}, {
	info.c_time = new Timestamp(new Date().getTime())
	info.tss = '9000';
	JcTemplate.INSTANCE().update(info);
})

return SimpleAjax.available();