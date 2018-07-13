package com.tay.dynamicds;

public class OrgCodeHolder {
	static final ThreadLocal<String> holder = new ThreadLocal<String>();

	public static void putOrgCode(String orgCode) {
		holder.set(orgCode);
	}
	
	public static void remove() {
		holder.remove();
	}

	public static String getOrgCode() {
		return holder.get();
	}
}
