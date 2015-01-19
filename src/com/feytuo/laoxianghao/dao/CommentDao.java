package com.feytuo.laoxianghao.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feytuo.laoxianghao.db.DatabaseHelper;
import com.feytuo.laoxianghao.domain.Comment;

public class CommentDao {

private SQLiteDatabase db;
	
	public CommentDao(Context context) {
		// TODO Auto-generated constructor stub
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
		db = dbHelper.getReadableDatabase();
	}
	
	/**
	 * 获取invId帖子的本地的所有评论
	 * @param invId
	 * @return
	 */
	public List<Comment> getAllComment(int invId){
		List<Comment> list = new ArrayList<Comment>();
		String sqlStr = "select id,uid,inv_id,com_words,com_voice,com_time,com_position from comment where inv_id=?";
		Cursor cursor = db.rawQuery(sqlStr, new String[]{""+invId});
		while(cursor.moveToNext()){
			Comment comment = new Comment();
			comment.setComId(cursor.getInt(0));
			comment.setuId(cursor.getInt(1));
			comment.setInvId(cursor.getInt(2));
			comment.setComWords(cursor.getString(3));
			comment.setComVoice(cursor.getString(4));
			comment.setComTime(cursor.getString(5));
			comment.setComPosition(cursor.getString(6));
			list.add(comment);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 插入一系列评论
	 * @param list
	 */
	public void insert2Comment(List<Comment> list){
		String sqlStr = "insert into comment(id,uid,inv_id,com_words,com_voice,com_time,com_position) values(?,?,?,?,?,?,?)";
		for(Comment comment : list){
			db.execSQL(sqlStr, new Object[]{comment.getComId(),comment.getuId(),
					comment.getInvId(),comment.getComWords(),comment.getComVoice(),
					comment.getComTime(),comment.getComPosition()});
		}
	}
	/**
	 * 删除某贴的全部本地评论
	 * @param invId
	 */
	public void deleteAllComment(int invId){
		String sqlStr = "delete from comment where inv_id =?";
		db.execSQL(sqlStr, new Object[]{invId});
	}
}
