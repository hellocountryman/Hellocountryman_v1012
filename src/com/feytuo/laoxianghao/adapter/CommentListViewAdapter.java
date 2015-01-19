package com.feytuo.laoxianghao.adapter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
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
import com.feytuo.laoxianghao.view.MyDialog;

/**
 * 
 * @author feytuo
 * 
 */
public class CommentListViewAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater m_Inflater;
	private List<? extends Map<String, ?>> data;// 声明List容器对象
	private Invitation inv;// 帖子id
	final int VIEW_TYPE = 2;// 一共是使用2个布局
	final int TYPE_1 = 0;
	final int TYPE_2 = 1;
	private MediaPlayer mp = new MediaPlayer();

	private SparseArray<Boolean> isAudioPlayArray;
	private boolean isPraised;

	private LXHUserDao userDao;
	private CityDao cityDao;
	private ImageLoader mImageLoader;

	public CommentListViewAdapter(Context context,
			List<? extends Map<String, ?>> data, Invitation inv) {
		this.inv = inv;
		this.data = data;
		this.context = context;
		m_Inflater = LayoutInflater.from(context);
		isPraised = false;
		isAudioPlayArray = new SparseArray<>();
		userDao = new LXHUserDao(context);
		cityDao = new CityDao(context);
		mImageLoader = new ImageLoader(context);
		// LayoutInflater作用是将layout的xml布局文件实例化为View类对象。
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return data.size() + 1;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		Object o = null;
		if (arg0 == 0) {
			o = inv;
		} else {
			o = data.get(arg0 - 1);
		}
		return o;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;

	}

	// getItemViewType(int) – 根据position返回相应的Item
	public int getItemViewType(int position) {
		int p = position;
		if (p == 0) {
			return TYPE_1;
		} else {
			return TYPE_2;
		}
	}

	// getViewTypeCount() – 该方法返回多少个不同的布局
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return VIEW_TYPE;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder1 holder1 = null;
		viewHolder2 holder2 = null;
		int type = getItemViewType(position);// 判断使用的布局
		// 如果没有创建布局
		switch (type) {
		case TYPE_1:
			if (convertView == null) {
				convertView = m_Inflater.inflate(R.layout.index_listview, null);// 同样是将布局转化成view
				holder1 = new viewHolder1();
				holder1.indexSupportLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_support_linerlayout);

				holder1.indexShareLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_share_linerlayout);

				holder1.indexProgressbarBtn = (ImageButton) convertView
						.findViewById(R.id.index_progressbar_btn);
				holder1.indexProgressbarTopImg = (ImageView) convertView
						.findViewById(R.id.index_progressbar_top_img);
				holder1.indexProgressbarLayout = (RelativeLayout) convertView
						.findViewById(R.id.index_progressbar_layout);

				holder1.titleImage = (ImageView) convertView
						.findViewById(R.id.title_img_id);
				holder1.supportImg = (ImageView) convertView
						.findViewById(R.id.support_img);
				holder1.commentImg = (ImageView) convertView
						.findViewById(R.id.comment_img);
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

				// View中的setTag(Onbect)表示给View添加一个格外的数据,以后可以用getTag()将这个数据取出来
			} else {
				holder1 = (viewHolder1) convertView.getTag();
			}
			break;
		case TYPE_2:

			if (convertView == null) {
				convertView = m_Inflater.inflate(R.layout.comment_item_copy,
						null);
				// 实例化holder以及控件
				holder2 = new viewHolder2();
				holder2.commentUserHead = (ImageView) convertView
						.findViewById(R.id.comment_head_image);
				holder2.commentNickName = (TextView) convertView
						.findViewById(R.id.comment_nick_name);
				holder2.commentTime = (TextView) convertView
						.findViewById(R.id.comment_time_date);
				holder2.commentTextContext = (TextView) convertView
						.findViewById(R.id.comment_text_context);
				holder2.commentPlayId = (Button) convertView
						.findViewById(R.id.comment_play_btn);
				holder2.commentMusicBottomBg = (RelativeLayout) convertView
						.findViewById(R.id.comment_music_bottom_bg);
				holder2.commentMusicTopBg = (ImageView) convertView
						.findViewById(R.id.comment_music_top_bg);
				convertView.setTag(holder2);
				// 用容器将已经实例化的convertView保存用于获取里面给的控件
			} else {
				holder2 = (viewHolder2) convertView.getTag();
			}
			break;

		}
		// 可以对控件进行操作
		switch (type) {
		case TYPE_1:
			if(inv.getInvId() != 0){
				setcontent(holder1, inv);
				setSubBtn(holder1, inv);
				Listener listener = new Listener(holder1);
				holder1.indexSupportLinerlayout.setOnClickListener(listener);
				holder1.indexShareLinerlayout.setOnClickListener(listener);
				// holder1.indexProgressbarBtn.setOnClickListener(listener);
				holder1.indexProgressbarLayout.setOnClickListener(listener);
			}
			break;
		case TYPE_2:
			setAudioPlayBtn(holder2, position);
			setCommentContent(holder2,position);
			holder2.commentMusicBottomBg.setOnClickListener(new AudioListener(position,holder2));
			break;
		}
		return convertView;

	}

	//评论的设置内容
	private void setCommentContent(viewHolder2 holder2, int position) {
		// TODO Auto-generated method stub
		Map<String, ?> map = data.get(position - 1);
		//设置用户头像和昵称
		CommonUtils.corner(context, R.drawable.default_avatar,holder2.commentUserHead);
		holder2.commentNickName.setText("");
		setUserInfo((Integer)map.get("uid"),
				holder2.commentNickName, holder2.commentUserHead);
		
		holder2.commentTime.setText(map.get("com_time") + "");
		holder2.commentTextContext.setText(map.get("com_words") + "");
	}

	/**
	 * 设置item的用户昵称
	 * 
	 * @param userName
	 * @param nameTV
	 * @param personHeadImg
	 */
	public void setUserInfo(int uId, TextView nameTV,
			ImageView personHeadImg) {
		LXHUser user = null;
		if(App.pre.getInt(Global.UID, -1) == uId){//当前用户发的帖子
			user = userDao.getNickAndHeadByUidFromUser(uId);
		}else{//其它用户发的帖子
			user = userDao.getNickAndHeadByUid(uId);
		}
		if (user != null) {// 如果本地数据库存在该用户
			nameTV.setText(user.getNickName());
			mImageLoader.loadCornerImage(user.getHeadUrl(), this,
					personHeadImg);
		} else {// 如果没有再从bmob上取
			new GetUserInfoTask().execute(uId, nameTV, personHeadImg,this);
		}
	}
	//评论的播音按钮设置
	private void setAudioPlayBtn(viewHolder2 holder, int position) {
		
		// 初始化时
		if (TextUtils.isEmpty(data.get(position - 1).get("com_voice").toString())
				|| "null".equals(data.get(position - 1).get("com_voice").toString())) {
			holder.commentMusicBottomBg.setVisibility(View.GONE);
			holder.commentMusicTopBg.setVisibility(View.GONE);
			holder.commentPlayId.setVisibility(View.INVISIBLE);
		} else {
			holder.commentMusicBottomBg.setVisibility(View.VISIBLE);
			holder.commentMusicTopBg.setVisibility(View.VISIBLE);
			holder.commentPlayId.setVisibility(View.VISIBLE);
			// 播放时的修改
			if (isAudioPlayArray.get(position, false)) {
				holder.commentPlayId.setBackgroundResource(R.anim.frameanim);// 播放录音的动画
				animationDrawable = (AnimationDrawable) holder.commentPlayId
						.getBackground();
				animationDrawable.start();
			} else {
				holder.commentPlayId.setBackgroundResource(R.drawable.musicplayone);
			}
		}
	}

	//帖子的内容设置
	private void setcontent(viewHolder1 holder, Invitation inv) {
		// TODO Auto-generated method stub
		//录音模块是否可见
		if(TextUtils.isEmpty(inv.getVoice())
				|| "null".equals(inv.getVoice())){
			holder.indexProgressbarLayout.setVisibility(View.GONE);
			holder.indexProgressbarTopImg.setVisibility(View.GONE);
		}else{
			holder.indexProgressbarLayout.setVisibility(View.VISIBLE);
			holder.indexProgressbarTopImg.setVisibility(View.VISIBLE);
		}
		//文字是否可见
		if(TextUtils.isEmpty(inv.getWords())){
			holder.indexTextDescribe.setVisibility(View.GONE);
		}else{
			holder.indexTextDescribe.setVisibility(View.VISIBLE);
			// 文字
			holder.indexTextDescribe.setText(inv.getWords());
		}
		holder.indexTextDescribe.setText(inv.getWords());
		holder.indexLocalsCountry.setText(inv.getPosition());
		holder.indexLocalsTime.setText(inv.getTime());
		// 地方话
		holder.home.setText(cityDao.getCityNameById(inv.getHome()) + "话");
		holder.indexProgressbarTime.setText(inv.getVoiceDuration() + "\"");
		if (1 == inv.getType()) {
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
			setUserInfo(inv.getuId(), holder.personUserNick,
					holder.personHeadImg);
		}
		if (inv.getPraiseNum() > 0) {
			holder.indexSupportNum.setText(inv.getPraiseNum() + "");
		} else {
			holder.indexSupportNum.setText("赞");
		}
		if (inv.getCommentNum() > 0) {
			holder.indexCommentNum.setText(inv.getCommentNum() + "");
		} else {
			holder.indexCommentNum.setText("评论");
		}
		holder.commentImg.setBackgroundResource(R.drawable.comment_no);
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
			mImageLoader.loadCornerImage(user.getHeadUrl(), this, personHeadImg);
		}
	}

	// 设置点赞等图片按钮
	private void setSubBtn(viewHolder1 holder, Invitation inv) {
		int invId = inv.getInvId();
		int uId = App.pre.getInt(Global.UID, -1);
		// 判断该帖子是否在点赞表里
		if (isPraised(invId, uId)) {
			holder.supportImg.setBackgroundResource(R.drawable.support_press);
			isPraised = true;
		} else {
			holder.supportImg.setBackgroundResource(R.drawable.support_no);
			isPraised = false;
		}
	}

	// 是否已经点赞
	private boolean isPraised(int invId, int uId) {
		return new PraiseDao(context).selectPraiseInvitation(invId, uId);
	}

	class AudioListener implements OnClickListener {
		private int position;
		private viewHolder2 holder;
		public AudioListener(int position,viewHolder2 holder) {
			// TODO Auto-generated constructor stub
			this.position = position;
			this.holder = holder;
		}
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.comment_music_bottom_bg:
				if (NetUtil.isNetConnect(context)) {// 检查是否联网
					if (lastPosition == position) {
						if (!isPlay) {
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
				break;
			}

		}

	}

	private int lastPosition;//上一个播放录音的view
	private boolean isPlay = false;
	private viewHolder2 lastView;//上一个播放录音的viewholder

	// 播放已经录好的音
	private void playAudio(final viewHolder2 holder, final int position) {
		stopInvitationAudio();// 当播放评论录音时，确保帖子录音时关闭的
		stopAudio(lastView, lastPosition);
		lastView = holder;
		lastPosition = position;
		isPlay = true;
		// 解决按钮状态也被重用的问题
		isAudioPlayArray.put(position, true);
		
//		v.setBackgroundResource(R.drawable.comment_audio_play);
		holder.commentPlayId.setBackgroundResource(R.anim.frameanim);// 播放录音的动画
		animationDrawable = (AnimationDrawable)holder.commentPlayId.getBackground();
		animationDrawable.start();
		
		String fileUrl = Global.FILE_HOST + data.get(position - 1).get("com_voice").toString();
		// 点击播放而已
		try {
			mp.reset();
			mp.setDataSource(fileUrl);
			mp.prepareAsync();
			mp.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
				}
			});
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					stopAudio(holder, position);
				}
			});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopAudio() {
		stopAudio(lastView, lastPosition);
	}

	private void stopAudio(final viewHolder2 holder, int position) {
		// TODO Auto-generated method stub
		isPlay = false;
		if (holder != null) {
//			v.setBackgroundResource(R.drawable.comment_audio_selector);
			holder.commentPlayId.setBackgroundResource(R.drawable.musicplayone);
			animationDrawable.stop();
		}
		isAudioPlayArray.put(position, false);
		if (mp != null && mp.isPlaying()) {
			mp.stop();
		}
	}

	// viewHolder把你getView方法中每次返回的View存起来，可以下次再用。
	// 这样做的好处就是不必每次都到布局文件中去拿到你的View，提高了效率
	class viewHolder1 {
		private LinearLayout indexSupportLinerlayout;// 赞
		private LinearLayout indexShareLinerlayout;// 分享
		private ImageView titleImage;// 热门/地理位置图标
		private ImageView supportImg;// 点赞的图标
		private ImageButton personHeadImg;// 头像
		private TextView personUserNick;// 昵称
		private TextView home;// 地方话
		private TextView indexSupportNum;// 点赞数
		private TextView indexCommentNum;// 评论数
		private ImageView commentImg;// 评论的图标
		private RelativeLayout indexProgressbarLayout;// 录音的背景
		private ImageView indexProgressbarTopImg;// 录音指向用户头像的背景
		private ImageButton indexProgressbarBtn;// 在进度条中的播放停止按钮
		private TextView indexProgressbarTime;
		private TextView indexTextDescribe;// 帖子内容文字
		private TextView indexLocalsCountry;// 帖子内容城市
		private TextView indexLocalsTime;// 帖子内容时间
	}

	class viewHolder2 {
		ImageView commentUserHead;// 楼层
		TextView commentNickName; // 评论的用户所在的位置
		TextView commentTime; // 评论的时间
		TextView commentTextContext; // 评论的文字内容
		Button commentPlayId;// 语言的按钮
		RelativeLayout commentMusicBottomBg;// 评论中的背景；
		ImageView commentMusicTopBg;// 评论中指向用户头像的背景
	}

	class Listener implements OnClickListener {

		private viewHolder1 holder;

		public Listener(viewHolder1 holder1) {
			this.holder = holder1;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.index_share_linerlayout:
				Dialog dialog = new MyDialog(context, inv, R.style.MyDialog);
				dialog.show();
				break;
			case R.id.index_support_linerlayout:
				// 主列表需要刷新
				if (NetUtil.isNetConnect(context)) {// 检查是否联网
					App.pre.edit()
							.putBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, true)
							.commit();
					dealSupportBtn(holder);
				}
				break;
			case R.id.index_progressbar_layout:
				if (NetUtil.isNetConnect(context)) {// 检查是否联网
					if (!isInvitationPlay) {
						playInvitationAudio(holder);// 播放语音
					} else {
						stopInvitationAudio(holder);
					}
				}
				break;
			default:
				break;
			}
		}

	}

	public void dealSupportBtn(viewHolder1 holder) {
		// TODO Auto-generated method stub
		// 点赞+1的动态效果
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.nn);
		if (!isPraised) {// 点赞
			holder.supportImg.startAnimation(animation);
			holder.supportImg.setBackgroundResource(R.drawable.support_press);
			String number = holder.indexSupportNum.getText().toString();
			if ("赞".equals(number)) {
				holder.indexSupportNum.setText("1");
			} else {
				holder.indexSupportNum.setText((Integer.parseInt(number) + 1)
						+ "");
			}
			isPraised = true;
			addPraise();
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
			isPraised = false;
			deletePraise();
		}
	}

	// 向服务器和本地数据库添加点赞数据
	private void addPraise() {
		// TODO Auto-generated method stub
//		// 该贴点赞数+1
		new ChangePraiseNumTask().execute(inv.getInvId(),0);
//		Invitation invitation = new Invitation();
//		invitation.increment("praiseNum", 1);
//		invitation.update(context, inv.getObjectId(), new UpdateListener() {
//			@Override
//			public void onSuccess() {
//				int praiseNum = inv.getPraiseNum();
//				inv.setPraiseNum(praiseNum + 1);
//				// Toast.makeText(context, "点赞数+1", Toast.LENGTH_SHORT).show();
//			}
//
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				// Toast.makeText(context, "点赞数+1失败",
//				// Toast.LENGTH_SHORT).show();
//			}
//		});

		new PraiseDao(context).insertPraise(
				inv.getInvId(),App.pre.getInt(Global.UID, -1));
	}

	private void deletePraise() {
		// TODO Auto-generated method stub
		// 该贴点赞数-1
		new ChangePraiseNumTask().execute(inv.getInvId(),1);
//		Invitation invitation = new Invitation();
//		invitation.increment("praiseNum", -1);
//		invitation.update(context, inv.getObjectId(), new UpdateListener() {
//			@Override
//			public void onSuccess() {
//				int praiseNum = inv.getPraiseNum();
//				inv.setPraiseNum(praiseNum - 1);
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
		praiseDao.deletePraise(inv.getInvId(),
				App.pre.getInt(Global.UID, -1));
	}

	private boolean isInvitationPlay;
	private int voiceDuration;
	private MyCount mCountDownTimer;// 当前录音倒计时
