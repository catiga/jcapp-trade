package com.jeancoder.trade.ready.counterperm

import com.jeancoder.trade.ready.entity.CollectSetting

class RoleDiscount {

	//def dl_list = [new DL(id:1,discount:8), new DL(id:2,discount:8.5), new DL(id:3,discount:9.5), new DL(id:4,discount:10)];
	
	def dl = [];
	
	def add(CollectSetting col) {
		def insert = true;
		for(x in dl) {
			if(x['discount']==col.vv) {
				insert = false;
				break;
			}
		}
		if(insert) {
			dl.add(new DL(id:col.id, discount:col.vv, tip:col.tip));
		}
	}
	
	def add(List<CollectSetting> settings) {
		for(x in settings) {
			if(x.cscode=='1000') {
				this.add(x);
			}
		}
	}
}
