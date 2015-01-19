package com.feytuo.laoxianghao.global;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.feytuo.chat.Constant;
import com.feytuo.chat.activity.MainActivity;
import com.feytuo.chat.db.UserDao;
import com.feytuo.chat.domain.User;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.GetSystemDateTime;
import com.feytuo.laoxianghao.util.SDcardTools;
import com.feytuo.laoxianghao.util.StringTools;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.feytuo.laoxianghao.view.OnloadDialog;

public class UserLogin {

	private OnloadDialog pd;
	private boolean progressShow;
	
	private Context context;
	private String uName;
	private String uPwd;
	private String uKey;
	private String nickName;
	private Bitmap headBitmap;
	private int uId;

	public UserLogin(final Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				progressShow = true;
				pd = new OnloadDialog(context);
				pd.setCanceledOnTouchOutside(false);
				pd.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						progressShow = false;
					}
				});
				pd.show();
			}
		});
		
	}
	/**
	 * 普通登录
	 * @param context
	 * @param uName
	 * @param uKey
	 * @param nickName
	 * @param headBitmap
	 */
	public void NormalLogin(final String uName,
			final String uPwd) {
		// TODO Auto-generated constructor stub
		this.uName = uName.toLowerCase();
		this.uKey = "normal";
		this.uPwd = uPwd;
		this.nickName = "";
		
		new NormalLogin().execute();
		//返回是否已经注册
		//上传用户信息
	}
	/**
	 * 注册
	 * @param context
	 * @param uName
	 * @param uKey
	 * @param nickName
	 * @param headBitmap
	 */
	public void register(final String uName,
			final String uPwd) {
		// TODO Auto-generated constructor stub
		this.uName = uName.toLowerCase();
		this.uKey = "normal";
		this.uPwd = uPwd;
		this.nickName = "";
		
		new Register().execute();
		//返回是否已经注册
		//上传用户信息
	}
	/**
	 * 第三方登录
	 * @param context
	 * @param uName
	 * @param uKey
	 * @param nickName
	 * @param headBitmap
	 */
	public void ThreeLogin(final String uName,
			final String uKey, String nickName, Bitmap headBitmap) {
		// TODO Auto-generated constructor stub
		Log.i("UserLogin", "openId:" + uName);
		Log.i("UserLogin", "uKey:" + uKey);
		Log.i("UserLogin", "nickName:" + nickName);
		Log.i("UserLogin", "bitmap:" + headBitmap);
		this.uName = uName.toLowerCase();
		this.uKey = uKey;
		this.nickName = nickName;
		this.headBitmap = headBitmap;
		
		//调用第三方登录接口
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				new ThreeLoginTask().execute();
			}
		});
		//返回是否已经注册
		//上传用户信息
	}
	
	
	/**
	 * 第三方登录
	 *
	 */
	class ThreeLoginTask extends AsyncTask<Object, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pd.setMessage("正在登录...");
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			SyncHttp client = new SyncHttp(Global.THREE_LOGIN);
			try {
				String response = client.post(getParams());
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				JSONObject jsonData = jsonObject.getJSONObject("data");
				if(code == Global.code.SUCCESS){//新用户
					uId = Integer.parseInt(jsonData.getString("u_id"));
					return Global.code.SUCCESS;
				}else if(code == Global.code.FAILURE){//登录失败
					return Global.code.FAILURE;
				}else{//老用户
					uId = Integer.parseInt(jsonData.getString("u_id"));
					return Global.code.OLD_USER;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Global.code.ERROR;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			if(!progressShow){
				return;
			}
			switch (result) {
			case Global.code.SUCCESS: {
				new SetUserInfoTask().execute(1);
			}
				break;
			case Global.code.FAILURE: {
				pd.dismiss();
			}
				break;
			case Global.code.ERROR: {
				pd.dismiss();
			}
				break;
			case Global.code.OLD_USER: {
				loginHX(uName,uKey);
			}
				break;
			}
			super.onPostExecute(result);
		}
		private HashMap<String, Object> getParams(){
			HashMap<String, Object> urlParams = new HashMap<String, Object>();
			urlParams.put("u_name", uName);
			urlParams.put("u_key", uKey);
			urlParams.put("u_divice_id", AppInfoUtil.getDeviceId(context));
			Log.i("client", "u_name"+uName+"/n"+"u_key"+uKey);
			return urlParams;
		}
		
	}
	/**
	 * 用户登录
	 */
	class NormalLogin extends AsyncTask<Object, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pd.setMessage("正在登录...");
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			SyncHttp client = new SyncHttp(Global.NORMAL_LOGIN);
			try {
				String response = client.post(getParams());
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == Global.code.SUCCESS){//成功
					if(!jsonObject.isNull("data")){
						JSONObject jsonData = jsonObject.getJSONObject("data");
						uId = Integer.parseInt(jsonData.getString("u_id"));
					}
					return Global.code.SUCCESS;
				}else if(code == Global.code.FAILURE){//失败
					return Global.code.FAILURE;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Global.code.ERROR;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			switch (result) {
			case Global.code.SUCCESS: {
				loginHX(uName, uPwd);
			}
				break;
			case Global.code.FAILURE: {
				Toast.makeText(context, "用户名或密码错误",Toast.LENGTH_SHORT).show();
				pd.dismiss();
			}
				break;
			case Global.code.ERROR: {
				pd.dismiss();
			}
				break;
			}
			super.onPostExecute(result);
		}
		private HashMap<String, Object> getParams(){
			HashMap<String, Object> urlParams = new HashMap<String, Object>();
			urlParams.put("u_name", uName);
			urlParams.put("u_pwd", uPwd);
			urlParams.put("u_divice_id", AppInfoUtil.getDeviceId(context));
			Log.i("client", "u_name:"+uName+"---"+"u_pwd:"+uPwd);
			return urlParams;
		}
	}
	
	/**
	 * 用户登录
	 */
	class Register extends AsyncTask<Object, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pd.setMessage("正在注册...");
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			SyncHttp client = new SyncHttp(Global.REGISTER);
			try {
				String response = client.post(getParams());
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == Global.code.SUCCESS){//成功
					if(!jsonObject.isNull("data")){
						JSONObject jsonData = jsonObject.getJSONObject("data");
						uId = Integer.parseInt(jsonData.getString("u_id"));
					}
					return Global.code.SUCCESS;
				}else if(code == Global.code.FAILURE){//失败
					return Global.code.FAILURE;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Global.code.ERROR;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			switch (result) {
			case Global.code.SUCCESS: {
				new SetUserInfoTask().execute(2);
			}
				break;
			case Global.code.FAILURE: {
				Toast.makeText(context, "用户信息有误",Toast.LENGTH_SHORT).show();
				pd.dismiss();
			}
				break;
			case Global.code.ERROR: {
				pd.dismiss();
			}
				break;
			}
			super.onPostExecute(result);
		}
		private HashMap<String, Object> getParams(){
			HashMap<String, Object> urlParams = new HashMap<String, Object>();
			urlParams.put("u_name", uName);
			urlParams.put("u_pwd", uPwd);
			urlParams.put("u_divice_id", AppInfoUtil.getDeviceId(context));
			Log.i("client", "u_name:"+uName+"---"+"u_pwd:"+uPwd);
			return urlParams;
		}
	}
	
	
	/**
	 * 设置用户信息
	 *
	 */
	class SetUserInfoTask extends AsyncTask<Object, Void, Integer>{

		private int type;//1代表第三方登录，2代表注册
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			type = (int)params[0];
			SyncHttp client = new SyncHttp(Global.SET_USER_INFO);
			try {
				String response;
				if(type == 1){
					response = client.filePost(getParams(), saveBitmap2File(context,headBitmap),"u_head");
				}else{
					response = client.post(getParams());
				}
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == 100){//成功
					String headUrl = jsonObject.getString("data");
					//保存到本地当前用户表
					LXHUser lxhUser = new LXHUser();
					lxhUser.setuId(uId);
					lxhUser.setuName(uName);
					lxhUser.setuKey(uKey);
					lxhUser.setNickName(nickName);
					lxhUser.setHeadUrl(headUrl);
					lxhUser.setPersonSign("好好学习，天天乡乡");
					lxhUser.setHome("北京");
					lxhUser.setSex("男");
					lxhUser.setAge(18);
					new LXHUserDao(context).insertCurrentUser(lxhUser);
					return Global.code.SUCCESS;
				}else if(code == 101){//失败
					return Global.code.FAILURE;
				}else{//code == 801用户失效
					return Global.code.TOKEN_DISABLE;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Global.code.ERROR;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			if(!progressShow){
				return;
			}
			switch (result) {
			case Global.code.SUCCESS: {
				if(type == 1){
					registerHX(uName,uKey);
				}else{
					registerHX(uName, uPwd);
				}
			}
				break;
			case Global.code.FAILURE: {
			}
				break;
			case Global.code.ERROR: {
			}
				break;
			case Global.code.TOKEN_DISABLE: {
			}
				break;
			}
			super.onPostExecute(result);
		}
		private HashMap<String, Object> getParams(){
			HashMap<String, Object> urlParams = new HashMap<String, Object>();
			urlParams.put("u_id", uId);
			urlParams.put("u_divice_id", AppInfoUtil.getDeviceId(context));
			urlParams.put("u_token", App.pre.getString(Global.TOKEN, ""));
			urlParams.put("u_nick", nickName);
			urlParams.put("u_home", "北京");
			urlParams.put("u_person_sign", "好好学习，天天乡乡");
			urlParams.put("u_sex", "男");
			urlParams.put("u_age", 18);
			return urlParams;
		}
		
	}
	
	/**
	 * 3、注册环信服务器
	 * 
	 * @param uId
	 * @param pwd
	 */
	public void registerHX(final String userName,final String userPwd) {
		// TODO Auto-generated method stub
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				pd.setMessage("正在注册聊天服务器...");
			}
		});
		if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPwd)) {
			new Thread(new Runnable() {
				public void run() {
					try {
						// 调用sdk注册方法
						EMChatManager.getInstance().createAccountOnServer(
								userName, userPwd);
						// 登录服务器
						loginHX(userName,userPwd);
					} catch (final Exception e) {
						pd.dismiss();
						((Activity) context).runOnUiThread(new Runnable() {
							public void run() {
								if (e != null && e.getMessage() != null) {
									String errorMsg = e.getMessage();
									if (errorMsg
											.indexOf("EMNetworkUnconnectedException") != -1) {
										Toast.makeText(context, "网络异常，请检查网络！",
												Toast.LENGTH_SHORT).show();
									} else if (errorMsg.indexOf("conflict") != -1) {
										Log.i("UserLogin", "用户已存在");
										loginHX(userName,userPwd);
									}/*
									 * else if (errorMsg.indexOf(
									 * "not support the capital letters") != -1)
									 * { Toast.makeText(getApplicationContext(),
									 * "用户名不支持大写字母！", 0).show(); }
									 */else {
										Log.i("UserLogin",
												"注册失败: " + e.getMessage());
									}

								} else {
									Toast.makeText(context, "注册失败: 未知异常",
											Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			}).start();

		}
	}

	/**
	 * 4、登录环信服务器
	 * 
	 * @param context
	 * @param username
	 * @param pwd
	 */
	public void loginHX(final String userName,final String userPwd) {
		// TODO Auto-generated method stub
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				pd.setMessage("正在登陆聊天服务器...");
			}
		});
		if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPwd)) {
			progressShow = true;
			// 调用sdk登陆方法登陆聊天服务器
			EMChatManager.getInstance().login(userName, userPwd, new EMCallBack() {

				@Override
				public void onSuccess() {
					if (!progressShow) {
						return;
					}
					// 登陆成功，保存用户名密码
					App.getInstance().setUserName(userName);
					App.getInstance().setPassword(userPwd);
					App.pre.edit().putString(Global.TOKEN, "")
									.putInt(Global.UID,uId)
									.putString(Global.UNAME,userName).commit();
					//更新环信服务器上的昵称
					boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(nickName);
					if (!updatenick) {
						EMLog.e("UserLogin", "update current user nick fail");
					}
					turnToMainActivity();//进入主界面
					try {
						// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
						List<String> usernames = EMContactManager.getInstance()
								.getContactUserNames();
						// 获取所有人的昵称和头像url
						for(String name : usernames){
							new GetFriendInfoTask().execute(name);
						}
					} catch (Exception e) {
						Log.i("UserLogin", "用户名或昵称获取失败");
						pd.dismiss();
						e.printStackTrace();
					}
				}

				@Override
				public void onProgress(int progress, String status) {

				}

				@Override
				public void onError(final int code, final String message) {
					if (!progressShow) {
						return;
					}
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							if(code == -1005 && "用户名或密码错误".equals(message)){
								registerHX(userName,userPwd);
							}else{
								pd.dismiss();
								Toast.makeText(context, "登录失败",Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});
		} else {
			// 用户名或者密码为空
		}
	}

	/**
	 * 跳转到主界面
	 */
	private void turnToMainActivity(){
		((Activity) context).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				pd.progressFinish("登录成功");
				//跳转主界面
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.dismiss();
						Intent intent = new Intent();
						intent.putExtra("isfromtocity", 0);// 判断是从那里进入的城市选择
						intent.setClass(context, MainActivity.class);
						context.startActivity(intent);
						((Activity)context).finish();
					}
				}, 1000l);
			}
		});
	}
	
	/*
	 * 
	 * 初始化数据
	 */
	private File saveBitmap2File(Context context, Bitmap headBitmap) {
		if (!SDcardTools.isHaveSDcard()) {
			Toast.makeText(context, "请插入SD卡以便存储文件", Toast.LENGTH_LONG).show();
			return null;
		}

		// 要保存的文件的路径
		String filePath = SDcardTools.getSDPath() + "/" + "laoxianghaoAudio";
		// 实例化文件夹
		File dir = new File(filePath);
		if (!dir.exists()) {
			// 如果文件夹不存在 则创建文件夹
			dir.mkdirs();
		}

		// 保存文件名
		String fileName = "/head" + GetSystemDateTime.now()
				+ StringTools.getRandomString(2) + ".png";
		File headFile = new File(filePath + fileName);
		try {
			FileOutputStream fos = new FileOutputStream(headFile);
			headBitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return headFile;
	}
	
	class GetFriendInfoTask extends AsyncTask<Object, Void, Integer>{

		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String param = "u_name="+(String)params[0]
					+"&u_divice_id="+AppInfoUtil.getDeviceId(App.applicationContext)
					+"&u_token="+App.pre.getString(Global.TOKEN, "");
			SyncHttp client = new SyncHttp(Global.GET_FRIEND_INFO);
			try {
				String response = client.get(param);
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == Global.code.SUCCESS){//成功
					JSONObject dataObject = jsonObject.getJSONObject("data");
					User user = new User();
					String userName = dataObject.getString("u_name");
					user.setUsername(userName);
					user.setNickName(dataObject.getString("u_nick"));
					user.setHeadUrl(dataObject.getString("u_head"));
					setUserHearder(userName, user);
					// 存入db
					UserDao dao = new UserDao(context);
					dao.saveContact(user);
					return Global.code.SUCCESS;
				}else if(code == Global.code.FAILURE){//失败
					return Global.code.FAILURE;
				}else if(code == Global.code.TOKEN_DISABLE){//token
					return Global.code.TOKEN_DISABLE;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Global.code.ERROR;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			switch (result) {
			case Global.code.SUCCESS: {
			}
				break;
			case Global.code.FAILURE: {
			}
				break;
			case Global.code.ERROR: {
			}
				break;
			case Global.code.TOKEN_DISABLE: {
			}
				break;
			}
			super.onPostExecute(result);
		}
	}
	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
	
}
