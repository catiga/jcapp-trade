package com.jeancoder.trade.ready.counterperm

class AllowCusPrice {

	static def dic = [:];
	
	static {
		dic.put(new BigDecimal('1100').setScale(2), '小数抹零');
		dic.put(new BigDecimal('1101').setScale(2), '个位抹零');
		dic.put(new BigDecimal('1102').setScale(2), '十位抹零');
	}
	
	def static all() {
		return dic;
	}
	
	def static compare(BigDecimal bdv) {
		def value = null;
		dic.forEach({
			k,v ->
			if(k.compareTo(bdv)==0) {
				value = v;
				return;
			}
		});
		return value;
	}
	
	def static cutprice(BigDecimal bdv, BigDecimal total) {
		total = total.divide(new BigDecimal(100));
		if(bdv.toString()=='1100.00') {
			total = total.setScale(0, BigDecimal.ROUND_DOWN);	//直接去掉小数
		} else if(bdv.toString()=='1101.00') {
			total = total.divide(new BigDecimal(10));
			total = total.setScale(0, BigDecimal.ROUND_DOWN);
			total = total.multiply(new BigDecimal(10));
		} else if(bdv.toString()=='1102.00') {
			total = total.divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));
		}
		return total.multiply(new BigDecimal(100));
	}
	
	static void main(def ar) {
		BigDecimal bdv = new BigDecimal('1102').setScale(2);
		BigDecimal total = new BigDecimal('166345.2');
		println cutprice(bdv, total);
	}
}
