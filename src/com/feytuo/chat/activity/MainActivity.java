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

import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.feytuo.chat.Constant;
import com.feytuo.chat.db.InviteMessgeDao;
import com.feytuo.chat.db.UserDao;
import com.feytuo.chat.domain.InviteMessage;
import com.feytuo.chat.domain.InviteMessage.InviteMesageStatus;
import com.feytuo.chat.domain.User;
import com.feytuo.chat.utils.CommonUtils;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.PublishActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.WelcomeActivity;
import com.feytuo.laoxianghao.fragment.FindFragment;
import com.feytuo.laoxianghao.fragment.MainFragment;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.share_sina.Share_Weibo;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.umeng.analytics.MobclickAgent;

/**
 * 主界面
 * 
 * @author feytuo
 * 
 */
public class MainActivity extends FragmentActivity implements
		IWeiboHandler.Response {

	public static final String TAG = "MainActivity";
	private Button[] mTabs;
	public ChatAndContactFragment cacFragment;// 乡聊
	private SettingsFragment settingFragment;// 设置
	private Fragment mainFragment;// 主界面
	private FindFragment findFragment;// 发现
	private Fragment[] fragments;
	private int index;
	// 当前fragment的index
	private int currentTabIndex;
	private NewMessageBroadcastReceiver msgReceiver;
	private Share_Weibo shareWeibo;
	// 账号在别处登录
	private boolean isConflict = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		shareWeibo = new Share_Weibo(this);
		((App)getApplication()).setMainActivity(this);
		initShare(savedInstanceState);
		initView(savedInstanceState);
		inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);

		if (App.isLogin()) {
			registerHXListeners();
		}
	}
	
	// 初始化环信监听和广播
	public void registerHXListeners() {
		// 注册一个接收消息的BroadcastReceiver
		Log.i(TAG, "注册了聊天广播");
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager
				.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		// 注册一个离线消息的BroadcastReceiver
		// IntentFilter offlineMessageIntentFilter = new
		// IntentFilter(EMChatManager.getInstance()
		// .getOfflineMessageBroadcastAction());
		// registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);

		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(
				new MyContactListener());
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());
		// 注册群聊相关的listener
		EMGroupManager.getInstance().addGroupChangeListener(
				new MyGroupChangeListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}

	private void initShare(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (savedInstanceState != null) {
			shareWeibo.getmWeiboShareAPI().handleWeiboResponse(getIntent(),
					this);
		}
	}

	public void onTabpublishClicked(View view) {
		if (App.isLogin()) {
			Intent intentpublish = new Intent();
			intentpublish.setClass(MainActivity.this, PublishActivity.class);
			startActivity(intentpublish);
		}
	}

	/**
	 * 初始化组件
	 * @param savedInstanceState 
	 */
	private void initView(Bundle savedInstanceState) {
		mTabs = new Button[4];
		mTabs[0] = (Button) findViewById(R.id.btn_main_invitation);
		mTabs[1] = (Button) findViewById(R.id.btn_find);
		mTabs[2] = (Button) findViewById(R.id.btn_address_list);
		mTabs[3] = (Button) findViewById(R.id.btn_setting);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		// 主帖界面
		if(savedInstanceState == null){
			mainFragment = new MainFragment();
			// 发现
			findFragment = new FindFragment();
			// 乡聊fragment
			cacFragment = new ChatAndContactFragment();
			// 设置fragment
			settingFragment = new SettingsFragment();
			transaction.add(R.id.fragment_container, mainFragment, "mainFragment");
			transaction.add(R.id.fragment_container, findFragment, "findFragment");
			transaction.add(R.id.fragment_container, cacFragment, "cacFragment");
			transaction.add(R.id.fragment_container, settingFragment, "settingFragment");
//			Log.i(TAG, "------> new mainFragment:"+mainFragment);
		}else{
			mainFragment = manager.findFragmentByTag("mainFragment");
			findFragment = (FindFragment) manager.findFragmentByTag("findFragment");
			cacFragment = (ChatAndContactFragment) manager.findFragmentByTag("cacFragment");
			settingFragment =  (SettingsFragment) manager.findFragmentByTag("settingFragment");
//			Log.i(TAG, "------> old mainFragment:"+mainFragment);
		}
		
		fragments = new Fragment[] { mainFragment, findFragment, cacFragment,
				settingFragment };
		// 添加显示第一个fragment
		transaction
			.hide(cacFragment).hide(findFragment).hide(settingFragment)
			.show(mainFragment).commit();
	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_main_invitation:
			index = 0;
			break;
		case R.id.btn_find:
			index = 1;
			break;
		case R.id.btn_address_list:
			index = 2;
			break;
		case R.id.btn_setting:
			index = 3;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播接收者
		try {
			unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(ackMessageReceiver);
		} catch (Exception e) {
		}
		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}

	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			cacFragment.getUnreadLabel().setText(String.valueOf(count));
			cacFragment.getUnreadLabel().setVisibility(View.VISIBLE);
		} else {
			cacFragment.getUnreadLabel().setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
					cacFragment.getUnreadAddressLable().setText(
							String.valueOf(count));
					cacFragment.getUnreadAddressLable().setVisibility(
							View.VISIBLE);
				} else {
					cacFragment.getUnreadAddressLable().setVisibility(
							View.INVISIBLE);
				}
			}
		});

	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		if (App.getInstance().getContactList()
				.get(Constant.NEW_FRIENDS_USERNAME) != null)
			unreadAddressCountTotal = App.getInstance().getContactList()
					.get(Constant.NEW_FRIENDS_USERNAME).getUnreadMsgCount();
		return unreadAddressCountTotal;
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

			// 消息id
