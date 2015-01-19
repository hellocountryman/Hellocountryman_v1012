package com.feytuo.laoxianghao.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feytuo.laoxianghao.db.DatabaseHelper;
import com.feytuo.laoxianghao.domain.LXHUser;

public class LXHUserDao {

private SQLiteDatabase db;
private DatabaseHelper dbHelper;
	
	public LXHUserDao(Context context) {
		// TODO Auto-generated constructor stub
		dbHelper = DatabaseHelper.getInstance(context);
	}
	
	/**
	 * 从帖子用户表中根据id获取用户昵称和头像
	 * @param uId
	 * @return
	 */
	public LXHUser getNickAndHeadByUid(int uId){
		db = dbHelper.getReadableDatabase();
		LXHUser user = null;
		String sqlStr = "select u_nickname,u_headurl from invitation_user where uid=?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{""+uId});
		while(cursor.moveToNext()){
			user = new LXHUser();
			user.setNickName(cursor.getString(cursor.getColumnIndex("u_nickname")));
			user.setHeadUrl(cursor.getString(cursor.getColumnIndex("u_headurl")));
		}
		cursor.close();
		return user;
	}
	/**
	 * 从帖子用户表中根据用户名获取用户昵称和头像
	 * @param uName
	 * @return
	 */
	public LXHUser getNickAndHeadByUname(String uName){
		db = dbHelper.getReadableDatabase();
		LXHUser user = null;
		String sqlStr = "select u_nickname,u_headurl from invitation_user where u_name=?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{uName});
		while(cursor.moveToNext()){
			user = new LXHUser();
			user.setNickName(cursor.getString(cursor.getColumnIndex("u_nickname")));
			user.setHeadUrl(cursor.getString(cursor.getColumnIndex("u_headurl")));
		}
		cursor.close();
		return user;
	}
	
	/**
	 * 向帖子用户表插入一个用户
	 * @param user
	 */
	public void insertUser(LXHUser user){
		db = dbHelper.getWritableDatabase();
		String sql = "insert into invitation_user(uid,u_nickname,u_headurl,u_home,u_personsign,u_sex,u_age) values(?,?,?,?,?,?,?)";
		db.execSQL(sql, new Object[]{user.getuId(),user.getNickName(),user.getHeadUrl(),
				user.getHome(),user.getPersonSign(),user.getSex(),user.getAge()});
	}
	
	/**
	 * 清空帖子用户表中所有数据
	 */
	public void deleteAllUserInfo(){
		db = dbHelper.getWritableDatabase();
		String sql = "delete from invitation_user";
		db.execSQL(sql);
	}
	/*************************主人用户表********************************/
	/**
	 * 主人表插入一个用户
	 * @param user
	 */
	public void insertCurrentUser(LXHUser user){
		db = dbHelper.getWritableDatabase();
		String sql = "insert into user(uid,u_name,u_key,u_nickname,u_headurl,u_home,u_personsign,u_sex,u_age) values(?,?,?,?,?,?,?,?,?)";
		db.execSQL(sql, new Object[]{user.getuId(),user.getuName(),user.getuKey(),user.getNickName(),user.getHeadUrl(),
				user.getHome(),user.getPersonSign(),user.getSex(),user.getAge()});
	}
	/**
	 * 更新主人家乡
	 * @param userId
	 * @param home
	 */
	public void updateUserHome(int userId,String home){
		db = dbHelper.getWritableDatabase();
		String sql = "update user set u_home=? where uid=?";
		db.execSQL(sql, new Object[]{home,userId});
	}
	/**
	 * 更新主人头像
	 * @param userId
	 * @param headUrl
	 */
	public void updateUserHeadUrl(String userName,String headUrl){
		db = dbHelper.getWritableDatabase();
		String sql = "update user set u_headurl=? where u_name=?";
		db.execSQL(sql, new Object[]{headUrl,userName});
	}
	/**
	 * 更新主人昵称
	 * @param userId
	 * @param nickName
	 */
	public void updateUserNickName(int userId,String nickName){
		db = dbHelper.getWritableDatabase();
		String sql = "update user set u_nickname=? where uid=?";
		db.execSQL(sql, new Object[]{nickName,userId});
	}
	/**
	 * 更新主人个性签名
	 * @param userId
	 * @param home
	 */
	public void updateUserPersonSign(int userId,String personSign){
		db = dbHelper.getWritableDatabase();
		String sql = "update user set u_personsign=? where uid=?";
		db.execSQL(sql, new Object[]{personSign,userId});
	}
	
	/**
	 * 根据uid获取当前用户所有信息
	 * @param userId
	 * @return
	 */
	public LXHUser getCurrentUserInfo(int userId){
		db = dbHelper.getWritableDatabase();
		LXHUser user = null;
		String sql = "select * from user where uid=?";
		Cursor cursor = db.rawQuery(sql, new String[]{""+userId});
		while(cursor.moveToNext()){
			user = new LXHUser();
			user.setuId(cursor.getInt(cursor.getColumnIndex("uid")));
			user.setuName(cursor.getString(cursor.getColumnIndex("u_name")));
			user.setuKey(cursor.getString(cursor.getColumnIndex("u_key")));
			user.setNickName(cursor.getString(cursor.getColumnIndex("u_nickname")));
			user.setHeadUrl(cursor.getString(cursor.getColumnIndex("u_headurl")));
			user.setHome(cursor.getString(cursor.getColumnIndex("u_home")));
			user.setPersonSign(cursor.getString(cursor.getColumnIndex("u_personsign")));
			user.setSex(cursor.getString(cursor.getColumnIndex("u_sex")));
			user.setAge(cursor.getInt(cursor.getColumnIndex("u_age")));
		}
		cursor.close();
		return user;
	}
	
	/**
	 * 从当前用户表中根据id获取用户昵称和头像
	 * @param uId
	 * @return
	 */
	public LXHUser getNickAndHeadByUidFromUser(int uId){
		db = dbHelper.getReadableDatabase();
		LXHUser user = null;
		String sqlStr = "select u_nickname,u_headurl from user where uid=?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{""+uId});
		while(cursor.moveToNext()){
			user = new LXHUser();
			user.setNickName(cursor.getString(cursor.getColumnIndex("u_nickname")));
			user.setHeadUrl(cursor.getString(cursor.getColumnIndex("u_headurl")));
		}
		cursor.close();
		return user;
	}
	/**
	 * 从当前用户表中根据id获取用户头像
	 * @param uId
	 * @return
	 */
	public String getHeadByUidFromUser(String uName){
		db = dbHelper.getReadableDatabase();
		String headUrl = null;
		String sqlStr = "select u_headurl from user where u_name=?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{uName});
		while(cursor.moveToNext()){
			headUrl = cursor.getString(cursor.getColumnIndex("u_headurl"));
		}
		cursor.close();
		return headUrl;
	}
}
