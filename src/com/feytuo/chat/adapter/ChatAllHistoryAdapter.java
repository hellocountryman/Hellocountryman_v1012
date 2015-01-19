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
package com.feytuo.chat.adapter;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.feytuo.chat.Constant;
import com.feytuo.chat.db.UserDao;
import com.feytuo.chat.utils.SmileUtils;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.ImageLoader;
import com.feytuo.laoxianghao.util.SyncHttp;

/**
 * 显示所有聊天记录adpater
 * 
 */
public class ChatAllHistoryAdapter extends ArrayAdapter<EMConversation> {

	private String TAG = "ChatAllHistoryAdapter";
	private LayoutInflater inflater;
	private Context context;
	private ImageLoader mImageLoader;
	
	private UserDao userDao;

	public ChatAllHistoryAdapter(Context context, int textViewResourceId, List<EMConversation> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		inflater = LayoutInflater.from(context);
		mImageLoader = new ImageLoader(context);
		userDao = new UserDao(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_chat_history, parent, false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.msgState = convertView.findViewById(R.id.msg_state);
			holder.list_item_layout = (RelativeLayout) convertView.findViewById(R.id.list_item_layout);
			convertView.setTag(holder);
		}
		
		holder.list_item_layout.setBackgroundResource(R.drawable.common_selector);

		// 获取与此用户/群组的会话
		EMConversation conversation = getItem(position);
		// 获取用户username或者群组groupid
		String username = conversation.getUserName();
		List<EMGroup> groups = EMGroupManager.getInstance().getAllGroups();
		EMContact contact = null;
		boolean isGroup = false;
		for (EMGroup group : groups) {
			if (group.getGroupId().equals(username)) {
				isGroup = true;
				contact = group;
				break;
			}
		}
		if (isGroup) {
			// 群聊消息，显示群聊头像
			holder.avatar.setImageResource(R.drawable.group_icon);
			holder.name.setText(contact.getNick() != null ? contact.getNick() : username);
		} else {
			if (username.equals(Constant.GROUP_USERNAME)) {
				holder.name.setText("群聊");

			} else if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
				holder.name.setText("申请与通知");
			}
			//会话名
			setUserNickName(username,holder.name);
			
			// 本地或者服务器获取用户详情，以用来显示头像和nick
			holder.avatar.setImageResource(R.drawable.default_avatar);
			setUserHeadUrl(username,holder.avatar);
		}

		if (conversation.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
			holder.unreadLabel.setVisibility(View.VISIBLE);
		} else {
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}

		if (conversation.getMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();
			holder.message.setText(SmileUtils.getSmiledText(getContext(), getMessageDigest(lastMessage, (this.getContext()))),
					BufferType.SPANNABLE);

			holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
				holder.msgState.setVisibility(View.VISIBLE);
			} else {
				holder.msgState.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
			if (message.direct == EMMessage.Direct.RECEIVE) {
				// 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
				// digest = EasyUtils.getAppResourceString(context,
				// "location_recv");
				digest = getStrng(context, R.string.location_recv);
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
				// digest = EasyUtils.getAppResourceString(context,
				// "location_prefix");
				digest = getStrng(context, R.string.location_prefix);
			}
			break;
		case IMAGE: // 图片消息
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = getStrng(context, R.string.picture) + imageBody.getFileName();
			break;
		case VOICE:// 语音消息
			digest = getStrng(context, R.string.voice);
			break;
		case VIDEO: // 视频消息
			digest = getStrng(context, R.string.video);
			break;
		case TXT: // 文本消息
			if(!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = txtBody.getMessage();
			}else{
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
			}
			break;
		case FILE: // 普通文件消息
			digest = getStrng(context, R.string.file);
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}

	private static class ViewHolder {
		/** 和谁的聊天记录 */
		TextView name;
		/** 消息未读数 */
		TextView unreadLabel;
		/** 最后一条消息的内容 */
		TextView message;
		/** 最后一条消息的时间 */
		TextView time;
		/** 用户头像 */
		ImageView avatar;
		/** 最后一条消息的发送状态 */
		View msgState;
		/** 整个list中每一行总布局 */
		RelativeLayout list_item_layout;

	}
	
