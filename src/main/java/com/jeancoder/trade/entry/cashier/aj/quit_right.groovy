package com.jeancoder.trade.entry.cashier.aj
import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.log.JCLoggerFactory
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.util.GlobalHolder
import com.jeancoder.trade.ready.util.RemoteUtil

CashierService ser = CashierService.INSTANCE;
JCLogger  Logger = JCLoggerFactory.getLogger(this.getClass())
def cash_do_log_id = JC.request.param('id')?.trim();
CashDoLog cash_do_log = ser.get_counter_log(cash_do_log_id);
if(cash_do_log==null) {
	return SimpleAjax.notAvailable('log_not_found');
}

//SimpleAjax login_result = RemoteUtil.connect(SimpleAjax, 'project', '/incall/logout_account', [token:cash_do_log.utoken]);
SimpleAjax login_result = JC.internal.call(SimpleAjax, 'project', '/incall/logout', [token:cash_do_log.utoken, pid:GlobalHolder.proj.id]);

if(!login_result.isAvailable()) {
	return SimpleAjax.notAvailable('logout_fail');
}


// 默认为待计算
cash_do_log.settle = '10';
try {
	// 查询支付记录
	sql = "select  * from TradePayInfo  where log_id =?  and flag!=?";
	List<TradePayInfo> tpiList =  JcTemplate.INSTANCE.find(TradePayInfo,sql,cash_do_log.id,-1);
	if(tpiList == null || tpiList.isEmpty()){
		cash_do_log.settle = '00';
	}
} catch (any) {
	Logger.error("",any);
}
cash_do_log.end_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
cash_do_log.ss = '10';


cash_do_log.end_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
cash_do_log.ss = '10';
JcTemplate.INSTANCE().update(cash_do_log);

CashCounter counter = ser.get_counter(cash_do_log.ccid);
if(counter!=null) {
	counter.inuse = 0;
	JcTemplate.INSTANCE().update(counter);
}

return SimpleAjax.available();


