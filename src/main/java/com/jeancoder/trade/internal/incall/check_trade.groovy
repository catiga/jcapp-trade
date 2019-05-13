package com.jeancoder.trade.internal.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.ProjectConfigSimple
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService

def tnum = JC.internal.param('tnum')?.toString()?.trim();
def pid = JC.internal.param('pid');
try {
	pid = new BigInteger(pid.toString());
} catch(any) {
	any.printStackTrace();
	return SimpleAjax.notAvailable('pid_error,pid参数错误');
}

TradeInfo trade = TradeService.INSTANCE().get_trade_by_num(tnum);

if(!trade) {
	return SimpleAjax.notAvailable('obj_not_found,交易未找到');
}


//获取交易可用支付方式
SimpleAjax result = JC.internal.call(SimpleAjax, 'project', '/incall/project_pts', [tyc:'1000', pid:trade.pid]);

def sys = [];
if(result==null||!result.available) {
	return SimpleAjax.notAvailable('empty');
}

result.data.each{
	item->
	if(item.sc_code[3]=='0') 
		sys.add(new ProjectConfigSimple(tyc:item.sc_type,code:item.sc_code,name:item.sc_name,disname:item.disname,tips:item.sc_info))
}

return SimpleAjax.available('', [trade, sys]);
