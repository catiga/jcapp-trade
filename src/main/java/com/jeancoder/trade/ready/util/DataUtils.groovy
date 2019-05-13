package com.jeancoder.trade.ready.util

class DataUtils {
	public static boolean isNumber(String string) {
		if(string==null||string.trim().equals("")) {
			return false;
		}
		int point_num = 0;
		for (int i = 0; i < string.length(); i++) {
			String c = new String(string.charAt(i));
			if(i==0) {
				if(c.equals("-")||c.equals("+")) {
					continue;
				}
			}
			if(c.equals(".")) {
				point_num++;
				continue;
			} else {
				if (!Character.isDigit(string.charAt(i))) {
					return false;
				}
			}
		}
		if(string.startsWith(".")||string.endsWith(".")) {
			return false;
		} else {
			if(point_num>1) {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] argc) {
		String a = "12200.00";
		boolean c = isNumber(a);
		System.out.println(c);
	}
}
