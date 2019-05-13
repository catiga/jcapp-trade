package com.jeancoder.trade.entry.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.log.JCLoggerFactory
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.counterperm.AllowCusPrice
import com.jeancoder.trade.ready.dto.PrepData
import com.jeancoder.trade.ready.entity.CollectSetting
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePreferential
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.util.RemoteUtil
import com.jeancoder.trade.ready.util.StringUtil

/**
* 统一优惠计算服务，接收参数如下
* 
* @param tnum :  tnum;		//传入交易编号
* @param unicode : 会员标识
* @param ct :  支付方式
* @param coupons : coupons;	//逗号分割
*/

def tnum = JC.request.param('tnum')?.trim();
def unicode = JC.request.param('unicode')?.trim();
def ct = JC.request.param('ct')?.trim();
def coupons = JC.request.param('coupons')?.trim();
JCLogger logger = LoggerSource.getLogger(this.getClass());
//设计好计算顺序
println 'coupons=' + coupons;
println 'ct=' + ct;
println 'tnum=' + tnum;
println 'unicode=' + unicode;

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

//置空tp_amount
List<TradeOrder> trade_ordersList = TradeService.INSTANCE().trade_items(trade);
for(x in trade_ordersList) {
	x.tp_amount = null;
	JcTemplate.INSTANCE().update(x);
}
// 清空优惠信息
def list = JcTemplate.INSTANCE.find(TradePreferential, "select * from TradePreferential where  t_id =? and  flag!=?", trade.id,-1);
for (TradePreferential tf : list) {
	tf.flag = -1;
	JcTemplate.INSTANCE.update(tf);
}
// 恢复支付价格
trade.pay_amount = trade.total_amount;
JcTemplate.INSTANCE().update(trade);
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
def global_pref_radios = [:];

