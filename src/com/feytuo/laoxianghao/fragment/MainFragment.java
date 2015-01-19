package com.feytuo.laoxianghao.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.SelsectedCountry;
import com.feytuo.laoxianghao.adapter.ListViewAdapter;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.feytuo.listviewonload.SimpleFooter;
import com.feytuo.listviewonload.SimpleHeader;
import com.feytuo.listviewonload.ZrcListView;
import com.feytuo.listviewonload.ZrcListView.OnStartListener;
import com.umeng.analytics.MobclickAgent;

public class MainFragment extends Fragment {

	private ListViewAdapter adapter;
	private ZrcListView indexListView;
	private ImageView noInvitationImageView;
	private Handler handler;
	private Button indexSelectCity;// 选择城市
	private List<Map<String, Object>> listItems;
	private List<Map<String, Object>> tempListItems;
	private List<Invitation> listData;
	private Invitation topicInvitation;

	private int curPage = 1;// 当前页的编号，从1开始

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		//如果已经进入主界面证明下次进入非第一次进入，设置首次标记
		App.pre.edit().putBoolean(Global.IS_FIRST_USE, false).commit();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initview();
		initlistview();
		// 先从本地加载数据
		getListDataFromLocal();
		indexListView.refresh();
		super.onActivityCreated(savedInstanceState);
	}

	// 从本地数据库获取数据
	private void getListDataFromLocal() {
		// TODO Auto-generated method stub
		InvitationDao invDao = new InvitationDao(getActivity());
		listData = invDao.getAllInfo(App.pre.getInt(Global.CURRENT_NATIVE, 0));
		invDao.setTypeInvitationFromClass(topicInvitation, 1);// 获取本地保存的最新的一条话题
		for (Invitation inv : listData) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("inv_id", inv.getInvId());
			map.put("uid", inv.getuId());
			map.put("home", inv.getHome());
			map.put("position", inv.getPosition());
			map.put("words", inv.getWords());
			map.put("voice", inv.getVoice());
			map.put("voice_duration", inv.getVoiceDuration());
			map.put("time", inv.getTime());
			map.put("ishot", inv.getIsHot());
			map.put("praise_num", inv.getPraiseNum());
			map.put("comment_num", inv.getCommentNum());
			map.put("type", inv.getType());
			listItems.add(map);
		}
		adapter.notifyDataSetChanged();
	}

	public void initview() {
		noInvitationImageView = (ImageView)getView().findViewById(R.id.main_no_invitation_img);
		indexSelectCity = (Button) getActivity().findViewById(
				R.id.index_select_city);
		indexSelectCity.setOnClickListener(new listener());
		indexListView = (ZrcListView) getActivity().findViewById(
				R.id.index_listview);

	}
	private void setNoInvitationBackGround(){
		if(adapter != null && adapter.getCount() == 0){
			noInvitationImageView.setVisibility(View.VISIBLE);
		}else{
			noInvitationImageView.setVisibility(View.GONE);
		}
	}

	 class listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.index_select_city:
				Intent intentselsectcity = new Intent();
				intentselsectcity.putExtra("isfromtocity", 1);// 判断是从那里进入的城市选择
				intentselsectcity.setClass(getActivity(), SelsectedCountry.class);
				startActivity(intentselsectcity);
				break;
			default:
				break;
			}
		}
		
	}

	public void initlistview() {
		handler = new Handler();

		// 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
		float density = getResources().getDisplayMetrics().density;
		indexListView.setFirstTopOffset((int) (50 * density));

		// 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
		SimpleHeader header = new SimpleHeader(getActivity());
		header.setTextColor(getResources().getColor(R.color.indexbg));
		header.setCircleColor(getResources().getColor(R.color.indexbg));
		indexListView.setHeadable(header);

		// 设置加载更多的样式（可选）
		SimpleFooter footer = new SimpleFooter(getActivity());
		footer.setCircleColor(getResources().getColor(R.color.indexbg));
		indexListView.setFootable(footer);

		// 设置列表项出现动画（可选）
		indexListView.setItemAnimForTopIn(R.anim.topitem_in);
		indexListView.setItemAnimForBottomIn(R.anim.bottomitem_in);

		// 下拉刷新事件回调（可选）
		indexListView.setOnRefreshStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				refresh();
			}
		});

		// 加载更多事件回调（可选）
		indexListView.setOnLoadMoreStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				loadMore();
			}
		});

		topicInvitation = new Invitation();
		listItems = new ArrayList<Map<String, Object>>();
		tempListItems = new ArrayList<Map<String, Object>>();
		adapter = new ListViewAdapter(getActivity(), listItems, topicInvitation);
		indexListView.setLayoutAnimation(getListAnim());
		indexListView.setAdapter(adapter);
	}

	/**
	 * 
	 * 加载listview初始动画
	 * 
	 * @tangpeng
	 */
	private LayoutAnimationController getListAnim() {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(300);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(500);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		return controller;
	}

	// 下拉刷新事件回调
	private void refresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				adapter.stopAudio();
				new GetLatestTopic().execute();
				new GetMainInvitation().execute(1,true);
			}
		}, 2 * 1000);
	}

	// 加载更多事件回调
	private void loadMore() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				adapter.stopAudio();
				new GetMainInvitation().execute(curPage,false);

			}
		}, 2 * 1000);
	}

	/**
	 * 获取最新话题
	 */
	class GetLatestTopic extends AsyncTask<Object, Void, Integer>{

		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String param = App.getCommonParams();
			SyncHttp client = new SyncHttp(Global.GET_LATEST_TOPIC);
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
				adapter.notifyDataSetChanged();
			}
				break;
			case Global.code.FAILURE: {
			}
				break;
			case Global.code.ERROR: {
				indexListView.setRefreshFail("刷新失败，请检查网络...");
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
			try {
				JSONObject itemObject = jsonObject.getJSONObject("data");
				topicInvitation.setInvId(itemObject.getInt("inv_id"));
				topicInvitation.setuId(itemObject.getInt("u_id"));
				topicInvitation.setHome(itemObject.getInt("home"));
				topicInvitation.setPosition(itemObject.getString("position"));
				topicInvitation.setWords(itemObject.getString("words"));
				topicInvitation.setVoice(itemObject.getString("voice"));
				topicInvitation.setVoiceDuration(itemObject.getInt("voice_duration"));
				topicInvitation.setTime(itemObject.getString("time"));
				topicInvitation.setIsHot(itemObject.getInt("is_hot"));
				topicInvitation.setPraiseNum(itemObject.getInt("praise_num"));
				topicInvitation.setCommentNum(itemObject.getInt("comment_num"));
				topicInvitation.setType(itemObject.getInt("type"));
				// 存入本地数据库
				InvitationDao inv = new InvitationDao(getActivity());
				List<Invitation> list = new ArrayList<Invitation>();
				list.add(topicInvitation);
				inv.insert2InvitationClass(list, 1, false);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取首页帖子
	 *params 0代表page，1代表是否刷新（否加载更多）
	 */
	class GetMainInvitation extends AsyncTask<Object, Void, Integer>{

		private boolean isRefresh = true;
		private List<Invitation> invs;
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			isRefresh = (boolean)params[1];
			String param = App.getCommonParams()
					+"&home="+App.pre.getInt(Global.CURRENT_NATIVE, 0)
					+"&page="+(int)params[0]
					+"&u_sex=男"
					+"&u_pre_age="+0
					+"&u_post_age="+0;
			SyncHttp client = new SyncHttp(Global.GET_MAIN_INVITATION);
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
				if (isRefresh) {
					curPage = 1;
					listItems.clear();
				}
				// 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
				curPage++;
				listItems.addAll(tempListItems);
				adapter.notifyDataSetChanged();
				setNoInvitationBackGround();
				// 存入本地数据库
				InvitationDao inv = new InvitationDao(getActivity());
				if (isRefresh) {
					inv.insert2Invitation(invs, false);
					indexListView.setRefreshSuccess("加载成功");
					indexListView.startLoadMore(); // 开启LoadingMore功能
				} else{//加载更多
					inv.insert2Invitation(invs, true);
					indexListView.setLoadMoreSuccess();
				}
				if (invs.size() == 0) {
					if (isRefresh) {//该版块没有一点内容
						indexListView.setRefreshSuccess("暂无更新");
					} else {//没有更多内容
						indexListView.stopLoadMore();// 关闭上拉加载的功能
					}
				}
			}
				break;
			case Global.code.FAILURE: {
			}
				break;
			case Global.code.ERROR: {
				indexListView.setRefreshFail("刷新失败，请检查网络...");
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
			invs = new ArrayList<Invitation>();
			tempListItems.clear();
			if(jsonObject.isNull("data")){
				return ;
			}
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
					invs.add(inv);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 是否需要更新列表
		if (App.pre.getBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, false)) {
			if (indexListView != null) {
				indexListView.setAdapter(adapter);
				indexListView.refresh();
			}
			App.pre.edit().putBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, false)
					.commit();
		}
		MobclickAgent.onPageStart("MainFragment"); // 友盟统计页面
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		MobclickAgent.onPageEnd("MainFragment");// 友盟保证 onPageEnd 在onPause
												// 之前调用,因为 onPause 中会保存信息
	}

}
