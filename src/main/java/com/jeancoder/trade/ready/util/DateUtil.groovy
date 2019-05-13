package com.jeancoder.trade.ready.util;

import java.sql.Timestamp
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	private static SimpleDateFormat _yyyyMMddHHmmss_ = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private static SimpleDateFormat _yyyy_MM_dd_HH_mm_ss_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static format(Date d) {
		return _yyyyMMddHHmmss_.format(d);
	}
	
	static format(Date d, def formater) {
		if(formater=="yyyy-MM-dd HH:mm:ss") {
			return _yyyy_MM_dd_HH_mm_ss_.format(d);
		} else {
			return _yyyyMMddHHmmss_.format(d);
		}
	}
}
