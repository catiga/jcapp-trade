package com.jeancoder.trade.ready.util

import java.math.RoundingMode


class MoneyUtil {
	private static BigDecimal bd_100 = new BigDecimal(100);
	
	public static String get_cent_from_yuan(String yuan) {
		if(yuan==null) {
			return null;
		}
		try {
			BigDecimal bd = new BigDecimal(yuan);
			return bd.multiply(bd_100).intValue() + "";
		} catch(Exception e) {
		}
		return null;
	}
	
	public static String get_yuan_from_cent(String fen) {
		if(fen==null) {
			return null;
		}
		try {
			BigDecimal bd = new BigDecimal(fen);
			return bd.divide(bd_100, 2, RoundingMode.HALF_UP).toString();
		} catch(Exception e) {
		}
		return null;
	}

	public static String add(String one, String two) {
		if(one==null&&two==null) {
			return null;
		}
		if(one==null) {
			return two;
		}
		if(two==null) {
			return one;
		}
		try {
			BigDecimal bd = new BigDecimal(one);
			BigDecimal bd_two = new BigDecimal(two);
			return bd.add(bd_two).toString();
		}catch(Exception e){
			return null;
		}
	}
	
	public static String multiple(String one, String two) {
		if(one==null||two==null) {
			return null;
		}
		if(!DataUtils.isNumber(one)||!DataUtils.isNumber(two)) {
			return null;
		}
		BigDecimal bd = new BigDecimal(one);
		BigDecimal bd_two = new BigDecimal(two);
		return bd.multiply(bd_two).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	// 除
	public static String divide(String one, String two) {
		if(one==null||two==null) {
			return null;
		}
		if(!DataUtils.isNumber(one)||!DataUtils.isNumber(two)) {
			return null;
		}
		BigDecimal bd = new BigDecimal(one);
		BigDecimal bd_2 = new BigDecimal(two);
		String s = bd.divide(bd_2, 2, BigDecimal.ROUND_HALF_DOWN).toString();
		return s;
	}
	
	public static void  main(String[] agr) {
		println multiple("5","10");
	}
}
