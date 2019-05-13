package com.jeancoder.trade.entry.invset.aj

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.InvoiceSet
import com.jeancoder.trade.ready.ser.InvoiceService
import com.jeancoder.trade.ready.util.GlobalHolder

def id = JC.request.param('id');
def name = JC.request.param('name');
def tax = JC.request.param('tax');
def bank = JC.request.param('bank');
def account = JC.request.param('account');
def addr = JC.request.param('addr');
def phone = JC.request.param('phone');
def tax_fee = JC.request.param('tax_fee');

def update = false;
def invoice_set = null;

if(id&&id!='0') {
	invoice_set = InvoiceService.INSTANCE().get_invoice_set(id);
	if(!invoice_set) {
		return SimpleAjax.notAvailable('obj_not_found,付款单位未找到');
	}
	update = true;
} else {
	invoice_set = new InvoiceSet();
	invoice_set.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
	invoice_set.flag = 0;
	invoice_set.pid = GlobalHolder.proj.id;
}
invoice_set.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
try {
	invoice_set.tax_fee = new BigDecimal(tax_fee);
}catch(any) {
	return SimpleAjax.notAvailable('param_error,税率设置错误');
}
invoice_set.unit_account = account;
invoice_set.unit_addr = addr;
invoice_set.unit_bank = bank;
invoice_set.unit_name = name;
invoice_set.unit_phone = phone;
invoice_set.unit_tax = tax;

if(update) {
	JcTemplate.INSTANCE().update(invoice_set);
} else {
	invoice_set.id = JcTemplate.INSTANCE().save(invoice_set);
}
return SimpleAjax.available();








