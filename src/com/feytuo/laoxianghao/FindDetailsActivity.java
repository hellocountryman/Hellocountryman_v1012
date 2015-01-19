package com.feytuo.laoxianghao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.feytuo.laoxianghao.adapter.FindListViewAdapter;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.feytuo.listviewonload.SimpleFooter;
import com.feytuo.listviewonload.SimpleHeader;
import com.feytuo.listviewonload.ZrcListView;
import com.feytuo.listviewonload.ZrcListView.OnStartListener;
import com.umeng.analytics.MobclickAgent;

public class FindDetailsActivity extends Activity {

	private FindListViewAdapter adapter;
	private ZrcListView findListView;
	private Handler handler;
	private List<Map<String, Object>> listItems;
	private List<Map<String, Object>> tempListItems;
	private List<Invitation> listData;
	private TextView findTypeText;
	private int curPage = 0;// 当前页的编号，从0开始
	private int type;// 帖子类型
	private Button findPublishBtn;// 在find中发布帖子

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_details_activity);

		initview();
		initlistview();
		getListDataFromLocal();
		findListView.refresh();
	}

	public void initview() {
		findTypeText = (TextView) findViewById(R.id.find_type_text);
		findPublishBtn = (Button) findViewById(R.id.find_publish_btn);

		//判断是find的那一个版块
		type = getIntent().getIntExtra("type", 0);
		if (type == 1) {
			findTypeText.setText("方言话题");
			findPublishBtn.setVisibility(View.GONE);// 方言话题中不能有发布
		} else if (type == 2) {
			findTypeText.setText("方言段子");
		} else if (type == 3) {
			findTypeText.setText("方言KTV");
		} else if (type == 4) {
			findTypeText.setText("方言秀场");
		} else {
			findTypeText.setText("发方言");
		}

		//不同的发布type
		findPublishBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(FindDetailsActivity.this, PublishActivity.class);
				switch (v.getId()) {
				case R.id.find_publish_btn:
					if (type == 2) {
						intent.putExtra("type", 2);
					} else if (type == 3) {
						intent.putExtra("type", 3);
					} else if (type == 4) {
						intent.putExtra("type", 4);
					}
					startActivity(intent);
					break;
				}
			}
		});
	}

	public void findDetailsReturnBtn(View v) {
		finish();
	}

	// 从本地数据库获取数据
	private void getListDataFromLocal() {
		// TODO Auto-generated method stub
		listData = new InvitationDao(FindDetailsActivity.this)
				.getInvitationFromClass(type);
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

	public void initlistview() {
		findListView = (ZrcListView) findViewById(R.id.find_listview);
		handler = new Handler();

		// 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
		float density = getResources().getDisplayMetrics().density;
		findListView.setFirstTopOffset((int) (50 * density));

		// 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
		SimpleHeader header = new SimpleHeader(this);
		header.setTextColor(getResources().getColor(R.color.indexbg));
		header.setCircleColor(getResources().getColor(R.color.indexbg));
		findListView.setHeadable(header);

		// 设置加载更多的样式（可选）
		SimpleFooter footer = new SimpleFooter(this);
		footer.setCircleColor(getResources().getColor(R.color.indexbg));
		findListView.setFootable(footer);

		// 设置列表项出现动画（可选）
		findListView.setItemAnimForTopIn(R.anim.topitem_in);
		findListView.setItemAnimForBottomIn(R.anim.bottomitem_in);

		// 下拉刷新事件回调（可选）
		findListView.setOnRefreshStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				refresh();
			}
		});

		// 加载更多事件回调（可选）
		findListView.setOnLoadMoreStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				loadMore();
			}
		});

		listItems = new ArrayList<Map<String, Object>>();
		tempListItems = new ArrayList<Map<String, Object>>();
		adapter = new FindListViewAdapter(FindDetailsActivity.this, listItems,
				R.layout.index_listview, new String[] { "position", "words",
						"time", "praise_num", "comment_num" }, new int[] {
						R.id.index_locals_country, R.id.index_text_describe,
						R.id.index_locals_time, R.id.index_support_num,
						R.id.index_comment_num });
		findListView.setLayoutAnimation(getListAnim());
		findListView.setAdapter(adapter);
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
				new GetClassifyInvitation().execute(1,true);
			}
		}, 2 * 1000);
	}

	// 加载更多事件回调
	private void loadMore() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				adapter.stopAudio();
				new GetClassifyInvitation().execute(curPage,false);

			}
		}, 2 * 1000);
	}

	/**
	 * 获取首页帖子
	 *params 0代表page，1代表是否刷新（否加载更多）
	 */
	class GetClassifyInvitation extends AsyncTask<Object, Void, Integer>{

		private boolean isRefresh = true;
		private List<Invitation> invs;
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			isRefresh = (boolean)params[1];
			String param = App.getCommonParams()
					+"&page="+(int)params[0]
					+"&type="+type;
			SyncHttp client = new SyncHttp(Global.GET_CLASSIFY_INVITATION);
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
				// 存入本地数据库
				InvitationDao invDao = new InvitationDao(FindDetailsActivity.this);
				if (isRefresh) {
					invDao.insert2InvitationClass(invs, type, false);
					findListView.setRefreshSuccess("加载成功");
					findListView.startLoadMore(); // 开启LoadingMore功能
				} else{//加载更多
					invDao.insert2InvitationClass(invs, type, true);
					findListView.setLoadMoreSuccess();
				}
				if (invs.size() == 0) {
					if (isRefresh) {//该版块没有一点内容
						findListView.setRefreshSuccess("暂无更新");
					} else {//没有更多内容
						findListView.stopLoadMore();// 关闭上拉加载的功能
					}
				}
			}
				break;
			case Global.code.FAILURE: {
			}
				break;
			case Global.code.ERROR: {
				findListView.setRefreshFail("刷新失败，请检查网络...");
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
			if (findListView != null) {
				findListView.refresh();
			}
			App.pre.edit().putBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, false)
					.commit();
		}
		MobclickAgent.onPageStart("FindDetailsActivity"); // 友盟统计页面
		MobclickAgent.onResume(FindDetailsActivity.this);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		MobclickAgent.onPageEnd("FindDetailsActivity");// 友盟保证 onPageEnd 在onPause
												// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(FindDetailsActivity.this);
	}

}
