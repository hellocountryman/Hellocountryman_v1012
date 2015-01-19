package com.feytuo.chat.activity;

import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.ImageLoader;
import com.feytuo.laoxianghao.util.SyncHttp;

/**
 * 黑名单列表页面
 * 
 */
public class BlacklistActivity extends BaseActivity {
	private ListView listView;
	private BlacklistAdapater adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_list);

		listView = (ListView) findViewById(R.id.list);

		List<String> blacklist = null;
		try {
			// 获取黑名单
			blacklist = EMContactManager.getInstance().getBlackListUsernames();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 显示黑名单列表
		if (blacklist != null) {
			Collections.sort(blacklist);
			adapter = new BlacklistAdapater(this, 1, blacklist);
			listView.setAdapter(adapter);
		}

		// 注册上下文菜单
		registerForContextMenu(listView);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.remove_from_blacklist, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.remove) {
			final String tobeRemoveUser = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 把目标user移出黑名单
			removeOutBlacklist(tobeRemoveUser);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 移出黑民单
	 * 
	 * @param tobeRemoveUser
	 */
	void removeOutBlacklist(final String tobeRemoveUser) {
		try {
			// 移出黑民单
			EMContactManager.getInstance().deleteUserFromBlackList(tobeRemoveUser);
			adapter.remove(tobeRemoveUser);
		} catch (EaseMobException e) {
			e.printStackTrace();
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext(), "移出失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	/**
	 * adapter
	 * 
	 */
	private class BlacklistAdapater extends ArrayAdapter<String> {

		private LXHUserDao userDao;
		private ImageLoader mImageLoader;
		public BlacklistAdapater(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			userDao = new LXHUserDao(context);
			mImageLoader = new ImageLoader(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getContext(), R.layout.row_contact, null);
				holder.avatarImageView = (ImageView)convertView.findViewById(R.id.avatar);
				holder.nickTextView = (TextView)convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}

			/*************id问题引起的错误****************/
			// 设置昵称和头像
			setUserInfo(getItem(position),
					holder.nickTextView, holder.avatarImageView);
			return convertView;
		}
		
		class ViewHolder{
			private ImageView avatarImageView;
			private TextView nickTextView;
		}
		
		/**
		 * 设置item的用户昵称、头像
		 * @param userName
		 * @param nameTV
		 * @param personHeadImg
		 */
		public void setUserInfo(String uName, TextView nameTV,
				ImageView personHeadImg) {
			LXHUser user = null;
			user = userDao.getNickAndHeadByUname(uName);
			if (user != null) {// 如果本地数据库存在该用户
				nameTV.setText(user.getNickName());
				Log.i("ImageLoader", ""+user.getHeadUrl());
				mImageLoader.loadImage(Global.FILE_HOST+user.getHeadUrl(), this,
						personHeadImg);
			} else {// 如果没有再从bmob上取
				new GetUserInfoByNameTask().execute(uName, nameTV, personHeadImg,this);
			}
		}

		/**
		 * 根据用户名获取用户信息
		 * @author feytuo
		 *
		 */
		class GetUserInfoByNameTask extends AsyncTask<Object, Void, Integer>{

			private LXHUser mUser;
			private TextView nameTV;
			private ImageView headIV;
			private BaseAdapter adapter;
			@Override
			protected Integer doInBackground(Object... params) {
				// TODO Auto-generated method stub
				nameTV = (TextView)params[1];
				headIV = (ImageView)params[2];
				adapter = (BaseAdapter)params[3];
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
					if(mUser != null){
						nameTV.setText(mUser.getNickName());
						new ImageLoader(App.applicationContext).loadImage(
								Global.FILE_HOST+mUser.getHeadUrl(), adapter, headIV);
						new LXHUserDao(App.applicationContext).insertUser(mUser);
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
		}
	}
	

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}
}
