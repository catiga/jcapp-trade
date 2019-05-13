package com.jeancoder.trade.entry.cashier
import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.tpl.Param_100
import com.jeancoder.trade.ready.util.SSUtil

CashierService ser = CashierService.INSTANCE;

def ccid = JC.request.param('ccid')?.trim();
if(ccid=='') 
	ccid = null;

CashCounter counter = ser.get_counter(ccid);

Result view = new Result();
view.setView('cashier/cfg');
view.addObject('counter', counter);

def counter_ocs = null;
if(counter!=null) {
	counter_ocs = ser.get_counter_oc(counter.id);
}

view.addObject('counter_ocs', counter_ocs);

view.addObject('all_ocs', SSUtil.supp_order_cat);

def counter_confs = ser.find_cfts(counter);
for(x in counter_confs) {
	try {
		Param_100 param = JackSonBeanMapper.fromJson(x.param, Param_100);
		println param;
		x.paraminfo = param;
	} catch(any) {
	}
}

view.addObject('counter_confs', counter_confs);

return view;



