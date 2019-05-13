package com.jeancoder.trade.entry.pt

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.jdbc.JcPage
import com.jeancoder.trade.ready.entity.SysSupp
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.SuppService
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.SSUtil
import com.jeancoder.trade.ready.util.TradeTssUtil

def supp_dics = SSUtil.findall();

SuppService ser = SuppService.INSTANCE();
def result = ser.findall();

for(x in supp_dics) {
	def is_in = false;
	for(y in result) {
		if(x.tyc==y.tyc&&x.code==y.code) {
			x.id = y.id;
			x.c_time = y.c_time;
			//x.rb = y.rb;
			x.flag = y.flag;
			is_in = true;
			break;
		}
	}
	if(!is_in) {
		x.flag = -1;
	}
}

//补充自定义支付方式
for(y in result) {
	if(y.code_cat=='900') {
		supp_dics+=y;
	}
}


Result view = new Result();
view.setView('pt/index');
view.addObject('supp_dics', supp_dics);

return view;

