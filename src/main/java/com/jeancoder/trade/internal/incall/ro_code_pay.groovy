package com.jeancoder.trade.internal.incall

import java.sql.Timestamp

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.RequestSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.log.JCLoggerFactory
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.dto.ProjectGeneralConfig
import com.jeancoder.trade.ready.dto.SysProjectInfo
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePreferential
import com.jeancoder.trade.ready.notify.NotifyService
import com.jeancoder.trade.ready.ser.TradeService
import com.jeancoder.trade.ready.ser.TriggerService
import com.jeancoder.trade.ready.unipay.OnlinePayService
import com.jeancoder.trade.ready.util.JackSonBeanMapper
import com.jeancoder.trade.ready.util.NativeUtil
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
 * ct:201101    微信支付公众号唤起
 * 
 * ct:202       支付宝支付
 * ct:202001    支付宝被扫
 * ct:202002    支付宝主扫
 * 
 * ct:301001	现在支付扫码刷卡
 */
/**
 * 线上收银台调用
 * 通用参数
 */
def ct = JC.internal.param('ct');
def tnum = JC.internal.param('tnum');
def unicode = JC.internal.param('unicode')?.trim();
def coupon_id = JC.internal.param('coupon_id');
def o_rem = JC.internal.param('o_rem')?.trim();
def domain = JC.internal.param('domain')?.trim();
def ap_id = JC.internal.param('ap_id');
try {
	ap_id = new BigInteger(ap_id);
} catch(any) {
	ap_id = new BigInteger(0);
}

JCLogger LOGGER = JCLoggerFactory.getLogger('ro_code_pay');

PayResult ret_data = new PayResult();

TradeService trade_service = TradeService.INSTANCE();
TradeInfo trade = trade_service.get_trade_by_num(tnum);

if(trade==null) {
	ret_data.code = '-1';
	ret_data.err_code = 'trade_not_found';
	ret_data.err_code_des = '交易初始化失败，请重新下单';
	ret_data.text = ret_data.err_code_des;
	return ret_data;
}
if(!trade.tss.startsWith('0')) {
	ret_data.code = '-1';
	ret_data.err_code = 'trade_status_invalid';
	ret_data.err_code_des = '交易状态错误，请检查';
	ret_data.text = ret_data.err_code_des;
	return ret_data;
}

def pid = trade.pid;
LOGGER.info('CT_CODE==========' + ct);
//获取项目配置信息
ProjectGeneralConfig supp_config = JC.internal.call(ProjectGeneralConfig, 'project', '/incall/project_single_pt', [sc_type:'1000',sc_code:ct,pid:pid]);
if(supp_config==null||supp_config.partner==null) {
	ret_data.code = '-1';
	ret_data.err_code = 'pay_config_error';
	ret_data.err_code_des = '系统配置错误，请稍后再试';
	ret_data.text = ret_data.err_code_des;
	return ret_data;
}
LOGGER.info('supp_config==========' + JackSonBeanMapper.toJson(supp_config));
if(!domain) {
	SysProjectInfo choo_proj = JC.internal.call(SysProjectInfo, 'project', '/incall/project_by_id', [pid:pid]);
	if(choo_proj) {
		domain = choo_proj.domain;
	}
}
def personal_param = [:];
def pwd = JC.internal.param('pwd');
if(pwd) {
	personal_param['pwd'] = pwd;
}
personal_param['unicode'] = unicode;
personal_param['pid'] = pid;
personal_param['domain'] = domain;

long start = System.currentTimeMillis();

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
//设置当前用户选择的支付方式
trade.pay_type = supp_config.sc_code;
// 恢复支付价格
trade.pay_amount = trade.total_amount;
if(trade.buyerid==null && ap_id!=0) {
	trade.buyerid = ap_id;
}
JcTemplate.INSTANCE().update(trade);
// 清空卡劵
SimpleAjax clear_coupon_result = NativeUtil.connect(SimpleAjax, 'market', '/coupon/clear_coupon_info', [pid:trade.pid.toString(),orders:orders]);
if (clear_coupon_result == null || !clear_coupon_result.available) {
	def msg = clear_coupon_result == null?'清空优惠信息失败':clear_coupon_result.messages[0];
	return SimpleAjax.notAvailable(msg);
}

/** 开始计算优惠 **/
List<TradeOrder> trade_orders = TradeService.INSTANCE().trade_items(trade);

def global_pref_radios = [:];

