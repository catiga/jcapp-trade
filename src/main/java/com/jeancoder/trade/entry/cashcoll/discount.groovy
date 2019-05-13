package com.jeancoder.trade.entry.cashcoll
import com.jeancoder.app.sdk.JC
import com.jeancoder.core.result.Result
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CollectSetting
import com.jeancoder.trade.ready.util.GlobalHolder

//首先获取系统角色
SimpleAjax retobj = JC.internal.call(SimpleAjax, 'project', '/auth/roles', [pid:GlobalHolder.proj.id]);

Result view = new Result();
view.setView('cashcoll/discount');

def param = [];
if(retobj.data) {
	for(x in retobj.data) {
		def sql = 'select * from CollectSetting where flag!=? and role_id=? and cscode=? order by vv desc';
		def result = JcTemplate.INSTANCE().find(CollectSetting, sql, -1, x['id'], '1000');
		param.add([x, result]);
	}
}

view.addObject('sys_roles', param);
return view;



