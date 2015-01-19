package com.feytuo.laoxianghao.util;

import android.text.format.Time;

/**
 * 得到系统时间
 * 
 * @author tangpeng
 * 
 */
public class GetSystemDateTime {

	/**
	 * ******************************************* 得到系统时间
	 */
	public static String now() {
		Time localTime = new Time();
		localTime.setToNow();
		return localTime.format("%Y%m%d%H%M%S");
	}
	
	public static String getDate(){
		Time localTime = new Time();
		localTime.setToNow();
		return localTime.format("%Y-%m-%d %H:%M:%S");
	}
}