	/**
	 * 设置item的用户昵称
	 * @param userName
	 * @param nameTV
	 */
	public void setUserNickName(String userName ,TextView nameTV){
		String nickName = userDao.getUserNickName(userName);
		Log.i(TAG, "setnickname:"+nickName);
		if(!TextUtils.isEmpty(nickName)){//如果本地数据库存在该用户
			nameTV.setText(nickName);
		}else{//如果没有再从bmob上取
//			getNickNameFromBmob(userName,nameTV);
			Log.i("ChatAllHistoryAdapter", "setnickname");
			new GetUserInfoTask().execute(userName,nameTV,null,this);
		}
	}

//	private void getNickNameFromBmob(final String userName, final TextView nameTV) {
//		// TODO Auto-generated method stub
//		BmobQuery<LXHUser> query = new BmobQuery<LXHUser>();
//		query.addWhereEqualTo("objectId", userName);
//		query.addQueryKeys("nickName");
//		query.findObjects(context, new FindListener<LXHUser>() {
//			
//			@Override
//			public void onSuccess(List<LXHUser> arg0) {
//				// TODO Auto-generated method stub
//				if(arg0.size() > 0){
//					nameTV.setText(arg0.get(0).getNickName());
//					userDao.updateNickName2Conversation(userName, arg0.get(0).getNickName());
//				}else{
//					nameTV.setText(userName);
//				}
//			}
//			
//			@Override
//			public void onError(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				Log.i(TAG, "查找昵称失败："+arg1);
//			}
//		});
//	}
	/**
	 * 获取用户headurl
	 * @param username
	 * @param avatar
	 */
	private void setUserHeadUrl(String userName, ImageView headUrlIV) {
		// TODO Auto-generated method stub
		String headUrl = userDao.getUserHeadUrl(userName);
		Log.i(TAG, "sethead:"+headUrl);
		if(!TextUtils.isEmpty(headUrl)){//如果本地数据库存在该用户
			mImageLoader.loadImage(Global.FILE_HOST+headUrl, this, headUrlIV);
		}else{//如果没有再从bmob上取
//			getHeadUrlFromBmob(userName,headUrlIV);
			Log.i("ChatAllHistoryAdapter", "sethead");
			new GetUserInfoTask().execute(userName,null,headUrlIV,this);
		}
	}

//	private void getHeadUrlFromBmob(final String userName, final ImageView headUrlIV) {
//		// TODO Auto-generated method stub
//		BmobQuery<LXHUser> query = new BmobQuery<LXHUser>();
//		query.addWhereEqualTo("objectId", userName);
//		query.addQueryKeys("headUrl");
//		query.findObjects(context, new FindListener<LXHUser>() {
//			
//			@Override
//			public void onSuccess(List<LXHUser> arg0) {
//				// TODO Auto-generated method stub
//				if(arg0.size() > 0 && !TextUtils.isEmpty(arg0.get(0).getHeadUrl())){
//					mImageLoader.loadImage(arg0.get(0).getHeadUrl(), ChatAllHistoryAdapter.this, headUrlIV);
//					userDao.updateHeadUrl2Conversation(userName, arg0.get(0).getHeadUrl());
//				}else{
//					headUrlIV.setImageResource(R.drawable.default_avatar);
//				}
//			}
//			
//			@Override
//			public void onError(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				Log.i(TAG, "查找头像url失败："+arg1);
//			}
//		});
//	}
	/**
	 * 获取用户信息
	 * params[0] uName
	 * params[1] nickNameTextView
	 * params[2] headImageView
	 * params[3] adapter
	 */
	class GetUserInfoTask extends AsyncTask<Object, Void, Integer>{

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
					+"&u_divice_id="+AppInfoUtil.getDeviceId(context)
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
					if(nameTV != null){
						nameTV.setText(mUser.getNickName());
						userDao.updateNickName2Conversation(mUser.getuName(), mUser.getNickName());
					}
					if(headIV != null){
						mImageLoader.loadImage(Global.FILE_HOST+mUser.getHeadUrl(), adapter, headIV);
						userDao.updateHeadUrl2Conversation(mUser.getuName(), mUser.getHeadUrl());
					}
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
	String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}
}
