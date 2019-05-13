package com.jeancoder.trade.ready.util

class TradeTssUtil {

	static def _all_tss_ = [];
	static {
		_all_tss_.add([code:'0000', name:'创建订单', info:'']);
		
		_all_tss_.add([code:'1000', name:'支付成功', info:'一般支付成功']);
		
		/**
		 * 中间状态
		 */
		_all_tss_.add([code:'1010', name:'支付成功', info:'支付成功待确认（或审核）']);
		/**
		 * 普通商品:待出库
		 * 合成商品:待制作
		 * 电影票类:待出票
		 * 等等
		 */
		_all_tss_.add([code:'1020', name:'支付成功', info:'支付成功待操作']);
		
		
		_all_tss_.add([code:'2000', name:'发货与出库', info:'已发货或已出库或已出票']);
		
		_all_tss_.add([code:'3000', name:'已收货', info:'已收货或已取货']);
		
		
		_all_tss_.add([code:'8800', name:'退款成功', info:'交易支付信息已退款']);
		_all_tss_.add([code:'8850', name:'部分退款成功', info:'交易支付信息部分已退款']);
		_all_tss_.add([code:'9000', name:'交易关闭', info:'交易超时自动关闭']);
	}
	
	static def all() {
		return _all_tss_;
	}
	
	def static payed() {
		return ["1000","1010","1020","2000","3000"];
	}
	
	def static init() {
		return ["0000"];
	}
	
	def static back() {
		return ["8800", '8850'];
	}
}