//			String msgId = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			// EMMessage message =
			// EMChatManager.getInstance().getMessage(msgId);

			// 刷新bottom bar消息未读数
			updateUnreadLabel();
			// 当前页面如果为聊天历史页面，刷新此页面
			if (cacFragment != null
					&& cacFragment.getChatHistoryFragment() != null
					&& cacFragment.getChatHistoryFragment().isInitialized()) {
				cacFragment.getChatHistoryFragment().refresh();
			}
			// 注销广播，否则在ChatActivity中会收到这个广播
			abortBroadcast();
		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
			abortBroadcast();
		}
	};

	/**
	 * 离线消息BroadcastReceiver sdk 登录后，服务器会推送离线消息到client，这个receiver，是通知UI
	 * 有哪些人发来了离线消息 UI 可以做相应的操作，比如下载用户信息
	 */
	// private BroadcastReceiver offlineMessageReceiver = new
	// BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// String[] users = intent.getStringArrayExtra("fromuser");
	// String[] groups = intent.getStringArrayExtra("fromgroup");
	// if (users != null) {
	// for (String user : users) {
	// System.out.println("收到user离线消息：" + user);
	// }
	// }
	// if (groups != null) {
	// for (String group : groups) {
	// System.out.println("收到group离线消息：" + group);
	// }
	// }
	// abortBroadcast();
	// }
	// };

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	/***
	 * 好友变化listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			//1、获取用户头像和昵称
			//2、添加到本地并显示在界面
			getUserInfoFromBmob(usernameList);
		}

		private void getUserInfoFromBmob(List<String> usernameList) {
//			// TODO Auto-generated method stub
			/********domain***********/
