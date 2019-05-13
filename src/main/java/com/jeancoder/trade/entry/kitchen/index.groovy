package com.jeancoder.trade.entry.kitchen
import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.trade.ready.dto.StoreInfo
import com.jeancoder.trade.ready.ser.KitchenService
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.RemoteUtil
import com.jeancoder.trade.ready.util.SSUtil

KitchenService ser = KitchenService.INSTANCE;

def p = JC.request.param('p');
def cn = JC.request.param('cn');
def counters = ser.find_counter(p, cn);

Result view = new Result();
view.setView('kitchen/index');
view.addObject('counters', counters);

view.addObject('ocs', SSUtil.supp_order_cat);

view.addObject('now_proj', GlobalHolder.proj);
view.addObject('c_num', cn);
view.addObject('p', p);

//获取门店
List<StoreInfo> stores = RemoteUtil.connectAsArray(StoreInfo, 'project', '/incall/mystore', null);
view.addObject('all_stores', stores);
return view;



