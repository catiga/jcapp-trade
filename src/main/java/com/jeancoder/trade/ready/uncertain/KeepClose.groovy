package com.jeancoder.trade.ready.uncertain

import java.sql.Timestamp
import java.text.SimpleDateFormat
import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.ser.TradeService

class KeepClose {
	static JCLogger Logger = LoggerSource.getLogger(this.getClass().getName());
	private static def CloseURL = [:]
	
	static {
		Close smc = new Close('scm','1000','order/close_order_status')
		Close reserveTicketingsys = new Close('ticketingsys','2010','incall/order/close_order_status')
		Close ticketingsys = new Close('ticketingsys','2010','incall/order/close_order_status')
		
		Close crm = new Close('crm','8000','order/close_order_status')
		Close recharge = new Close('crm','8001','order/close_order_status')
		
		Close reserve = new Close('crm','5000','order/close_order_status')
		
		CloseURL['1000'] = smc;
		CloseURL['2010'] = reserveTicketingsys;
		CloseURL['2000'] = ticketingsys;
		CloseURL['8000'] = crm;
		CloseURL['8001'] = recharge;
		CloseURL['8001'] = recharge;
		CloseURL['5000'] = reserve;
	}
	
	
	
	def static fuckoff(TimerTask obj) {
		Logger.info("trade fuckoff........")
		try {
			String sql = 'select * from   TradeInfo WHERE tss in (0000)  AND   a_time <= DATE_SUB(NOW()  , INTERVAL 15 MINUTE) AND flag!=?';
			List<TradeInfo> infoList =JcTemplate.INSTANCE().find(TradeInfo,sql,-1);
			if (infoList== null || infoList.isEmpty()) {
				return;
			}
			for (TradeInfo info : infoList) {
				def orderss = '';
				List<TradeOrder> trade_orders = TradeService.INSTANCE().trade_items(info);
				for (TradeOrder order : trade_orders) {
					def close = CloseURL[order.oc];
					def orders = order.order_num + ","+order.oc;
					JC.internal.call(SimpleAjax, close.name, close.url,[orders:orders,pid:info.pid.toString()]);
					orderss += orders + ";"
				}	
				JC.thread.run(5000, {
					SimpleAjax relse = JC.internal.call(SimpleAjax, 'market', '/coupon/clear_coupon_info',[orders:orderss,pid:info.pid.toString()]);
					Logger.info("clear_coupon_info_:" + JackSonBeanMapper.toJson(relse));
					if (relse == null || !relse.available) {
						return;
					}
					info.c_time = new Timestamp(new Date().getTime())
					info.tss = '9000';
					JcTemplate.INSTANCE().update(info);
				}, {
					
				})
			}
		}catch(any) {
			Logger.error("",any)
			if(obj)
				obj.cancel();
		}
		
	}
}

public class Close{
	def oc ;
	def url;
	def name;
	Close (name,oc,url) {
		this.oc = oc;
		this.name = name;
		this.url = url;
	}
}
