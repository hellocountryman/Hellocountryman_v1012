package com.feytuo.laoxianghao.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feytuo.laoxianghao.db.DatabaseHelper;

public class CityDao {

	private SQLiteDatabase db;
	
	public CityDao(Context context) {
		// TODO Auto-generated constructor stub
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
		db = dbHelper.getReadableDatabase();
	}
	
	/**
	 * 获取所有城市的名字列表
	 * @return
	 */
	public List<String> getAllCityName(){
		List<String> list = new ArrayList<>();
		String sqlStr = "select city_name from city";
		Cursor cursor = db.rawQuery(sqlStr, null);
		while(cursor.moveToNext()){
			list.add(cursor.getString(0));
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 通过城市名字获取城市id
	 * @param cityName
	 * @return
	 */
	public int getCityIdByName(String cityName){
		int cityId = 0;
		String sqlStr = "select city_id from city where city_name = ?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{cityName});
		while(cursor.moveToNext()){
			cityId = cursor.getInt(0);
		}
		cursor.close();
		return cityId;
	}
	
	public String getCityNameById(int cityId){
		String cityName = "";
		String sqlStr = "select city_name from city where city_id = ?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{cityId+""});
		while(cursor.moveToNext()){
			cityName = cursor.getString(0);
		}
		cursor.close();
		return cityName;
	}
}
