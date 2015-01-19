package com.feytuo.laoxianghao.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

/**
 * 获取当前app信息（包名、版本号、版本名、diviceID）
 * 
 * @author hand
 * 
 */
public class AppInfoUtil {

	/**
	 * 获取app包名
	 * 
	 * @param context
	 * @return 包名
	 */
	public static String getAppPackageName(Context context) {

		return context.getPackageName();
	}

	/**
	 * 获取app版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取app版本名
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取设备diviceID
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  
		return tm.getDeviceId();
	}
	
}
