package com.jeancoder.trade.entry.incall

import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.ProjectConfigSimple
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.util.RemoteUtil

def result = RemoteUtil.connectAsArray(ProjectGeneralConfig.class, 'project', '/incall/project_pts', [tyc:'1000']);

def sys = [];
if(result==null) {
	return SimpleAjax.notAvailable('empty');
}

result.each{
	item->if(item.sc_code[3]=='0') sys.add(new ProjectConfigSimple(tyc:item.sc_type,code:item.sc_code,name:item.sc_name,disname:item.disname,tips:item.sc_info))
}

return SimpleAjax.available('', sys);


