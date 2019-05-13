package com.jeancoder.trade.entry.incall

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.log.JCLoggerFactory
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.counterperm.AllowCusPrice
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.entity.CashDoLog
import com.jeancoder.trade.ready.entity.CollectSetting
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePreferential
import com.jeancoder.trade.ready.notify.NotifyService
import com.jeancoder.trade.ready.printer.FeedPrinter
import com.jeancoder.trade.ready.printer.PrinterUtil
import com.jeancoder.trade.ready.ser.CashierService
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.ser.TriggerService
import com.jeancoder.trade.ready.unipay.PayService
import com.jeancoder.trade.ready.util.NativeUtil
import com.jeancoder.trade.ready.util.RemoteUtil
import com.jeancoder.trade.ready.util.StringUtil

/**
 * ct:01****    现金类支付
 * ct:10****    体系内支付
 * ct:20****    移动支付账户类
 * ct:30****    刷卡类（需对接pos）
 * 
 * 统一编码
 * ct:001001    现金支付
 * 
 * ct:101       会员账户支付
 * ct:101001    会员卡余额被扫
 * 
 * ct:201       微信支付
 * ct:201001    微信支付被扫（商户扫码用户）
 * ct:201002    微信支付主扫（用户扫码商户）
 * 
 * ct:202       支付宝支付
 * ct:202001    支付宝被扫
 * ct:202002    支付宝主扫
 * 
 * ct:301001	现在支付扫码刷卡
 * 
 * 活动类
 * mp:1***      手工操作类优惠
 * mp:2***      优惠券类优惠
 * mp:3***		优惠活动类
 * 
 * mp:1001		手工打折
 * mp:1002		手工改价
 * 
 * 
 */
/**
 * 收银台调用
 * 通用参数
 */

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

def tnum = JC.request.param('tnum')?.trim();
def unicode = JC.request.param('unicode')?.trim();
def ct = JC.request.param('ct')?.trim();
def coupons = JC.request.param('coupons')?.trim();
def mpv = JC.request.param('mpv')?.trim();
def mpc = JC.request.param('mpc')?.trim();
def cash_col = JC.internal.param('cash_col')?.toString()?.trim();
if(coupons=='null') {
	coupons = null;
}

if(ct!='001001'&&ct!='301001'&&!ct.startsWith("900")) {
	if(!unicode) {
		return SimpleAjax.notAvailable('param_error,支付信息关键要素缺失，请确认输入或扫描信息');
	}
}

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

JCLogger LOGGER = JCLoggerFactory.getLogger('uni_code_pay');

def current_token = JC.request.param('current_token');
println 'cashier_counter_token_now=' + current_token;

TradeService trade_service = TradeService.INSTANCE();
TriggerService trigger_service = TriggerService.INSTANCE();

TradeInfo trade = trade_service.get_trade_by_num(tnum);

if(trade==null) {
	return SimpleAjax.notAvailable('trade_not_found');
}
if(!trade.tss.startsWith('0')) {
	return SimpleAjax.notAvailable('trade_status_invalid');
}
def pid = trade.pid;
//获取项目支付信息配置
ProjectGeneralConfig supp_config = JC.internal.call(ProjectGeneralConfig, 'project', '/incall/project_single_pt', [sc_type:'1000',sc_code:ct,pid:pid]);
if(supp_config==null||supp_config.partner==null) {
	return SimpleAjax.notAvailable('pay_config_error');
}

def personal_param = [:];
def pwd = JC.request.param('pwd');
if(pwd) {
	personal_param['pwd'] = pwd;
}
if(cash_col) {
	try {
		personal_param['cash_col'] = new BigDecimal(cash_col.toString()).multiply(new BigDecimal(100));
	}catch(any) {}
}
personal_param['pid'] = pid;
//置空tp_amount
String orders = '';
List<TradeOrder> trade_ordersList = TradeService.INSTANCE().trade_items(trade);
for(x in trade_ordersList) {
	orders += x.order_num+","+x.oc +";"
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
// 清空卡劵
SimpleAjax clear_coupon_result = NativeUtil.connect(SimpleAjax, 'market', '/coupon/clear_coupon_info', [pid:trade.pid.toString(),orders:orders]);
if (clear_coupon_result == null || !clear_coupon_result.available) {
	def msg = clear_coupon_result == null?'清空优惠信息失败':clear_coupon_result.messages[0];
	return SimpleAjax.notAvailable(msg);
}

/** 开始计算优惠 **/
List<TradeOrder> trade_orders = TradeService.INSTANCE().trade_items(trade);

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
			SimpleAjax result_price = RemoteUtil.connect(SimpleAjax, 'scm', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200',op:'use']);
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
			SimpleAjax result_price = RemoteUtil.connect(SimpleAjax, 'ticketingsys', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200',op:'use']);
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
			SimpleAjax result_price = RemoteUtil.connect(SimpleAjax, 'ticketingsys', '/incall/order/preferential_reserve', [o_id:x.order_id,unicode:coupon_ids,pref:'200',op:'use']);
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
			def pref_data = [pref_id:pd['order_id'],oc:pd['oc'],pref_code:pd['prefcode'],pref_type:pd['prefcode'],pref_name:pd['prefcode'],real_deduct:pd['pref_amount'],max_deduct:pd['pref_amount']];
			trade_use_prefs.add(pref_data);
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
				def pref_data = [pref_id:x.order_id,oc:'1000',pref_code:'410',pref_type:'400',pref_name:'410',real_deduct:goods_pref_amount.toString(),max_deduct:goods_pref_amount.toString()];
				trade_use_prefs.add(pref_data);
			}
		}


		//		def count_amount = total_amount.multiply(cut_price(discount.vv)).setScale(2, BigDecimal.ROUND_HALF_UP);
		//		def deduct_amount = total_amount.subtract(count_amount);
		//		total_amount = count_amount;
		//		def pref_data = [pref_id:mpv,pref_code:'410',pref_type:'400',pref_name:'410',real_deduct:deduct_amount.toString(),max_deduct:deduct_amount.toString()];
		//		trade_use_prefs.add(pref_data);
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
				def pref_data = [pref_id:x.order_id,oc:'1000',pref_code:'420',pref_type:'400',pref_name:'420',real_deduct:goods_pref_amount.toString(),max_deduct:goods_pref_amount.toString()];
				trade_use_prefs.add(pref_data);
			}
		}

		//		def count_amount = AllowCusPrice.cutprice(discount.vv, total_amount);
		//		def deduct_amount = total_amount.subtract(count_amount);
		//		total_amount = count_amount;
		//		def pref_data = [pref_id:mpv,pref_code:'420',pref_type:'400',pref_name:'420',real_deduct:deduct_amount.toString(),max_deduct:deduct_amount.toString()];
		//		trade_use_prefs.add(pref_data);
	}
}

