package com.jeancoder.trade.internal.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.counterperm.AllowCusPrice
import com.jeancoder.trade.ready.dto.SysProjectInfo
import com.jeancoder.trade.ready.entity.CollectSetting
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.StringUtil

/**
* 统一优惠计算服务，接收参数如下
* 
* @param tnum :  tnum;		//传入交易编号
* @param unicode : 会员标识
* @param ct :  支付方式
* @param coupons : coupons;	//逗号分割
* @param mpv : 手工折扣的id
* @param mpc : 手工改价的id
*/
JCLogger logger = LoggerSource.getLogger(this.getClass());
def tnum = JC.internal.param('tnum');
def unicode = JC.internal.param('unicode');
def ct = JC.internal.param('ct');
def coupons = JC.internal.param('coupons');
def mpv = JC.internal.param('mpv');
def mpc = JC.internal.param('mpc');

//设计好计算顺序
println 'coupons=' + coupons;
println 'ct=' + ct;
println 'tnum=' + tnum;
println 'unicode=' + unicode;
println 'mpv=' + mpv;
println 'mpc=' + mpc;

//处理coupons
def coupon_arr = [:];
if(coupons) {
	coupons = coupons.split(";");
	for(x in coupons) {
		def arr_x = x.split(",");
		def arr_values = coupon_arr.get(arr_x[1]);
		if(arr_values==null) {
			arr_values = arr_x[0];
		} else {
			arr_values = arr_values + "," + arr_x[0];
		}
		coupon_arr.put(arr_x[1], arr_values);
	}
}

TradeInfo trade = TradeService.INSTANCE().get_trade_by_num(tnum);
if(trade==null) {
	return SimpleAjax.notAvailable('trade_not_found,交易编号错误，请重试');
}
if(!trade.tss.startsWith('0')) {
	return SimpleAjax.notAvailable("交易状态错误请检查");
}
def pid = trade.pid;
/** 开始计算优惠 **/
List<TradeOrder> trade_orders = TradeService.INSTANCE().trade_items(trade);
// 清除优惠券
for (TradeOrder order : trade_orders) {
	def orders = order.order_num + ","+order.oc;
	JC.thread.run(5000, {
		SimpleAjax relse = JC.internal.call(SimpleAjax, 'market', '/coupon/clear_coupon_info',[orders:orders,pid:pid.toString()]);
		if (relse == null || !relse.available) {
			return;
		}
		return order;
	}, {
	})
}
def queried_orders = [];
def mc_data = null;
for(x in trade_orders) {
	if(x.oc=='1000') {
		//支持商品价格计算
		if(unicode) {
			//计算会员卡价格
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'scm', '/incall/order/preferential', [o_id:x.order_id,unicode:unicode,pref:'100', pid:pid]);
			queried_orders.add(result_price);
			if(result_price&&result_price.available&&mc_data==null) {
				mc_data = result_price.data;
			}
		}
		if(coupon_arr&&coupon_arr.get('1000')!=null) {
			def coupon_ids = coupon_arr.get('1000');
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'scm', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200', pid:pid]);
			if (!StringUtil.isEmpty(coupon_ids)) {
				if (result_price == null)  {
					return SimpleAjax.notAvailable("卡劵模块通讯异常")
				}
				if (!result_price.available)  {
					return result_price;
				}
			}
			queried_orders.add(result_price);
		}
	} else if(x.oc=='2000') {
		//支持影票价格计算
		if(unicode) {
			//计算会员卡价格
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'ticketingsys', '/incall/order/preferential', [o_id:x.order_id,unicode:unicode,pref:'100', pid:pid]);
			queried_orders.add(result_price);
			if(result_price&&result_price.available&&mc_data==null) {
				mc_data = result_price.data;
			}
		}
		if(coupon_arr&&coupon_arr.get('2000')!=null) {
			def coupon_ids = coupon_arr.get('2000');
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'ticketingsys', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200', pid:pid]);
			if (!StringUtil.isEmpty(coupon_ids)) {
				if (result_price == null)  {
					return SimpleAjax.notAvailable("卡劵模块通讯异常")
				}
				if (!result_price.available)  {
					return result_price;
				}
			}
			queried_orders.add(result_price);
		}
	} else if(x.oc=='2010') {
		//支持影票预约价格计算
		if(unicode) {
			//计算会员卡价格
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'ticketingsys', '/incall/order/preferential_reserve', [o_id:x.order_id,unicode:unicode,pref:'100', pid:pid]);
			queried_orders.add(result_price);
			if(result_price&&result_price.available&&mc_data==null) {
				mc_data = result_price.data;
			}
		}
		if(coupon_arr&&coupon_arr.get('2000')!=null) {
			def coupon_ids = coupon_arr.get('2000');
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'ticketingsys', '/incall/order/preferential_reserve', [o_id:x.order_id,unicode:coupon_ids,pref:'200', pid:pid]);
			if (!StringUtil.isEmpty(coupon_ids)) {
				if (result_price == null)  {
					return SimpleAjax.notAvailable("卡劵模块通讯异常")
				}
				if (!result_price.available)  {
					return result_price;
				}
			}
			queried_orders.add(result_price);
		}
	}  else if(x.oc=='5000') {
		if(unicode) {
			//计算会员卡价格
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'reserve', '/incall/order/preferential', [o_id:x.order_id,unicode:unicode,pref:'100',pid:pid]);
			queried_orders.add(result_price);
			if(result_price&&result_price.available&&mc_data==null) {
				mc_data = result_price.data;
			}
		}
		if(coupon_arr&&coupon_arr.get('5001')!=null) {
			def coupon_ids = coupon_arr.get('5001');
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'reserve', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200',pid:pid]);
			logger.info("coupon_ids" + coupon_ids)
			logger.info("coupon_ids" + JackSonBeanMapper.toJson(result_price));
			if (!StringUtil.isEmpty(coupon_ids)) {
				if (result_price == null)  {
					return SimpleAjax.notAvailable("卡劵模块通讯异常")
				}
				if (!result_price.available)  {
					return result_price;
				}
			}
			queried_orders.add(result_price);
		}
	} else {
		//TODO 其他的待处理
	}
}

