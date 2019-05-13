package com.jeancoder.trade.entry.invset.aj

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.InvoiceSet
import com.jeancoder.trade.ready.ser.InvoiceService
import com.jeancoder.trade.ready.util.GlobalHolder

def id = JC.request.param('id');
def invoice_set = InvoiceService.INSTANCE().get_invoice_set(id);
if(!invoice_set) {
	return SimpleAjax.notAvailable('obj_not_found,付款单位未找到');
}
return SimpleAjax.available('', invoice_set);








