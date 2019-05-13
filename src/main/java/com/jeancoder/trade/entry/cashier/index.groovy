package com.jeancoder.trade.entry.cashier
import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.dto.StoreInfo
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.SSUtil

CashierService ser = CashierService.INSTANCE;

def p = JC.request.param('p');
def cn = JC.request.param('cn');
def counters = ser.find_counter(p, cn);

Result view = new Result();
view.setView('cashier/index');
view.addObject('counters', counters);

view.addObject('ocs', SSUtil.supp_order_cat);

view.addObject('now_proj', GlobalHolder.proj);
view.addObject('c_num', cn);
view.addObject('p', p);

//获取门店
//List<StoreInfo> stores = RemoteUtil.connectAsArray(StoreInfo, 'project', '/incall/mystore', null);
def result = JC.internal.call('project', '/incall/mystore', [pid:GlobalHolder.proj.id]);
def stores = [];
try {
	stores = JackSonBeanMapper.jsonToList(result, StoreInfo);
} catch(any) {
	any.printStackTrace();
}
view.addObject('all_stores', stores);
return view;



