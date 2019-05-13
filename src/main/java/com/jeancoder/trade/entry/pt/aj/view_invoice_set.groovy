package com.jeancoder.trade.entry.pt.aj

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.InvoiceSet
import com.jeancoder.trade.ready.ser.InvoiceService

def id = JC.request.param('id')?.trim();

InvoiceSet entity = InvoiceService.INSTANCE().get_invoice_set(id);
if(entity==null) {
	return SimpleAjax.notAvailable('obj_not_found');
}

return SimpleAjax.available('', entity);



