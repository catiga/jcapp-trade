package com.jeancoder.trade.entry.cashier.aj
import com.jeancoder.app.sdk.JC
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CounterConf
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.tpl.OcParam
import com.jeancoder.trade.ready.tpl.Param_100
import com.jeancoder.trade.ready.util.SSUtil

CashierService ser = CashierService.INSTANCE;

def id = JC.request.param('id')?.trim();
CounterConf conf = ser.get_cft(id);
if(conf==null) {
	return SimpleAjax.notAvailable('obj_not_found,配置未找到');
}

def sel_oc = JC.request.param('sel')?.trim().split(',');
def ocs = SSUtil.supp_order_cat;
def sel_ocs = [];
for(x in ocs) {
	for(y in sel_oc) {
		if(x.code==y) {
			sel_ocs.add(new OcParam(oc:x.code, name:x.name));
		}
	}
}

Param_100 param = new Param_100();
param.oc = sel_ocs;

conf.param = param;

ser.update_cft(conf);

return SimpleAjax.available('', JackSonBeanMapper.listToJson(sel_ocs));





