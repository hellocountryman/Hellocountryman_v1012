package com.feytuo.laoxianghao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.feytuo.laoxianghao.db.DBFileManager;
import com.feytuo.laoxianghao.global.Global;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		/*********youmeng**********/
		// 设置定义启动一次程序的时间间隔是5秒
		MobclickAgent.setSessionContinueMillis(5000);
		// 禁止默认的页面统计方式
		MobclickAgent.openActivityDurationTrack(false);
		// 数据发送策略
		MobclickAgent.updateOnlineConfig(this);
		/**********youmeng*********/
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				copyDbFile();
			}
		}).start();
		
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				enterApp();
			}
		}, 3000l);
	}

	/**
	 * 将db文件拷贝到指定路径
	 */
	private void copyDbFile() {
		// TODO Auto-generated method stub
		DBFileManager dbFileManager = new DBFileManager(this);
		if (dbFileManager.copyDBFile()) {
			Log.i("WelcomeActivity", "拷贝成功");
		} else {
			Log.i("WelcomeActivity", "拷贝失败");
		}
	}
	/**
	 * 跳转界面
	 * 如果不是首次使用，直接跳转主界面
	 * 如果是，选择家乡界面
	 */
	public void enterApp() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		if(isFistLogin()){
			intent.setClass(this, SimpleSelsectedCountry.class);
		}else{
			intent.setClass(this, com.feytuo.chat.activity.MainActivity.class);
		}
		startActivity(intent);
		finish();
	}

	/**
	 * 判断用户是否首次使用
	 * @return
	 */
	private boolean isFistLogin() {
		// TODO Auto-generated method stub
		boolean isFirstUse = App.pre.getBoolean(Global.IS_FIRST_USE, true);
		return isFirstUse;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("WelcomeActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("WelcomeActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