def queried_orders = [];
def mc_data = null;
for(x in trade_orders) {
	if(x.oc=='1000') {
		//支持商品价格计算
		if(unicode) {
			//计算会员卡价格
			SimpleAjax result_price = RemoteUtil.connect(SimpleAjax, 'scm', '/incall/order/preferential', [o_id:x.order_id,unicode:unicode,pref:'100']);
			queried_orders.add(result_price);
			if(result_price&&result_price.available&&mc_data==null) {
				mc_data = result_price.data;
				//重置优惠系数
				try {
					def pref_amount = new BigDecimal(mc_data['pref_amount']);
					def total_amount = new BigDecimal(mc_data['pay_amount']);
					//radio = (total_amount.subtract(pref_amount)).divide(total_amount).setScale(2);
					radio = (total_amount.subtract(pref_amount)).divide(total_amount , 4,BigDecimal.ROUND_HALF_UP);
					global_pref_radios['1000'] = radio;
				}catch(any) {
					logger.error('', any);
				}
			}
			if (result_price == null)  {
				return SimpleAjax.notAvailable("会员模块通讯异常")
			}
			if (!result_price.available)  {
				return result_price;
			}
		}
		if(coupon_arr&&coupon_arr.get('1000')!=null) {
			def coupon_ids = coupon_arr.get('1000');
			SimpleAjax result_price = RemoteUtil.connect(SimpleAjax, 'scm', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200']);
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
			SimpleAjax result_price = RemoteUtil.connect(SimpleAjax, 'ticketingsys', '/incall/order/preferential', [o_id:x.order_id,unicode:unicode,pref:'100']);
			queried_orders.add(result_price);
			if(result_price&&result_price.available&&mc_data==null) {
				mc_data = result_price.data;
				//重置优惠系数
				try {
					def pref_amount = new BigDecimal(mc_data['pref_amount']);
					def total_amount = new BigDecimal(mc_data['pay_amount']);
					radio = (total_amount.subtract(pref_amount)).divide(total_amount,4,BigDecimal.ROUND_HALF_UP);
					global_pref_radios['2000'] = radio;
				}catch(any) {
					logger.error('', any);
				}
			}
			if (result_price == null)  {
				return SimpleAjax.notAvailable("会员模块通讯异常")
			}
			if (!result_price.available)  {
				return result_price;
			}
		}
		if(coupon_arr&&coupon_arr.get('2000')!=null) {
			def coupon_ids = coupon_arr.get('2000');
			SimpleAjax result_price = RemoteUtil.connect(SimpleAjax, 'ticketingsys', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200']);
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
			SimpleAjax result_price = RemoteUtil.connect(SimpleAjax, 'ticketingsys', '/incall/order/preferential_reserve', [o_id:x.order_id,unicode:unicode,pref:'100']);
			queried_orders.add(result_price);
			if(result_price&&result_price.available&&mc_data==null) {
				mc_data = result_price.data;
				//重置优惠系数
				try {
					def pref_amount = new BigDecimal(mc_data['pref_amount']);
					def total_amount = new BigDecimal(mc_data['pay_amount']);
					radio = (total_amount.subtract(pref_amount)).divide(total_amount,4,BigDecimal.ROUND_HALF_UP);
					global_pref_radios['2010'] = radio;
				}catch(any) {
					logger.error('', any);
				}
			}
			if (result_price == null)  {
				return SimpleAjax.notAvailable("会员模块通讯异常")
			}
			if (!result_price.available)  {
				return result_price;
			}
		}
		if(coupon_arr&&coupon_arr.get('2000')!=null) {
			def coupon_ids = coupon_arr.get('2000');
			SimpleAjax result_price = RemoteUtil.connect(SimpleAjax, 'ticketingsys', '/incall/order/preferential_reserve', [o_id:x.order_id,unicode:coupon_ids,pref:'200']);
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
	} else if(x.oc=='5000') {
		if(unicode) {
			//计算会员卡价格
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'reserve', '/incall/order/preferential', [o_id:x.order_id,unicode:unicode,pref:'100',pid:pid]);
			queried_orders.add(result_price);
			if(result_price&&result_price.available&&mc_data==null) {
				mc_data = result_price.data;
				//重置优惠系数
				try {
					def pref_amount = new BigDecimal(mc_data['pref_amount']);
					def total_amount = new BigDecimal(mc_data['pay_amount']);
					radio = (total_amount.subtract(pref_amount)).divide(total_amount,4,BigDecimal.ROUND_HALF_UP);
					global_pref_radios['5000'] = radio;
				}catch(any) {
					logger.error('', any);
				}
			}
			if (result_price == null)  {
				return SimpleAjax.notAvailable("会员模块通讯异常")
			}
			if (!result_price.available)  {
				return result_price;
			}
		}
		if(coupon_arr&&coupon_arr.get('5001')!=null) {
			def coupon_ids = coupon_arr.get('5001');
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'reserve', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200',pid:pid]);
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
	}
}

def total_amount = trade.total_amount;
def trade_use_prefs = [];
println "==========" + JackSonBeanMapper.toJson(queried_orders);
if(queried_orders) {
	//这里处理会员卡和优惠券
	for(SimpleAjax x in queried_orders) {
		if(x.available) {
			//开始准备循环价格
			def pd = x.data;
			def pd_oc = pd['oc'];
			def pd_type = pd['prefcode'];
			
			BigDecimal need_cut_price = cut_price(pd['pref_amount']);
			need_cut_price = need_cut_price.setScale(2);
			
			
			println pd_type + '============================' + trade.total_amount + '=======' + need_cut_price;
			println pd_type + '============================' + trade.total_amount.compareTo(need_cut_price);
			if(pd_type!='100' && !trade.total_amount.compareTo(need_cut_price).toString().equals("0")) {
				// 如果优惠券优惠金额等于 总金额就不应该再乘以相应系数
				//不是会员卡需要乘以相应系数
				BigDecimal radio = global_pref_radios.get(pd_oc);
				if(radio&&radio.compareTo(new BigDecimal(0))==1) {
					need_cut_price = need_cut_price.multiply(radio).setScale(0,BigDecimal.ROUND_HALF_UP);
				}
			}
			total_amount = total_amount.subtract(need_cut_price);
			if(total_amount.compareTo(new BigDecimal(0))<0) {
				total_amount = new BigDecimal(0);
			}
			logger.info("total_amount__" + total_amount.toString());
			def pref_data = [pref_id:pd['order_id'],oc:pd['oc'],pref_code:pd['prefcode'],pref_type:pd['prefcode'],
				pref_name:pd['prefcode'],real_deduct:need_cut_price,max_deduct:pd['pref_amount']];
			trade_use_prefs.add(pref_data);
		}
	}
} 

/** 结束计算优惠 **/

def total_ret_data = [total_amount, mc_data];

def pref_amount = trade.total_amount.subtract(total_amount);
total_ret_data.add(pref_amount);

return SimpleAjax.available('', total_ret_data);




def cut_price(def price) {
	try {
		return new BigDecimal(price);
	} catch(any) {
		return new BigDecimal(0);
	}
}







