/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feytuo.chat.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.feytuo.chat.adapter.AddContactAdapter;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.umeng.analytics.MobclickAgent;

public class AddContactActivity extends BaseActivity{
	private EditText editText;
	private Button searchBtn;
	private ListView addContactListView;
	private AddContactAdapter adapter;
	private List<LXHUser> listData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		listData = new ArrayList<LXHUser>();
		
		addContactListView = (ListView)findViewById(R.id.add_contact_listview);
		adapter = new AddContactAdapter(this,listData);
		addContactListView.setAdapter(adapter);
		
		editText = (EditText) findViewById(R.id.edit_note);
		searchBtn = (Button) findViewById(R.id.search);
		
	}
	
	
	/**
	 * 查找contact
	 * @param v
	 */
	public void searchContact(View v) {
		final String name = editText.getText().toString();
		String saveText = searchBtn.getText().toString();
		
		if (getString(R.string.button_search).equals(saveText)) {
//			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "请输入好友昵称"));
				return;
			}
			
			// TODO 从服务器获取此contact,如果不存在提示不存在此用户
			//根据昵称或者id获取用户信息
			new SearchFriend().execute(name);
		} 
	}	
	
	/**
	 * 查找好友
	 */
	class SearchFriend extends AsyncTask<Object, Void, Integer>{

		private List<LXHUser> mUserList;
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String param = App.getCommonParams()
					+"&name="+(String)params[0];
			SyncHttp client = new SyncHttp(Global.SEARCH_FRIEND);
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
				listData.clear();
				listData.addAll(mUserList);
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
			mUserList = new ArrayList<LXHUser>();
			if(jsonObject.isNull("data")){
				return ;
			}
			try {
				JSONArray dataObject = jsonObject.getJSONArray("data");
				for(int i = 0; i < dataObject.length(); i++){
					JSONObject itemObject = dataObject.optJSONObject(i);
					LXHUser mUser = new LXHUser();
					mUser.setuId(itemObject.getInt("u_id"));
					mUser.setuName(itemObject.getString("u_name"));
					mUser.setuKey(itemObject.getString("u_key"));
					mUser.setHeadUrl(itemObject.getString("u_head"));
					mUser.setNickName(itemObject.getString("u_nick"));
					mUser.setHome(itemObject.getString("u_home"));
					mUser.setPersonSign(itemObject.getString("u_person_sign"));
					mUser.setSex(itemObject.getString("u_sex"));
					mUser.setAge(itemObject.getInt("u_age"));
					mUserList.add(mUser);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void back(View v) {
		finish();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("AddContactActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("AddContactActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
