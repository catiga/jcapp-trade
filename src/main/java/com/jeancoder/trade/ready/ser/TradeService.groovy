package com.jeancoder.trade.ready.ser

import groovy.sql.Sql

import java.sql.Timestamp

import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.jdbc.JcPage
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.dto.ActOrder
import com.jeancoder.trade.ready.dto.DataTcSsReserveOrderInfo
import com.jeancoder.trade.ready.dto.DataTcSsSaleOrderInfo
import com.jeancoder.trade.ready.dto.McRechargeOrderDto
import com.jeancoder.trade.ready.dto.OrderInfo
import com.jeancoder.trade.ready.dto.OrderMcDto
import com.jeancoder.trade.ready.dto.PayResult
import com.jeancoder.trade.ready.dto.SaleOrder
import com.jeancoder.trade.ready.dto.ServiceOrder
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.entity.TradePreferential
import com.jeancoder.trade.ready.gen.OrderNoGenerator
import com.jeancoder.trade.ready.util.GlobalHolder

class TradeService {

	private static final TradeService __instance__ = new TradeService();

	JcTemplate jc_template = JcTemplate.INSTANCE();

	public static TradeService INSTANCE() {
		return __instance__;
	}

	List<TradeInfo> get_trade_by_order(def order_id, def oc) {
		String sql = 'select * from TradeInfo where flag!=? and id in (select t_id from TradeOrder where order_id=? and oc=?)';
		return jc_template.find(TradeInfo.class, sql, -1, order_id, oc);
	}

	def find(JcPage<TradeInfo> page, def tnum, def order_num, def store_id, def tss = null, def third_id = null) {
		String sql = 'select * from TradeInfo where flag!=?';
		def param = [];
		param.add(-1);
		if(GlobalHolder.proj.root!=1) {
			sql += ' and pid=?';
			param.add(GlobalHolder.proj.id);
		}
		if(tnum!=null&&tnum!='') {
			sql += ' and tnum=?';
			param.add(tnum);
		}
		if(order_num!=null&&order_num!='') {
			sql += ' and id in (select t_id from TradeOrder where order_num=?)';
			param.add(order_num);
		}
		if(store_id) {
			sql += ' and storeid=?';
			param.add(store_id);
		}
		if(tss!=null) {
			sql += ' and tss in (';
			tss.each({
				sql += '?,';
				param.add(it);
			});
			sql = sql.substring(0, sql.length() - 1) + ')';
		}
		if(third_id!=null) {
			sql += ' and buyerid=?';
			param.add(third_id);
		}
		sql += ' order by a_time desc';
		return jc_template.find(TradeInfo.class, page, sql, param.toArray());
	}

	def trade_items(TradeInfo trade) {
		if(trade==null) {
			return null;
		}
		String sql = 'select * from TradeOrder where t_id=?';
		return jc_template.find(TradeOrder.class, sql, trade.id);
	}

	def get_trade(def t_id) {
		if(!t_id) {
			return null;
		}
		String sql = 'select * from TradeInfo where id=?';
		return jc_template.get(TradeInfo.class, sql, t_id);
	}

	def get_trade_by_num(def trade_num) {
		if(trade_num==null||trade_num.toString().trim()=='') {
			return null;
		}
		String sql = 'select * from TradeInfo where tnum=? and flag!=?';
		return jc_template.get(TradeInfo.class, sql, trade_num.toString().trim(), -1);
	}

	def get_trade_order(def trade_id, def order_num) {
		def sql = 'select * from TradeOrder where t_id=? and order_num=?';
		def param = []; param+=trade_id; param+=order_num;
		if(GlobalHolder.proj.root!=1) {
			sql += ' and t_id in (select id from TradeInfo where pid=?)';
			param+=GlobalHolder.proj.id;
		}
		return jc_template.find(TradeOrder, sql, param.toArray());
	}

	def get_trade_order_by_toid(def toid) {
		def sql = 'select * from TradeOrder where id=?';
		def param = []; param+=toid;
		if(GlobalHolder.proj.root!=1) {
			sql += ' and t_id in (select id from TradeInfo where pid=?)';
			param+=GlobalHolder.proj.id;
		}
		return jc_template.get(TradeOrder, sql, param.toArray());
	}

	def merge_trade(TradeInfo trade, def orders) {
		trade.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());

