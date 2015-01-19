package com.feytuo.chat.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.feytuo.chat.adapter.ChatAllHistoryAdapter;
import com.feytuo.chat.db.InviteMessgeDao;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 * 
 */
public class ChatAllHistoryFragment extends Fragment {

	private InputMethodManager inputMethodManager;
	private ListView listView;
//	private Map<String, User> contactList;
	private ChatAllHistoryAdapter adapter;
//	private EditText query;
//	private ImageButton clearSearch;
	private ImageView noChatImageView;
	public RelativeLayout errorItem;
	public TextView errorText;
	private boolean hidden;
	private List<EMGroup> groups;
	private boolean isInitialized = false;//有没有初始化
	
	private List<EMConversation> conversationDataList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_conversation_history, container, false);
		errorItem = (RelativeLayout) view.findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(App.isLogin() && !isInitialized){
			initView();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		isInitialized = true;
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		// contact list
//		contactList = App.getInstance().getContactList();
		noChatImageView = (ImageView)getView().findViewById(R.id.conversation_no_chat_img);
		listView = (ListView) getView().findViewById(R.id.list);
		conversationDataList = loadConversationsWithRecentChat();
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationDataList);
		groups = EMGroupManager.getInstance().getAllGroups();

		// 设置adapter
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EMConversation conversation = adapter.getItem(position);
				String username = conversation.getUserName();
				if (username.equals(App.getInstance().getUserName()))
					Toast.makeText(getActivity(), "不能和自己聊天", Toast.LENGTH_SHORT).show();
				else {
					// 进入聊天页面
					Intent intent = new Intent(getActivity(), ChatActivity.class);
					EMContact emContact = null;
					groups = EMGroupManager.getInstance().getAllGroups();
					for (EMGroup group : groups) {
						if (group.getGroupId().equals(username)) {
							emContact = group;
							break;
						}
					}
					if (emContact != null && emContact instanceof EMGroup) {
						// it is group chat
						intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
						intent.putExtra("groupId", ((EMGroup) emContact).getGroupId());
					} else {
						// it is single chat
						intent.putExtra("userName", username);
					}
					startActivity(intent);
				}
			}
		});
		// 注册上下文菜单
		registerForContextMenu(listView);

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getActivity().getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
//		// 搜索框
//		query = (EditText) getView().findViewById(R.id.query);
//		// 搜索框中清除button
//		clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
//		query.addTextChangedListener(new TextWatcher() {
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//				adapter.getFilter().filter(s);
//				if (s.length() > 0) {
//					clearSearch.setVisibility(View.VISIBLE);
//				} else {
//					clearSearch.setVisibility(View.INVISIBLE);
//				}
//			}
//
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//			}
//
//			public void afterTextChanged(Editable s) {
//			}
//		});
//		clearSearch.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				query.getText().clear();
//
//			}
//		});
		setNoChatBackGround();
	}

	//设置没有会话时的背景
	private void setNoChatBackGround() {
		// TODO Auto-generated method stub
		if(adapter != null && adapter.getCount() == 0){
			noChatImageView.setVisibility(View.VISIBLE);
		}else{
			noChatImageView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
		getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_message) {
			EMConversation tobeDeleteCons = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 删除此会话
			EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(),tobeDeleteCons.isGroup());
			InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
			inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
			adapter.remove(tobeDeleteCons);
			adapter.notifyDataSetChanged();
			setNoChatBackGround();

			// 更新消息未读数
			((MainActivity) getActivity()).updateUnreadLabel();

			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		Log.i("ChatAllHistoryFragment", "ChatAllHistoryFragment刷新了");
//		adapter = new ChatAllHistoryAdapter(getActivity(), R.layout.row_chat_history, loadConversationsWithRecentChat());
//		listView.setAdapter(adapter);
		conversationDataList = loadConversationsWithRecentChat();
		adapter.notifyDataSetChanged();
		setNoChatBackGround();
	}

	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return
	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		List<EMConversation> conversationList = new ArrayList<EMConversation>();
		//过滤掉messages seize为0的conversation
		for(EMConversation conversation : conversations.values()){
			if(conversation.getAllMessages().size() != 0)
				conversationList.add(conversation);
		}
		// 排序
		sortConversationByLastChatTime(conversationList);
		return conversationList;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(List<EMConversation> conversationList) {
		Collections.sort(conversationList, new Comparator<EMConversation>() {
			@Override
			public int compare(final EMConversation con1, final EMConversation con2) {

				EMMessage con2LastMessage = con2.getLastMessage();
				EMMessage con1LastMessage = con1.getLastMessage();
				Log.i("ChatAllHistoryFragment",con1.getUserName()+"#####"+con2.getUserName());
				Log.i("ChatAllHistoryFragment",con1LastMessage+"=="+con2LastMessage);
				if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
					return 0;
				} else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden && isInitialized) {
			Log.i("ChatAllHistoryFragment", "HiddenChanged");
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(App.isLogin() && !isInitialized){
			Log.i("ChatAllHistoryFragment", "initView");
			initView();
		}
		if (!hidden && isInitialized) {
			refresh();
		}
	}

	public boolean isInitialized() {
		return isInitialized;
	}
	
}