def queried_orders = [];
def mc_data = null;
for(x in trade_orders) {
	if(x.oc=='1000') {
		//针对商品订单，更新备注信息
		if(o_rem) {
			JC.thread.run(5000, {
				def update_result = JC.internal.call('scm', '/order/update_remark', [pid:pid,o_rem:o_rem,o_id:x.order_id]);
				return update_result;
			}, {
				e->
				println e.success;
				println e.data;
			});
		}
		//支持商品价格计算
		if(unicode) {
			//计算会员卡价格
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'scm', '/incall/order/preferential', [o_id:x.order_id,unicode:unicode,pref:'100',pid:pid]);
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
					LOGGER.error('', any);
				}
			}
		}
		def coupon_ids = coupon_id;
		SimpleAjax result_price = JC.internal.call(SimpleAjax, 'scm', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200',op:'use',pid:pid]);
		if (!StringUtil.isEmpty(coupon_ids)) {
			if (result_price == null)  {
				return SimpleAjax.notAvailable("卡劵模块通讯异常")
			}
			if (!result_price.available)  {
				return result_price;
			}
		}
		queried_orders.add(result_price);
	} else if(x.oc=='2000') {
		//支持影票价格计算
		if(unicode) {
			//计算会员卡价格
			SimpleAjax result_price = JC.internal.call(SimpleAjax, 'ticketingsys', '/incall/order/preferential', [o_id:x.order_id,unicode:unicode,pref:'100',pid:pid]);
			queried_orders.add(result_price);
			if(result_price&&result_price.available&&mc_data==null) {
				mc_data = result_price.data;
				//重置优惠系数
				try {
					def pref_amount = new BigDecimal(mc_data['pref_amount']);
					def total_amount = new BigDecimal(mc_data['pay_amount']);
					//radio = (total_amount.subtract(pref_amount)).divide(total_amount).setScale(2);
					radio = (total_amount.subtract(pref_amount)).divide(total_amount,4,BigDecimal.ROUND_HALF_UP);
					global_pref_radios['2000'] = radio;
				}catch(any) {
					LOGGER.error('', any);
				}
			}
		}
		def coupon_ids = coupon_id;
		SimpleAjax result_price = JC.internal.call(SimpleAjax, 'ticketingsys', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200',op:'use',pid:pid]);
		if (!StringUtil.isEmpty(coupon_ids)) {
			if (result_price == null)  {
				return SimpleAjax.notAvailable("卡劵模块通讯异常")
			}
			if (!result_price.available)  {
				return result_price;
			}
		}
		queried_orders.add(result_price);
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
					//radio = (total_amount.subtract(pref_amount)).divide(total_amount).setScale(2);
					radio = (total_amount.subtract(pref_amount)).divide(total_amount,4,BigDecimal.ROUND_HALF_UP);
					global_pref_radios['5000'] = radio;
				}catch(any) {
					LOGGER.error('', any);
				}
			}
		}
		def coupon_ids = coupon_id;
		SimpleAjax result_price = JC.internal.call(SimpleAjax, 'reserve', '/incall/order/preferential', [o_id:x.order_id,unicode:coupon_ids,pref:'200',op:'use',pid:pid]);
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

def total_amount = trade.total_amount;
def trade_use_prefs = [];
if(queried_orders) {
	//这里处理会员卡和优惠券
	def index = 0;
	for(SimpleAjax x in queried_orders) {
		if(x==null) {continue; }
		if(x.available) {
			//开始准备循环价格
			def pd = x.data;
			def pd_oc = pd['oc'];
			def pd_type = pd['prefcode'];
			
			BigDecimal need_cut_price = cut_price(pd['pref_amount']);
			
			if(pd_type!='100' && !trade.total_amount.compareTo(need_cut_price) == 0) {
				//不是会员卡需要乘以相应系数
				BigDecimal radio = global_pref_radios.get(pd_oc);
				if(radio&&radio.compareTo(new BigDecimal(0))==1) {
					need_cut_price = need_cut_price.multiply(radio).setScale(0,BigDecimal.ROUND_HALF_UP);
				}
			}
			println '=====' + pd_oc + '=====' + pd_type + '======' + need_cut_price;
			total_amount = total_amount.subtract(need_cut_price);
			if(total_amount.compareTo(new BigDecimal(0))<0) {
				total_amount = new BigDecimal(0);
			}
			def pref_data = [pref_id:pd['order_id'],oc:pd['oc'],pref_code:pd['prefcode'],pref_type:pd['prefcode'],
				pref_name:pd['prefcode'],real_deduct:need_cut_price,max_deduct:pd['pref_amount']];
			trade_use_prefs.add(pref_data);
		}
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

TriggerService trigger_service = TriggerService.INSTANCE();
OnlinePayService pay_service = OnlinePayService.INSTANCE();

def zero_total_amount_judge = trade.total_amount;
def zero_pay_amount_judge = trade.pay_amount;

zero_total_amount_judge = zero_total_amount_judge.add(trade.handle_fee).add(trade.service_fee);
zero_pay_amount_judge = zero_pay_amount_judge.add(trade.handle_fee).add(trade.service_fee);

if(zero_total_amount_judge.compareTo(new BigDecimal("0"))<=0||zero_pay_amount_judge.compareTo(new BigDecimal("0"))<=0) {
	//0元的交易直接归到现金支付里面
	ct = '001001';
}

ret_data = pay_service.pay_router(supp_config, ct, unicode, trade, personal_param);
LOGGER.info 'ct pay_result ret_data=' + JackSonBeanMapper.toJson(ret_data);

if(ret_data.code!='0') {
	// 不成功，直接返回
	if(ret_data.err_code_des!=null&&ret_data.err_code_des!='') {
		ret_data.text = ret_data.text + ':' + ret_data.err_code_des;
	}
	return ret_data;
}

if(ct.startsWith('101') || ct=='001001') {
	def store_id = trade.storeid;
	def store_name = '';
	if(trade.storename!=null) {
		store_name = URLEncoder.encode(URLEncoder.encode(trade.storename, "UTF-8"), "UTF-8");
	}
	//账户内支付，需要调用成功通知接口
	def op_errors = NotifyService.incallPub(trade);
	if(!op_errors.empty) {
		ret_data.other = op_errors;
		ret_data.text = ret_data.text + '.库存或出票信息操作失败，请检查订单详情';
	}
}
LOGGER.info 'ct pub ret_data=' + JackSonBeanMapper.toJson(ret_data);
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







