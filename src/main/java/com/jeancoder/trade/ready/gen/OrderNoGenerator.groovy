package com.jeancoder.trade.ready.gen;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单编号生成策略 [年月日时分秒+机器ip后三位+4位编号]
 * @author zghhost
 */
public class OrderNoGenerator {
	//年-秒key
	private static String key = "";
	//每一秒的计数器
	private static Integer cnt = 0;
	//计数补0格式
	private static String numfmt = "%04d";
	//如果为获取到ip地址  使用256 
	private static String ipp = "56";
	static {
		try{
			//获取本机完整ip 兼容ipv6
			Enumeration<NetworkInterface> interfs = NetworkInterface.getNetworkInterfaces();
			String ipv4 = null,ipv6 = null;
			while (interfs.hasMoreElements()) {
				NetworkInterface interf = interfs.nextElement();
				Enumeration<InetAddress> addres = interf.getInetAddresses();
				while (addres.hasMoreElements()) {
					InetAddress ins = addres.nextElement();
					if (ins instanceof Inet4Address) {
						//临时处理下  忽略127.0.0.1
						if(!"127.0.0.1".equals(ins.getHostAddress())){
							ipv4 = ins.getHostAddress();
						}
					} else if (ins instanceof Inet6Address) {
						ipv6 = ins.getHostAddress();
					}
				}
			}
			//取完整ip去掉非数字后的后三位 做为机器标识 优先使用ipv4
			String ipnum = null;
			if(ipv4 != null){
				ipnum = ipv4.replaceAll("[^\\d]", "");
			}else if(ipv6 != null){
				ipnum = ipv6.replaceAll("[^\\d]", "");
			}
			OrderNoGenerator.ipp = ipnum.substring(ipnum.length()-2);
		}catch(Exception e){
		}
	}
	/**
	 * 生成订单编号
	 * @return
	 */
	public synchronized static String generateNo(){
		String newkey = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		StringBuffer no = new StringBuffer(newkey);
		no.append(OrderNoGenerator.ipp);
		
		if(newkey.equals(OrderNoGenerator.key)){
			//同一秒计数累加
			no.append(String.format(numfmt, ++OrderNoGenerator.cnt));
		}else{
			//新一秒计数归位
			OrderNoGenerator.key = newkey;
			OrderNoGenerator.cnt = 0;
			no.append(String.format(numfmt, ++OrderNoGenerator.cnt));
		}
		return no.toString();
	}
	
	
	static void main(def args) {
		println generateNo();
	}
}
