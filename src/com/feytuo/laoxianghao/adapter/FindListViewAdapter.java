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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.CommentActivity;
import com.feytuo.laoxianghao.FindDetailsActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.dao.CityDao;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.dao.PraiseDao;
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
public class FindListViewAdapter extends SimpleAdapter {
	
	private Context context;
	private LayoutInflater m_Inflater;
	private int resource;
	private List<Map<String, Object>> list;// 声明List容器对象
	private SparseArray<Boolean> praiseMap; // 标记点赞对象
	private SparseArray<Boolean> collectionMap;// 标记收藏对象
	private SparseArray<Boolean> isAudioPlayArray;// 记录是否正在播放音乐
	private boolean isCurrentItemAudioPlay;
	private AnimationDrawable animationDrawable;
	
	private LXHUserDao userDao;
	private CityDao cityDao;
	private ImageLoader mImageLoader;

	public FindListViewAdapter(Context context,
			List<Map<String, Object>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.list = data;
		this.context = context;
		this.resource = resource;
		m_Inflater = LayoutInflater.from(context);
		praiseMap = new SparseArray<>();
		collectionMap = new SparseArray<>();
		isAudioPlayArray = new SparseArray<>();
		userDao = new LXHUserDao(context);
		cityDao = new CityDao(context);
		mImageLoader = new ImageLoader(context);
	}

