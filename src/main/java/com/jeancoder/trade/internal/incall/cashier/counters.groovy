package com.jeancoder.trade.internal.incall.cashier

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.dto.SysProjectInfo
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.ser.CashierService

CashierService ser = CashierService.INSTANCE;

def jc_domain = JC.internal.param('jc_domain');

SysProjectInfo project = JC.internal.call(SysProjectInfo, 'project', '/incall/project', [domain:jc_domain]);

List<CashCounter> result = ser.find_now_counters(project?.id);
if(result && !result.empty) {
	Iterator<CashCounter> cash_its = result.iterator();
	while(cash_its.hasNext()) {
		CashCounter cc = cash_its.next();
		if(cc.flag==1) {
			//停用状态，需要删除掉
			cash_its.remove();
		}
	}
}

return result;


