package com.jeancoder.trade.ready.printer

import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.List

import com.jeancoder.app.sdk.JC
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.OrderItem
import com.jeancoder.trade.ready.dto.OrderItemPack
import com.jeancoder.trade.ready.dto.ProjectConfigSimple
import com.jeancoder.trade.ready.entity.SysSupp
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.entity.TradePayInfo
import com.jeancoder.trade.ready.util.MoneyUtil
import com.jeancoder.trade.ready.util.NativeUtil
import com.jeancoder.trade.ready.util.SSUtil

class FeedPrinter {

	static SimpleDateFormat _sdf_ = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss');

	static void main(def ar) {
		
	}
	
	/**
	 * 票务专用
	 * @param trade
	 * @param trade_order
	 * @return
	 */
	static String get_printer_content_for_ticket(TradeInfo trade, TradeOrder trade_order) {
		def tp_store_name = trade.storename;
		def tp_store_address = tp_store_name;
		def tp_store_phone = '';
		def tp_order_num = trade_order.order_num;
		
		def base_smarttemplate = """
			<div style="position:relative; z-index:100; width:350px; height:380px;">
				<font id="p_ycmc" style="font-size: 13px;position:absolute; z-index:200; top:0px; left:80px;">[[store_name]]</font>
				<font id="p_yingtingmingcheng" style="font-size: 13px;position:absolute; z-index:200; top:20px; left:25px;">[[hall_name]]</font>
				<font id="p_shijian" style="font-size: 13px;position:absolute; z-index:200; top:20px;; left:130px;">[[plan_date]]</font>
				<font id="p_yingpianmingcheng" style="font-size: 13px;position:absolute; z-index:200; top:40px; left:25px">[[film_name]]</font>
				<font id="p_zuowei" style="font-size: 13px;position:absolute; z-index:200; top:60px; left:25px;">[[ticket_seat]]</font>
				<font id="p_ttype" style="font-size: 13px;position:absolute; z-index:200; top:80px; left:25px;">现场票</font>
				<font id="p_stime" style="font-size: 13px;position:absolute; z-index:200; top:100px; left:45px;">[[plan_time]]</font>
				<font id="p_price" style="font-size: 13px;position:absolute; z-index:200; top:60px; left:130px;">[[ticket_price]]</font>
				<font id="p_o_price" style="font-size: 13px;position:absolute; z-index:200; top:73px; left:130px;">服务费0元</font>
				<font id="p_seller" style="font-size: 13px;position:absolute; z-index:200; top:100px; left:130px;">006</font>
				
				<font id="pf_ticn1" style="font-size: 13px;position:absolute; z-index:200; top:185px; left:215px;"></font>
				<font id="pf_ticn2" style="font-size: 13px;position:absolute; z-index:200; top:198px; left:215px;"></font>
				
				<font id="pf_piaojia" style="font-size: 13px;position:absolute; z-index:200; top:245px; left:215px;"></font>
				<font id="pf_tic_no" style="font-size: 13px;position:absolute; z-index:200; top:228px; left:50px;">[[order_num]]</font>
				
				<font id="tip" style="font-size: 13px;position:absolute; z-index:200; top:255px; left:50px;"></font>
			</div>
		""";
		return base_smarttemplate;	//默认打印模版
		
		/*
		//开始去票务获取详细订单信息
		SimpleAjax ret_tickets = JC.internal.call(SimpleAjax, 'ticketingsys', '/ticketing/ticket_order_detail', [order_id:trade_order.order_id]);
		if(!ret_tickets.available) {
			return;
		}
		def sale_order = ret_tickets.data[0];
		def sale_seats = ret_tickets.data[1];
		
		def order_id = sale_order['id'];
		def store_name = sale_order['store_name'];
		def hall_name = sale_order['hall_name'];
		def plan_date = sale_order['plan_date'];
		def plan_time = sale_order['plan_time'];
		def film_name = sale_order['film_name'];
		def pay_amount = MoneyUtil.divide(sale_order['pay_amount'] + '', '100');
		def ticket_num = sale_order['ticket_sum'];
		
		def pp_tnum = sale_order['order_no'];
		
		base_smarttemplate = base_smarttemplate.replace('[[store_name]]', store_name);
		base_smarttemplate = base_smarttemplate.replace('[[hall_name]]', hall_name);
		base_smarttemplate = base_smarttemplate.replace('[[film_name]]', film_name);
		base_smarttemplate = base_smarttemplate.replace('[[plan_time]]', plan_time);
		
		base_smarttemplate = base_smarttemplate.replace('[[order_num]]', pp_tnum);
		base_smarttemplate = base_smarttemplate.replace('[[plan_date]]', plan_date);
		
		
		def ret_seats = [];
		for(s in sale_seats) {
			def seat_ss_id = s['id'];
			def seat_ss_sr = s['seat_sr'];
			def seat_ss_sc = s['seat_sc'];
			def seat_ss_price = s['sale_fee'];
			
			seat_ss_price = MoneyUtil.divide(seat_ss_price + '', '100');
			
			base_smarttemplate = base_smarttemplate.replace("[[ticket_seat]]", seat_ss_sr + '排' + seat_ss_sc + '座');
			base_smarttemplate = base_smarttemplate.replace("[[ticket_price]]", seat_ss_price);
			
			TicketContentHtml tch = new TicketContentHtml();
			tch.html = base_smarttemplate;
			tch.order_id = order_id + '';
			tch.order_seat_id = seat_ss_id + '';
			
			//ret_seats.add(base_smarttemplate);
			ret_seats.add(tch);
		}
		return ret_seats;
		*/
	}

