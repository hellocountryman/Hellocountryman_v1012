package com.feytuo.chat.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feytuo.laoxianghao.R;
import com.umeng.analytics.MobclickAgent;

@SuppressLint({ "ResourceAsColor", "CutPasteId" })
public class ChatAndContactFragment extends Fragment {

	private final String TAG = "ChatAndContactFragment";
	private Fragment[] fragments;
	private ContactlistFragment contactListFragment;
	private ChatAllHistoryFragment chatHistoryFragment;

	private ViewPager viewPager;
	private ImageView cursorImage;
	private Button addContactView;// 添加好友
	private Button btnConversation;// 消息按钮
	private Button btnAddressList;// 好友消息按钮

	private RelativeLayout cursorConversationBtn;
	private RelativeLayout cursorFriendBtn;
	// 未读消息textview
	private TextView unreadLabel;
	// 未读通讯录textview
	private TextView unreadAddressLable;

	private int cursorOffset;// 每一格偏移量
	private int currentOffset;// 当前总偏移量
	private int currentTabInCAC;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_contact_viewpager,
				container, false);
		// 初始化viewpager
		initViewPager(view);
		// 初始化view
		initView(view);
		// 初始化滑动条
		initCursor(view);
		return view;
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i(TAG, "----->onStop");
		super.onStop();
	}
	private void initViewPager(View view) {
		// TODO Auto-generated method stub
		chatHistoryFragment = new ChatAllHistoryFragment();
		contactListFragment = new ContactlistFragment();
		fragments = new Fragment[] { chatHistoryFragment, contactListFragment };
		viewPager = (ViewPager) view.findViewById(R.id.contact_viewpager);
		viewPager.setAdapter(new MyFragmentPagerAdapter(
				getChildFragmentManager()));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			private int currentPager;

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if(arg0 == 0){
					leftcolor();
				}else{
					rightcolor();
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				if (currentPager != arg0) {
					currentOffset = arg0 * cursorOffset;
				} else {
					// 图片移动偏移量
//					Log.i("ChatAndContactFragment", "当前移动总量："
//							+ (currentOffset + cursorOffset * arg1));
					final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cursorImage
							.getLayoutParams();
					if (arg0 == 0 && arg2 == 0) {
						params.setMargins((int) (currentOffset + cursorOffset
								* arg1), 0, 0, 0);
					} else {
						params.setMargins((int) (currentOffset + cursorOffset
								* arg1) + 1, 0, 0, 0);
					}
					// 首次加载后不会刷新，必须强制放到ui线程刷新ui
					cursorImage.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							cursorImage.setLayoutParams(params);
						}
					});
				}
				currentPager = arg0;
				currentTabInCAC = arg0;
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@SuppressLint("ResourceAsColor")
	private void initView(View view) {
		// TODO Auto-generated method stub
		btnConversation = (Button) view.findViewById(R.id.btn_conversation);
		btnAddressList = (Button) view.findViewById(R.id.btn_friends_list);

		cursorImage = (ImageView) view.findViewById(R.id.cursor);
		unreadLabel = (TextView) view.findViewById(R.id.unread_msg_number);
		unreadAddressLable = (TextView) view
				.findViewById(R.id.unread_address_number);
		cursorConversationBtn = (RelativeLayout) view
				.findViewById(R.id.btn_container_conversation);
		cursorFriendBtn = (RelativeLayout) view
				.findViewById(R.id.btn_container_address_list);
		addContactView = (Button) view
				.findViewById(R.id.fragment_contact_viewpager_iv_new_contact);
		// 进入添加好友页
		addContactView.setOnClickListener(listener);
		cursorConversationBtn.setOnClickListener(listener);
		cursorFriendBtn.setOnClickListener(listener);
	}

	/**
	 * viewpager在左边的时候
	 */
	private void leftcolor() {
		btnConversation.setTextColor(getResources().getColor(R.color.indexbg));
		btnAddressList.setTextColor(getResources().getColor(R.color.grey));
	}

	/*
	 * viewpager在右边的时候
	 */
	private void rightcolor() {
		btnConversation.setTextColor(getResources().getColor(R.color.grey));
		btnAddressList.setTextColor(getResources().getColor(R.color.indexbg));
	}

	private void initCursor(View view) {
		// TODO Auto-generated method stub
		// 获取屏幕分辨率宽度
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		cursorOffset = screenW / 2;

		LayoutParams params = cursorImage.getLayoutParams();
		params.width = cursorOffset;
		cursorImage.setLayoutParams(params);
		currentTabInCAC = 0;
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_container_conversation: // 会话按钮
				leftcolor();
				setCursor(viewPager.getCurrentItem(), 0);
				viewPager.setCurrentItem(0, false);
				currentTabInCAC = 0;
				break;
			case R.id.btn_container_address_list:// 好友列表按钮
				rightcolor();
				setCursor(viewPager.getCurrentItem(), 1);
				viewPager.setCurrentItem(1, false);
				currentTabInCAC = 1;
				break;
			case R.id.fragment_contact_viewpager_iv_new_contact:// 好友列表按钮
				startActivity(new Intent(getActivity(),
						AddContactActivity.class));
				break;
			}
		}
	};

	class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return fragments[arg0];
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragments.length;
		}
	}

	/**
	 * 点击时滑动块移动
	 * 
	 * @param currentNum
	 * 
	 * @param targetNum
	 */
	private void setCursor(int currentNum, int targetNum) {
		int offsetNum = 0;
		offsetNum = targetNum - currentNum;
		currentOffset = currentOffset + offsetNum * cursorOffset;
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cursorImage
				.getLayoutParams();
		params.setMargins(currentOffset, 0, 0, 0);
		cursorImage.requestLayout();
	}

	public int getCurrentTabInCAC() {
		return currentTabInCAC;
	}

	public ChatAllHistoryFragment getChatHistoryFragment() {
		return chatHistoryFragment;
	}

	public ContactlistFragment getContactListFragment() {
		return contactListFragment;
	}

	public TextView getUnreadLabel() {
		return unreadLabel;
	}

	public TextView getUnreadAddressLable() {
		return unreadAddressLable;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("ChatAndContactFragment"); // 统计页面
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("ChatAndContactFragment");
	}

}