/** 结束计算优惠 **/
//开始保存优惠信息
if(trade_use_prefs) {
	for(x in trade_use_prefs) {
		TradePreferential tp = new TradePreferential();
		tp.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		tp.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		tp.flag = 0;
		tp.max_deduct = cut_price(x['max_deduct']);
		tp.oc = x['oc'];
		tp.order_id = x['pref_id'];
		tp.pref_code = x['pref_code'];
		tp.pref_type = x['pref_type'];
		tp.pref_name = x['pref_code'];
		tp.real_deduct = cut_price(x['real_deduct']);
		tp.t_id = trade.id;
		tp.t_num = trade.tnum;
		JcTemplate.INSTANCE().save(tp);

		//查找交易对应的trade_order_id，并修改tp_amount
		TradeOrder trade_order = JcTemplate.INSTANCE().get(TradeOrder, 'select * from TradeOrder where t_id=? and order_id=? and oc=?', trade.id, x['pref_id'], x['oc']);
		if(trade_order) {
			def to_amount = trade_order.tp_amount;
			if(!to_amount) {
				to_amount = trade_order.total_amount;
			}
			def fixed_amount = to_amount.subtract(tp.real_deduct);
			if(fixed_amount.compareTo(new BigDecimal(0))<0) {
				fixed_amount = new BigDecimal(0);
			}
			trade_order.tp_amount = fixed_amount.setScale(2);
			JcTemplate.INSTANCE().update(trade_order);
		}
	}
	//更新trade价格
	trade.pay_amount = total_amount;
	JcTemplate.INSTANCE().update(trade);
} 




PayService pay_service = PayService.INSTANCE();

PayResult ret_data = pay_service.scan_pay_router(supp_config, ct, unicode, trade, personal_param);

LOGGER.info 'ct pay_result ret_data=' + JackSonBeanMapper.toJson(ret_data);

if(ret_data.code!='0') {
	// 不成功，直接返回
	if(ret_data.err_code_des!=null&&ret_data.err_code_des!='') {
		ret_data.text = ret_data.text + ':' + ret_data.err_code_des;
	}
	return ret_data;
}

//获取打印模板
def smarttemplate = "<html></html>";
def ccid = null;
if (trade.log_id != null)  {
	CashDoLog log = CashierService.INSTANCE.get_counter_by_token(trade.log_id);
	ccid = log.ccid;
}

for(x in trade_orders) {
	if(x.oc=='1000') {
		x = TradeService.INSTANCE().getTradeById(x.id)
		smarttemplate = new FeedPrinter().get_printer_content(trade, x).toString();
		smarttemplate = URLEncoder.encode(smarttemplate, 'UTF-8');
		smarttemplate = URLEncoder.encode(smarttemplate, 'UTF-8');
		smarttemplate = PrinterUtil.getPrinterDtoLsit(pid,ccid, x.oc, smarttemplate);
	}
}
ret_data.smarttemplet = smarttemplate;

def op_errors = NotifyService.pub(trade);
if(!op_errors.empty) {
	ret_data.other = op_errors;
	ret_data.text = ret_data.text + '.库存或出票信息操作失败，请检查订单详情';
}
LOGGER.info 'ct pub_result ret_data=' + JackSonBeanMapper.toJson(op_errors);
return ret_data;

/**
 0:成功，其他均为不成功
 def code;
 def text;
 def err_code;
 def err_code_desc;
 def other;
 */


def cut_price(def price) {
	try {
		return new BigDecimal(price.toString());
	} catch(any) {
		return new BigDecimal(0);
	}
}




