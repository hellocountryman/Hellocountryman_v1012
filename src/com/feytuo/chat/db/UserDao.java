/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feytuo.chat.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.util.HanziToPinyin;
import com.feytuo.chat.Constant;
import com.feytuo.chat.domain.User;

public class UserDao {
	public static final String TABLE_NAME = "users";
	public static final String CONVERSATION_TABLE_NAME = "conversation_users";
	public static final String COLUMN_NAME_ID = "username";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_HEAD_URL = "headurl";
	public static final String COLUMN_NAME_IS_STRANGER = "is_stranger";

	private DbOpenHelper dbHelper;

	public UserDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}



	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, User> users = new HashMap<String, User>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				String headUrl = cursor.getString(cursor.getColumnIndex(COLUMN_HEAD_URL));
				Log.i("UserDao", "本地数据库获取到的："+nick+"=="+headUrl);
				User user = new User();
				user.setUsername(username);
				user.setNickName(nick);
				user.setHeadUrl(headUrl);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNickName())) {
					headerName = user.getNickName();
				} else {
					headerName = user.getUsername();
				}
				
				if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
							.get(0).target.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}
	
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteContact(String username){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{username});
		}
	}
	
	
	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveContact(User user){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, user.getUsername());
		if(user.getNickName() != null)
			values.put(COLUMN_NAME_NICK, user.getNickName());
		if(user.getHeadUrl() != null)
			values.put(COLUMN_HEAD_URL, user.getHeadUrl());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
	
	
	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<User> contactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (User user : contactList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, user.getUsername());
				if(user.getNickName() != null)
					values.put(COLUMN_NAME_NICK, user.getNickName());
				if(user.getHeadUrl() != null)
					values.put(COLUMN_HEAD_URL, user.getHeadUrl());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}
	/****************************会话用户表**********************************/
	/**
	 * 从会话用户列表中获取昵称
	 * @param userName
	 * @return
	 */
	public String getUserNickName(String userName){
		String nickName = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sqlStr = "select "+COLUMN_NAME_NICK+" from "+CONVERSATION_TABLE_NAME+" where "+COLUMN_NAME_ID+"=?";
			Cursor cursor = db.rawQuery(sqlStr, new String[]{userName});
			while(cursor.moveToNext()){
				nickName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
			}
			cursor.close();
		}
		return nickName;
	}
	/**
	 * 从会话用户列表中获取头像
	 * @param userName
	 * @return
	 */
	public String getUserHeadUrl(String userName){
		String headUrl = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sqlStr = "select "+COLUMN_HEAD_URL+" from "+CONVERSATION_TABLE_NAME+" where "+COLUMN_NAME_ID+"=?";
			Cursor cursor = db.rawQuery(sqlStr, new String[]{userName});
			while(cursor.moveToNext()){
				headUrl = cursor.getString(cursor.getColumnIndex(COLUMN_HEAD_URL));
			}
			cursor.close();
		}
		return headUrl;
	}
	/**
	 * 保存一个联系人的昵称到会话用户表中去
	 * @param user
	 */
	public void updateNickName2Conversation(String userName,String nickName){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sqlStr = "select * from "+CONVERSATION_TABLE_NAME+" where "+COLUMN_NAME_ID+"=?";
		if(db.isOpen()){
			Cursor cursor = db.rawQuery(sqlStr, new String[]{userName});
			Log.i("UserDao", "cursor的nick数据:"+cursor.getCount());
			if(cursor.getCount() > 0 ){
				sqlStr ="update "+CONVERSATION_TABLE_NAME+" set "+COLUMN_NAME_NICK+"=? where "+COLUMN_NAME_ID+"=?";
				db.execSQL(sqlStr, new Object[]{nickName,userName});
			}else{
				sqlStr ="insert into "+CONVERSATION_TABLE_NAME+"("+COLUMN_NAME_ID+","+COLUMN_NAME_NICK+") values(?,?)";
				db.execSQL(sqlStr, new Object[]{userName,nickName});
			}
			cursor.close();
		}
	}
	/**
	 * 保存一个联系人的头像到会话用户表中去
	 * @param user
	 */
	public void updateHeadUrl2Conversation(String userName,String headUrl){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sqlStr = "select * from "+CONVERSATION_TABLE_NAME+" where "+COLUMN_NAME_ID+"=?";
		if(db.isOpen()){
			Cursor cursor = db.rawQuery(sqlStr, new String[]{userName});
			Log.i("UserDao", "cursor的head数据:"+cursor.getCount());
			if(cursor.getCount() > 0 ){
				sqlStr ="update "+CONVERSATION_TABLE_NAME+" set "+COLUMN_HEAD_URL+"=? where "+COLUMN_NAME_ID+"=?";
				db.execSQL(sqlStr, new Object[]{headUrl,userName});
			}else{
				sqlStr ="insert into "+CONVERSATION_TABLE_NAME+"("+COLUMN_NAME_ID+","+COLUMN_HEAD_URL+") values(?,?)";
				db.execSQL(sqlStr, new Object[]{userName,headUrl});
			}
			cursor.close();
		}
	}
	
	/**
	 * 删除会话用户表中所有用户信息
	 */
	public void deleteAllUserInfoInConversation(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "delete from " +CONVERSATION_TABLE_NAME;
		if(db.isOpen()){
			db.execSQL(sql);
		}
	}
	
}