def total_amount = trade.total_amount;
def trade_use_prefs = [];
if(queried_orders) {
	//这里处理会员卡和优惠券
	for(SimpleAjax x in queried_orders) {
		println JackSonBeanMapper.toJson(x.data);
		if(x.available) {
			//开始准备循环价格
			def pd = x.data;
			total_amount = total_amount.subtract(cut_price(pd['pref_amount']));
			if(total_amount.compareTo(new BigDecimal(0))<0) {
				total_amount = new BigDecimal(0);
			}
			trade_use_prefs.add(pd);
		}
	}
}
//开始计算折扣
if(mpv) {
	CollectSetting discount = JcTemplate.INSTANCE().get(CollectSetting, 'select * from CollectSetting where id=? and flag!=?', mpv, -1);
	if(discount!=null) {
		for(x in trade_orders) {
			if(x.oc=='1000') {
				//只针对卖品打折
				//total_amount = total_amount.multiply(cut_price(discount.vv)).setScale(2, BigDecimal.ROUND_HALF_UP);
				
				def trade_order_payment = x.pay_amount;
				def goods_pref_amount = new BigDecimal(1).subtract(cut_price(discount.vv)).multiply(trade_order_payment);
				total_amount = total_amount.subtract(goods_pref_amount).setScale(2, BigDecimal.ROUND_HALF_UP);
				if(total_amount.compareTo(new BigDecimal(0))<0) {
					total_amount = new BigDecimal(0);
				}
			}
		}
	}
}
//开始进行抹零操作
if(mpc) {
	CollectSetting discount = JcTemplate.INSTANCE().get(CollectSetting, 'select * from CollectSetting where id=? and flag!=?', mpc, -1);
	if(discount!=null) {
		for(x in trade_orders) {
			if(x.oc=='1000') {
				//只针对卖品抹零
				//total_amount = AllowCusPrice.cutprice(discount.vv, total_amount);
				
				def trade_order_payment = x.pay_amount;
				def goods_pref_amount = AllowCusPrice.cutprice(discount.vv, trade_order_payment);
				
				total_amount = total_amount.subtract(trade_order_payment.subtract(goods_pref_amount)).setScale(2, BigDecimal.ROUND_HALF_UP);
				if(total_amount.compareTo(new BigDecimal(0))<0) {
					total_amount = new BigDecimal(0);
				}
			}
		}
	}
}
/** 结束计算优惠 **/

def total_ret_data = [total_amount, mc_data];
//if(unicode) {
//	//暂时只有在会员卡的情况下取优惠券数据
//	def phone = '13333333333';
//	
//	def coupon_list = JC.internal.call('market', '/coupon/coupon_list', [mobile:phone,pid:GlobalHolder.proj.id]);
//	def coupon_result = JackSonBeanMapper.jsonToMap(coupon_list);
//	if(coupon_result['code']=='0') {
//		total_ret_data.add(coupon_result['list']);
//	}
//}

def pref_amount = trade.pay_amount.subtract(total_amount);
total_ret_data.add(pref_amount);
SimpleAjax ret_obj = SimpleAjax.available('', total_ret_data);

if(ct=='201002' || ct=='202002') {
	//微信支付或支付宝支付，返回主扫二维x码
	SysProjectInfo project = JC.internal.call(SysProjectInfo, 'project', '/incall/project_by_id', [pid:pid]);
	if(project!=null) {
		//ret_obj.qrcode = JC.request.get().getSchema() + project.domain + '/' + tnum;
		ret_obj.qrcode = project.domain + '/' + tnum;
	}
}
return ret_obj;



def cut_price(def price) {
	try {
		return new BigDecimal(price);
	} catch(any) {
		return new BigDecimal(0);
	}
}







