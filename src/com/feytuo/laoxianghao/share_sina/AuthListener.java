package com.feytuo.laoxianghao.share_sina;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.global.UserLogin;
import com.feytuo.laoxianghao.view.OnloadDialog;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

public class AuthListener implements WeiboAuthListener{

	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    private Context context;
    private boolean isLogin;
    private String words;
    private int resource;
    private long uid;
    private OnloadDialog loadDialog;
    public AuthListener(Context context , boolean isLogin,String words,int resource) {
    	this.isLogin = isLogin;
		this.context = context;
		this.words = words;
		this.resource = resource;
	}
	 @Override
     public void onComplete(Bundle values) {
         // 从 Bundle 中解析 Token
         mAccessToken = Oauth2AccessToken.parseAccessToken(values);
         if (mAccessToken.isSessionValid()) {
             
             // 保存 Token 到 SharedPreferences
             AccessTokenKeeper.writeAccessToken(context, mAccessToken);
//             Toast.makeText(context, 
//                     R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
             /**
              * 需要判断，如果是登录，不需要弹出
              */
             if(!isLogin){
            	 new Share_Weibo(context).sendMessage((Activity)context,words,resource);
             }else{
		         /**
		          * 两种方式获取新浪微博登录id
		          */
	             uid = Long.parseLong(mAccessToken.getUid());
//	             Log.i("WeiboAuthTest", uid+"=="+values.getString("uid"));
	             getUserInfo();
             }
             Log.i("AuthListener", "授权成功");
             //授权成功即可
         } else {
             // 以下几种情况，您会收到 Code：
             // 1. 当您未在平台上注册的应用程序的包名与签名时；
             // 2. 当您注册的应用程序包名与签名不正确时；
             // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
             String code = values.getString("code");
             String message = context.getString(R.string.weibosdk_demo_toast_auth_failed);
             if (!TextUtils.isEmpty(code)) {
                 message = message + "\nObtained the code: " + code;
             }
             Log.i("AuthListener", message);
//             Toast.makeText(context, message, Toast.LENGTH_LONG).show();
         }
     }

     @Override
     public void onCancel() {
    	 Log.i("AuthListener", "授权取消");
//         Toast.makeText(context, 
//                 R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
     }

     @Override
     public void onWeiboException(WeiboException e) {
//         Toast.makeText(context, 
//                 "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
    	 Log.i("AuthListener", "报错了："+e.getMessage());
     }
     
     public void getUserInfo() {
    	 loadDialog = new OnloadDialog(context);
    	 loadDialog.setCanceledOnTouchOutside(false);
    	 loadDialog.show();
    	 loadDialog.setMessage("正在获取微博登录信息");
 		// 获取当前已保存过的 Token
 		Oauth2AccessToken mAccessToken = AccessTokenKeeper
 				.readAccessToken(context);
 		if (mAccessToken != null && mAccessToken.isSessionValid()) {// token不可用，先sso授权
 			UsersAPI mUsersAPI = new UsersAPI(mAccessToken);
 			long uid = Long.parseLong(mAccessToken.getUid());
 			mUsersAPI.show(uid, getUserInfoListener);
 		}
 	}

 	/**
 	 * 微博 OpenAPI 回调接口。
 	 */
 	private RequestListener getUserInfoListener = new RequestListener() {
 		@Override
 		public void onComplete(String response) {
 			if (!TextUtils.isEmpty(response)) {
 				// 获取用户信息
 				final User user = User.parse(response);
 				if (user != null) {//获取昵称
// 					Message msg = new Message();
// 					msg.obj = user.screen_name;
// 					msg.what = 0;
// 					mHandler.sendMessage(msg);
 					new Thread() {//获取头像

 						@Override
 						public void run() {
 							if (user.profile_image_url != null) {// 获取头像
 								Bitmap bitmap = null;
 								bitmap = com.feytuo.laoxianghao.share_qq.Util
 										.getbitmap(user.profile_image_url);
 								if(loadDialog.isShowing()){
 									loadDialog.dismiss();
 								}
 								new UserLogin(context).ThreeLogin(uid+"", "Sina",user.screen_name,bitmap);
 							}
 						}

 					}.start();
 				}
 			}
 		}

 		@Override
 		public void onWeiboException(WeiboException e) {
 			// LogUtil.e(TAG, e.getMessage());
 			if(loadDialog.isShowing()){
					loadDialog.dismiss();
				}
 			ErrorInfo info = ErrorInfo.parse(e.getMessage());
 			Toast.makeText(context, info.toString(), Toast.LENGTH_LONG).show();
 		}
 	};
}
