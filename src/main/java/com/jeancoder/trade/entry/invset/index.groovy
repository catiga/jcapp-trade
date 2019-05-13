package com.jeancoder.trade.entry.invset

import com.jeancoder.core.result.Result
import com.jeancoder.jdbc.JcPage
import com.jeancoder.trade.ready.ser.InvoiceService
import com.jeancoder.trade.ready.util.RemoteUtil
import com.jeancoder.trade.ready.util.SSUtil
import com.jeancoder.trade.ready.util.TradeTssUtil

//获取发票设置
def invoice_sets = InvoiceService.INSTANCE().find_invoice_sets();

Result view = new Result();
view.setView('invset/index');
view.addObject('invoice_sets', invoice_sets);

return view;