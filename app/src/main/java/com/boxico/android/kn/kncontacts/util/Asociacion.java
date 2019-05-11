package com.boxico.android.kn.kncontacts.util;

public class Asociacion {
	
	private final Object key;
	private Object value;
	public Object getKey() {
		return key;
	}
// --Commented out by Inspection START (12/11/2018 12:34):
//	public void setKey(Object key) {
//		this.key = key;
//	}
// --Commented out by Inspection STOP (12/11/2018 12:34)
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Asociacion(Object key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	

}
