package com.feytuo.laoxianghao.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feytuo.laoxianghao.db.DatabaseHelper;

public class PraiseDao {

	private SQLiteDatabase db;

	public PraiseDao(Context context) {
		// TODO Auto-generated constructor stub
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
		db = dbHelper.getReadableDatabase();
	}

	/**
	 * 删除该用户所有点赞信息
	 * 
	 * @param uId
	 */
	public void deleteAllPraise(String uId) {
		String sqlStr = "delete from praise where uid=?";
		db.execSQL(sqlStr, new Object[] { uId });
	}

	/**
	 * 向表中插入一条数据
	 * 
	 * @param praise
	 */
	public void insertPraise(int invId,int uId) {
		String sqlStr = "insert into praise(uid,inv_id) values(?,?)";
		db.execSQL(sqlStr, new Object[] {uId,invId });
	}
	
	public boolean selectPraiseInvitation(int invId,int uId){
		boolean in = false;
		String sqlStr = "select praise_id from praise where inv_id=? and uid=?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{""+invId,""+uId});
		if(cursor.getCount() == 0){
			in = false;
		}else{
			in = true;
		}
		cursor.close();
		return in;
	}
	
	/**
	 * 获取点赞id
	 * @param invId
	 * @param uId
	 * @return
	 */
	public String getPraiseId(String invId,String uId){
		String praiseId = "";
		String sqlStr = "select praise_id from praise where inv_id=? and uid = ?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{invId,uId});
		while(cursor.moveToNext()){
			praiseId = cursor.getString(0);
		}
		cursor.close();
		return praiseId;
	}
	
	/**
	 * 删除点赞表中一条记录
	 * @param invId
	 * @param uId
	 */
	public void deletePraise(int invId,int uId){
		String sqlStr = "delete from praise where inv_id=? and uid=?";
		db.execSQL(sqlStr, new Object[]{invId,uId});
	}
}
