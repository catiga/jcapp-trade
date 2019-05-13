package com.jeancoder.trade.ready.util

import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class JsonUtil {

	static JCLogger LOGGER = LoggerSource.getLogger(JsonUtil.class.getName());
	
	static def camle(String s){
		return s;
	}
	
	public static<T, X> X convert(Class<T> mapped, def json) {
		if(mapped.isArray()) {
			return convertArray(mapped, json);
		} else {
			return convertObj(mapped, json);
		}
	}
	
	public static <T> T convertObj(Class<T> mapped, def json) {
		try {
			def gdsObj = mapped.newInstance();
			def gdsJson = new JsonSlurper().parseText(json);
			gdsJson.each {
				Map.Entry entry ->
				String propName = camle(entry.key);
				if(gdsObj.metaClass.hasProperty(gdsObj,propName)){
					gdsObj[propName] = entry.value
				}
			}
			return gdsObj;
		} catch(Exception e) {
			LOGGER.error("convert json error:" + json);
			return null;
		}
	}
	
	public static <T> T[] convertArray(Class<T> mapped, def json) {
		try {
			JsonSlurper js = new JsonSlurper();
			JsonOutput jo = new JsonOutput();
			def array = js.parseText(json);
			def retarry = [];
			for(x in array) {
				println x;
				T obj = convertObj(mapped, jo.toJson(x));
				retarry.add(obj);
			}
			return retarry;
		} catch(Exception e) {
			LOGGER.error("convert json error:" + json);
			return null;
		}
	}
}
