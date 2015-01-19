package com.feytuo.laoxianghao.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.util.AppInfoUtil;

/**
 * 将res/raw下的*.db文件导入到对应位置，执行数据库操作
 * @author hand
 *
 */
public class DBFileManager{
	
    private final int BUFFER_SIZE = 128;
    public static final String DB_NAME = "app.db"; //保存的数据库文件名
    public static final int DB_ID = R.raw.app;//RAW文件夹下数据库文件id
    private String packageName ;
    private String dbPath;
    
    
//    private SQLiteDatabase database;
    private Context context;

    public DBFileManager(Context context) {
        this.context = context;
        packageName = AppInfoUtil.getAppPackageName(context)+"/databases";
        dbPath = "/data"
                + Environment.getDataDirectory().getAbsolutePath() + "/"
                + packageName;  //在手机里存放数据库的位置(/data/data/包名/app.db)
    }

    public boolean copyDBFile() {
    	System.out.println(dbPath + "/" + DB_NAME);
        return this.openDatabase(dbPath + "/" + DB_NAME);
    }

    private boolean openDatabase(String dbfile) {
    	
        try {
        	File dir = new File(dbPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File dbf = new File(dbfile);
			if (!dbf.exists()) {
				dbf.createNewFile();
            	//判断数据库文件是否存在，若不存在则执行导入，否则直接返回true
				dbf.createNewFile();
                InputStream is = this.context.getResources().openRawResource(
                		DB_ID);//欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            return true;

        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return false;
    }
    
    
}
