package com.feytuo.laoxianghao.adapter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.CommentActivity;
import com.feytuo.laoxianghao.FindDetailsActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.UserToPersonActivity;
import com.feytuo.laoxianghao.dao.CityDao;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.dao.PraiseDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.ChangePraiseNumTask;
import com.feytuo.laoxianghao.global.GetUserInfoTask;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.CommonUtils;
import com.feytuo.laoxianghao.util.ImageLoader;
import com.feytuo.laoxianghao.util.NetUtil;
import com.feytuo.laoxianghao.util.StringTools;
import com.feytuo.laoxianghao.view.MyDialog;

/**
 * 
 * @author feytuo
 * 
 */
@SuppressLint({ "HandlerLeak", "UseSparseArrays" })
public class ListViewAdapter extends BaseAdapter {

	private final String TAG = "ListViewAdapter";
	private final int TYPE_1 = 0;
	private final int TYPE_2 = 1;
	private Context context;
	private LayoutInflater m_Inflater;
	private List<Map<String, Object>> list;// 声明List容器对象
	private Invitation topicInv;//话题帖子
	private SparseArray<Boolean> praiseMap; // 标记点赞对象
	private SparseArray<Boolean> collectionMap;// 标记收藏对象
	private SparseArray<Boolean> isAudioPlayArray;// 记录是否正在播放音乐
	private boolean isCurrentItemAudioPlay;

	private LXHUserDao userDao;
	private CityDao cityDao;
	private ImageLoader mImageLoader;

	public ListViewAdapter(Context context, List<Map<String, Object>> data,
			Invitation topicInvitation) {
		this.list = data;
		this.topicInv = topicInvitation;
		this.context = context;
		m_Inflater = LayoutInflater.from(context);
		praiseMap = new SparseArray<>();
		collectionMap = new SparseArray<>();
		isAudioPlayArray = new SparseArray<>();
		userDao = new LXHUserDao(context);
		cityDao = new CityDao(context);
		mImageLoader = new ImageLoader(context);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(list.size() < 3){
			return list.size();
		}else{
			return list.size()+1;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(position < 2){
			return list.get(position);
		}else if(position == 2){
			return topicInv;
		}else{
			return list.get(position-1);
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	// getItemViewType(int) – 根据position返回相应的Item
	public int getItemViewType(int position) {
		int p = position;
		if (p != 2) {
			return TYPE_1;
		} else {//第三个位置
			return TYPE_2;
		}
	}
	// getViewTypeCount() – 该方法返回多少个不同的布局
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	@Override
	// 获取listitem下面的view的值
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder1 = null;
		ViewHolder holder2 = null;
		int type = getItemViewType(position);
		// ViewHolder不是Android的开发API，而是一种设计方法，就是设计个静态类，缓存一下，省得Listview更新的时候，还要重新操作。
		switch(type){
		case TYPE_1:{
			if (convertView == null) {
				holder1 = new ViewHolder();
				convertView = m_Inflater.inflate(R.layout.index_listview, null);
				holder1.indexSupportLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_support_linerlayout);
				holder1.indexCommentLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_comment_linerlayout);
				holder1.indexShareLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_share_linerlayout);
				holder1.indexProgressbarLayout = (RelativeLayout) convertView
						.findViewById(R.id.index_progressbar_layout);
				holder1.indexProgressbarTopImg = (ImageView)convertView.findViewById(R.id.index_progressbar_top_img);
				holder1.indexProgressbarBtn = (ImageButton) convertView
						.findViewById(R.id.index_progressbar_btn);

				holder1.supportImg = (ImageView) convertView
						.findViewById(R.id.support_img);
				holder1.personHeadImg = (ImageButton) convertView
						.findViewById(R.id.index_user_head);
				holder1.personUserNick = (TextView) convertView
						.findViewById(R.id.index_user_nick);
				holder1.home = (TextView) convertView
						.findViewById(R.id.index_home_textview);
				holder1.indexSupportNum = (TextView) convertView
						.findViewById(R.id.index_support_num);
				holder1.indexCommentNum = (TextView) convertView
						.findViewById(R.id.index_comment_num);
				holder1.indexProgressbarTime = (TextView) convertView
						.findViewById(R.id.index_progressbar_time);

				holder1.indexTextDescribe = (TextView) convertView
						.findViewById(R.id.index_text_describe);
				holder1.indexLocalsCountry = (TextView) convertView
						.findViewById(R.id.index_locals_country);
				holder1.indexLocalsTime = (TextView) convertView
						.findViewById(R.id.index_locals_time);
				convertView.setTag(holder1);
				// Tag从本质上来讲是就是相关联的view的额外的信息。它们经常用来存储(set）一些view的数据，用的时候（get）这样做非常方便而不用存入另外的单独结构。
			} else {
				holder1 = (ViewHolder) convertView.getTag();
			}
			/**
			 * 因为话题有可能为不可见，这里需要复原可见
			 */
			if(!convertView.isShown()){
				convertView.setVisibility(View.VISIBLE);
			}
			int listIndex;//数据的下表，当大于2时需要position-1
			if(position < 2){
				listIndex = position;
			}else{
				listIndex = position - 1;
			}
			Listener listener = new Listener(holder1, position,listIndex);
			convertView.setClickable(true);
			convertView.setOnClickListener(listener);
			holder1.personHeadImg.setOnClickListener(listener);
			holder1.indexSupportLinerlayout.setOnClickListener(listener);
			holder1.indexCommentLinerlayout.setOnClickListener(listener);
			holder1.indexShareLinerlayout.setOnClickListener(listener);
			holder1.indexProgressbarLayout.setOnClickListener(listener);
			setSubBtn(holder1, position,listIndex);
			setcontent(holder1, position,listIndex);
			setAudioState(holder1, position,listIndex);
		}
			break;
		case TYPE_2:{
			if(convertView == null){
				holder2 = new ViewHolder();
				convertView = m_Inflater.inflate(R.layout.index_topic_listview, null);
				holder2.indexTopicTopLayout = (RelativeLayout) convertView
						.findViewById(R.id.index_topic_top_layout);
				holder2.indexSupportLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_topic_support_linerlayout);
				holder2.indexCommentLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_topic_comment_linerlayout);
				holder2.indexShareLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_topic_share_linerlayout);
				holder2.indexProgressbarLayout = (RelativeLayout) convertView
						.findViewById(R.id.index_topic_progressbar_layout);
				holder2.indexProgressbarTopImg = (ImageView)convertView.findViewById(R.id.index_topic_progressbar_top_img);
				holder2.indexProgressbarBtn = (ImageButton) convertView
						.findViewById(R.id.index_topic_progressbar_btn);
				
