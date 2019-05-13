package com.jeancoder.trade.entry.cashier.aj
import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CounterConf
import com.jeancoder.trade.ready.ser.CashierService

CashierService ser = CashierService.INSTANCE;

def id = JC.request.param('id')?.trim();
def name = JC.request.param('name')?.trim();
def info = JC.request.param('info')?.trim();
def sn = JC.request.param('sn')?.trim();
def type = JC.request.param('type')?.trim();

def cc_id = JC.request.param('cc_id')?.trim();

CashCounter counter = ser.get_counter(cc_id);
if(counter==null) {
	return SimpleAjax.notAvailable('obj_not_found,工作台未找到');
}
if(name==null||name=='') {
	return SimpleAjax.notAvailable('param_error,请输入配置名称');
}
if(sn==null||sn=='') {
	return SimpleAjax.notAvailable('param_error,请输入配置代码');
}

def update = true;
CounterConf conf = null;
if(id&&id!='0') {
	conf = ser.get_cft(id);
	if(conf==null) {
		return SimpleAjax.notAvailable('obj_not_found,配置未找到');
	}
} else {
	update = false;
	conf = new CounterConf();
	conf.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
	conf.flag = 0;
}
conf.code = sn;
conf.name = name;
conf.info = info;
conf.code = sn;
conf.cft = type;
conf.cc_id = counter.id;
conf.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());

if(update) {
	ser.update_cft(conf);
} else {
	ser.save_cft(conf);
}
return SimpleAjax.available();





