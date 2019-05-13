package com.jeancoder.trade.entry.kitchen
import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.jdbc.JcPage
import com.jeancoder.trade.ready.ser.KitchenService

KitchenService ser = KitchenService.INSTANCE;

def pn = JC.request.param('pn')?.trim();
if(!pn) {
	pn = 1;
}
def ccid = JC.request.param('ccid')?.trim();
if(ccid=='') 
	ccid = null;

def page = new JcPage<>();
page.pn = pn;
page.ps = 50;

page = ser.find_counter_log(page, ccid);

Result view = new Result();
view.setView('kitchen/logs');
view.addObject('page', page);
view.addObject("ccid", ccid);
return view;



