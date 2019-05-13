package com.jeancoder.trade.ready.util

class MpUtil {

	def static build(def mp, def value) {
		if(mp!='1001'&&mp!='1002') {
			return null;
		}
		try {
			BigDecimal bd = new BigDecimal(value);
			if(bd>0&&bd<10)
				return ['mp':mp,v:bd];
		}catch(e) {
		}
	}
}
