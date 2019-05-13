package com.jeancoder.trade.entry.cashier.aj

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.StoreInfo
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.RemoteUtil

def cc_id = JC.request.param('cc_id')?.trim();
def store_id = JC.request.param('store_id')?.trim();

//List<StoreInfo> stores = RemoteUtil.connectAsArray(StoreInfo.class, 'project', '/incall/mystore', null);
def result = JC.internal.call('project', '/incall/mystore', [pid:GlobalHolder.proj.id]);
def stores = [];
try {
	stores = JackSonBeanMapper.jsonToList(result, StoreInfo);
} catch(any) {
	any.printStackTrace();
}
StoreInfo select_store = null;
for(x in stores) {
	if(x.id.toString()==store_id) {
		select_store = x; break;
	}
}
if(select_store==null) {
	return SimpleAjax.notAvailable('obj_not_found,门店未找到');
}

CashCounter counter = CashierService.INSTANCE.get_counter(cc_id);
if(counter==null) {
	return SimpleAjax.notAvailable('obj_not_found,收银台未找到');
}
counter.sid = select_store.id;
counter.sname = select_store.store_name;

CashierService.INSTANCE.update_counter(counter);

return SimpleAjax.available();
