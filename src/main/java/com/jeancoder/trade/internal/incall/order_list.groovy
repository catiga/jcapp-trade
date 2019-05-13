package com.jeancoder.trade.internal.incall

import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.jdbc.JcPage
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.common.AvailabilityStatus
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.util.StreamUtil
import com.jeancoder.trade.ready.util.StringUtil

JCLogger logger = LoggerSource.getLogger();
def data = JC.internal.param("data");// 日期
def type = JC.internal.param("type");// 1000  手机号 2000 订单编号 3000 交易编号
def key = JC.internal.param("key");// 查询条件
def oss = JC.internal.param("oss");//  0000  所有, 1000 支付成功 2000  支付失败
def ps = JC.internal.param("ps"); // 每页数量
def pn = JC.internal.param("pn");// 当前页数
def pid = JC.internal.param("pid");//  
data = StringUtil.trim(data);
key = StringUtil.trim(key);
logger.info("order_list:{data="+data+",type="+type+",key="+key+",oss="+oss+",ps="+ps+",pn="+pn,"pid"+pid+"}");
if (StringUtil.isEmpty(pn)){
	pn = 1;
} else {
	pn = Integer.parseInt(pn);
}

if (StringUtil.isEmpty(ps)){
	ps = 10;
} else {
	ps = Integer.parseInt(ps);
}
try {
	if ("1000".equals(type) && !StringUtil.isEmpty(key)) {
		// 如果是手机号 需要转化成 会员卡号
		AvailabilityStatus status = JC.internal.call(AvailabilityStatus,"crm", "api/member/queryByMobileOrCard", [pid:1,query_key:StringUtil.trim(key)])
		if (!status.isAvailable()) {
			return SimpleAjax.notAvailable(status.messages[0]);
		}  else if (status.isAvailable() && (status.obj ==  null || status.obj.length == 0) ) {
			return SimpleAjax.notAvailable("未查到相关会员信息");
		} else {
			 key = "";
			 for (def item : status.obj) {
				 key += item.mc_num + ",";
			 }
			 if (key.length() != 0) {
				 key = key.substring(0,key.length()-1);
			 }
		}
	}
} catch(any) {
	logger.info("查询会员信息失败",any);
	return SimpleAjax.notAvailable("会员模块通信失败");
}





JcPage<TradeOrder> page = new JcPage<TradeOrder>();
page.setPn(pn)
page.setPs(ps);
if ("1000".equals(type) && !StringUtil.isEmpty(key)) {
	page = listByMobile(page, key, data, oss, pid);
} else {
	def param = [];
	String sql = "SELECT o.* FROM   trade_order  o, trade_info info WHERE  o.t_id=info.id  AND  info.flag != -1";
	// 日期筛选
	if(!StringUtil.isEmpty(data)) {
		sql += " AND  DATE_FORMAT(o.a_time  , '%Y-%m-%d') =? "
		param.add(data);
	}
	// 订单状态筛选
	if ("1000".equals(oss))  {
		sql += " AND  o.oss in (" + paySuccess() + ") "
	}
	if ("2000".equals(oss))  {
		sql += " AND  o.oss  not in (" + paySuccess() + ") "
	}
	sql += " AND  info.pid= ? ";
	param.add(pid);
	if ("2000".equals(type) && !StringUtil.isEmpty(key)) {
		sql += " AND  order_num= ? ";
		param.add(key);
	}
	if ("3000".equals(type) && !StringUtil.isEmpty(key)) {
		sql += " AND  tnum= ? ";
		param.add(key);
	}
	sql += " AND o.ouid IS NOT NULL"
	println param.toArray();
	JcTemplate jc_template = JcTemplate.INSTANCE();
	page =  jc_template.find(TradeOrder,page, sql, param.toArray());
}
def orders_mc = ""
def orders_scm = "";
def orders_movie = "";

for (def item : page.result) {
	if ("1000".equals(item.oc)) {
		orders_scm += item.order_num + ",1000" +";"
	}
	if ("2000".equals(item.oc)) {
		orders_movie += item.order_num + ",2000" +";"
	}
	if ("2010".equals(item.oc)) {
		orders_movie += item.order_num + ",2010" +";"
	}
	if ("8000".equals(item.oc)) {
		orders_mc += item.order_num + ",8000" +";"
	}
	if ("8001".equals(item.oc)) {
		orders_mc += item.order_num + ",8001" +";"
	}
}

if (!StringUtil.isEmpty(orders_mc)) {
	SimpleAjax ajax = JC.internal.call(SimpleAjax,"crm", "order/order_list_by_no_oc", [pid:pid.toString(),os:orders_mc])
	if (!ajax.available) {
		return ajax;
	}
	orders_mc = ajax.data
} else {
	orders_mc = []
}
if (!StringUtil.isEmpty(orders_scm)) {
	SimpleAjax ajax = JC.internal.call(SimpleAjax,"scm", "order/order_list_by_no_oc", [pid:pid.toString(),os:orders_scm])
	if (!ajax.available) {
		return ajax;
	}
	orders_scm = ajax.data
} else {
	orders_scm = []
}
if (!StringUtil.isEmpty(orders_movie)) {
	SimpleAjax ajax = JC.internal.call(SimpleAjax,"ticketingsys", "api/order_list_by_no_oc", [pid:pid.toString(),os:orders_movie])
	if (!ajax.available) {
		return ajax;
	}
	orders_movie = ajax.data
} else {
	orders_movie = []
}

for (def order : page.result) {
	order.order_data = null;
	if ("1000".equals(order.oc)) {
		for (def o : orders_scm) {
			if (order.order_num.equals(o.order_no)) {
				order.order_detail = o;
			}
		}
		continue;
	}
	if ("2000".equals(order.oc) ||"2010".equals(order.oc)  ) {
		for (def o : orders_movie) {
			if (order.order_num.equals(o.order_no) && order.oc.equals(o.oc) ) {
				order.order_detail = o;
			}
		}
		continue;
	}
	if ("8000".equals(order.oc) || "8001".equals(order.oc)) {
		for (def o : orders_scm) {
			if (order.order_num.equals(o.order_no)  && order.oc.equals(o.oc)) {
				order.order_detail = o;
			}
		}
		continue;
	}
}




return SimpleAjax.available('', page);

public String paySuccess() {
	return "1000,1010,1020,2000,3000" ;
}

def  listByMobile(JcPage<TradeOrder> page, String key, String data, String oss, def pid) {
	def param = [];
	String sql = "SELECT o.* FROM   trade_order  o, trade_info info,  trade_pay_info pay_info WHERE  o.t_id=info.id   AND info.id = pay_info.t_id AND  info.flag != -1";
	// 日期筛选
	if(!StringUtil.isEmpty(data)) {
		sql += " AND  DATE_FORMAT(o.a_time  , '%Y-%m-%d') =? "
		param.add(data);
	}
	// 订单状态筛选
	if ("1000".equals(oss))  {
		sql += " AND  o.oss in (" + paySuccess() + ") "
	}
	if ("2000".equals(oss))  {
		sql += " AND  o.oss  not in (" + paySuccess() + ") "
	}
	sql += " AND  info.pid= ? AND pay_info.trans_user_id =?";
	sql += " AND o.ouid IS NOT NULL"
	param.add(pid);
	param.add(key);
	JcTemplate jc_template = JcTemplate.INSTANCE();
	return jc_template.find(TradeOrder,page, sql, param.toArray());
}




