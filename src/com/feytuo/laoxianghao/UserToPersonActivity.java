package com.feytuo.laoxianghao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.util.HanziToPinyin;
import com.feytuo.chat.Constant;
import com.feytuo.chat.activity.ChatActivity;
import com.feytuo.chat.db.UserDao;
import com.feytuo.chat.domain.User;
import com.feytuo.laoxianghao.adapter.FindListViewAdapter;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.ImageLoader;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.feytuo.laoxianghao.view.OnloadDialog;
import com.umeng.analytics.MobclickAgent;

public class UserToPersonActivity extends Activity {

	private FindListViewAdapter adapter;
	private ListView userToPersonListView;
	private List<Map<String, Object>> listItems;
	private List<Map<String, Object>> tempListItems;
	private TextView toPersonHome;// 家乡;
	private ImageView toPersonHeadImg;
	private TextView toPersonNick;
	private TextView toPersonSignText;
	private Button addFriendBtn;
	private Button chatBtn;
	private LXHUser mUser;
	
	private int userId;//用户id
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_to_person_activity);
		userId=getIntent().getIntExtra("userid", -1);
		initView();
		initlistview();
		new GetUserInfo().execute();//获取用户信息
		new GetUserInvitation().execute();//根据用户ID从网络上面获取到数据
	}

	//初始化view
	private void initView() {
		// TODO Auto-generated method stub
		toPersonHeadImg = (ImageView)findViewById(R.id.to_person_head_img);
		toPersonNick = (TextView)findViewById(R.id.to_person_nick);
		toPersonHome = (TextView)findViewById(R.id.to_person_home);
		toPersonSignText = (TextView)findViewById(R.id.to_person_sign_text);	
		addFriendBtn = (Button)findViewById(R.id.user_info_add_friend_btn);
		chatBtn = (Button)findViewById(R.id.user_to_person_chat_btn);
	}

	/**
	 * 点击返回按钮
	 * @param v
	 */
	public void toPersonReturnBtn(View v) {
		finish();
	}
	/**
	 * 点击聊天按钮
	 * @param v
	 */
	public void toPersonChat(View v){
		if(userId != App.pre.getInt(Global.UID, -1) && !TextUtils.isEmpty(mUser.getuName())){
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
			intent.putExtra("userName", mUser.getuName());
			startActivity(intent);
		}else{
			//不能和自己聊天,信息未加载不能聊天
		}
	}


	//初始化listview
	private void initlistview() {

		userToPersonListView = (ListView) findViewById(R.id.user_to_person_listview);
		listItems = new ArrayList<Map<String, Object>>();
		tempListItems = new ArrayList<Map<String, Object>>();
		adapter = new FindListViewAdapter(UserToPersonActivity.this, listItems,
				R.layout.index_listview, new String[] { "position", "words",
						"time", "praise_num", "comment_num" }, new int[] {
						R.id.index_locals_country, R.id.index_text_describe,
						R.id.index_locals_time, R.id.index_support_num,
						R.id.index_comment_num });
		userToPersonListView.setAdapter(adapter);
	}
	/**
	 * 获取用户信息
	 */
	class GetUserInfo extends AsyncTask<Object, Void, Integer>{

		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String param = "u_id="+userId
					+"&u_divice_id="+AppInfoUtil.getDeviceId(UserToPersonActivity.this)
					+"&u_token="+App.pre.getString(Global.TOKEN, "");
			SyncHttp client = new SyncHttp(Global.GET_USER_INFO);
			try {
				String response = client.get(param);
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == Global.code.SUCCESS){//成功
					JSONObject dataObject = jsonObject.getJSONObject("data");
					mUser = new LXHUser();
					mUser.setuId(dataObject.getInt("u_id"));
					mUser.setuName(dataObject.getString("u_name"));
					mUser.setuKey(dataObject.getString("u_key"));
					mUser.setHeadUrl(dataObject.getString("u_head"));
					mUser.setNickName(dataObject.getString("u_nick"));
					mUser.setHome(dataObject.getString("u_home"));
					mUser.setPersonSign(dataObject.getString("u_person_sign"));
					mUser.setSex(dataObject.getString("u_sex"));
					mUser.setAge(dataObject.getInt("u_age"));
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
				setUserInfo(mUser);
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
	//设置用户信息
	private void setUserInfo(LXHUser user) {
		// TODO Auto-generated method stub
		if(user != null){
			new ImageLoader(this).loadNoImage(Global.FILE_HOST+user.getHeadUrl(), null, toPersonHeadImg);
			toPersonNick.setText(user.getNickName());
			if(!TextUtils.isEmpty(user.getHome())){
				toPersonHome.setText(user.getHome()+"人");
			}
			toPersonSignText.setText(user.getPersonSign());
			//添加好友按钮初始化
			if(App.getInstance().getContactList().containsKey(user.getuId())){
				addFriendBtn.setVisibility(View.INVISIBLE);
			}else{
				addFriendBtn.setVisibility(View.VISIBLE);
			}
		}else{
			addFriendBtn.setVisibility(View.INVISIBLE);
			chatBtn.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 获取用户帖子
	 */
	class GetUserInvitation extends AsyncTask<Object, Void, Integer>{

		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String param = "u_id="+userId
					+"&u_divice_id="+AppInfoUtil.getDeviceId(UserToPersonActivity.this)
					+"&u_token="+App.pre.getString(Global.TOKEN, "");
			SyncHttp client = new SyncHttp(Global.GET_USER_INVITATION);
			try {
				String response = client.get(param);
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == Global.code.SUCCESS){//成功
					updateData(jsonObject);
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
				listItems.addAll(tempListItems);
				adapter.notifyDataSetChanged();
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
		private void updateData(JSONObject jsonObject) {
			// TODO Auto-generated method stub
			if(jsonObject.isNull("data")){
				return ;
			}
			tempListItems.clear();
			try {
				JSONArray dataObject = jsonObject.getJSONArray("data");
				for(int i = 0; i < dataObject.length(); i++){
					JSONObject itemObject = dataObject.optJSONObject(i);
					HashMap<String, Object> map = new HashMap<>();
					map.put("inv_id", itemObject.getInt("inv_id"));
					map.put("uid", itemObject.getInt("u_id"));
					map.put("home", itemObject.getInt("home"));
					map.put("position", itemObject.getString("position"));
					map.put("words", itemObject.getString("words"));
					map.put("voice", itemObject.getString("voice"));
					map.put("voice_duration", itemObject.getInt("voice_duration"));
					map.put("time", itemObject.getString("time"));
					map.put("ishot", itemObject.getInt("is_hot"));
					map.put("praise_num", itemObject.getInt("praise_num"));
					map.put("comment_num", itemObject.getInt("comment_num"));
					map.put("type", itemObject.getInt("type"));
					tempListItems.add(map);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 点击添加好友按钮
	 * @param v
	 */
	public void addFriend(View v){
		if(mUser != null){
			addContact();
		}
	}
	
	private OnloadDialog pd;
	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(){
		pd = new OnloadDialog(this);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		pd.setMessage("正在发送请求...");
		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo写死了个reason，实际应该让用户手动填入
					EMContactManager.getInstance().addContact(mUser.getuId()+"", "加个好友呗");
					//将添加的好友持久到本地数据库
					addToLocalDB(mUser.getuId()+"",mUser.getNickName(),mUser.getHeadUrl());
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(UserToPersonActivity.this, "成功添加"+mUser.getNickName(), Toast.LENGTH_SHORT).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(UserToPersonActivity.this, "添加好友失败,请稍候再试...", Toast.LENGTH_SHORT).show();
							Log.i("UserToPersonActivity","添加好友失败：" + e.getMessage());
						}
					});
				}
			}
		}).start();
		
		
	}
	
	
	private void addToLocalDB(String username,String userNick,String headUrl){
		// 保存增加的联系人
		Map<String, User> localUsers = App.getInstance()
				.getContactList();
		Map<String, User> toAddUsers = new HashMap<String, User>();
		User user = new User();
		user.setUsername(username);
		user.setNickName(userNick);
		user.setHeadUrl(headUrl);
		setUserHead(user);
		// 暂时有个bug，添加好友时可能会回调added方法两次
		UserDao userDao = new UserDao(this);
		if (!localUsers.containsKey(username)) {
			userDao.saveContact(user);
		}
		toAddUsers.put(username, user);
		localUsers.putAll(toAddUsers);
		pd.dismiss();
	}
	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	void setUserHead(User user) {
		String headerName = null;
		String username = user.getUsername();
		if (!TextUtils.isEmpty(user.getNickName())) {
			headerName = user.getNickName();
		} else {
			headerName = username;
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
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("UserToPersonActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		MobclickAgent.onPageEnd("UserToPersonActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

}