		def trade_items = [];
		BigDecimal total_amount = trade.total_amount;
		BigDecimal pay_amount = trade.pay_amount;
		BigDecimal total_handle_fee = new BigDecimal(0);
		BigDecimal total_service_fee = new BigDecimal(0);
		for(o in orders) {
			if(o instanceof OrderInfo) {
				//商品订单
				OrderInfo goods_order = (OrderInfo)o;
				TradeOrder to = new TradeOrder();
				to.a_time = goods_order.a_time;
				to.c_time = goods_order.c_time;
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(goods_order);
				to.order_id = goods_order.id;
				to.order_num = goods_order.order_no;
				to.oc = '1000';
				to.total_amount = goods_order.total_amount;
				to.pay_amount = goods_order.pay_amount;
				to.pid = goods_order.pid;
				to.storeid = goods_order.store_id;
				to.storename = goods_order.store_name;
				to.buyerid = goods_order.buyerid;
				to.buyername = goods_order.buyername;
				to.buyerphone = goods_order.buyerphone;
				to.ouid = goods_order.ouid;
				to.ouname = goods_order.ouname;
				to.oss = trade.tss;
				if(trade.storeid==null) {
					trade.storeid = to.storeid;
					trade.storename = to.storename;
				}
				to.t_id = trade.id;
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				trade_items.add(to);
				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			} else if(o instanceof SaleOrder) {
				//选座订单
				SaleOrder tcss_order = (SaleOrder)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(tcss_order);
				to.order_id = tcss_order.id;
				to.order_num = tcss_order.order_no;
				to.oc = '2000';
				to.total_amount = new BigDecimal(tcss_order.total_amount.toString());
				try {
					to.pay_amount = new BigDecimal(tcss_order.pay_amount.toString());
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				//to.storeid = tcss_order.store_id;
				if(tcss_order.store_basic)
					to.storeid = tcss_order.store_basic;
				else
					to.storeid = tcss_order.store_id;
				to.storename = tcss_order.store_name;
				to.t_id = trade.id;
				to.handle_fee = tcss_order.handle_fee;
				to.service_fee = tcss_order.service_fee;
				trade_items.add(to);
				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			} else if(o instanceof OrderMcDto) {
				//会员开卡订单
				OrderMcDto mc_order = (OrderMcDto)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(mc_order);
				to.order_id = mc_order.id;
				to.order_num = mc_order.order_no;
				to.oc = '8000';
				to.total_amount = new BigDecimal(mc_order.total_amount);
				try {
					to.pay_amount = new BigDecimal(mc_order.pay_amount);
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				to.t_id = trade.id;
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				trade_items.add(to);
				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			} else if(o instanceof McRechargeOrderDto) {
				//会员开卡订单
				McRechargeOrderDto buy_order = (McRechargeOrderDto)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(buy_order);
				to.order_id = buy_order.id;
				to.order_num = buy_order.order_no;
				to.oc = '8001';
				to.total_amount = new BigDecimal(buy_order.total_amount);
				try {
					to.pay_amount = new BigDecimal(buy_order.pay_amount);
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				to.t_id = trade.id;
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				trade_items.add(to);
				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			} else if(o instanceof ServiceOrder) {
				//预约类订单
				ServiceOrder buy_order = (ServiceOrder)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(buy_order);
				to.order_id = buy_order.id;
				to.order_num = buy_order.order_no;
				to.oc = '5000';

				to.storeid = buy_order.store_id;
				to.storename = buy_order.storename;
				to.buyerid = buy_order.ap_id;

				to.total_amount = new BigDecimal(buy_order.total_amount);
				try {
					to.pay_amount = new BigDecimal(buy_order.pay_amount);
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				trade_items.add(to);

				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			} else if(o instanceof ActOrder) {
				//活动类订单
				ActOrder buy_order = (ActOrder)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(buy_order);
				to.order_id = buy_order.id;
				to.order_num = buy_order.order_no;
				to.oc = '5000';

				to.buyerid = buy_order.ap_id;

				to.total_amount = new BigDecimal(buy_order.total_amount);
				try {
					to.pay_amount = new BigDecimal(buy_order.pay_amount);
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				trade_items.add(to);

				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			}
		}
		//更新交易金额
		trade.pay_amount = pay_amount;
		trade.total_amount = total_amount;
		trade.handle_fee = total_handle_fee;
		trade.service_fee = total_service_fee;
		
		jc_template.update(trade);

		for(ti in trade_items) {
			jc_template.save(ti);
		}

		return trade;
	}

	/**
	 * @param orders
	 * @return
	 */
	def create_trade(def pid = GlobalHolder.proj.id, def orders,def log_id = null) {
		TradeInfo trade = new TradeInfo();
		trade.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		trade.c_time = trade.a_time;
		trade.flag = 0;
		trade.tss = '0000';
		trade.pid = pid;
		trade.log_id = log_id;
		
		def trade_items = [];
		BigDecimal total_amount = new BigDecimal(0);
		BigDecimal pay_amount = new BigDecimal(0);
		BigDecimal total_handle_fee = new BigDecimal(0);
		BigDecimal total_service_fee = new BigDecimal(0);
		for(o in orders) {
			if(o instanceof OrderInfo) {
				//商品订单
				OrderInfo goods_order = (OrderInfo)o;
				TradeOrder to = new TradeOrder();
				to.a_time = goods_order.a_time;
				to.c_time = goods_order.c_time;
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(goods_order);
				to.order_id = goods_order.id;
				to.order_num = goods_order.order_no;
				to.oc = '1000';
				to.total_amount = goods_order.total_amount;
				to.pay_amount = goods_order.pay_amount;
				to.pid = goods_order.pid;
				to.storeid = goods_order.store_id;
				to.storename = goods_order.store_name;
				to.buyerid = goods_order.buyerid;
				to.buyername = goods_order.buyername;
				to.buyerphone = goods_order.buyerphone;
				to.ouid = goods_order.ouid;
				to.ouname = goods_order.ouname;
				to.oss = trade.tss;
				if(trade.storeid==null) {
					trade.storeid = to.storeid;
					trade.storename = to.storename;
				}
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				if (to.handle_fee  == null) {
					to.handle_fee = new BigDecimal(0);
				}
				if (to.service_fee  == null) {
					to.service_fee = new BigDecimal(0);
				}
				trade_items.add(to);

				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			} else if(o instanceof SaleOrder) {
				//选座订单
				//DataTcSsSaleOrderInfo tcss_order = (DataTcSsSaleOrderInfo)o;
				SaleOrder tcss_order = (SaleOrder)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(tcss_order);
				to.order_id = tcss_order.id;
				to.order_num = tcss_order.order_no;
				to.oc = '2000';
				to.total_amount = new BigDecimal(tcss_order.total_amount.toString());
				try {
					to.pay_amount = new BigDecimal(tcss_order.pay_amount.toString());
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				//to.storeid = tcss_order.store_id;
				if(tcss_order.store_basic)
					to.storeid = tcss_order.store_basic;
				else
					to.storeid = tcss_order.store_id;
				to.storename = tcss_order.store_name;
				if(!trade.storeid) {
					trade.storeid = to.storeid;
					trade.storename = to.storename;
				}
				to.handle_fee = tcss_order.handle_fee;
				to.service_fee = tcss_order.service_fee;
				
				if (to.handle_fee  == null) {
					to.handle_fee = new BigDecimal(0);
				}
				if (to.service_fee  == null) {
					to.service_fee = new BigDecimal(0);
				}
				trade_items.add(to);

				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			}else if(o instanceof DataTcSsReserveOrderInfo) {
				//预约选座订单
				DataTcSsReserveOrderInfo tcss_order = (DataTcSsReserveOrderInfo)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(tcss_order);
				to.order_id = tcss_order.id;
				to.order_num = tcss_order.order_no;
				to.oc = '2010';
				to.total_amount = new BigDecimal(tcss_order.total_amount.toString());
				try {
					to.pay_amount = new BigDecimal(tcss_order.pay_amount.toString());
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				to.storeid = tcss_order.store_id;
				to.storename = tcss_order.store_name;
				if(!trade.storeid) {
					trade.storeid = to.storeid;
					trade.storename = to.storename;
				}
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				if (to.handle_fee  == null) {
					to.handle_fee = new BigDecimal(0);
				}
				if (to.service_fee  == null) {
					to.service_fee = new BigDecimal(0);
				}
				trade_items.add(to);

				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			} else if(o instanceof OrderMcDto) {
				//会员开卡订单
				OrderMcDto mc_order = (OrderMcDto)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(mc_order);
				to.order_id = mc_order.id;
				to.order_num = mc_order.order_no;
				to.oc = '8000';
				to.total_amount = new BigDecimal(mc_order.total_amount);
				try {
					to.pay_amount = new BigDecimal(mc_order.pay_amount);
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				trade_items.add(to);

				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			} else if(o instanceof McRechargeOrderDto) {
				//会员卡充值订单
				McRechargeOrderDto buy_order = (McRechargeOrderDto)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(buy_order);
				to.order_id = buy_order.id;
				to.order_num = buy_order.order_no;
				to.oc = '8001';
				to.total_amount = new BigDecimal(buy_order.total_amount);
				try {
					to.pay_amount = new BigDecimal(buy_order.pay_amount);
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				trade_items.add(to);

				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			} else if(o instanceof ServiceOrder) {
				//预约类订单
				ServiceOrder buy_order = (ServiceOrder)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(buy_order);
				to.order_id = buy_order.id;
				to.order_num = buy_order.order_no;
				to.oc = '5000';

				to.storeid = buy_order.store_id;
				to.storename = buy_order.storename;
				to.buyerid = buy_order.ap_id;

				to.total_amount = new BigDecimal(buy_order.total_amount);
				try {
					to.pay_amount = new BigDecimal(buy_order.pay_amount);
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				trade_items.add(to);

				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			} else if(o instanceof ActOrder) {
				//活动类订单
				ActOrder buy_order = (ActOrder)o;
				TradeOrder to = new TradeOrder();
				to.a_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				to.flag = 0;
				to.order_data = JackSonBeanMapper.toJson(buy_order);
				to.order_id = buy_order.id;
				to.order_num = buy_order.order_no;
				to.oc = '5000';

				to.buyerid = buy_order.ap_id;

				to.total_amount = new BigDecimal(buy_order.total_amount);
				try {
					to.pay_amount = new BigDecimal(buy_order.pay_amount);
				}catch(Exception e) {
					to.pay_amount = new BigDecimal(0);
				}
				to.oss = trade.tss;
				to.handle_fee = new BigDecimal(0);
				to.service_fee = new BigDecimal(0);
				trade_items.add(to);

				total_amount = total_amount.add(to.total_amount);
				pay_amount = pay_amount.add(to.pay_amount);
				total_handle_fee = total_handle_fee.add(to.handle_fee);
				total_service_fee = total_service_fee.add(to.service_fee);
			}
		}
		//TODO 901 需要用算法计算生成规则
		trade.tnum = '901' + OrderNoGenerator.generateNo();
		trade.pay_amount = pay_amount;
		trade.total_amount = total_amount;
		trade.handle_fee = total_handle_fee;
		trade.service_fee = total_service_fee;
		
		BigDecimal trade_id = jc_template.save(trade);
		trade.id = trade_id;
		for(ti in trade_items) {
			ti.t_id = trade_id;
			jc_template.save(ti);
		}
		return trade;
	}

	def save_trade_pay_data(TradeInfo trade, def pay_data_info) {
		if(!trade.tss.startsWith('0')) { return; } //不属于可支付的交易状态，仅创建类交易可执行该操作

		PayResult pay_result = pay_data_info[0];
		Map<String, String> pay_data = pay_data_info[1];
		if(pay_result.code!='0') {
			return false;
		}
		TradePayInfo pay_obj = new TradePayInfo();
		pay_obj.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		pay_obj.flag = 0;
		pay_obj.pay_code = pay_result.paycode;
		pay_obj.pay_time = pay_obj.c_time;
		pay_obj.t_id = trade.id;
		pay_obj.t_num = trade.tnum;
		pay_obj.pay_type = '10';			//定义为扣款交易
		pay_obj.bank_type = pay_data['bank_type'];
		pay_obj.trans_id = pay_data['trans_id'];
		pay_obj.trans_pay_amount = pay_data['trans_pay_amount'];
		pay_obj.trans_total_amount = pay_data['trans_total_amount'];
		pay_obj.trans_type = pay_data['trans_type'];
		pay_obj.trans_user_id = pay_data['trans_user_id'];
		pay_obj.trans_get_account = pay_data['trans_get_account'];
		pay_obj.pay_id = pay_data['pay_id'];
		
		if(pay_data['cash_col']) {
			pay_obj.trans_cash_col = pay_data['cash_col'];
		}
		jc_template.save(pay_obj);

		//更新trade的交易状态
		trade.tss = '1000';	//更新为支付成功状态
		trade.pay_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		trade.pay_type = pay_result.paycode;
		jc_template.update(trade);
		//更新项下为同步状态
		List<TradeOrder> trade_orders = this.trade_items(trade);
		for(x in trade_orders) {
			x.oss = trade.tss;
			jc_template.update(x)
		}
		return trade;
	}

	def save_trade_refund_data(TradeInfo trade, def pay_data_info) {
		if(!trade.tss.startsWith('1')) { return; } //不属于可支付的交易状态，仅创建类交易可执行该操作

		PayResult pay_result = pay_data_info[0];
		Map<String, String> pay_data = pay_data_info[1];
		if(pay_result.code!='0') {
			return false;
		}
		TradePayInfo pay_obj = new TradePayInfo();
		pay_obj.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		pay_obj.flag = 0;
		pay_obj.pay_code = pay_result.paycode;
		pay_obj.pay_time = pay_obj.c_time;
		pay_obj.t_id = trade.id;
		pay_obj.t_num = trade.tnum;
		pay_obj.pay_type = '60';			//定义为退款交易
		pay_obj.bank_type = pay_data['bank_type'];
		pay_obj.trans_id = pay_data['trans_id'];
		pay_obj.trans_pay_amount = pay_data['trans_pay_amount'];
		pay_obj.trans_total_amount = pay_data['trans_total_amount'];
		pay_obj.trans_type = pay_data['trans_type'];
		pay_obj.trans_user_id = pay_data['trans_user_id'];
		pay_obj.trans_get_account = pay_data['trans_get_account'];
		pay_obj.pay_id = pay_data['pay_id'];
		jc_template.save(pay_obj);

		//更新trade的交易状态
		trade.tss = '8800';	//更新为退款成功状态
		jc_template.update(trade);
		return trade;
	}

	def save_trade_order_refund_data(TradeInfo trade, TradeOrder order, def pay_data_info) {
		if(!trade.tss.startsWith('1')) { return; } //不属于可支付的交易状态，仅创建类交易可执行该操作

		PayResult pay_result = pay_data_info[0];
		Map<String, String> pay_data = pay_data_info[1];
		if(pay_result.code!='0') {
			return false;
		}
		//增加一笔退款交易
		TradePayInfo pay_obj = new TradePayInfo();
		pay_obj.c_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		pay_obj.flag = 0;
		pay_obj.pay_code = pay_result.paycode;
		pay_obj.pay_time = pay_obj.c_time;
		pay_obj.t_id = trade.id;
		pay_obj.t_num = trade.tnum;
		pay_obj.pay_type = '60';			//定义为退款交易
		pay_obj.bank_type = pay_data['bank_type'];
		pay_obj.trans_id = pay_data['trans_id'];
		pay_obj.trans_pay_amount = pay_data['trans_pay_amount'];
		pay_obj.trans_total_amount = pay_data['trans_total_amount'];
		pay_obj.trans_type = pay_data['trans_type'];
		pay_obj.trans_user_id = pay_data['trans_user_id'];
		pay_obj.trans_get_account = pay_data['trans_get_account'];
		pay_obj.pay_id = pay_data['pay_id'];
		jc_template.save(pay_obj);

		//更新trade的交易状态
		trade.tss = '8850';	//更新为部分退款状态
		jc_template.update(trade);
		//更新交易订单
		order.oss = '8800';
		jc_template.update(order);
		return trade;
	}

	def get_trade_pay(TradeInfo trade) {
		String sql = 'select * from TradePayInfo where flag!=? and t_id=?';
		def trade_id = trade.id;

		return jc_template.find(TradePayInfo, sql, -1, trade_id);
	}

	def get_trade_pre(TradeInfo trade) {
		def sql = 'select * from TradePreferential where flag!=? and t_id=?';
		return jc_template.find(TradePreferential, sql, -1, trade.id);
	}

	def getTradeById(BigInteger id){
		String sql = "select * from TradeOrder where id=? and flag!=?";
		return jc_template.get(TradeOrder, sql, id, -1);
	}
	
	def getTradeByNum(String num, def oc, def pid){
		String sql = "SELECT o.* FROM TradeOrder o,trade_info t  WHERE   o.flag!=?   AND o.oc=?     AND  o.order_num=? AND o.t_id = t.id   AND  t.pid =? ";
		List<TradeOrder> order = jc_template.find(TradeOrder, sql, -1,oc,num,pid);
		if (order == null || order.isEmpty()) {
			return null;
		}
		return  order.get(0);
	}
}
