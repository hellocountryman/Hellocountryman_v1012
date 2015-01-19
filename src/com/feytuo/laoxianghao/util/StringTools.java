package com.feytuo.laoxianghao.util;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Random;

import android.graphics.Bitmap;
import android.util.Base64;

/*
 *  
 *   字符串常用操作的工具类
 * 
 */
public class StringTools {
	private final static long minute = 60 * 1000;// 1分钟
	private final static long hour = 60 * minute;// 1小时
	private final static long day = 24 * hour;// 1天
	private final static long month = 31 * day;// 月
	private final static long year = 12 * month;// 年

	/**
	 * ************************************************************
	 * 
	 * 获取随机字符串
	 * 
	 * @param len
	 *            字符串的长度
	 * @return
	 */
	public static String getRandomString(int len) {
		String returnStr = "";
		char[] ch = new char[len];
		Random rd = new Random();
		for (int i = 0; i < len; i++) {
			ch[i] = (char) (rd.nextInt(9) + 97);
		}
		returnStr = new String(ch);
		return returnStr;
	}

	/**
	 * . 获取时间差xx小时xx分钟前（类似于新浪微博 的某条微博发表于几小时几分钟前）
	 * 
	 * @param currentTime
	 *            当前时间 2012-9-10 11:50:18
	 * @param oldTime
	 *            老时间 2012-9-10 10:20:08
	 * @return 描述
	 * @author tangpeng
	 */
	public static String getTimeGap(String currentTime, String oldTime) {
		String hDes = "";
		String mDes = "";
		String[] newtime = currentTime.split(":");
		int newH = Integer.parseInt(newtime[0]);
		int newM = Integer.parseInt(newtime[1]);

		String[] oldtime = oldTime.split(":");
		int oldH = Integer.parseInt(oldtime[0]);
		int oldM = Integer.parseInt(oldtime[1]);

		int h = newH - oldH;
		int m = newM - oldM;
		int i = 0;
		int k = 0;
		if (0 < h) {
			if (0 < m) {
				hDes = h + "小时";
				mDes = m + "分钟";
			} else if (0 > m) {
				i = 60 - oldM + newM;
				mDes = i + "分钟";
				if (1 < h) {
					k = h - 1;
					hDes = k + "小时";
				}
			} else if (0 == m) {
				hDes = h + "小时";
			}
		} else if (0 < m) {
			mDes = m + "分钟";
		}
		return hDes + mDes + "前";
	}

	/**
	 * 返回文字描述的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getTimeFormatText(Date date) {
		if (date == null) {
			return null;
		}
		long diff = new Date().getTime() - date.getTime();
		long r = 0;
		if (diff > year) {
			r = (diff / year);
			return r + "年前";
		}
		if (diff > month) {
			r = (diff / month);
			return r + "个月前";
		}
		if (diff > day) {
			r = (diff / day);
			return r + "天前";
		}
		if (diff > hour) {
			r = (diff / hour);
			return r + "个小时前";
		}
		if (diff > minute) {
			r = (diff / minute);
			return r + "分钟前";
		}
		return "刚刚";
	}
	
	/**
	 * 将bitmap转换成base64形式的string
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmap2StringByBase64(Bitmap bitmap) {
		if (bitmap != null) {
			ByteArrayOutputStream ous = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, ous);
			byte[] byteArray = ous.toByteArray();
			String bitmapStr = Base64.encodeToString(byteArray, 0,
					byteArray.length, Base64.DEFAULT);
			return bitmapStr;
		}
		return null;
	}

}