	@Override
	// 获取listitem下面的view的值
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// ViewHolder不是Android的开发API，而是一种设计方法，就是设计个静态类，缓存一下，省得Listview更新的时候，还要重新操作。
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = m_Inflater.inflate(resource, null);
			if (resource == R.layout.index_listview) {
				holder.indexSupportLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_support_linerlayout);
				holder.indexCommentLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_comment_linerlayout);
				holder.indexShareLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_share_linerlayout);
				holder.indexProgressbarLayout = (RelativeLayout) convertView
						.findViewById(R.id.index_progressbar_layout);
				holder.indexProgressbarTopImg = (ImageView) convertView
						.findViewById(R.id.index_progressbar_top_img);
				holder.indexProgressbarBtn = (ImageButton) convertView
						.findViewById(R.id.index_progressbar_btn);

				holder.titleImage = (ImageView) convertView
						.findViewById(R.id.title_img_id);
				holder.supportImg = (ImageView) convertView
						.findViewById(R.id.support_img);
				holder.personHeadImg = (ImageButton) convertView
						.findViewById(R.id.index_user_head);
				holder.personUserNick = (TextView) convertView
						.findViewById(R.id.index_user_nick);
				holder.home = (TextView) convertView
						.findViewById(R.id.index_home_textview);
				holder.indexSupportNum = (TextView) convertView
						.findViewById(R.id.index_support_num);
				holder.indexCommentNum = (TextView) convertView
						.findViewById(R.id.index_comment_num);
				holder.indexProgressbarTime = (TextView) convertView
						.findViewById(R.id.index_progressbar_time);

				holder.indexTextDescribe = (TextView) convertView
						.findViewById(R.id.index_text_describe);
				holder.indexLocalsCountry = (TextView) convertView
						.findViewById(R.id.index_locals_country);
				holder.indexLocalsTime = (TextView) convertView
						.findViewById(R.id.index_locals_time);
				convertView.setTag(holder);
				// Tag从本质上来讲是就是相关联的view的额外的信息。它们经常用来存储(set）一些view的数据，用的时候（get）这样做非常方便而不用存入另外的单独结构。
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Listener listener = new Listener(holder, position);
		convertView.setClickable(true);
		convertView.setOnClickListener(listener);
		holder.indexSupportLinerlayout.setOnClickListener(listener);
		holder.indexCommentLinerlayout.setOnClickListener(listener);
		holder.indexShareLinerlayout.setOnClickListener(listener);
		holder.indexProgressbarLayout.setOnClickListener(listener);
		setSubBtn(holder, position);
		setcontent(holder, position);
		setAudioState(holder, position);
		return convertView;
	}

	// 根据音乐是否播放设置item
	private void setAudioState(final ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		holder.indexProgressbarBtn.setBackgroundResource(R.anim.frameanim);// 播放录音的动画
		animationDrawable = (AnimationDrawable) holder.indexProgressbarBtn
				.getBackground();
		if (!isAudioPlayArray.get(position, false)) {// 没有播放的
			if (mHolder != null && mHolder.equals(holder)) {
				isCurrentItemAudioPlay = false;
				holder.indexProgressbarTime.setText((Integer) list
						.get(position).get("voice_duration") + "\"");
				animationDrawable.stop();
				holder.indexProgressbarBtn.setBackgroundResource(R.drawable.musicplayone);

			}
		} else {// 正在播放的
			isCurrentItemAudioPlay = true;
			animationDrawable.start();
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void setcontent(ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		//录音模块是否可见
		if(list.get(position).get("voice") == null 
				|| TextUtils.isEmpty(list.get(position).get("voice").toString())
				|| "null".equals(list.get(position).get("voice").toString())){
			holder.indexProgressbarLayout.setVisibility(View.GONE);
			holder.indexProgressbarTopImg.setVisibility(View.GONE);
		}else{
			holder.indexProgressbarLayout.setVisibility(View.VISIBLE);
			holder.indexProgressbarTopImg.setVisibility(View.VISIBLE);
		}
		//文字是否可见
		if(list.get(position).get("words") == null || TextUtils.isEmpty(list.get(position).get("words").toString())){
			holder.indexTextDescribe.setVisibility(View.GONE);
		}else{
			holder.indexTextDescribe.setVisibility(View.VISIBLE);
			// 文字
			holder.indexTextDescribe.setText(list.get(position).get("words") + "");
		}
		// 地点
		holder.indexLocalsCountry.setText(list.get(position).get("position")
				.toString());
		//地方话
		holder.home.setText(cityDao.getCityNameById((int)list.get(position).get("home"))+"话");
		if (1 == (int) list.get(position).get("type")) {
			holder.titleImage.setVisibility(View.GONE);
			holder.indexLocalsCountry.setVisibility(View.GONE);
			holder.home.setVisibility(View.GONE);
			// 设置头像、昵称
			holder.personUserNick.setText("乡乡话题");
			CommonUtils.corner(context, R.drawable.ic_launcher,
					holder.personHeadImg);
		} else {
			holder.titleImage.setVisibility(View.VISIBLE);
			holder.indexLocalsCountry.setVisibility(View.VISIBLE);
			holder.home.setVisibility(View.VISIBLE);
			holder.titleImage.setBackgroundResource(R.drawable.geographical);
			holder.indexLocalsCountry.setTextColor(context.getResources()
					.getColor(R.color.indexbg));
			// 设置头像、昵称
			CommonUtils.corner(context, R.drawable.default_avatar,holder.personHeadImg);
			holder.personUserNick.setText("");
			setUserInfo((Integer) list.get(position).get("uid"), holder.personUserNick,
					holder.personHeadImg);
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 如果要奖Sring转为达特型需要用的到方法
		Date date = null;
		try {
			date = df.parse(list.get(position).get("time") + "");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.indexLocalsTime.setText(StringTools.getTimeFormatText(date));
		holder.indexProgressbarTime.setText(list.get(position).get(
				"voice_duration")
				+ "\"");
		// 点赞数
		if ((Integer) (list.get(position).get("praise_num")) > 0) {
			holder.indexSupportNum.setText(list.get(position).get("praise_num")
					.toString());
		} else {
			holder.indexSupportNum.setText("赞");
		}
		// 评论数
		if ((Integer) (list.get(position).get("comment_num")) > 0) {
			holder.indexCommentNum.setText(list.get(position)
					.get("comment_num").toString());
			// holder.commentImg.setBackgroundResource(R.drawable.comment_press);
		} else {
			holder.indexCommentNum.setText("评论");
		}
	}

	// 设置点赞等图片按钮
	private void setSubBtn(ViewHolder holder, int position) {
		if (App.isLogin()) {
			int invId = (int) list.get(position).get("inv_id");
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
	 * @param userName
	 * @param nameTV
	 * @param personHeadImg 
	 */
	public void setUserInfo(int uId ,TextView nameTV, ImageButton personHeadImg){
		LXHUser user = null;
		if(App.pre.getInt(Global.UID, -1) == uId){//当前用户发的帖子
			user = userDao.getNickAndHeadByUidFromUser(uId);
		}else{//其它用户发的帖子
			user = userDao.getNickAndHeadByUid(uId);
		}
		if(user != null){//如果本地数据库存在该用户
			nameTV.setText(user.getNickName());
			mImageLoader.loadCornerImage(user.getHeadUrl(), this, personHeadImg);
		}else{//如果没有再从bmob上取
			new GetUserInfoTask().execute(uId, nameTV, personHeadImg,this);
		}
	}

	class Listener implements OnClickListener {

		private int position;
		private ViewHolder holder;

		public Listener(ViewHolder holder, int position) {
			this.position = position;
			this.holder = holder;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.index_share_linerlayout:
				Dialog dialog = new MyDialog(context, list.get(position),
						R.style.MyDialog);
				dialog.show();
				break;
			case R.id.index_comment_linerlayout:
				if (App.isLogin()) {
					turnToComment(holder, position);
				}
				// Toast.makeText(context, "点击了所在" + position + "的评论",
				// Toast.LENGTH_SHORT).show();
				break;
			case R.id.index_support_linerlayout:
				if (App.isLogin()) {
					if (NetUtil.isNetConnect(context)) {// 检查是否联网
						dealSupportBtn(holder, position);
					}
				}
				break;
			case R.id.index_progressbar_layout:
				if (NetUtil.isNetConnect(context)) {// 检查是否联网
					if (lastPosition == position) {
						if (!isPlay) {
							// Toast.makeText(context, "你点击了录音的播放按钮" + position,
							// Toast.LENGTH_SHORT).show();

							playAudio(holder, position);// 播放语音

						} else {
							stopAudio(holder, position);
						}
					} else {
						playAudio(holder, position);// 播放语音
					}
				}
				break;
			default:
				if (App.isLogin()) {
					turnToComment(holder,position);
				}
				break;
			}
		}
	}

	// 跳转到评论页面
	private void turnToComment(ViewHolder holder,int position) {
		// TODO Auto-generated method stub
		String invId = list.get(position).get("inv_id").toString();
		Intent intentComment = new Intent();
		intentComment.setClass(context, CommentActivity.class);
		intentComment.putExtra("invId", invId);
		if(context instanceof FindDetailsActivity){
			intentComment.putExtra("enterFrom", 2);
		}else{
			intentComment.putExtra("enterFrom", 3);
		}
		context.startActivity(intentComment);
	}

	public void dealSupportBtn(ViewHolder holder, int position) {
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
			addPraise(position);
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
			deletePraise(position);
		}
	}

	// 向服务器和本地数据库添加点赞数据
	private void addPraise(final int position) {
		// TODO Auto-generated method stub
		int invId = (int)list.get(position).get("inv_id");
//		// 该贴点赞数+1
		new ChangePraiseNumTask().execute(invId,0);
//		Invitation inv = new Invitation();
//		inv.increment("praiseNum", 1);
//		inv.update(context, invId, new UpdateListener() {
//			@Override
//			public void onSuccess() {
//				int praiseNum = (Integer) (list.get(position).get("praise_num"));
//				list.get(position).put("praise_num", praiseNum + 1);
//				// Toast.makeText(context, "点赞数+1", Toast.LENGTH_SHORT).show();
//			}
//
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				// Toast.makeText(context, "点赞数+1失败",
//				// Toast.LENGTH_SHORT).show();
//			}
//		});

		new PraiseDao(context).insertPraise(invId
				,App.pre.getInt(Global.UID, -1));
	}

	private void deletePraise(final int position) {
		// TODO Auto-generated method stub
		int invId = (int)list.get(position).get("inv_id");
		// 该贴点赞数-1
		new ChangePraiseNumTask().execute(invId,1);
//		Invitation inv = new Invitation();
//		inv.increment("praiseNum", -1);
//		inv.update(context, invId, new UpdateListener() {
//			@Override
//			public void onSuccess() {
//				int praiseNum = (Integer) (list.get(position).get("praise_num"));
//				list.get(position).put("praise_num", praiseNum - 1);
//				// Toast.makeText(context, "点赞数-1", Toast.LENGTH_SHORT).show();
//			}
//
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				// Toast.makeText(context, "点赞数-1失败",
//				// Toast.LENGTH_SHORT).show();
//			}
//		});

		PraiseDao praiseDao = new PraiseDao(context);
		praiseDao.deletePraise(invId, App.pre.getInt(Global.UID, -1));
	}

	class ViewHolder {
//		private LinearLayout indexBottomLinearlayout;// 帖子底部栏
		private LinearLayout indexSupportLinerlayout;// 赞
		private LinearLayout indexCommentLinerlayout;// 评论
		private LinearLayout indexShareLinerlayout;// 分享
		private RelativeLayout indexProgressbarLayout;
		private ImageView indexProgressbarTopImg;
		private ImageView titleImage;// 热门/地理位置图标
		private ImageView supportImg;// 点赞的图标
		private ImageButton personHeadImg;// 头像
		private TextView personUserNick;//昵称
		private TextView home;//地方话
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

	// 播放已经录好的音
	public void playAudio(ViewHolder holder, int position) {

		stopAudio(mHolder, lastPosition);
		// 设置
		mHolder = holder;
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
		String audioUrl = list.get(position).get("voice").toString();
		voiceDuration = (Integer) list.get(position).get("voice_duration");
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
		// 进度条走完
		isPlay = false;
		isAudioPlayArray.put(position, false);
		if (holder != null) {
			holder.indexProgressbarTime.setText(voiceDuration + "\"");
			animationDrawable.stop();
			holder.indexProgressbarBtn.setBackgroundResource(R.drawable.musicplayone);
			
		}
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
//			if (isCurrentItemAudioPlay) {
				mHolder.indexProgressbarTime.setText(0 + "\"");
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(0);
					}
				}, 1000l);
//			}

		}

		@Override
		public void onTick(long millisUntilFinished) {
			// Log.i("countdown", millisUntilFinished + "");
			Log.i("FindListViewAdapter", "isCurrentItemAudioPlay:"
					+ isCurrentItemAudioPlay);
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
