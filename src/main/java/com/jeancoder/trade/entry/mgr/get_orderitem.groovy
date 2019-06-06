package com.jeancoder.trade.entry.mgr

import org.eclipse.jetty.server.handler.ContextHandler.Availability

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.result.Result
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.common.AvailabilityStatus
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.util.NativeUtil

Result view = new Result();

JCLogger Logger = LoggerSource.getLogger(this.class);
def order_id = JC.request.param('order_id');
def order_no = JC.request.param('order_no');
def oc = JC.request.param('oc');

if(oc.equals("1000")){
	//SimpleAjax order_item = NativeUtil.connect(SimpleAjax.class, "scm", "order/order_item_scm_list", ["order_no":order_id])
	SimpleAjax order_item = JC.internal.call(SimpleAjax.class, "scm", "order/order_item_scm_list", ["order_no":order_no, order_id:order_id])
	return order_item;
}else if(oc.equals("8000")){
	//SimpleAjax order_item = NativeUtil.connect(SimpleAjax.class, "crm", "order/order_mc_list", ["order_no":order_id])
	SimpleAjax order_item = JC.internal.call(SimpleAjax.class, "crm", "order/order_mc_list", ["order_no":order_no, order_id:order_id])
	return order_item;
}else if(oc.equals("8001")){
	//SimpleAjax order_item = NativeUtil.connect(SimpleAjax.class, "crm", "order/order_mc_recharge_list", ["order_no":order_id])
	SimpleAjax order_item = JC.internal.call(SimpleAjax.class, "crm", "order/order_mc_recharge_list", ["order_no":order_no, order_id:order_id])
	return order_item;
}else if(oc.equals("2000")){
	//SimpleAjax order_item = NativeUtil.connect(SimpleAjax.class, "ticketingsys", "ticketing/ticketing_choose_seat", ["order_no":order_id])
	SimpleAjax order_item = JC.internal.call(SimpleAjax.class, "ticketingsys", "ticketing/ticketing_choose_seat", ["order_no":order_no, order_id:order_id])
	return order_item;
}else if(oc.equals("2010")){
	//SimpleAjax order_item = NativeUtil.connect(SimpleAjax.class, "ticketingsys", "ticketing/ticketing_reserve_seat", ["order_no":order_id])
	SimpleAjax order_item = JC.internal.call(SimpleAjax.class, "ticketingsys", "ticketing/ticketing_reserve_seat", ["order_no":order_no, order_id:order_id])
	return order_item;
}