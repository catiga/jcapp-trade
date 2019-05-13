package com.jeancoder.trade.ready.common;

import java.io.Serializable;

public final class AvailabilityStatus implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public boolean isAvailable() {
		return available;
	}
	
	public String[] getMessages() {
		return messages;
	}
	
	public Object getObj() {
		return obj.getObj();
	}

	public static AvailabilityStatus notAvailable(String name) {
		String[] suggestions = new String[1];
		for (int i = 0; i < suggestions.length; i++) {
			suggestions[i] = name;
		}
		return new AvailabilityStatus(false, suggestions);
	}
	
	public static AvailabilityStatus notAvailable(Object obj) {
		
		return new AvailabilityStatus(false, ["",""] as String[], new RetObj() {
			@Override
			public Object getObj() {
				return obj;
			}
		});
	}
	public static AvailabilityStatus notAvailable(String[] messages) {
		return new AvailabilityStatus(false, messages);
	}
	
	public static AvailabilityStatus notAvailable(String[] messages, RetObj obj) {
		return new AvailabilityStatus(false, messages, obj);
	}
	
	public static AvailabilityStatus available() {
		return AVAILABLE_INSTANCE;
	}
	
	public static AvailabilityStatus available(RetObj obj) {
		return new AvailabilityStatus(true, [""] as String[], obj);
	}
	
	public static AvailabilityStatus available(Object obj) {
		
		return new AvailabilityStatus(true, [""] as String[], new RetObj() {
			@Override
			public Object getObj() {
				return obj;
			}
		});
	}
	
	public static AvailabilityStatus available(String[] messages) {
		return new AvailabilityStatus(true, messages);
	}
	
	public static AvailabilityStatus available(String[] messages, RetObj obj) {
		return new AvailabilityStatus(true, messages, obj);
	}
	
	// internal
	
	private static final AvailabilityStatus AVAILABLE_INSTANCE = new AvailabilityStatus(true, new String[0]);
	
	private boolean available;
	
	private String[] messages;
	
	private RetObj obj = new RetObj() {
		
		@Override
		public Object getObj() {
			return "-1";
		}
	};
	
	private AvailabilityStatus(boolean available, String[] messages) {
		this.available = available;
		this.messages = messages;
	}
	
	private AvailabilityStatus(boolean available, String[] messages, RetObj obj) {
		this.available = available;
		this.messages = messages;
		this.obj = obj;
	}
}