	static def get_printer_content(TradeInfo trade, TradeOrder trade_order) {
		def tp_store_name = trade.storename;
		def tp_store_address = tp_store_name;
		def tp_store_phone = '';
		def tp_order_num = trade_order.order_num;
		//开始获取门店信息
		def storeinfo = JC.internal.call('project', '/incall/store_by_id', [pid:trade.pid,id:trade.storeid]);
		try {
			storeinfo = JackSonBeanMapper.jsonToMap(storeinfo);
			tp_store_address = storeinfo['address'];
			tp_store_phone = storeinfo['phone'];
		} catch(any) {}

		def tp_pay_code = trade.pay_type;
		def result = NativeUtil.connect(SimpleAjax.class, 'project', '/incall/project_pts', [tyc:'1000',pid:trade.pid]);
		if(result!=null && result.available) {
			for (def item : result.data) {
				if(item.sc_code.equals(tp_pay_code)) {
					tp_pay_code = item.sc_name;
				}
			}
		}

		def tp_cashier_name = trade_order.ouname==null?"":trade_order.ouname;
		def tp_cash_time = _sdf_.format(trade_order.a_time);

		def order_details = "";
		def sum_amount = new BigDecimal("0");
		def order = null;
		SimpleAjax rules = JC.internal.call(SimpleAjax, "scm", "/order/get_order_details",[order_no:trade_order.order_num]);
		if (rules != null && rules.available) {
			order = rules.data;
		}
		def caller_num = '';
		def table_num = '';
		def room_num = '';
		if (order != null && order.nerm != null) {
			caller_num = order.nerm.caller_num==null?'':order.nerm.caller_num;
			table_num = order.nerm.table_num==null?'':order.nerm.table_num;
			room_num = order.nerm.room_num==null?'':order.nerm.room_num;
		}
		
		String sql = 'select * from TradePayInfo where flag!=? and t_id=? and pay_type=?';
		List<TradePayInfo> infoList = JcTemplate.INSTANCE.find(TradePayInfo, sql, -1, trade.id,'10');
		BigDecimal  trans_cash_col = null;
		if (infoList!= null && !infoList.isEmpty()) {
			trans_cash_col = infoList.get(0).trans_cash_col;
		}
		def zero_amount = '0'
		for(OrderItem y : order.items) {
			def goods_name = y.goods_name;
			def goods_num = new BigDecimal(y.buy_num.toString());
			def unit_amount = new BigDecimal(y.unit_amount.toString()).divide(new BigDecimal(100)).setScale(2);
			def pay_amount = new BigDecimal(y.pay_amount.toString()).divide(new BigDecimal(100)).setScale(2);
			pay_amount = pay_amount.multiply(goods_num).setScale(2);
			sum_amount = sum_amount.add(pay_amount);
			if ('200'.equals(y.tycode)) {
				order_details += getItemHtml(y);
				continue;
			}
			
			def remark = y.remark==null?"":y.remark;
			def item = """
				<div>
					<div style="width:20mm;margin-top:1mm;float:left;">
					  <span style="width:20mm;margin-top:1mm;display:inline-block;font-size:8pt;word-break:break-all;word-wrap:break-word;float:left;">${goods_name} </span>
					</div>
					<div style="width:8mm;margin-top:1mm;float:left;margin-left:1mm">
					  <span style="width:8mm;margin-top:1mm;display:inline-block;font-size:8pt;float:left;">X${goods_num}</span>
					</div>
					<div style="width:8mm;margin-top:1mm;float:left;margin-left:1mm">
					  <span style="width:8mm;margin-top:1mm;display:inline-block;font-size:8pt;float:left;">${unit_amount}</span>
					</div>
					<div style="width:8mm;margin-top:1mm;float:left;margin-left:1mm">
					  <span style="width:8mm;margin-top:1mm;display:inline-block;font-size:8pt;float:left;">${pay_amount}</span>
					</div>
					<div style="clear:both"></div>
					<div>
					  <span style="font-size:8pt;">备注：</span>
					  <span style="margin-top:1mm;display:inline-block;width:42mm;font-size:8pt;word-break:break-all;word-wrap:break-word;">${remark}</span>
					</div>
				</div>
			""";
			order_details += item;
		}

		BigDecimal real_money = new BigDecimal("0");
		
		def free_money =new BigDecimal("0");
		if (trade_order.tp_amount != null) {
			real_money=trade_order.tp_amount.divide(new BigDecimal(100)).setScale(2,RoundingMode.HALF_UP);
			free_money= sum_amount.subtract(real_money).setScale(2,RoundingMode.HALF_UP);
		} else {
			real_money = trade_order.pay_amount.divide(new BigDecimal(100)).setScale(2,RoundingMode.HALF_UP);
		}
		
		if(real_money==0){
			real_money=real_money.setScale(2);
		}
		if (free_money==0) {
			free_money=free_money.setScale(2);
		}
		
		if (trans_cash_col == null) {
			trans_cash_col = real_money;
		} else {
			zero_amount = new BigDecimal(trans_cash_col.toString()).subtract(new BigDecimal(real_money.toString()).multiply(new BigDecimal(100))).setScale(2)
			zero_amount = new BigDecimal(zero_amount.toString()).divide(new BigDecimal(100)).setScale(2)
			trans_cash_col = new BigDecimal(trans_cash_col.toString()).divide(new BigDecimal(100)).setScale(2)
		}
		//打印模版
		def goods_smarttemplate = """
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>前台小票打印</title>
    </head>
    <body style="font-size:8pt;font-family:微软雅黑;padding-bottom:15px;">
     <div style="width:58mm">
      <div>
             <div>
              <span style="font-size:8pt;font-family:微软雅黑;text-align:left;word-break:break-all;word-wrap:break-word;">${tp_store_name}</span><br>
              <span style="font-size:8pt;word-break:break-all;word-wrap:break-word;">地址：${tp_store_address}</span><br>
              <span style="font-size:8pt">电话：</span><span style="font-size:8pt">${tp_store_phone}</span>
             </div>
             <span style="text-align:left">----------------------------------------</span>

             <div>
              <span style="font-size:8pt;word-break:break-all;word-wrap:break-word;">单号：${tp_order_num}</span><br>
              <span style="font-size:8pt">时间：</span><span style="font-size:8pt">${tp_cash_time}</span><br>
			  <span style="font-size:8pt">收银员：</span><span style="font-size:8pt">${tp_cashier_name}</span><br>
             </div>
             <span style="text-align:left">----------------------------------------</span>
			 <div>
            	<div>
	        		<div style="width:20mm;margin-top:1mm;float:left;">
	          			<span style="font-size:8pt;">商品名称</span>
	        		</div>
	        		<div style="width:8mm;margin-top:1mm;float:left;margin-left:1mm">
	          			<span style="font-size:8pt;">数量</span>
	        		</div>
	        		<div style="width:8mm;margin-top:1mm;float:left;margin-left:1mm">
	          			<span style="font-size:8pt;">单价</span>
	        		</div>
	        		<div style="width:8mm;margin-top:1mm;float:left;margin-left:1mm">
	          			<span style="font-size:8pt;">金额</span>
	        		</div>
	        	</div>
			</div>
			  ${order_details}
		    <div>
	        <span style="font-size:8pt;">合计：</span>
	        <span style="margin-top:1mm;display:inline-block;font-size:8pt;word-break:break-all;word-wrap:break-word;margin-left:2mm;float:right;">${sum_amount}</span>
      		</div>
            <span style="text-align:left">---------------------------------</span>

             <div>
                 <table style="width:48mm;font-family:微软雅黑;">
                     <tr>
                         <td style="font-size:8pt;text-align:left">支付方式：</td>
                         <td style="font-size:8pt;text-align:right">${tp_pay_code}</td>
                     </tr>
                     <tr>
                         <td style="font-size:8pt;text-align:left">消费金额：</td>
                         <td style="font-size:8pt;text-align:right">${sum_amount}</td>
                     </tr>
                     <tr>
                         <td style="font-size:8pt;text-align:left">优惠金额：</td>
                         <td style="font-size:8pt;text-align:right">${free_money}</td>
                     </tr>
                     <tr>
                         <td style="font-size:8pt;text-align:left">支付金额：</td>
                         <td style="font-size:8pt;text-align:right">${real_money}</td>
                     </tr>
                     <tr>
                         <td style="font-size:8pt;text-align:left">实付：</td>
                         <td style="font-size:8pt;text-align:right">${trans_cash_col}</td>
                     </tr>
                     <tr>
                         <td style="font-size:8pt;text-align:left">找零：</td>
                         <td style="font-size:8pt;text-align:right">${zero_amount}</td>
                     </tr>
                 </table>
             </div>
             <span>----------------------------------------</span>
			<div>
                 <table style="width:50mm;font-family:微软雅黑;">
                     <tr>
                         <td style="font-size:8pt;text-align:left">桌号：</td>
                         <td style="font-size:8pt;text-align:right">${table_num}</td>
                     </tr>
					<tr>
                         <td style="font-size:8pt;text-align:left">取餐号：</td>
                         <td style="font-size:8pt;text-align:right">${caller_num}</td>
                     </tr>
					<tr>
                         <td style="font-size:8pt;text-align:left">房间号:</td>
                         <td style="font-size:8pt;text-align:right">${room_num}</td>
                     </tr>
				 </table>
			 <div>
			<span>----------------------------------------</span>

         </div>
     </div>
    </body>
</html>
""";
		return goods_smarttemplate.toString();
	}
	
	
	 
	
	
