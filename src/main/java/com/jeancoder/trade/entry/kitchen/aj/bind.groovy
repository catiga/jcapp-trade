package com.jeancoder.trade.entry.kitchen.aj

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.StoreInfo
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.ser.KitchenService
import com.jeancoder.trade.ready.util.RemoteUtil

def cc_id = JC.request.param('cc_id')?.trim();
def store_id = JC.request.param('store_id')?.trim();

List<StoreInfo> stores = RemoteUtil.connectAsArray(StoreInfo.class, 'project', '/incall/mystore', null);
StoreInfo select_store = null;
for(x in stores) {
	if(x.id.toString()==store_id) {
		select_store = x; break;
	}
}
if(select_store==null) {
	return SimpleAjax.notAvailable('obj_not_found,门店未找到');
}

CashCounter counter = KitchenService.INSTANCE.get_counter(cc_id);
if(counter==null) {
	return SimpleAjax.notAvailable('obj_not_found,工作台未找到');
}
counter.sid = select_store.id;
counter.sname = select_store.store_name;

KitchenService.INSTANCE.update_counter(counter);

return SimpleAjax.available();
