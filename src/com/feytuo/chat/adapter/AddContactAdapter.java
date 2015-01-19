package com.feytuo.chat.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.util.HanziToPinyin;
import com.feytuo.chat.Constant;
import com.feytuo.chat.db.UserDao;
import com.feytuo.chat.domain.User;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.ImageLoader;
import com.feytuo.laoxianghao.view.OnloadDialog;

public class AddContactAdapter extends BaseAdapter{

	private List<LXHUser> data;
	private Context context;
	private ImageLoader mImageLoader;
	private OnloadDialog pd;
	public AddContactAdapter(Context context,List<LXHUser> listData) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.data = listData;
		mImageLoader = new ImageLoader(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.add_contact_list_item, null);
			holder.head = (ImageView)convertView.findViewById(R.id.add_contact_avatar);
			holder.nickName = (TextView)convertView.findViewById(R.id.add_contact_nickname);
			holder.add = (Button)convertView.findViewById(R.id.add_contact_btn);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.nickName.setText(data.get(position).getNickName());
		mImageLoader.loadImage(Global.FILE_HOST+data.get(position).getHeadUrl(), this, holder.head);
		holder.add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addContact(data.get(position));
			}
		});
		return convertView;
	}
	
	class ViewHolder {
		private ImageView head;
		private TextView nickName;
		private Button add;
	}

	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(final LXHUser user){
		/********domain***********/
//		if(App.getInstance().getUserName().equals(user.getObjectId())){
//			context.startActivity(new Intent(context, AlertDialog.class).putExtra("msg", "不能添加自己"));
//			return;
//		}
//		
//		if(App.getInstance().getContactList().containsKey(user.getObjectId())){
//			context.startActivity(new Intent(context, AlertDialog.class).putExtra("msg", "此用户已是你的好友"));
//			return;
//		}
		
		pd = new OnloadDialog(context);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		pd.setMessage("正在发送请求...");
		
		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo写死了个reason，实际应该让用户手动填入
					EMContactManager.getInstance().addContact(user.getuName(), "加个好友呗");
					//将添加的好友持久到本地数据库
					addToLocalDB(user.getuName(),user.getNickName(),user.getHeadUrl());
					((Activity)context).runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(context, "成功添加"+user.getNickName(), Toast.LENGTH_SHORT).show();
						}
					});
				} catch (final Exception e) {
					((Activity)context).runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(context, "添加好友失败,请稍候再试...", Toast.LENGTH_SHORT).show();
							Log.i("AddContactActivity","添加好友失败：" + e.getMessage());
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
		UserDao userDao = new UserDao(context);
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
}
