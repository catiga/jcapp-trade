package com.jeancoder.trade.ready.util

import com.jeancoder.trade.ready.entity.SysSupp

class SSUtil {

	static def supp_dics = [];
	
	static supp_order_cat = [];
	
	static {
		supp_dics.add(new SysSupp(tyc:'1000', code_cat:'001', code:'001001', rb:1, name:'现金支付'));
		supp_dics.add(new SysSupp(tyc:'1000', code_cat:'101', code:'101001', rb:1, name:'会员卡余额'));
		
		supp_dics.add(new SysSupp(tyc:'1000', code_cat:'201', code:'201001', rb:1, name:'微信支付被扫'));
		supp_dics.add(new SysSupp(tyc:'1000', code_cat:'201', code:'201002', rb:1, name:'微信支付主扫'));
		supp_dics.add(new SysSupp(tyc:'1000', code_cat:'201', code:'201101', rb:1, name:'微信支付公众号'));
		supp_dics.add(new SysSupp(tyc:'1000', code_cat:'201', code:'201102', rb:1, name:'微信支付小程序'));
		
		supp_dics.add(new SysSupp(tyc:'1000', code_cat:'202', code:'202001', rb:1, name:'支付宝被扫'));
		supp_dics.add(new SysSupp(tyc:'1000', code_cat:'202', code:'202002', rb:1, name:'支付宝主扫'));
		
		supp_dics.add(new SysSupp(tyc:'1000', code_cat:'301', code:'301001', rb:1, name:'现在支付POS'));
		
		supp_dics.add(new SysSupp(tyc:'2000', code:'100', name:'到店'));
		supp_dics.add(new SysSupp(tyc:'2000', code:'200', name:'配送'));
		
		supp_order_cat.add(new SysSupp(tyc:'3000', code:'1000', name:'商品订单'));
		supp_order_cat.add(new SysSupp(tyc:'3000', code:'2000', name:'影票订单'));
		supp_order_cat.add(new SysSupp(tyc:'3000', code:'2010', name:'影票预约订单'));
		supp_order_cat.add(new SysSupp(tyc:'3000', code:'8000', name:'会员卡开卡订单'));
		supp_order_cat.add(new SysSupp(tyc:'3000', code:'8001', name:'会员卡充值订单'));
		supp_order_cat.add(new SysSupp(tyc:'3000', code:'5000', name:'预约订单'));
	}
	
	static SysSupp get(def tyc, def code) {
		for(x in supp_dics) {
			if(x.tyc==tyc&&x.code==code) {
				return x;
			}
		}
	}
	
	static def findall() {
		supp_dics.each({it -> it.id = null})
		return supp_dics;
		
	}
	
	static find_order_types() {
		supp_order_cat.each({it -> it.id = null})
		return supp_order_cat;
	}
	
	static is_valid_order_type(def oc) {
		supp_order_cat.each{item-> item.code==oc?true:false;}
	}
}
