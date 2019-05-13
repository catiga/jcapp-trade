package com.jeancoder.trade.ready.entity

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID

@JCBean(tbname = "cashier_counter_ocs")
class CounterOc {

	@JCID
	BigInteger id;
	
	@JCForeign
	BigInteger cc_id;
	
	String oc;
}
