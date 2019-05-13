package com.jeancoder.trade.entry.mgr.aj

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.jdbc.JcPage
import com.jeancoder.trade.ready.dto.StoreInfo
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.RemoteUtil
import com.jeancoder.trade.ready.util.SSUtil
import com.jeancoder.trade.ready.util.TradeTssUtil

TradeService t_ser = TradeService.INSTANCE();

def tnum = JC.request.param('tnum');
def onum = JC.request.param('onum');
def store = JC.request.param('store');

def pn = JC.request.param('pn');
JcPage<TradeInfo> page = new JcPage<>();
if(!pn) {
	pn = 1;
}
page.pn = pn;
page.ps = 20;

page = t_ser.find(page, tnum, onum, store);

Result view = new Result();
view.setView('mgr/index');
view.addObject('page', page);

view.addObject('tnum', tnum);
view.addObject('onum', onum);
view.addObject('store', store);

def all_tss = TradeTssUtil.all();
view.addObject('all_tss', all_tss);

//获取系统门店
def all_stores = RemoteUtil.connect(JcPage.class, 'project', '/incall/pure/stores', null);
view.addObject('all_stores', all_stores);

//支付方式
def supp_dics = SSUtil.findall();
view.addObject('supp_dics', supp_dics);
return view;