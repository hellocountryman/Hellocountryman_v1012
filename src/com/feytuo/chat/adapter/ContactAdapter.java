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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.feytuo.chat.Constant;
import com.feytuo.chat.domain.User;
import com.feytuo.chat.widget.Sidebar;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.ImageLoader;

/**
 * 简单的好友Adapter实现
 *
 */
public class ContactAdapter extends ArrayAdapter<User>  implements SectionIndexer{

	private final String TAG = "ContactAdapter";
	private LayoutInflater layoutInflater;
	private EditText query;
	private ImageButton clearSearch;
	private SparseIntArray positionOfSection;
	private SparseIntArray sectionOfPosition;
	private Sidebar sidebar;
	private int res;
	private List<User> data;
	private List<User> mOriginalValues;
	
	private ImageLoader mImageLoader;

	public ContactAdapter(Context context, int resource, List<User> objects,Sidebar sidebar) {
		super(context, resource, objects);
		this.res = resource;
		this.sidebar=sidebar;
		layoutInflater = LayoutInflater.from(context);
		mImageLoader = new ImageLoader(context);
		this.data = objects;
		mOriginalValues = new ArrayList<User>();
		for(User user : data){
			mOriginalValues.add(user);
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return position == -1 ? 0 : 1;//隐藏，如果像是搜索框与0比较
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == -1) {//隐藏搜索框，如果现实position = 0
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.search_bar_with_padding, null);
				query = (EditText) convertView.findViewById(R.id.query);
				clearSearch = (ImageButton) convertView.findViewById(R.id.search_clear);
				query.addTextChangedListener(new TextWatcher() {
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						getFilter().filter(s);
						if (s.length() > 0) {
							clearSearch.setVisibility(View.VISIBLE);
							if (sidebar != null)
								sidebar.setVisibility(View.GONE);
						} else {
							clearSearch.setVisibility(View.INVISIBLE);
							if (sidebar != null)
								sidebar.setVisibility(View.VISIBLE);
						}
					}
	
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
	
					public void afterTextChanged(Editable s) {
					}
				});
				clearSearch.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						if (((Activity) getContext()).getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
							if (((Activity) getContext()).getCurrentFocus() != null)
							manager.hideSoftInputFromWindow(((Activity) getContext()).getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
						//清除搜索框文字
						query.getText().clear();
					}
				});
			}
		}else{
			if(convertView == null){
				convertView = layoutInflater.inflate(res, null);
			}
			
			ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);
			TextView unreadMsgView = (TextView) convertView.findViewById(R.id.unread_msg_number);
			TextView nameTextview = (TextView) convertView.findViewById(R.id.name);
			TextView tvHeader = (TextView) convertView.findViewById(R.id.header);
			User user = getItem(position);
			if(user == null)
				Log.d("ContactAdapter", position + "");
			//设置nick，demo里不涉及到完整user，用username代替nick显示
			String username = user.getUsername();
			String header = user.getHeader();
			if (position == 0 || header != null && !header.equals(getItem(position - 1).getHeader())) {
				if ("".equals(header)) {
					tvHeader.setVisibility(View.GONE);
				} else {
					tvHeader.setVisibility(View.VISIBLE);
					tvHeader.setText(header);
				}
			} else {
				tvHeader.setVisibility(View.GONE);
			}
			//显示申请与通知item
			if(username.equals(Constant.NEW_FRIENDS_USERNAME)){
				nameTextview.setText(user.getNick());
				avatar.setImageResource(R.drawable.new_friends_icon);
				if(user.getUnreadMsgCount() > 0){
					unreadMsgView.setVisibility(View.VISIBLE);
					unreadMsgView.setText(user.getUnreadMsgCount()+"");
				}else{
					unreadMsgView.setVisibility(View.INVISIBLE);
				}
			}else if(username.equals(Constant.GROUP_USERNAME)){
				//群聊item
				nameTextview.setText(user.getNick());
				avatar.setImageResource(R.drawable.groups_icon);
			}else{
				if(unreadMsgView != null)
					unreadMsgView.setVisibility(View.INVISIBLE);
				Log.i("ContactAdapter", "昵称："+user.getNickName());
				if(user.getNickName() != null){
					nameTextview.setText(user.getNickName());
				}else{
					nameTextview.setText(user.getUsername());
				}
				avatar.setImageResource(R.drawable.default_avatar);
				mImageLoader.loadImage(Global.FILE_HOST+user.getHeadUrl(), this, avatar);
			}
		}
		
		return convertView;
	}
	
	@Override
	public User getItem(int position) {
//		return position == 0 ? new User() : data.get(position - 1);现实搜索框
		return data.get(position);//隐藏搜索框
	}
	
	@Override
	public int getCount() {
//		return data.size() + 1;有搜索框，count+1
		return data.size();
	}

	public int getPositionForSection(int section) {
		return positionOfSection.get(section);
	}

	public int getSectionForPosition(int position) {
		return sectionOfPosition.get(position);
	}

	@Override
	public Object[] getSections() {
		positionOfSection = new SparseIntArray();
		sectionOfPosition = new SparseIntArray();
		int count = getCount();
		List<String> list = new ArrayList<String>();
		list.add(getContext().getString(R.string.search_header));
		positionOfSection.put(0, 0);
		sectionOfPosition.put(0, 0);
		for (int i = 1; i < count; i++) {

			String letter = getItem(i).getHeader();
			System.err.println("contactadapter getsection getHeader:" + letter + " name:" + getItem(i).getUsername());
			int section = list.size() - 1;
			if (list.get(section) != null && !list.get(section).equals(letter)) {
				list.add(letter);
				section++;
				positionOfSection.put(section, i);
			}
			sectionOfPosition.put(i, section);
		}
		return list.toArray(new String[list.size()]);
	}

	public Filter getFilter() {
		Filter filter = new Filter() {
			
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				// TODO Auto-generated method stub
				data = (ArrayList<User>) results.values;
				notifyDataSetChanged();
			}
			
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// TODO Auto-generated method stub
				FilterResults results = new FilterResults();
				List<User> FilteredArrList = new ArrayList<User>();

                if (mOriginalValues == null) {
                	mOriginalValues = new ArrayList<User>(); // saves the original data in mOriginalValues
                }

                /********
                 * 
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)  
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return  
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        User user = mOriginalValues.get(i);
                        String words;
                        if(user.getNickName() != null){
                        	words = user.getNickName().toString();
                        }else {
                        	words = user.getUsername().toString();
                        }
                        if (words.toLowerCase().contains(constraint.toString())) {
                        	FilteredArrList.add(user);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
				return results;
			}
		};
		return filter;
	};
}
