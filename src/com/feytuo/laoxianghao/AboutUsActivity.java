package com.feytuo.laoxianghao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.feytuo.chat.db.UserDao;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.DataCleanManager;
import com.feytuo.laoxianghao.util.SDcardTools;
import com.feytuo.laoxianghao.view.MyDialog;
import com.feytuo.laoxianghao.view.OnloadDialog;
import com.umeng.analytics.MobclickAgent;

public class AboutUsActivity extends Activity {

	private OnloadDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
	}

	public void me_about_us_ret(View v) {
		finish();
	}

	public void activity_about_us_feedback(View v) {
		Intent intent = new Intent(this, FeedbackActivity.class);
		startActivity(intent);
	}

	public void our_privacy_olicy(View v) {
		Intent intent = new Intent(this, MeAboutUsPrivacyPolicyActivity.class);
		startActivity(intent);
	}
	/**
	 * 清理缓存
	 * @param v
	 */
	public void cleanCache(View v){
		pd = new OnloadDialog(this);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		pd.setMessage("正在清理...");
		/**
		 * 需要清空如下：
		 * 1、本地两个文件夹laoxianghaoAudio和XX_image
		 * 2、XX_contact.db里面的conversation_users表清空
		 * 3、app.db里面的invitation_user表清空
		 */
		DataCleanManager.cleanInternalCache(this);
		DataCleanManager.cleanExternalCache(this);
		DataCleanManager.cleanCustomCache(SDcardTools.getSDPath() + "/" + "laoxianghaoAudio");
		DataCleanManager.cleanCustomCache(SDcardTools.getSDPath() + "/" + "xx_ImgCach");
		new LXHUserDao(this).deleteAllUserInfo();
		new UserDao(this).deleteAllUserInfoInConversation();
		pd.progressFinish("清理成功");
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pd.dismiss();
			}
		}, 1000l);
	}
	/**
	 * 分享给好友
	 * @param v
	 */
	public void shareToFriends(View v){
		LXHUser user = new LXHUserDao(this).getCurrentUserInfo(App.pre.getInt(Global.UID, -1));
		Invitation inv = new Invitation();
		inv.setWords("我是"+user.getHome()+"人，我在乡乡。说方言找老乡，听方言发现好玩的。#熟悉的才是好玩的#");
		inv.setVoice("");
		MyDialog dialog = new MyDialog(this, inv,
				R.style.MyDialog);
		dialog.show();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("AboutUsActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("AboutUsActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
