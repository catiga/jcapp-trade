package com.jeancoder.trade.entry.incall.ticket

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.ResponseSource
import com.jeancoder.core.http.JCResponse
import com.jeancoder.trade.ready.dto.ProjectConfigSimple
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.printer.FeedPrinter
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.NativeUtil

//两者需要对应
def trade_id = JC.request.param('tid');		//要打印的交易
def order_id = JC.request.param('oid');		//要打印的订单

TradeInfo trade = TradeService.INSTANCE().get_trade(trade_id);
List<TradeOrder> trade_orders = TradeService.INSTANCE().trade_items(trade);

TradeOrder print_order = null;
for(x in trade_orders) {
	if(x.id.toString()==order_id) {
		print_order = x;
		break;
	}
}

def result = NativeUtil.connectAsArray(ProjectGeneralConfig.class, 'project', '/incall/project_pts', [tyc:'1000',pid:print_order.pid]);

//def sys = [];
//if(result!=null) {
//	result.each{
//		item->if(item.sc_code[3]=='0') sys.add(new ProjectConfigSimple(tyc:item.sc_type,code:item.sc_code,name:item.sc_name,disname:item.disname,tips:item.sc_info))
//	}
//}

def smarttemplate = '<html><body>check_error</body></html>';
if(print_order!=null && print_order.oc=='1000') {
	smarttemplate = new FeedPrinter().get_printer_content(trade, print_order);
}
JCResponse response = ResponseSource.getResponse();
response.setContentType('text/html');
response.setCharacterEncoding("UTF-8");
response.getWriter().write(smarttemplate);