				holder2.supportImg = (ImageView) convertView
						.findViewById(R.id.support_topic_img);
				holder2.personUserNick = (TextView) convertView
						.findViewById(R.id.index_topic_user_nick);
				holder2.indexSupportNum = (TextView) convertView
						.findViewById(R.id.index_topic_support_num);
				holder2.indexCommentNum = (TextView) convertView
						.findViewById(R.id.index_topic_comment_num);
				holder2.indexProgressbarTime = (TextView) convertView
						.findViewById(R.id.index_topic_progressbar_time);
				
				holder2.indexTextDescribe = (TextView) convertView
						.findViewById(R.id.index_topic_text_describe);
				holder2.indexLocalsTime = (TextView) convertView
						.findViewById(R.id.index_topic_locals_time);
				convertView.setTag(holder2);
			}else{
				holder2 = (ViewHolder)convertView.getTag();
			}
			if(topicInv.getInvId() == 0){
				convertView.setVisibility(View.GONE);
			}else{
				convertView.setVisibility(View.VISIBLE);
				Listener listener = new Listener(holder2, position,-1);
				convertView.setClickable(true);
				convertView.setOnClickListener(listener);
				holder2.indexSupportLinerlayout.setOnClickListener(listener);
				holder2.indexCommentLinerlayout.setOnClickListener(listener);
				holder2.indexShareLinerlayout.setOnClickListener(listener);
				holder2.indexProgressbarLayout.setOnClickListener(listener);
				holder2.indexTopicTopLayout.setOnClickListener(listener);
				setSubBtn(holder2, position,-1);
				setcontent(holder2, position,-1);
				setAudioState(holder2, position,-1);
			}
		}
			break;
		}
