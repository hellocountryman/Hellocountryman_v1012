package com.feytuo.laoxianghao.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.adapter.NoticeListViewAdapter;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.ScreenUtils;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.umeng.analytics.MobclickAgent;

public class MyInvitationFragment extends Fragment {

	private ListView mymessageListview;
	private NoticeListViewAdapter adapter;
	private List<Map<String, Object>> listItems;
	private List<Map<String, Object>> tempListItems;
	private List<Map<String, Object>> localListItems;
	private List<Invitation> listData;
	public View rootView;
	private SparseArray<Boolean> commentMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		commentMap = new SparseArray<>();
		rootView = inflater.inflate(R.layout.fragment1, container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initView();
		getListDataFromLocal();
		super.onActivityCreated(savedInstanceState);
	}

	public void initView() {
		mymessageListview = (ListView) getActivity().findViewById(
				R.id.message_listview);

		listItems = new ArrayList<Map<String, Object>>();
		tempListItems = new ArrayList<Map<String, Object>>();
		localListItems = new ArrayList<Map<String, Object>>();
		adapter = new NoticeListViewAdapter(getActivity(), listItems,
				R.layout.index_listview, new String[] { "position",
						"words", "time", "praise_num", "comment_num" },
				new int[] { R.id.index_locals_country,
						R.id.index_text_describe, R.id.index_locals_time,
						R.id.index_support_num, R.id.index_comment_num }, this);

		mymessageListview.setLayoutAnimation(ScreenUtils.getListAnim());// 一个简单的动画效果
		mymessageListview.setAdapter(adapter);
	}

	// 从本地数据库获取数据
	private void getListDataFromLocal() {
		Log.i("Fragment1", "load localdata");
		// TODO Auto-generated method stub
		listData = new InvitationDao(getActivity()).getAllInfoFromMy();
		for (Invitation inv : listData) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("inv_id", inv.getInvId());
			map.put("position", inv.getPosition());
			map.put("time", inv.getTime());
			map.put("words", inv.getWords());
			map.put("voice_duration", inv.getVoiceDuration());
			map.put("praise_num", inv.getPraiseNum());
			map.put("comment_num", inv.getCommentNum());
			map.put("voice", inv.getVoice());
			map.put("ishot", inv.getIsHot());
			map.put("uid", inv.getuId());
			map.put("home", inv.getHome());
			map.put("invitation", inv);
			localListItems.add(map);
		}
		listItems.addAll(localListItems);
		adapter.notifyDataSetChanged();
		// 从网络获取更新
		new GetUserInvitation().execute();
	}

	/**
	 * "我的帖子"的评论数是否有更新
	 * @param invList 
	 * 
	 * @return
	 */
	private void setMyCommentNotice(List<Invitation> invList) {
		if(localListItems.size() > 0){
			final List<Integer> localCommentNum = new ArrayList<Integer>();
			final List<Integer> netCommentNum = new ArrayList<Integer>();
			List<String> invIds = new ArrayList<String>();
			for (Map<String, Object> inv : localListItems) {
				invIds.add(inv.get("inv_id").toString());
				localCommentNum.add((Integer) inv.get("comment_num"));
			}
			for (Invitation inv : invList) {
				netCommentNum.add(inv.getCommentNum());
			}
			// 对比本地和服务器评论数是否有不同
			for (int i = 0; i < netCommentNum.size(); i++) {
				for (int j = 0; j < localListItems.size(); j++) {
					if (invList.get(i).getInvId()==(Integer)localListItems.get(j).get("inv_id")) {
						if (netCommentNum.get(i) != localCommentNum.get(j)) {
							// 添加修改UI代码
							commentMap.put(i, true);
							Log.i("Fragment1", "有更新:");
						} else {
							commentMap.put(i, false);
							Log.i("Fragment1", "无");
						}
						break;
					}
				}
			}
		}
	}
	/**
	 * 获取用户帖子
	 */
	class GetUserInvitation extends AsyncTask<Object, Void, Integer>{
		private List<Invitation> invList;
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String param = "u_id="+App.pre.getInt(Global.UID, -1)
					+"&u_divice_id="+AppInfoUtil.getDeviceId(getActivity())
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
				listItems.clear();
				listItems.addAll(tempListItems);
				adapter.notifyDataSetChanged();
				setMyCommentNotice(invList);
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
			invList = new ArrayList<Invitation>();
			// 存入本地数据库
			InvitationDao invDao = new InvitationDao(getActivity());
			invDao.deleteAllDataInMy();
			tempListItems.clear();
			try {
				JSONArray dataObject = jsonObject.getJSONArray("data");
				Log.i("dataLength", "长度为："+dataObject.length());
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
					
					Invitation inv = new Invitation();
					inv.setInvId(itemObject.getInt("inv_id"));
					inv.setuId(itemObject.getInt("u_id"));
					inv.setHome(itemObject.getInt("home"));
					inv.setPosition(itemObject.getString("position"));
					inv.setWords(itemObject.getString("words"));
					inv.setVoice(itemObject.getString("voice"));
					inv.setVoiceDuration(itemObject.getInt("voice_duration"));
					inv.setTime(itemObject.getString("time"));
					inv.setIsHot(itemObject.getInt("is_hot"));
					inv.setPraiseNum(itemObject.getInt("praise_num"));
					inv.setCommentNum(itemObject.getInt("comment_num"));
					inv.setType(itemObject.getInt("type"));
					invList.add(inv);
					invDao.insert2Invitation(inv);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public SparseArray<Boolean> getCommentMap() {
		return commentMap;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapter.notifyDataSetChanged();
		MobclickAgent.onPageStart("MyInvitationFragment"); // 统计页面
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		MobclickAgent.onPageEnd("MyInvitationFragment");
	}

}
