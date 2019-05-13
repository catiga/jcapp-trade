package com.jeancoder.trade.internal.incall.cashier

import com.jeancoder.app.sdk.JC
import com.jeancoder.trade.ready.dto.AuthToken
import com.jeancoder.trade.ready.dto.AuthUser
import com.jeancoder.trade.ready.dto.SysProjectInfo
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.util.GlobalHolder

CashierService ser = CashierService.INSTANCE;

def jc_domain = JC.internal.param('jc_domain');

SysProjectInfo project = JC.internal.call(SysProjectInfo, 'project', '/incall/project', [domain:jc_domain]);

def result = ser.find_now_counters(project?.id);

return result;


