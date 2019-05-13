package com.jeancoder.trade.entry.cashier.aj
import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.ser.CashierService

CashierService ser = CashierService.INSTANCE;


def id = JC.request.param('id')?.trim();

def name = JC.request.param('name')?.trim();
def info = JC.request.param('info')?.trim();
def sn = JC.request.param('sn')?.trim();

if(name==null||name=='') {
	return SimpleAjax.notAvailable('param_error,请输入工作台名称');
}
if(sn==null||sn=='') {
	return SimpleAjax.notAvailable('param_error,请输入工作台编号');
}

def update = true;
CashCounter counter = null;
if(id&&id!='0') {
	counter = ser.get_counter(id);
	if(counter==null) {
		return SimpleAjax.notAvailable('obj_not_found,工作台未找到');
	}
} else {
	update = false;
	counter = new CashCounter();
	counter.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
	counter.flag = 0;
	counter.inuse = 0;
}
counter.sn = sn;
counter.name = name;
counter.info = info;

if(update) {
	ser.update_counter(counter);
} else {
	ser.save_counter(counter);
}
return SimpleAjax.available();





