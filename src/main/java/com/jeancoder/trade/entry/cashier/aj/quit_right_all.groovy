package com.jeancoder.trade.entry.cashier.aj
import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.util.GlobalHolder

CashierService ser = CashierService.INSTANCE;
JCLogger  Logger = LoggerSource.getLogger(this.getClass())
def cash_id = JC.request.param('id')?.trim();

CashCounter cash_counter = ser.get_counter(cash_id);
if(cash_counter==null) {
	return SimpleAjax.notAvailable('obj_not_found,收银台未找到');
}
if(cash_counter.inuse==0) {
	//可用状态
	return SimpleAjax.available();
}

String sql = 'select * from CashDoLog where flag!=? and ccid=?';
List<CashDoLog> cash_all_logs = JcTemplate.INSTANCE().find(CashDoLog, sql, -1, cash_id);

if(cash_all_logs && !cash_all_logs.empty) {
	for(cash_do_log in cash_all_logs) {
		SimpleAjax login_result = JC.internal.call(SimpleAjax, 'project', '/incall/logout', [token:cash_do_log.utoken, pid:GlobalHolder.proj.id]);
		if(!login_result.available) {
			continue;
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
	}
}

cash_counter.inuse = 0;
JcTemplate.INSTANCE().update(cash_counter);

return SimpleAjax.available();


