package com.jeancoder.trade.ready

class RetObj {

	Object data;
	
	public RetObj(Object e) {
		data = e;
	}
	
	static def build(Object e) {
		//return new RetObj(e);
		return e;
	}
}
