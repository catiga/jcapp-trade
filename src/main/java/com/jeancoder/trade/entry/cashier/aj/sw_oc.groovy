package com.jeancoder.trade.entry.cashier.aj

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.util.SSUtil

import groovy.swing.binding.JScrollBarProperties

def cc_id = JC.request.param('cc_id')?.trim();
def sel_oc = JC.request.param('sel')?.trim().split(',');

println "sel__"+ JC.request.param('sel')
CashCounter counter = CashierService.INSTANCE.get_counter(cc_id);
if(counter==null) {
	return SimpleAjax.notAvailable('obj_not_found,收银台未找到');
}

def ocs = SSUtil.supp_order_cat;
def sel_ocs = [];
for(x in ocs) {
	for(y in sel_oc) {
		if(x.code.equals(y.toString())) {
			sel_ocs.add(x);
		}
	}
}
println "update_counter_oc_"+ JackSonBeanMapper.toJson(sel_ocs)
CashierService.INSTANCE.update_counter_oc(counter, sel_ocs);

return SimpleAjax.available('', sel_ocs);
