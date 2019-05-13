package com.jeancoder.trade.entry.mgr.aj

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.unipay.RefundService

TradeService t_ser = TradeService.INSTANCE();

def toid = JC.request.param('toid')?.trim();

TradeOrder trade_order = t_ser.get_trade_order_by_toid(toid);
if(trade_order==null) {
	return SimpleAjax.notAvailable('obj_not_found,交易订单未找到');
}

TradeInfo trade = t_ser.get_trade(trade_order.t_id);
if(trade==null) {
	return SimpleAjax.notAvailable('obj_not_found,交易未找到');
}

if(trade_order.oss.startsWith('8')) {
	//说明该笔订单已经退款
	return SimpleAjax.notAvailable('status_invalid,交易订单已退款');
}
List<TradePayInfo> trade_pay = t_ser.get_trade_pay(trade);
//找到主支付方式
TradePayInfo main_tps = null;
for(x in trade_pay) {
	if(x.pay_type=='10') {
		main_tps = x;
		break;
	}
}
if(main_tps==null) {
	return SimpleAjax.notAvailable('op_no_need,该交易无主支付方式，无需退款操作');
}
def ct = main_tps.pay_code;

//获取项目支付信息配置
ProjectGeneralConfig supp_config = JC.internal.call(ProjectGeneralConfig, 'project', '/incall/project_single_pt', [sc_type:'1000', sc_code:ct]);
if(supp_config==null||supp_config.partner==null) {
	return SimpleAjax.notAvailable('pay_config_error');
}

RefundService refund_service = RefundService.INSTANCE();

return SimpleAjax.available('', trade);

