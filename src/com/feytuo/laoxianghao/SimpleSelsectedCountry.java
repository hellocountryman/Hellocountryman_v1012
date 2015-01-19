package com.feytuo.laoxianghao;

import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.global.UserLogin;
import com.feytuo.laoxianghao.share_qq.Share_QQ;
import com.feytuo.laoxianghao.share_sina.Share_Weibo;
import com.umeng.analytics.MobclickAgent;

public class SimpleSelsectedCountry extends Activity {
	private Share_Weibo shareWeibo;
	private EditText loginNameEV;//用户名
	private EditText loginPwdEV; //密码
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_selected_country);
		shareWeibo = new Share_Weibo(this);
		//初始化mob短信验证
		SMSSDK.initSDK(this, "4eb60d7634c5", "57ee908e657a36bd3234a8b25db8482a");
		loginNameEV = (EditText)findViewById(R.id.login_name_ev);
		loginPwdEV = (EditText)findViewById(R.id.login_pwd_ev);
	}

	public void appLogin(View v) {
		int vId = v.getId();
		switch (vId) {
		case R.id.login_qq_btn:
			new Share_QQ(this).qqLogin(this);
			break;
		case R.id.login_sina_btn:
			shareWeibo.SSOAuthorize(this, true, "", 0);
			break;
		case R.id.login_btn:
			new UserLogin(this).NormalLogin(loginNameEV.getText().toString(), loginPwdEV.getText().toString());
			break;
		case R.id.register_btn:
			// 打开注册页面
			RegisterPage registerPage = new RegisterPage();
			registerPage.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
						String country = (String) phoneMap.get("country");
						String phone = (String) phoneMap.get("phone");
						// 提交用户信息
						registerUser(country, phone);
						Intent intent = new Intent();
						intent.setClass(SimpleSelsectedCountry.this,RegisterActivity.class);
						startActivity(intent);
						finish();
					}
				}
			});
			registerPage.show(this);
			break;
		default:
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		if (shareWeibo.getmSsoHandler() != null) {
			shareWeibo.getmSsoHandler().authorizeCallBack(requestCode,
					resultCode, data);
		}
	}

	// 提交用户信息
	private void registerUser(String country, String phone) {
		Random rnd = new Random();
		int id = Math.abs(rnd.nextInt());
		String uid = String.valueOf(id);
		String nickName = "SmsSDK_User_" + uid;
		String avatar = Global.ICON;
		SMSSDK.submitUserInfo(uid, nickName, avatar, country, phone);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("SimpleSelsectedCountry"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("SimpleSelsectedCountry");// 友盟保证 onPageEnd
															// 在onPause
		// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
