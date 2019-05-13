package com.jeancoder.trade.internal.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.ProjectConfigSimple
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.util.NativeUtil
import com.jeancoder.trade.ready.util.RemoteUtil


def pid = JC.internal.param("pid")
def result = NativeUtil.connect(SimpleAjax.class, 'project', '/incall/project_pts', [tyc:'1000',pid:pid]);

def sys = [];
if(result==null || !result.available) {
	return SimpleAjax.notAvailable('empty');
}

for (def item : result.data) {
	if(item.sc_code[3]=='0') {
		sys.add(new ProjectConfigSimple(tyc:item.sc_type,code:item.sc_code,name:item.sc_name,disname:item.disname,tips:item.sc_info));
	}
}
return SimpleAjax.available('', sys);


