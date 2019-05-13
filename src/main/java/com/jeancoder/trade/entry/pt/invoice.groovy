package com.jeancoder.trade.entry.pt

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.jdbc.JcPage
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.entity.InvoiceSet
import com.jeancoder.trade.ready.entity.SysSupp
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.InvoiceService
import com.jeancoder.trade.ready.ser.SuppService
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.SSUtil
import com.jeancoder.trade.ready.util.TradeTssUtil

def invoice_sets = InvoiceService.INSTANCE().find_invoice_sets();

Result view = new Result();
view.setView('pt/invoice');
view.addObject('invoice_sets', invoice_sets);

return view;

