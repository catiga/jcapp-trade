package com.jeancoder.trade.entry.mgr

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.result.Result
import com.jeancoder.jdbc.JcPage
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.SSUtil
import com.jeancoder.trade.ready.util.TradeTssUtil

TradeService t_ser = TradeService.INSTANCE();

def tnum = JC.request.param('tnum');
def onum = JC.request.param('onum');
def store = JC.request.param('store');
def mob = JC.request.param('mob');
JCLogger Logger = LoggerSource.getLogger("test_____")

def pid = GlobalHolder.proj.id;

def third_id = null;
if(mob!=null&&mob!='') {
	//需要去会员中心查找当前手机号码对应的用户的 third_id
	SimpleAjax account_info =  JC.internal.call(SimpleAjax, 'crm', '/h5/p/info_by_mobile', [mobile:mob,pid:pid]);
	if(account_info && account_info.available) {
		third_id = account_info.data['ap_id'];
	}
}
/**
 * all
 * init
 * payed
 * back
 */
def ss = JC.request.param('ss');

def ss_str = null;
if(ss!=null) {
	if(ss=='init') {
		ss_str = TradeTssUtil.init();
	} else if(ss=='payed') {
		ss_str = TradeTssUtil.payed();
	} else if(ss=='back') {
		ss_str = TradeTssUtil.back();
	}
}

def pn = JC.request.param('pn');
JcPage<TradeInfo> page = new JcPage<>();
if(!pn) {
	pn = 1;
} else {
	pn = Integer.valueOf(pn);
}
page.pn = pn;
page.ps = 20;

page = t_ser.find(page, tnum, onum, store, ss_str, third_id);

Result view = new Result();
view.setView('mgr/index');
view.addObject('page', page);

view.addObject('tnum', tnum);
view.addObject('onum', onum);
view.addObject('store', store);
view.addObject('tmob', mob);

view.addObject('ss', ss);

def all_tss = TradeTssUtil.all();
view.addObject('all_tss', all_tss);
view.addObject('all_tss——s', "中文");

//获取系统门店
//def all_stores = RemoteUtil.connect(JcPage.class, 'project', '/incall/pure/stores', null);
def all_stores = JC.internal.call(SimpleAjax, 'project', '/incall/pure/stores', [pid:GlobalHolder.proj.id]);
view.addObject('all_stores', all_stores.data);
//println "all_tss___all_stores——萨达所" + JackSonBeanMapper.toJson(all_stores)
//System.out.print(JackSonBeanMapper.toJson(all_stores));
//Logger.info("all_tss___all_log_"+JackSonBeanMapper.toJson(all_stores));
//支付方式
def supp_dics = SSUtil.findall();
view.addObject('supp_dics', supp_dics);
return view;