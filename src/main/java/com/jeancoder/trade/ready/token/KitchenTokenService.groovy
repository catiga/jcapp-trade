package com.jeancoder.trade.ready.token

import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.CashCounter
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.ser.KitchenService

class KitchenTokenService {

	final static KitchenTokenService INSTANCE = new KitchenTokenService();
	
	def validate(def token) {
		KitchenService cashier_service = KitchenService.INSTANCE;
		
		List<CashDoLog> result_list = cashier_service.get_counter_by_token(token);
		if(result_list==null||result_list.empty) {
			return SimpleAjax.notAvailable('cashier_log_empty');
		}
		if(result_list.size()>1) {
			return SimpleAjax.notAvailable('cashier_log_repeat');
		}
		
		CashDoLog do_log = result_list.get(0);
		CashCounter counter = cashier_service.get_counter(do_log.ccid);
		if(counter==null) {
			return SimpleAjax.notAvailable('cashier_empty');
		}
		if(counter.inuse!=1) {
			return SimpleAjax.notAvailable('cashier_empty:' + counter.inuse);
		}
		
		return SimpleAjax.available('', counter);
	}
	
	def invalid(def token) {
		KitchenService cashier_service = KitchenService.INSTANCE;
		
		cashier_service.counter_log_out(token);
	}
}
