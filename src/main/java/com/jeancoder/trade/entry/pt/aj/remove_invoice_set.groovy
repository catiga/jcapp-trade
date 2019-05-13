package com.jeancoder.trade.entry.pt.aj

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.InvoiceSet
import com.jeancoder.trade.ready.ser.InvoiceService
import com.jeancoder.trade.ready.util.GlobalHolder

def id = JC.request.param('id')?.trim();

InvoiceSet entity = InvoiceService.INSTANCE().get_invoice_set(id);
if(entity==null) {
	return SimpleAjax.notAvailable('obj_not_found');
}

if(entity.pid!=GlobalHolder.proj.id) {
	return SimpleAjax.notAvailable('不可删除系统发票设置');
}

entity.flag = -1;
InvoiceService.INSTANCE().update_invoice_set(entity);

return SimpleAjax.available();



