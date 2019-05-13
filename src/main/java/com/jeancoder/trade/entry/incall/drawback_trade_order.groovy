package com.jeancoder.trade.entry.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.AuthToken
import com.jeancoder.trade.ready.dto.AuthUser
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.notify.NotifyService
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.token.TokenService
import com.jeancoder.trade.ready.unipay.RefundOrderService
import com.jeancoder.trade.ready.unipay.RefundService
import com.jeancoder.trade.ready.util.GlobalHolder

def onum = JC.request.param('onum');
def oid = JC.request.param('oid');
def oc = JC.request.param('oc');

//首先校验当前用户
AuthToken token = GlobalHolder.token;
AuthUser user = token?.user;
def token_key = token?.session?.token;

if(token_key==null) {
	//检查是否传入
	token_key = JC.request.param('token');
}

SimpleAjax ret = TokenService.INSTANCE.validate(token_key);
if(!ret.available) {
	return ret;
}

TradeService trade_service = TradeService.INSTANCE();
List<TradeInfo> trades = trade_service.get_trade_by_order(oid, oc);

if(trades==null||trades.isEmpty()) {
	return SimpleAjax.notAvailable('trade_not_found,交易未找到');
}

if(trades.size()>1) {
	return SimpleAjax.notAvailable('trade_repeat,交易重复，请联系管理员');
}

TradeInfo trade = trades.get(0);
if(!trade.tss.startsWith('1')&&!trade.tss.startsWith('2')&&!trade.tss.startsWith('3')) {
	return SimpleAjax.notAvailable('trade_status_invalid,交易状态不支持退单操作');
}

//开始执行交易旗下单笔订单退款
List<TradeOrder> trade_orders = trade_service.trade_items(trade);
//找到要操作的 trade order
TradeOrder aim_order = null;
for(x in trade_orders) {
	if(x.oc==oc&&x.order_id.toString()==oid) {
		aim_order = x; break;
	}
}
if(aim_order==null) {
	return SimpleAjax.notAvailable('obj_not_found,交易订单未找到');
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

RefundOrderService refund_service = RefundOrderService.INSTANCE();

def other_param = [:];
other_param.put('trans_id', main_tps.trans_id);
other_param.put('trans_user_id', main_tps.trans_user_id);
other_param.put('trans_get_account', main_tps.trans_get_account);
other_param.put('trans_type', main_tps.trans_type);
other_param.put('pay_code', main_tps.pay_code);
other_param.put('pid', GlobalHolder.proj.id);

PayResult ret_data = refund_service.refund_pay_router(supp_config, ct, trade, aim_order, main_tps, other_param)

if(ret_data.code=='0') {
	//退款成功
	def op_errors = NotifyService.pub(trade, 'refund');
	if(!op_errors.empty) {
		ret_data.other = op_errors;
		ret_data.text = ret_data.text + '.库存回退或退票操作失败，请检查订单详情';
	}
	return SimpleAjax.available('', ret_data);
}

return SimpleAjax.notAvailable('', ret_data);

