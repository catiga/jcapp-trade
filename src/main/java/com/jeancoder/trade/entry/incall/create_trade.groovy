package com.jeancoder.trade.entry.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.DataTcSsReserveOrderInfo
import com.jeancoder.trade.ready.dto.DataTcSsSaleOrderInfo
import com.jeancoder.trade.ready.dto.McRechargeOrderDto
import com.jeancoder.trade.ready.dto.OrderInfo
import com.jeancoder.trade.ready.dto.OrderMcDto
import com.jeancoder.trade.ready.dto.ServiceOrder
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.SSUtil

def oc = JC.request.param('oc')?.trim();
def od = JC.request.param('od')?.trim();
def tnum = JC.request.param('tnum')?.trim();
def log_id = JC.internal.param('log_id');
if(tnum=='null') {
	tnum = null;
}
println 'oc======' + oc;

//if(oc!='1000'&&oc!='2000') {
if(!SSUtil.is_valid_order_type(oc)) {
	return SimpleAjax.notAvailable('order_type_error,不支持的订单类型');
}
od = URLDecoder.decode(od, 'UTF-8');
od = URLDecoder.decode(od, 'UTF-8');
	
println 'od=' + od;

def o = null;
if(oc=='1000') {
	o = JackSonBeanMapper.fromJson(od, OrderInfo.class);
} else if(oc=='2000') {
	o = JackSonBeanMapper.fromJson(od, DataTcSsSaleOrderInfo.class);
} else if (oc == '2010') {
	o = JackSonBeanMapper.fromJson(od, DataTcSsReserveOrderInfo.class);
} else if (oc=='8000') {
	o = JackSonBeanMapper.fromJson(od, OrderMcDto.class);
} else if(oc=='8001') {
	o = JackSonBeanMapper.fromJson(od, McRechargeOrderDto.class);
} else if(oc=='5000') {
	//预约类订单
	o = JackSonBeanMapper.fromJson(od, ServiceOrder);
}
if(o==null) {
	return SimpleAjax.notAvailable('order_data_empty,订单信息为空');
}

if (log_id == null || log_id == "") {
	log_id = null;
} else {
	log_id = new BigInteger(log_id.toString());
}

TradeInfo trade = null;

TradeService trade_service = TradeService.INSTANCE();
if(tnum!=null&&tnum!='') {
	trade = trade_service.get_trade_by_num(tnum);
	if(trade==null) {
		return SimpleAjax.notAvailable('trade_not_exist,交易不存在');
	}
	if(trade.tss!='0000') {
		return SimpleAjax.notAvailable('trade_status_invalid,交易状态非法');
	}
	
	//开始进行交易合并操作
	trade = trade_service.merge_trade(trade, [o],log_id);
} else {
	trade = trade_service.create_trade([o]);
}

return SimpleAjax.available('', trade);