	/**
	 * 添加打印套餐明细
	 * @param item
	 * @return
	 */
	public static String getItemHtml(OrderItem item) {
		def goods_name = item.goods_name;
		def goods_num = new BigDecimal(item.buy_num.toString());
		def unit_amount = new BigDecimal(item.unit_amount.toString()).divide(new BigDecimal(100)).setScale(2);
		def pay_amount = new BigDecimal(item.pay_amount.toString()).divide(new BigDecimal(100)).setScale(2);
		pay_amount = pay_amount.multiply(goods_num).setScale(2);
		def remark = item.remark==null?"":item.remark;
		def order = """
				<div>
					<div style="width:20mm;margin-top:1mm;float:left;">
					  <span style="width:20mm;margin-top:1mm;display:inline-block;font-size:8pt;word-break:break-all;word-wrap:break-word;float:left;">${goods_name} </span>
					</div>
					<div style="width:10mm;margin-top:1mm;float:left;margin-left:3mm">
					  <span style="width:10mm;margin-top:1mm;display:inline-block;font-size:8pt;float:left;">X${goods_num}</span>
					</div>
					<div style="width:10mm;margin-top:1mm;float:left;margin-left:3mm">
					  <span style="width:10mm;margin-top:1mm;display:inline-block;font-size:8pt;float:left;">${unit_amount}</span>
					</div>
					<div style="width:10mm;margin-top:1mm;float:left;margin-left:3mm">
					  <span style="width:10mm;margin-top:1mm;display:inline-block;font-size:8pt;float:left;">${pay_amount}</span>
					</div>
					<div style="clear:both"></div>
					<div>
			""";
			
		 def end = """"
		 		 <span style="font-size:8pt;">备注：</span>
					  <span style="margin-top:1mm;display:inline-block;width:42mm;font-size:8pt;word-break:break-all;word-wrap:break-word;">${remark}</span>
					</div>
				</div>
		 	"""
		List<OrderItemPack> itemList = item.verts;
		if (item.verts == null || item.verts.isEmpty()) {
			return order + end;
		}
		def goods_smarttemplate = new ArrayList<String>();
		for( y in itemList) {
			def item_name = '--'+y.goods_name;
			//def item_num = new BigDecimal(y.buy_num.toString());
			def item_num = getBuy_num(item.buy_num==null?"":item.buy_num.toString(), item.buy_num==null?"":item.buy_num.toString()) ;
			def item_html = """
				<div>
					<div style="width:20mm;margin-top:1mm;float:left;">
					  <span style="width:20mm;margin-top:1mm;display:inline-block;font-size:8pt;word-break:break-all;word-wrap:break-word;float:left;">${item_name} </span>
					</div>
					<div style="width:10mm;margin-top:1mm;float:left;margin-left:3mm">
					  <span style="width:10mm;margin-top:1mm;display:inline-block;font-size:8pt;float:left;">X${item_num}</span>
					</div>
					<div style="width:10mm;margin-top:1mm;float:left;margin-left:3mm">
					  <span style="width:10mm;margin-top:1mm;display:inline-block;font-size:8pt;float:left;"></span>
					</div>
					<div style="width:10mm;margin-top:1mm;float:left;margin-left:3mm">
					  <span style="width:10mm;margin-top:1mm;display:inline-block;font-size:8pt;float:left;"></span>
					</div>
					<div style="clear:both"></div>
				</div>
			""";
			order += item_html;
			
			
		}
		return order + end;
	}
	
	public static String getBuy_num(String merge, String item) {
		if (merge == "") {
			return item;
		}
		if (item == "") {
			return item;
		}
		return MoneyUtil.multiple(merge,item);
	}
}