//			BmobQuery<LXHUser> query = new BmobQuery<LXHUser>();
//			query.addWhereContainedIn("objectId", usernameList);
//			query.addQueryKeys("objectId,nickName,headUrl");
//			query.findObjects(MainActivity.this, new FindListener<LXHUser>(){
//
//				@Override
//				public void onSuccess(List<LXHUser> arg0) {
//					// TODO Auto-generated method stub
//					Map<String, User> localUsers = App.getInstance()
//							.getContactList();
//					Map<String, User> toAddUsers = new HashMap<String, User>();
//					// Log.i("UserLogin", "服务器好友有："+usernames.size());
//					for (int i = 0; i < arg0.size(); i++) {
//						User user = new User();
//						String userName = arg0.get(i).getObjectId();
//						user.setUsername(userName);
//						user.setNickName(arg0.get(i).getNickName());
//						user.setHeadUrl(arg0.get(i).getHeadUrl());
//						setUserHead(userName,user);
//						// 暂时有个bug，添加好友时可能会回调added方法两次
//						if (!localUsers.containsKey(userName)) {
//							userDao.saveContact(user);
//						}
//						toAddUsers.put(userName, user);
//						Log.i("UserLogin", "添加了服务器好友：" + userName
//								+ "---" + arg0.get(i).getNickName()+"---"+arg0.get(i).getHeadUrl());
//					}
//					localUsers.putAll(toAddUsers);
//					// 刷新ui
//					if (cacFragment != null
//							&& cacFragment.getContactListFragment() != null) {
//						Log.i("MainActivity", "add");
//						cacFragment.getContactListFragment().refresh();
//					}
//				}
//				
//				@Override
//				public void onError(int arg0, String arg1) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
//			// 被删除
//			Map<String, User> localUsers = App.getInstance().getContactList();
//			for (String username : usernameList) {
//				localUsers.remove(username);
//				userDao.deleteContact(username);
//				inviteMessgeDao.deleteMessage(username);
//			}
//			runOnUiThread(new Runnable() {
//				public void run() {
//					// 如果正在与此用户的聊天页面
//					if (ChatActivity.activityInstance != null
//							&& usernameList
//									.contains(ChatActivity.activityInstance
//											.getToChatUsername())) {
//						Toast.makeText(
//								MainActivity.this,
//								ChatActivity.activityInstance
//										.getToChatUsername() + "已把你从他好友列表里移除",
//								Toast.LENGTH_SHORT).show();
//						ChatActivity.activityInstance.finish();
//					}
//					updateUnreadLabel();
//				}
//			});
//			// 刷新ui
//			if (cacFragment != null
//					&& cacFragment.getContactListFragment() != null) {
//				Log.i("MainActivity", "delete");
//				cacFragment.getContactListFragment().refresh();
//			}

		}

		@Override
		public void onContactInvited(String username, String reason) {
			// // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
			// List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			// for (InviteMessage inviteMessage : msgs) {
			// if (inviteMessage.getGroupId() == null &&
			// inviteMessage.getFrom().equals(username)) {
			// inviteMessgeDao.deleteMessage(username);
			// }
			// }
			// // 自己封装的javabean
			// InviteMessage msg = new InviteMessage();
			// msg.setFrom(username);
			// msg.setTime(System.currentTimeMillis());
			// msg.setReason(reason);
			// Log.d(TAG, username + "请求加你为好友,reason: " + reason);
			// // 设置相应status
			// msg.setStatus(InviteMesageStatus.BEINVITEED);
			// notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactAgreed(String username) {
			// List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			// for (InviteMessage inviteMessage : msgs) {
			// if (inviteMessage.getFrom().equals(username)) {
			// return;
			// }
			// }
			// // 自己封装的javabean
			// InviteMessage msg = new InviteMessage();
			// msg.setFrom(username);
			// msg.setTime(System.currentTimeMillis());
			// Log.d(TAG, username + "同意了你的好友请求");
			// msg.setStatus(InviteMesageStatus.BEAGREED);
			// notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能,demo未实现

		}

	}

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		// 提示有新消息
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

		// 刷新bottom bar消息未读数
		updateUnreadAddressLable();
		// 刷新好友页面ui
		if (cacFragment != null && cacFragment.getContactListFragment() != null) {
			cacFragment.getContactListFragment().refresh();
		}
	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		// 未读数加1
		User user = App.getInstance().getContactList()
				.get(Constant.NEW_FRIENDS_USERNAME);
		user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
	}

	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	void setUserHead(String username,User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNickName())) {
			headerName = user.getNickName();
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

	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements ConnectionListener {

		@Override
		public void onConnected() {
			Log.i("MyConnectionListener", "恢复连接");
			if (cacFragment != null
					&& cacFragment.getChatHistoryFragment() != null
					&& cacFragment.getChatHistoryFragment().errorItem != null) {
				cacFragment.getChatHistoryFragment().errorItem
						.setVisibility(View.GONE);
			}
		}

		@Override
		public void onDisConnected(String errorString) {
			Log.i("MyConnectionListener", "失去连接：" + errorString);
			if (errorString != null && errorString.contains("conflict")) {
				// 显示帐号在其他设备登陆dialog
				showConflictDialog();
			} else {
				if (cacFragment != null
						&& cacFragment.getChatHistoryFragment() != null
						&& cacFragment.getChatHistoryFragment().errorItem != null) {
					cacFragment.getChatHistoryFragment().errorItem
							.setVisibility(View.VISIBLE);
					if (NetUtils.hasNetwork(MainActivity.this)) {
						cacFragment.getChatHistoryFragment().errorText
								.setText("连接不到聊天服务器");
					} else {
						cacFragment.getChatHistoryFragment().errorText
								.setText("当前网络不可用，请检查网络设置");
					}
				}
			}
		}

		@Override
		public void onReConnected() {
			if (cacFragment != null
					&& cacFragment.getChatHistoryFragment() != null
					&& cacFragment.getChatHistoryFragment().errorItem != null) {
				cacFragment.getChatHistoryFragment().errorItem
						.setVisibility(View.GONE);
			}
		}

		@Override
		public void onReConnecting() {
		}

		@Override
		public void onConnecting(String progress) {
		}

	}

	/**
	 * MyGroupChangeListener
	 */
	private class MyGroupChangeListener implements GroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName,
				String inviter, String reason) {
			boolean hasGroup = false;
			for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					break;
				}
			}
			if (!hasGroup)
				return;

			// 被邀请
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(inviter + "邀请你加入了群聊"));
			// 保存邀请消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					// 当前页面如果为聊天历史页面，刷新此页面
					if (cacFragment != null
							&& cacFragment.getChatHistoryFragment() != null
							&& cacFragment.getChatHistoryFragment().isInitialized()) {
						cacFragment.getChatHistoryFragment().refresh();
					}
					if (CommonUtils.getTopActivity(MainActivity.this).equals(
							GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter,
				String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee,
				String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						// 当前页面如果为聊天历史页面，刷新此页面
						if (cacFragment != null
								&& cacFragment.getChatHistoryFragment() != null
								&& cacFragment.getChatHistoryFragment().isInitialized()) {
							cacFragment.getChatHistoryFragment().refresh();
						}
						if (CommonUtils.getTopActivity(MainActivity.this)
								.equals(GroupsActivity.class.getName())) {
							GroupsActivity.instance.onResume();
						}
					} catch (Exception e) {
						Log.e("###", "refresh exception " + e.getMessage());
					}

				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 当前页面如果为聊天历史页面，刷新此页面
					if (cacFragment != null
							&& cacFragment.getChatHistoryFragment() != null
							&& cacFragment.getChatHistoryFragment().isInitialized()) {
						cacFragment.getChatHistoryFragment().refresh();
					}
					if (CommonUtils.getTopActivity(MainActivity.this).equals(
							GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName,
				String applyer, String reason) {
			// 用户申请加入群聊
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			msg.setStatus(InviteMesageStatus.BEAPPLYED);
			notifyNewIviteMessage(msg);
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName,
				String accepter) {
			// 加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(accepter + "同意了你的群聊申请"));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					// 当前页面如果为聊天历史页面，刷新此页面
					if (cacFragment != null
							&& cacFragment.getChatHistoryFragment() != null
							&& cacFragment.getChatHistoryFragment().isInitialized()) {
						cacFragment.getChatHistoryFragment().refresh();
					}
					if (CommonUtils.getTopActivity(MainActivity.this).equals(
							GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName,
				String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (!isConflict && App.isLogin()) {
			updateUnreadLabel();
			updateUnreadAddressLable();
			EMChatManager.getInstance().activityResumed();
			if (currentTabIndex == 2) {
				if (cacFragment != null
						&& cacFragment.getContactListFragment() != null) {
					Log.i("MainActivity", "add");
					cacFragment.getContactListFragment().refresh();
				}
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//按返回键，重新将首页设为全部，并需要刷新列表
			App.pre.edit().putInt(Global.CURRENT_NATIVE, 0).commit();
			App.pre.edit().putBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, true)
			.commit();
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	private boolean isConflictDialogShow;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		App.getInstance().logout();

		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				conflictBuilder.setTitle("下线通知");
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								conflictBuilder = null;
								finish();
								startActivity(new Intent(MainActivity.this,
										WelcomeActivity.class));
							}
						});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				Log.e("###",
						"---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * 微博分享接口
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			// Toast.makeText(getActivity(),
			// R.string.weibosdk_demo_toast_share_success,
			// Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			// Toast.makeText(getActivity(),
			// R.string.weibosdk_demo_toast_share_canceled,
			// Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			// Toast.makeText(
			// getActivity(),
			// getString(R.string.weibosdk_demo_toast_share_failed)
			// + "Error Message: " + baseResp.errMsg,
			// Toast.LENGTH_LONG).show();
			break;
		}
	}

//	private Dialog loginDialog;
//
//	public void showLoginDialog() {
//		// TODO Auto-generated method stub
//		loginDialog = new MyLoginDialog(this, R.style.MyDialog);
//		loginDialog.show();
//	}

	/**
	 * 当 SSO 授权 Activity 退出时，该函数被调用。
	 * 
	 * @see {@link Activity#onActivityResult}
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		if (shareWeibo.getmSsoHandler() != null) {
			shareWeibo.getmSsoHandler().authorizeCallBack(requestCode,
					resultCode, data);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		}
		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		// mWeiboShareAPI.handleWeiboResponse(intent, getActivity());
		shareWeibo.getmWeiboShareAPI().handleWeiboResponse(intent, this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
