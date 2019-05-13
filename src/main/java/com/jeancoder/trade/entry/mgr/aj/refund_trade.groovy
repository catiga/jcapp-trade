package com.jeancoder.trade.entry.mgr.aj

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.RequestSource
import com.jeancoder.core.http.JCRequest
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.notify.NotifyService
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.ser.TriggerService
import com.jeancoder.trade.ready.unipay.RefundService
import com.jeancoder.trade.ready.util.GlobalHolder

TradeService trade_service = TradeService.INSTANCE();
TriggerService trigger_service = TriggerService.INSTANCE();

JCRequest  request = RequestSource.getRequest();
def tid = JC.request.param('tid')?.trim();
def domain = request.getServerName();
TradeInfo trade = trade_service.get_trade(tid);
if(trade==null) {
	return SimpleAjax.notAvailable('obj_not_found,交易未找到');
}

if(trade.tss.startsWith('8')) {
	//说明该笔订单已经退款
	return SimpleAjax.notAvailable('status_invalid,交易已退款');
}
List<TradePayInfo> trade_pay = trade_service.get_trade_pay(trade);
//找到主支付方式
TradePayInfo main_tps = null;
def exist_60 = false;
for(x in trade_pay) {
	if(x.pay_type=='10') {
		main_tps = x;
	} else if(x.pay_type=='60') {
		exist_60 = true;
	}
}
if(main_tps==null) {
	return SimpleAjax.notAvailable('op_no_need,该交易无主支付方式，无需退款操作');
}
if(exist_60) {
	return SimpleAjax.notAvailable('op_no_need,交易已存在退款信息，请勿重复操作');
}
def ct = main_tps.pay_code;

//获取项目支付信息配置
ProjectGeneralConfig supp_config = JC.internal.call(ProjectGeneralConfig, 'project', '/incall/project_single_pt', [pid:GlobalHolder.proj.id, sc_type:'1000', sc_code:ct]);
if(supp_config==null||supp_config.partner==null) {
	return SimpleAjax.notAvailable('pay_config_error');
}
PayResult ret_data = new PayResult();
// 先通知退订单
//def op_errors = NotifyService.pub(trade, 'refund');
def op_errors = NotifyService.incallPub(trade, 'refund');
if(!op_errors.empty) {
	ret_data.other = op_errors;
	ret_data.text = ret_data.text + '.库存回退或退票操作失败，请检查订单详情';
	return SimpleAjax.notAvailable('', ret_data);
}

RefundService refund_service = RefundService.INSTANCE();

def other_param = [:];
other_param.put('trans_id', main_tps.trans_id);
other_param.put('trans_user_id', main_tps.trans_user_id);
other_param.put('trans_get_account', main_tps.trans_get_account);
other_param.put('trans_type', main_tps.trans_type);
other_param.put('pay_code', main_tps.pay_code);
other_param.put('domain',domain)
other_param.put('pid', GlobalHolder.proj.id);

ret_data = refund_service.refund_pay_router(supp_config, ct, trade, main_tps, other_param)

if(ret_data.code=='0') {
	def trade_items = trade_service.trade_items(trade);
	for (TradeOrder item : trade_items) {
		item.oss = '8800';
		item.c_time = new Timestamp(new Date().getTime())
		JcTemplate.INSTANCE().update(item);
	}
	return SimpleAjax.available('', ret_data);
}

return SimpleAjax.notAvailable('', ret_data);



