package com.jeancoder.trade.ready.util

import com.jeancoder.app.sdk.source.CommunicationSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.log.JCLoggerFactory
import com.jeancoder.core.power.CommunicationParam
import com.jeancoder.core.power.CommunicationPower
import com.jeancoder.core.util.JackSonBeanMapper

class NativeUtil {

	
	static JCLogger LOGGER = JCLoggerFactory.getLogger(RemoteUtil.class.name);
	
	public static <T> T connect(Class<T> mapclass, def point, def address, def param_dic) {
		List<CommunicationParam> params = new ArrayList<CommunicationParam>();
		if(param_dic) {
			for(kv in param_dic) {
				CommunicationParam param = new CommunicationParam(kv.key, kv.value);
				params.add(param);
			}
		}
		CommunicationPower systemCaller = CommunicationSource.getCommunicatorNative(point);
		def ret = systemCaller.doworkAsString(address, params);
		
		try {
			T obj = JackSonBeanMapper.fromJson(ret, mapclass);
			return obj;
		}catch(any){
			LOGGER.error('', any)
			return null;
		}
	}
	
 
	public static <T> T connect( def point, def address, def param_dic) {
		List<CommunicationParam> params = new ArrayList<CommunicationParam>();
		if(param_dic) {
			for(kv in param_dic) {
				CommunicationParam param = new CommunicationParam(kv.key, kv.value);
				params.add(param);
			}
		}
		CommunicationPower systemCaller = CommunicationSource.getCommunicatorNative(point);
		def ret = systemCaller.doworkAsString(address, params);
	}
	
	
	public static String connectStr( def point, def address, def param_dic) {
		List<CommunicationParam> params = new ArrayList<CommunicationParam>();
		if(param_dic) {
			for(kv in param_dic) {
				CommunicationParam param = new CommunicationParam(kv.key, kv.value);
				params.add(param);
			}
		}
		CommunicationPower systemCaller = CommunicationSource.getCommunicatorNative(point);
		return systemCaller.doworkAsString(address, params);
	}
	
	
	public static <T> T connectAsArray(Class<T> mapclass, def point, def address, def param_dic) {
		List<CommunicationParam> params = new ArrayList<CommunicationParam>();
		if(param_dic) {
			for(kv in param_dic) {
				CommunicationParam param = new CommunicationParam(kv.key, kv.value);
				params.add(param);
			}
		}
		CommunicationPower systemCaller = CommunicationSource.getCommunicatorNative(point);
		def ret = systemCaller.doworkAsString(address, params);
		
		
		try {
			T obj = JsonUtil.convertArray(mapclass, ret);
			return obj;
		}catch(any){
			LOGGER.error('', any)
			return null;
		}
	}
}