//	private Timer mProgressTimer;// 当前进度条进度计时
//	private int mCount = 0;// 进度条度数
	private MediaPlayer mMeidaPlayer;
	private viewHolder1 mHolder1;// 记录前一个holder，在停止时调用
	private AnimationDrawable animationDrawable;

	private void playInvitationAudio(viewHolder1 holder) {
		// TODO Auto-generated method stub
		stopAudio();// 播放帖子录音时确保评论录音已经关闭
		stopInvitationAudio(mHolder1);
		// 重新获得
		mHolder1 = holder;

		mHolder1.indexProgressbarBtn.setBackgroundResource(R.anim.frameanim);// 播放录音的动画
		animationDrawable = (AnimationDrawable) mHolder1.indexProgressbarBtn
				.getBackground();
		animationDrawable.start();

		isInvitationPlay = true;
		String audioUrl = inv.getVoice();
		voiceDuration = inv.getVoiceDuration();
		// 点击播放而已
		try {
			mMeidaPlayer.reset();
			mMeidaPlayer.setDataSource(audioUrl);
			mMeidaPlayer.prepareAsync();
			mMeidaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
					// 实现倒计时
					mCountDownTimer = new MyCount((voiceDuration) * 1000 + 50,
							1000);
					mCountDownTimer.start();
//					// 显示进度条
//					showIndeterDialog(voiceDuration);
				}
			});

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopInvitationAudio() {
		// TODO Auto-generated method stub
		stopInvitationAudio(mHolder1);
	}

	private void stopInvitationAudio(final viewHolder1 holder) {
		// TODO Auto-generated method stub
//		 进度条走完
//		mCount = 0;
		isInvitationPlay = false;
		if (holder != null) {
			holder.indexProgressbarTime.setText(voiceDuration + "\"");
			animationDrawable.stop();
			holder.indexProgressbarBtn
					.setBackgroundResource(R.drawable.musicplayone);
		}
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		if (mMeidaPlayer == null) {
			mMeidaPlayer = new MediaPlayer();
		} else {
			if (mMeidaPlayer.isPlaying()) {
				mMeidaPlayer.stop();
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
			mHolder1.indexProgressbarTime.setText(0 + "\"");
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
			mHolder1.indexProgressbarTime.setText(millisUntilFinished / 1000
					+ "\"");
		}
	}

	/**
	 * Handler消息处理
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				mHolder1.indexProgressbarTime.setText(voiceDuration + "\"");
				animationDrawable.stop();
				mHolder1.indexProgressbarBtn
						.setBackgroundResource(R.drawable.musicplayone);
				isInvitationPlay = false;
			}
			super.handleMessage(msg);
		}
	};
}