//		Log.i(TAG, "convertView:"+convertView);
		return convertView;
	}



	// 根据音乐是否播放设置item
	private void setAudioState(final ViewHolder holder, final int position,int listIndex) {

		holder.indexProgressbarBtn.setBackgroundResource(R.anim.frameanim);// 播放录音的动画
		animationDrawable = (AnimationDrawable) holder.indexProgressbarBtn
				.getBackground();

		if (!isAudioPlayArray.get(position, false)) {// 没有播放的
			if (mHolder != null && mHolder.equals(holder)) {
				isCurrentItemAudioPlay = false;
				if(getItemViewType(position) == TYPE_1){
					holder.indexProgressbarTime.setText((Integer) list
							.get(listIndex).get("voice_duration") + "\"");
				}else{
					holder.indexProgressbarTime.setText(topicInv.getVoiceDuration() + "\"");
				}
				animationDrawable.stop();
				 holder.indexProgressbarBtn.setBackgroundResource(R.drawable.musicplayone);
			}
		} else {// 正在播放的
			isCurrentItemAudioPlay = true;
			animationDrawable.start();
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void setcontent(ViewHolder holder, int position,int listIndex) {
		// TODO Auto-generated method stub
		if(getItemViewType(position) == TYPE_1){
			//录音模块是否可见
			if(list.get(listIndex).get("voice") == null 
					|| TextUtils.isEmpty(list.get(listIndex).get("voice").toString())
					|| list.get(listIndex).get("voice").toString().equals("null")){
				holder.indexProgressbarLayout.setVisibility(View.GONE);
				holder.indexProgressbarTopImg.setVisibility(View.GONE);
			}else{
				holder.indexProgressbarLayout.setVisibility(View.VISIBLE);
				holder.indexProgressbarTopImg.setVisibility(View.VISIBLE);
			}
			//文字是否可见
			if(list.get(listIndex).get("words") == null || TextUtils.isEmpty(list.get(listIndex).get("words").toString())){
				holder.indexTextDescribe.setVisibility(View.GONE);
			}else{
				holder.indexTextDescribe.setVisibility(View.VISIBLE);
				// 文字
				holder.indexTextDescribe.setText(list.get(listIndex).get("words") + "");
			}
			// 地点
			holder.indexLocalsCountry.setText(list.get(listIndex).get("position")
					.toString());
			// 地方话
			holder.home.setText(cityDao.getCityNameById((int) list.get(listIndex)
					.get("home")) + "话");
			// 设置昵称和头像
			CommonUtils.corner(context, R.drawable.default_avatar, holder.personHeadImg);
			holder.personUserNick.setText("");
			setUserInfo((int)list.get(listIndex).get("uid"),
					holder.personUserNick, holder.personHeadImg);

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 如果要奖Sring转为达特型需要用的到方法
			Date date = null;
			try {
				date = df.parse(list.get(listIndex).get("time") + "");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			holder.indexLocalsTime.setText(StringTools.getTimeFormatText(date));
			holder.indexProgressbarTime.setText(list.get(listIndex).get(
					"voice_duration")
					+ "\"");
			// 点赞数
			if ((Integer) (list.get(listIndex).get("praise_num")) > 0) {
				holder.indexSupportNum.setText(list.get(listIndex).get("praise_num")
						.toString());
			} else {
				holder.indexSupportNum.setText("赞");
			}
			// 评论数
			if ((Integer) (list.get(listIndex).get("comment_num")) > 0) {
				holder.indexCommentNum.setText(list.get(listIndex)
						.get("comment_num").toString());
				// holder.commentImg.setBackgroundResource(R.drawable.comment_press);
			} else {
				holder.indexCommentNum.setText("评论");
			}
		}else{//话题板块
			//录音模块是否可见
			if(TextUtils.isEmpty(topicInv.getVoice())
					|| "null".equals(topicInv.getVoice())){
				holder.indexProgressbarLayout.setVisibility(View.GONE);
				holder.indexProgressbarTopImg.setVisibility(View.GONE);
			}else{
				holder.indexProgressbarLayout.setVisibility(View.VISIBLE);
				holder.indexProgressbarTopImg.setVisibility(View.VISIBLE);
			}
			//文字是否可见
			if(TextUtils.isEmpty(topicInv.getWords())){
				holder.indexTextDescribe.setVisibility(View.GONE);
			}else{
				holder.indexTextDescribe.setVisibility(View.VISIBLE);
				// 文字
				holder.indexTextDescribe.setText(topicInv.getWords());
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 如果要奖Sring转为达特型需要用的到方法
			Date date = null;
			try {
				date = df.parse(topicInv.getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			holder.indexLocalsTime.setText(StringTools.getTimeFormatText(date));
			holder.indexProgressbarTime.setText(topicInv.getVoiceDuration()+ "\"");
			// 点赞数
			if (topicInv.getPraiseNum() > 0) {
				holder.indexSupportNum.setText(topicInv.getPraiseNum()
						.toString());
			} else {
				holder.indexSupportNum.setText("赞");
			}
			// 评论数
			if (topicInv.getCommentNum() > 0) {
				holder.indexCommentNum.setText(topicInv.getCommentNum().toString());
				// holder.commentImg.setBackgroundResource(R.drawable.comment_press);
			} else {
				holder.indexCommentNum.setText("评论");
			}
		}
		
	}

	// 设置点赞等图片按钮
	private void setSubBtn(ViewHolder holder, int position,int listIndex) {
		if (App.isLogin()) {
			int invId;
			if(getItemViewType(position) == TYPE_1){
				invId = (Integer) list.get(listIndex).get("inv_id"); 
			}else{
				invId = topicInv.getInvId();
			}
			int uId = App.pre.getInt(Global.UID, -1);
			// 判断该帖子是否在点赞表里
			if (isPraised(invId, uId)) {
				holder.supportImg
						.setBackgroundResource(R.drawable.support_press);
				praiseMap.put(position, true);
			} else {
				holder.supportImg.setBackgroundResource(R.drawable.support_no);
				praiseMap.put(position, false);
			}
		} else {
			holder.supportImg.setBackgroundResource(R.drawable.support_no);
			praiseMap.put(position, false);
			collectionMap.put(position, false);
		}
	}

	// 是否已经点赞
	private boolean isPraised(int invId, int uId) {
		return new PraiseDao(context).selectPraiseInvitation(invId, uId);
	}

	/**
	 * 设置item的用户昵称
	 * 
	 * @param userName
	 * @param nameTV
	 * @param personHeadImg
	 */
	public void setUserInfo(int uId, TextView nameTV,
			ImageButton personHeadImg) {
		LXHUser user = null;
		if(App.pre.getInt(Global.UID, -1) == uId){//当前用户发的帖子
			user = userDao.getNickAndHeadByUidFromUser(uId);
		}else{//其它用户发的帖子
			user = userDao.getNickAndHeadByUid(uId);
		}
		if (user != null) {// 如果本地数据库存在该用户
			nameTV.setText(user.getNickName());
			mImageLoader.loadCornerImage(Global.FILE_HOST+user.getHeadUrl(), this,
					personHeadImg);
		} else {// 如果没有再从服务器上取
			new GetUserInfoTask().execute(uId, nameTV, personHeadImg,this);
		}
	}

	class Listener implements OnClickListener {

		private int position;
		private int listIndex;
		private ViewHolder holder;

		public Listener(ViewHolder holder, int position,int listIndex) {
			this.position = position;
			this.holder = holder;
			this.listIndex = listIndex;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.index_user_head:
				// 跳转到查看别人的个人中心
				int userid = (int)list.get(listIndex).get("uid");
//				if(App.pre.getInt(Global.UID, -1) != userid){
					if(NetUtil.isNetConnect(context)){
						Intent intentToPerson = new Intent();
						intentToPerson.setClass(context, UserToPersonActivity.class);
						intentToPerson.putExtra("userid", userid);
						context.startActivity(intentToPerson);
					}else{
						Toast.makeText(context, "网络不给力啊",Toast.LENGTH_SHORT).show();
					}
//				}
				break;
			case R.id.index_topic_share_linerlayout:
			case R.id.index_share_linerlayout:
				Dialog dialog;
				if(getItemViewType(position) == TYPE_1){
					dialog = new MyDialog(context, list.get(listIndex),
							R.style.MyDialog);
				}else{
					dialog = new MyDialog(context, topicInv,
							R.style.MyDialog);
				}
				dialog.show();
				break;
			case R.id.index_topic_comment_linerlayout:
			case R.id.index_comment_linerlayout:
				if (App.isLogin()) {
					turnToComment(holder, position,listIndex);
				}
				break;
			case R.id.index_topic_support_linerlayout://话题点赞按钮与普通帖相同操作
			case R.id.index_support_linerlayout:
				if (App.isLogin()) {
					if (NetUtil.isNetConnect(context)) {// 检查是否联网
						dealSupportBtn(holder, position,listIndex);
					}
				}
				break;
			case R.id.index_topic_progressbar_layout:
			case R.id.index_progressbar_layout:
				if (NetUtil.isNetConnect(context)) {// 检查是否联网
					if (lastPosition == position) {
						if (!isPlay) {
							// Toast.makeText(context, "你点击了录音的播放按钮" + position,
							// Toast.LENGTH_SHORT).show();

							playAudio(holder, position,listIndex);// 播放语音

						} else {
							stopAudio(holder, position);
						}
					} else {
						playAudio(holder, position,listIndex);// 播放语音
					}
				}
				break;
			case R.id.index_topic_top_layout:
				Intent intent = new Intent();
				intent.setClass(context, FindDetailsActivity.class);
				intent.putExtra("type", 1);
				context.startActivity(intent);
				break;
			default:
				turnToComment(holder, position,listIndex);
				break;
			}
		}
	}

	// 跳转到评论页面
	private void turnToComment(ViewHolder holder, int position,int listIndex) {
		// TODO Auto-generated method stub
		int invId;
		Intent intentComment = new Intent();
		if(getItemViewType(position) == TYPE_1){
			invId = (Integer)list.get(listIndex).get("inv_id");
			intentComment.putExtra("enterFrom", 0);
		}else{
			invId = topicInv.getInvId();
			intentComment.putExtra("enterFrom", 2);
		}
		intentComment.setClass(context, CommentActivity.class);
		intentComment.putExtra("invId", invId);
		context.startActivity(intentComment);
	}

	public void dealSupportBtn(ViewHolder holder, int position,int listIndex) {
		// TODO Auto-generated method stub
		// 点赞+1的动态效果
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.nn);
		if (!praiseMap.get(position)) {// 点赞
			holder.supportImg.startAnimation(animation);
			holder.supportImg.setBackgroundResource(R.drawable.support_press);
			String number = holder.indexSupportNum.getText().toString();
			if ("赞".equals(number)) {
				holder.indexSupportNum.setText("1");
			} else {
				holder.indexSupportNum.setText((Integer.parseInt(number) + 1)
						+ "");
			}
			praiseMap.put(position, true);
			addPraise(position,listIndex);
		} else {// 取消点赞
			holder.supportImg.setBackgroundResource(R.drawable.support_no);
			String number = holder.indexSupportNum.getText().toString();
			if ("1".equals(number)) {
				holder.indexSupportNum.setText("赞");
			} else if ("赞".equals(number)) {
				holder.indexSupportNum.setText("赞");
			} else {
				holder.indexSupportNum.setText((Integer.parseInt(number) - 1)
						+ "");
			}
			praiseMap.put(position, false);
			deletePraise(position,listIndex);
		}
	}

	// 向服务器和本地数据库添加点赞数据
	private void addPraise(int position,int listIndex) {
//		// TODO Auto-generated method stub
		int invId;
		if(getItemViewType(position) == TYPE_1){
			invId = (int)list.get(listIndex).get("inv_id");
			int praiseNum = (Integer) (list.get(listIndex).get("praise_num"));
			list.get(listIndex).put("praise_num", praiseNum + 1);
		}else{
			invId = topicInv.getInvId();
			topicInv.setPraiseNum(topicInv.getPraiseNum()+1);
		}
		// 该贴点赞数+1
		new ChangePraiseNumTask().execute(invId,0);

		new PraiseDao(context).insertPraise(
				invId,App.pre.getInt(Global.UID, -1));
	}

	private void deletePraise(int position,int listIndex) {
		// TODO Auto-generated method stub
		int invId;
		if(getItemViewType(position) == TYPE_1){
			invId = (int)list.get(listIndex).get("inv_id");
			int praiseNum = (Integer) (list.get(listIndex).get("praise_num"));
			list.get(listIndex).put("praise_num", praiseNum - 1);
		}else{
			invId = topicInv.getInvId();
			topicInv.setPraiseNum(topicInv.getPraiseNum()-1);
		}
		// 该贴点赞数-1
		new ChangePraiseNumTask().execute(invId,1);

		PraiseDao praiseDao = new PraiseDao(context);
		praiseDao.deletePraise(invId, App.pre.getInt(Global.UID, -1));
	}
	
	

	class ViewHolder {
//		private LinearLayout indexBottomLinearlayout;// 帖子底部栏
		private RelativeLayout indexTopicTopLayout;//话题顶部栏
		private LinearLayout indexSupportLinerlayout;// 赞
		private LinearLayout indexCommentLinerlayout;// 评论
		private LinearLayout indexShareLinerlayout;// 分享
		private RelativeLayout indexProgressbarLayout;// 点击录音的布局
		private ImageView indexProgressbarTopImg;// 录音的布局上面指向头像的那块布局
//		private ImageView titleImage;// 热门/地理位置图标
		private ImageView supportImg;// 点赞的图标
		private ImageButton personHeadImg;// 头像
		private TextView personUserNick;// 昵称
		private TextView home;// 地方话
		private TextView indexSupportNum;// 点赞数
		private TextView indexCommentNum;// 评论数
		private ImageButton indexProgressbarBtn;// 在进度条中的播放停止按钮
		private TextView indexProgressbarTime;
		private TextView indexTextDescribe;// 帖子内容文字
		private TextView indexLocalsCountry;// 帖子内容城市
		private TextView indexLocalsTime;// 帖子内容时间
	}

	private MyCount mCountDownTimer;// 当前录音倒计时
	private MediaPlayer mp;
	private int voiceDuration;
	private boolean isPlay = false;
	private ViewHolder mHolder;// 记录前一个holder，在停止时调用
	private int lastPosition;// 记录上一个position，在点击播放按钮时判断是否有其它item在播放
	private AnimationDrawable animationDrawable;

	// 播放已经录好的音
	public void playAudio(ViewHolder holder, int position,int listIndex) {

		stopAudio(mHolder, lastPosition);
		// 设置
		mHolder = holder;
		// mHolder.indexProgressbarBtn.setBackgroundResource(R.drawable.pause_ico);
		mHolder.indexProgressbarBtn.setBackgroundResource(R.anim.frameanim);// 播放录音的动画
		animationDrawable = (AnimationDrawable) mHolder.indexProgressbarBtn
				.getBackground();
		animationDrawable.start();

		isPlay = true;
		isCurrentItemAudioPlay = true;
		// 解决按钮状态也被重用的问题
		isAudioPlayArray.put(position, true);
		lastPosition = position;

		// 重新获得
		String audioUrl ="";
		if(getItemViewType(position) == TYPE_1){
			audioUrl = Global.FILE_HOST + list.get(listIndex).get("voice").toString();
			voiceDuration = (Integer) list.get(listIndex).get("voice_duration");
		}else{
			audioUrl = Global.FILE_HOST + topicInv.getVoice();
			voiceDuration = topicInv.getVoiceDuration();
		}
		// 点击播放而已
		try {
			mp.reset();
			mp.setDataSource(audioUrl);
			mp.prepareAsync();
			mp.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
					// 实现倒计时
					mCountDownTimer = new MyCount((voiceDuration) * 1000 + 50,
							1000);
					mCountDownTimer.start();
				}
			});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			stopAudio();
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			stopAudio();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context, "网络连接错误", Toast.LENGTH_SHORT).show();
			stopAudio();
			e.printStackTrace();
		}
	}

	public void stopAudio() {
		// TODO Auto-generated method stub
		stopAudio(mHolder, lastPosition);
	}

	public void stopAudio(final ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		if (holder != null) {
			holder.indexProgressbarTime.setText(voiceDuration + "\"");
			animationDrawable.stop();
			holder.indexProgressbarBtn.setBackgroundResource(R.drawable.musicplayone);
		}
		isPlay = false;
		isAudioPlayArray.put(position, false);
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		if (mp == null) {
			mp = new MediaPlayer();
		} else {
			if (mp.isPlaying()) {
				mp.stop();
			}
		}
	}

	/* 定义一个倒计时的内部类 */
	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// 完成的时候提示
			mHolder.indexProgressbarTime.setText(0 + "\"");
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mHandler.sendEmptyMessage(0);
				}
			}, 1000l);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			 Log.i(TAG, "isCurrentItemAudioPlay:"+isCurrentItemAudioPlay);
			if (isCurrentItemAudioPlay) {
				mHolder.indexProgressbarTime.setText(millisUntilFinished / 1000
						+ "\"");
			}
		}
	}

	/**
	 * Handler消息处理
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if (isCurrentItemAudioPlay) {
					mHolder.indexProgressbarTime.setText(voiceDuration + "\"");
					animationDrawable.stop();
					mHolder.indexProgressbarBtn.setBackgroundResource(R.drawable.musicplayone);
				}
				isAudioPlayArray.put(lastPosition, false);
				isPlay = false;
			}
			super.handleMessage(msg);
		}

	};

}
