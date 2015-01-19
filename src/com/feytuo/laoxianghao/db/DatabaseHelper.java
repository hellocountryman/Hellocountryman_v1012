package com.feytuo.laoxianghao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {  
    private static DatabaseHelper mInstance = null;  
 
//    /** 数据库名称 **/  
//    public static final String DATABASE_NAME = "app.db";  
 
    /** 数据库版本号 **/  
    private static final int DATABASE_VERSION = 1;
 
 
    DatabaseHelper(Context context) {  
    super(context, DBFileManager.DB_NAME, null, DATABASE_VERSION);  
    }  
      
    /**单例模式**/
    public static synchronized DatabaseHelper getInstance(Context context) {  
    if (mInstance == null) {  
        mInstance = new DatabaseHelper(context);  
    }
    return mInstance;
    }  
 
    @Override  
    public void onCreate(SQLiteDatabase db) {  
    	/**向数据中添加表**/ 
//    db.execSQL(NAME_TABLE_CREATE);  
    }  
 
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
    	/**可以拿到当前数据库的版本信息 与之前数据库的版本信息   用来更新数据库**/    
    }  
 
      
    /**  
     * 删除数据库 
     * @param context  
     * @return  
     */  
    public boolean deleteDatabase(Context context) {  
    	return context.deleteDatabase(DBFileManager.DB_NAME);  
    }  
    /**
     * 关闭数据库
     */
    public void closeDB() {
	    if (mInstance != null) {
	        try {
	            SQLiteDatabase db = mInstance.getWritableDatabase();
	            db.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        mInstance = null;
	    }
	}
}  