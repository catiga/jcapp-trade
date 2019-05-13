package com.jeancoder.trade.entry.cashcoll.aj

import com.jeancoder.app.sdk.JC
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CollectSetting

def id = JC.request.param('id')?.trim();

CollectSetting data = JcTemplate.INSTANCE().get(CollectSetting, 'select * from CollectSetting where id=? and flag!=?', id, -1);
if(data!=null) {
	data.flag = -1;
	JcTemplate.INSTANCE().update(data);
}

return SimpleAjax.available();