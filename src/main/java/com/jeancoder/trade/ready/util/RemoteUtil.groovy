package com.jeancoder.trade.ready.util

import com.jeancoder.app.sdk.source.CommunicationSource
import com.jeancoder.core.power.CommunicationMethod
import com.jeancoder.core.power.CommunicationParam
import com.jeancoder.core.power.CommunicationPower
import com.jeancoder.core.util.JackSonBeanMapper
import com.jeancoder.trade.ready.dto.AuthUser

class RemoteUtil {
	
	public static def connect(def point, def address, def param_dic) {
		List<CommunicationParam> params = new ArrayList<CommunicationParam>();
		if(param_dic) {
			for(kv in param_dic) {
				CommunicationParam param = new CommunicationParam(kv.key, kv.value);
				params.add(param);
			}
		}
		CommunicationPower systemCaller = CommunicationSource.getCommunicator(point);
		def ret = systemCaller.doworkAsString(address, params);
		return ret;
	}
	
	public static <T> T connect(Class<T> mapclass, def point, def address, def param_dic) {
		List<CommunicationParam> params = new ArrayList<CommunicationParam>();
		if(param_dic) {
			for(kv in param_dic) {
				CommunicationParam param = new CommunicationParam(kv.key, kv.value);
				params.add(param);
			}
		}
		CommunicationPower systemCaller = CommunicationSource.getCommunicator(point);
		def ret = systemCaller.doworkAsString(address, params);
		
		//T obj = JsonUtil.convertObj(mapclass, ret);
		T obj = null;
		try {
			obj = JackSonBeanMapper.fromJson(ret, mapclass);
		}catch(any) {
		}
		return obj;
	}
	
	public static <T> T connectAsArray(Class<T> mapclass, def point, def address, def param_dic) {
		List<CommunicationParam> params = new ArrayList<CommunicationParam>();
		if(param_dic) {
			for(kv in param_dic) {
				CommunicationParam param = new CommunicationParam(kv.key, kv.value);
				params.add(param);
			}
		}
		CommunicationPower systemCaller = CommunicationSource.getCommunicator(point);
		def ret = systemCaller.doworkAsString(address, params);
		
		//T obj = JsonUtil.convertArray(mapclass, ret);
		T[] obj = null;
		try {
			obj = JackSonBeanMapper.jsonToList(ret, mapclass);
		}catch(any) {}
		return obj;
	}
	
	public static AuthUser getToken() {
		return connect(AuthUser.class, 'project', '/incall/token_user', null);
	}
}
