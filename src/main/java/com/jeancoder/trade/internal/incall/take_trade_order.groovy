package com.jeancoder.trade.internal.incall

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.PrinterDto
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.printer.FeedPrinter
import com.jeancoder.trade.ready.printer.PrinterUtil
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.token.TokenService
import com.jeancoder.trade.ready.util.NativeUtil

def onum = JC.internal.param('onum')?.toString();
def oid = JC.internal.param('oid')?.toString();
def oc = JC.internal.param('oc')?.toString();

//首先校验当前用户
def token_key = JC.internal.param('token');

SimpleAjax ret = TokenService.INSTANCE.validate(token_key);
if(!ret.available) {
	return ret;
}

CashCounter current_cash_counter = ret.data;

TradeService trade_service = TradeService.INSTANCE();
List<TradeInfo> trades = trade_service.get_trade_by_order(oid, oc);

if(trades==null||trades.isEmpty()) {
	return SimpleAjax.notAvailable('trade_not_found,交易未找到');
}

if(trades.size()>1) {
	return SimpleAjax.notAvailable('trade_repeat,交易重复，请联系管理员');
}

TradeInfo trade = trades.get(0);
//if(!trade.tss.startsWith('1')) {
//	return SimpleAjax.notAvailable('trade_status_invalid,交易状态不支持取货操作');
//}

//开始执行交易旗下单笔订单退款
List<TradeOrder> trade_orders = trade_service.trade_items(trade);
//找到要操作的 trade order
TradeOrder aim_order = null;
for(x in trade_orders) {
	if(x.oc==oc&&x.order_id.toString()==oid.toString()) {
		aim_order = x; break;
	}
}
if(aim_order==null) {
	return SimpleAjax.notAvailable('obj_not_found,交易订单未找到');
}

if(aim_order.oss.startsWith('3')) {
	return SimpleAjax.notAvailable('status_invalid,订单已经完成取票不可重复操作');
}
if(!aim_order.oss.startsWith('2') &&!aim_order.oss.startsWith('1')) {
	return SimpleAjax.notAvailable('status_invalid,订单未支付不可取票');
}

aim_order.oss = '3000';
aim_order.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
JcTemplate.INSTANCE().update(aim_order);

def result = NativeUtil.connectAsArray(ProjectGeneralConfig.class, 'project', '/incall/project_pts', [tyc:'1000',pid:trade.pid]);

/*
 * 统一注释，替换成下面的代码 20190505
//获取打印模板
def smarttemplate = '<html><body>check_error</body></html>';
if(aim_order!=null && aim_order.oc=='1000') {
	smarttemplate = new FeedPrinter().get_printer_content(trade, aim_order);
}
smarttemplate = URLEncoder.encode(smarttemplate, 'UTF-8');
smarttemplate = URLEncoder.encode(smarttemplate, 'UTF-8');
return SimpleAjax.available('', smarttemplate);
*/


def pid = trade.pid;
def ccid = current_cash_counter.id;

def smarttemplate = "<html></html>";
if(aim_order.oc=='1000') {
	smarttemplate = new FeedPrinter().get_printer_content(trade, aim_order).toString();
	smarttemplate = URLEncoder.encode(smarttemplate, 'UTF-8');
	smarttemplate = URLEncoder.encode(smarttemplate, 'UTF-8');
	PrinterDto pricedto = PrinterUtil.getPrinterDtoLsit(pid,ccid, aim_order.oc, smarttemplate);
	
	List<PrinterDto> ret_ticket_printer = [];
	ret_ticket_printer.add(pricedto);
	smarttemplate = ret_ticket_printer;
} else if(aim_order.oc=='2000') {
	//打印电影票
	String default_smarttemplate = new FeedPrinter().get_printer_content_for_ticket(trade, aim_order);				//获得默认打印票务模版
	PrinterDto printer_dto_s = PrinterUtil.getPrinterDtoLsit(pid,ccid, aim_order.oc, default_smarttemplate);	//获取收银台对应的配置信息
	smarttemplate = PrinterUtil.fill_ticket_printer_dto(trade, aim_order, printer_dto_s);
}

return SimpleAjax.available('', smarttemplate);


