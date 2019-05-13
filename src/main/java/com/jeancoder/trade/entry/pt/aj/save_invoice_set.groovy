package com.jeancoder.trade.entry.pt.aj

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.InvoiceSet
import com.jeancoder.trade.ready.ser.InvoiceService
import com.jeancoder.trade.ready.util.GlobalHolder

def id = JC.request.param('id')?.trim();
def unit_name = JC.request.param('unit_name')?.trim();
def unit_tax = JC.request.param('unit_tax')?.trim();
def tax_fee = JC.request.param('tax_fee')?.trim();
def unit_bank = JC.request.param('unit_bank')?.trim();
def unit_account = JC.request.param('unit_account')?.trim();

def unit_addr = JC.request.param('unit_addr')?.trim();
def unit_phone = JC.request.param('unit_phone')?.trim();

def update = true;
InvoiceSet invo = null;
if(id&&id!='0') {
	invo = InvoiceService.INSTANCE().get_invoice_set(id);
	if(invo==null) {
		return SimpleAjax.notAvailable('obj_not_found');
	}
	if(invo.pid!=GlobalHolder.proj.id) {
		return SimpleAjax.notAvailable('不可编辑系统发票设置');
	}
} else {
	update = false;
	invo = new InvoiceSet(); invo.pid = GlobalHolder.proj.id;
	invo.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
	invo.flag = 0;
}

invo.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
try {
	invo.tax_fee = new BigDecimal(tax_fee);
}catch(any) {}
invo.unit_account = unit_account;
invo.unit_bank = unit_bank;
invo.unit_name = unit_name;
invo.unit_tax = unit_tax;
invo.unit_addr = unit_addr;
invo.unit_phone = unit_phone;

if(update) {
	InvoiceService.INSTANCE().update_invoice_set(invo);
} else {
	InvoiceService.INSTANCE().save_invoice_set(invo);
}
return SimpleAjax.available();



