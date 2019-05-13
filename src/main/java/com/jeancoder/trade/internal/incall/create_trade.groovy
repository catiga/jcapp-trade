package com.jeancoder.trade.internal.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.DataTcSsReserveOrderInfo
import com.jeancoder.trade.ready.dto.DataTcSsSaleOrderInfo
import com.jeancoder.trade.ready.dto.McRechargeOrderDto
import com.jeancoder.trade.ready.dto.OrderInfo
import com.jeancoder.trade.ready.dto.OrderMcDto
import com.jeancoder.trade.ready.dto.SaleOrder
import com.jeancoder.trade.ready.dto.ServiceOrder
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.SSUtil

def oc = JC.internal.param('oc')?.toString()?.trim();
def od = JC.internal.param('od')?.toString()?.trim();
def tnum = JC.internal.param('tnum')?.toString()?.trim();
def pid = JC.internal.param('pid')?.toString()?.trim();
def log_id = JC.internal.param('log_id');

try {
	pid = new BigInteger(pid.toString());
} catch(any) {
	any.printStackTrace();
	return SimpleAjax.notAvailable('pid_error,pid参数错误');
}
if(tnum=='null') {
	tnum = null;
}
if(!SSUtil.is_valid_order_type(oc)) {
	return SimpleAjax.notAvailable('order_type_error,不支持的订单类型');
}
od = URLDecoder.decode(od, 'UTF-8');
od = URLDecoder.decode(od, 'UTF-8');

JCLogger logger = LoggerSource.getLogger();
def o = null;
if(oc=='1000') {
	o = JackSonBeanMapper.fromJson(od, OrderInfo.class);
	//处理一下sku
	if(o&&o.items) {
		for(x in o.items) {
			if(x.goods_sku_name) {
				try {
					x.goods_sku_name = JackSonBeanMapper.jsonToMap(x.goods_sku_name);
				} catch(any) {}
			}
		}
	}
} else if(oc=='2000') {
	o = JackSonBeanMapper.fromJson(od, SaleOrder.class);
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
	trade = trade_service.merge_trade(trade, [o]);
} else {
	trade = trade_service.create_trade(pid, [o],log_id);
}

return SimpleAjax.available('', trade);

