package com.jeancoder.trade.ready.printer
import com.jeancoder.app.sdk.JC
import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.jdbc.JcTemplate
import com.jeancoder.trade.ready.SimpleAjax
import com.jeancoder.trade.ready.dto.PrinterDto
import com.jeancoder.trade.ready.entity.CounterConf
import com.jeancoder.trade.ready.entity.TradeInfo
import com.jeancoder.trade.ready.entity.TradeOrder
import com.jeancoder.trade.ready.util.JackSonBeanMapper
import com.jeancoder.trade.ready.util.MoneyUtil
class PrinterUtil {
	
	static JCLogger logger = LoggerSource.getLogger();
	
	private static Map<String,Dto> printerMap = new HashMap<String,Dto>();
	
	static {
		Dto test = new Dto(1,1,"EPT60","1000");// 测试系统
		Dto hdsdfh = new Dto(2,1,"MBIII_58","1000");// 邯郸
		printerMap.put(test.pid.toString()+"_" + test.oc, test);
		printerMap.put(hdsdfh.pid.toString()+"_" + hdsdfh.oc, hdsdfh);
	}
	
	public static List<PrinterDto> getPrinterDtoLsit(def pid, String oc,String html){
		Dto dto = printerMap.get(pid.toString()+"_"+oc);
		if (dto == null) {
			dto = new Dto(1,1,"EPT60","1000");
		}
		List<PrinterDto> list = new ArrayList<PrinterDto>();
		for (int i =0 ; i < dto.number; i++) {
			PrinterDto printerDto = new PrinterDto(dto.code, html);
			list.add(printerDto);
		}
		return list;
	}
	
	public static PrinterDto getPrinterDtoLsit(def pid, def counter_id, String oc,String html){
		List<CounterConf> counter_confs = JcTemplate.INSTANCE().find(CounterConf, 'select * from CounterConf where cc_id=? and flag!=?', counter_id, -1);
		logger.info('counter config params: pid=' + pid + ', counter_id=' + counter_id + ', and oc=' + oc);
		logger.info('result set===' + (counter_confs==null));
		if(counter_confs!=null) {
			logger.info('result set===' + (counter_confs.empty));
		}
		Dto dto = null;
		if(counter_confs && !counter_confs.empty) {
			for(x in counter_confs) {
				def param = x.param;
				if(param) {
					try {
						// param     :      {"oc":[{"oc":"2000","name":"影票订单"},{"oc":"8001","name":"会员卡充值订单"}]}
						param = JackSonBeanMapper.jsonToMap(param);
						for(y in param['oc']) {
							if(oc==y['oc']) {
								dto = new Dto(pid, 1, x.code, oc);
								logger.info('html=========' + x.html);
								if(x.html!=null && x.html!='') {
									html = x.html;
								}
								break;
							}
						}
					} catch(any) {}
					if(dto!=null) {
						break;
					}
				}
			}
		} 
		if(dto==null) {
			//返回默认
			dto = new Dto(1,1,"EPT60","1000");
		}
		
		PrinterDto printerDto = new PrinterDto(dto.code, html);
		return printerDto;
	}
	
	/*
	 * ticket_type  票类名称
	 */
	static def fill_ticket_printer_dto(TradeInfo trade, TradeOrder trade_order, PrinterDto printer_dto) {
		//开始去票务获取详细订单信息
		SimpleAjax ret_tickets = JC.internal.call(SimpleAjax, 'ticketingsys', '/ticketing/ticket_order_detail', [order_id:trade_order.order_id]);
		if(!ret_tickets.available) {
			return;
		}
		List<PrinterDto> ret_ticket_printer = [];
		
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
		
		for(s in sale_seats) {
			PrinterDto new_one = new PrinterDto();
			new_one.print_code = printer_dto.print_code;
			def base_smarttemplate = printer_dto.print_html;
			
			def seat_ss_id = s['id'];
			def seat_ss_sr = s['seat_sr'];
			def seat_ss_sc = s['seat_sc'];
			def seat_ss_price = s['sale_fee'];
			
			seat_ss_price = MoneyUtil.divide(seat_ss_price + '', '100');
			
			base_smarttemplate = base_smarttemplate.replace('[[store_name]]', store_name);
			base_smarttemplate = base_smarttemplate.replace('[[hall_name]]', hall_name);
			base_smarttemplate = base_smarttemplate.replace('[[film_name]]', film_name);
			base_smarttemplate = base_smarttemplate.replace('[[plan_time]]', plan_time);
			base_smarttemplate = base_smarttemplate.replace('[[order_num]]', pp_tnum);
			base_smarttemplate = base_smarttemplate.replace('[[plan_date]]', plan_date);
			base_smarttemplate = base_smarttemplate.replace("[[ticket_seat]]", seat_ss_sr + '排' + seat_ss_sc + '座');
			base_smarttemplate = base_smarttemplate.replace("[[ticket_price]]", seat_ss_price);
			
			base_smarttemplate = URLEncoder.encode(base_smarttemplate, 'UTF-8');
			base_smarttemplate = URLEncoder.encode(base_smarttemplate, 'UTF-8');
			
			new_one.print_html = base_smarttemplate;
			new_one.qrcode = order_id + ',' + seat_ss_id;
			
			ret_ticket_printer.add(new_one);
		}
		return ret_ticket_printer;
	}
}



class Dto {
	Integer number;
	String code;
	String oc;
	BigInteger pid;
	Dto(BigInteger pid,Integer number, String code, String oc) {
		this.pid = pid;
		this.number = number;
		this.code = code;
		this.oc = oc;
	}
}
