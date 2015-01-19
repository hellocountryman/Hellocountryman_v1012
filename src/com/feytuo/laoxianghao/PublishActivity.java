package com.feytuo.laoxianghao;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feytuo.laoxianghao.dao.CityDao;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.GetSystemDateTime;
import com.feytuo.laoxianghao.util.Location_Baidu;
import com.feytuo.laoxianghao.util.SDcardTools;
import com.feytuo.laoxianghao.util.StringTools;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.feytuo.laoxianghao.view.OnloadDialog;
import com.feytuo.laoxianghao.view.PositionChooseDialog;
import com.umeng.analytics.MobclickAgent;

public class PublishActivity extends Activity {

	private Button publishButton;// 发布按钮
	private ImageView publishRerecordButton;// 录音之后可以取消录音按钮
	private ImageView publishPlayRecordImgbutton;// 录音之后进度条中出现播放按钮
	private TextView publishHint;// 进度条中显示的文字提醒
	private TextView publishRecordTime;
	private TextView publishTypeText;// 发布的类型
	private TextView publishTitleLocation;
	private TextView publishHomeText;//方言地
	private EditText publishText;
	private ImageView headImage;
	private LinearLayout publishRecordingLinear;// 点击录音的时候出现动画提示，
	private ImageView publishRecordingImg;// //点击录音的时候出现动画提示，
	private MediaPlayer mp = new MediaPlayer();
	private String fileAudioName; // 保存的音频文件的名字
	private MediaRecorder mediaRecorder; // 录音控制
	private String filePath; // 音频保存的文件路径
	private boolean isLuYin; // 是否在录音 true 是 false否
	private File fileAudio; // 录音文件
	private boolean isReplay = false;
	private TextView publishwordnumText;// 还能输入多少字
	private CountDownTimer mCountDownTimer;
	private Timer mCountUpTimer;
	private Timer mProgressTimer;
	private int mRecordTime;
	private int type;// 0为全部，2为方言段子，3为方言KTV，4为方言秀场
	private RelativeLayout publishCityRela;//选择城市
	private static final int CHOOSE_CITY = 0;
	private String currentCity;
	private Dialog dialog;
	/**
	 * 进度条
	 */
	private Button progress = null;

	// 百度定位
	private Location_Baidu locationBaidu;
	// 初始化所在地选择对话框
	private PositionChooseDialog positionChooseDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish);

		initViewType();
		initView();
		// 初始化录音
		initData();
		// 开始定位
		initLocation();
		//初始化家乡话
		initHome();
	}

	private void initHome() {
		// TODO Auto-generated method stub
		LXHUser user = new LXHUserDao(this).getCurrentUserInfo(App.pre.getInt(Global.UID, -1));
		currentCity = user.getHome();
		publishHomeText.setText(user.getHome()+"话");
	}

	/*
	 * 百度定位
	 */
	private void initLocation() {
		// TODO Auto-generated method stub
		publishTitleLocation = (TextView) findViewById(R.id.publish_title_location);
		locationBaidu = new Location_Baidu(this, publishTitleLocation);
		locationBaidu.start();
	}
	/**
	 * 点击设置所在地按钮
	 * @param v
	 */
	public void setPosition(View v){
		positionChooseDialog = new PositionChooseDialog(this,publishTitleLocation.getText().toString());
		positionChooseDialog.show();
	}
	public void setPosition(String location){
		if(!TextUtils.isEmpty(location)){
			publishTitleLocation.setText(location);
		}
	}

	public void initViewType() {
		// 判断是发布的类型的那一个版块
		type = getIntent().getIntExtra("type", 0);
		publishTypeText = (TextView) findViewById(R.id.publish_type_text);// 发布的类型
		if (type == 2) {
			publishTypeText.setText("发布到方言段子");
		} else if (type == 3) {
			publishTypeText.setText("发布到方言KTV");
		} else if (type == 4) {
			publishTypeText.setText("发布到方言秀场");
		} else {
			publishTypeText.setText("发布到首页");
		}
		
	}

	/*
	 * 
	 * 初始化数据
	 */
	public void initView() {

		listener listenerlist = new listener();
		publishRecordingLinear = (LinearLayout) findViewById(R.id.publish_recording_linear);
		publishRecordingImg = (ImageView) findViewById(R.id.publish_recording_img);
		progress = (Button) findViewById(R.id.progressbar_id);
		publishwordnumText = (TextView) findViewById(R.id.publish_wordnum_text);
		publishText = (EditText) findViewById(R.id.publish_text);
		publishCityRela = (RelativeLayout)findViewById(R.id.publish_select_home_rela);
		publishHomeText = (TextView)findViewById(R.id.publish_home_text);
		// 还能够输入多少字
		publishText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				publishwordnumText.setText(42 - s.length() + "/42");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		publishHint = (TextView) findViewById(R.id.publish_hint);
		publishRecordTime = (TextView) findViewById(R.id.publish_record_time);
		publishButton = (Button) findViewById(R.id.publish_button);
		publishRerecordButton = (ImageView) findViewById(R.id.publish_rerecord_button);
		publishPlayRecordImgbutton = (ImageView) findViewById(R.id.publish_play_record_imgbutton);
		publishCityRela.setOnClickListener(listenerlist);
		progress.setOnClickListener(listenerlist);
		publishButton.setOnClickListener(listenerlist);
		publishRerecordButton.setOnClickListener(listenerlist);
		publishPlayRecordImgbutton.setOnClickListener(listenerlist);

	}

	/*
	 * 返回
	 */
	public void publishReturnRelative(View v) {
		finish();
	}

	/*
	 * 
	 * 初始化数据
	 */
	private void initData() {
		if (!SDcardTools.isHaveSDcard()) {
			Toast.makeText(PublishActivity.this, "请插入SD卡以便存储录音",
					Toast.LENGTH_LONG).show();
			return;
		}

		// 要保存的文件的路径
		filePath = SDcardTools.getSDPath() + "/" + "laoxianghaoAudio";
		// 实例化文件夹
		File dir = new File(filePath);
		if (!dir.exists()) {
			// 如果文件夹不存在 则创建文件夹
			dir.mkdir();
		}
	}

	class listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.publish_select_home_rela:
				Intent intentselsectcity = new Intent();
				intentselsectcity.putExtra("isfromtocity", 3);// 判断是从那里进入的城市选择
				intentselsectcity.setClass(PublishActivity.this, SelsectedCountry.class);
				startActivityForResult(intentselsectcity, CHOOSE_CITY);
				break;
			case R.id.progressbar_id:// 开始录音-结束录音按钮
				if (null != mediaRecorder) {
					stopAudio();
				} else {
					startAudio();
				}
				break;
			case R.id.publish_rerecord_button:// 重录按钮
				recordAudio();
				break;
			case R.id.publish_play_record_imgbutton:// 回放
				if (!isReplay) {
					playAudio();
				} else {
					pauseAudio();
				}
				break;
			case R.id.publish_button: // 发布按钮
				savePublish();
				break;
			}
		}

	}
	// 接收data返回的值
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == Global.RESULT_OK){
			String resultData = data.getStringExtra("data").toString().trim();
			switch(requestCode){
			case CHOOSE_CITY:
				currentCity = resultData;
				publishHomeText.setText(resultData+"话");
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}
	// 设置大头贴
	public void setHeadImg(int headResource) {
		headImage.setBackgroundResource(headResource);
	}

	/**
	 * 将数据保存到服务器和本地
	 */
	public void savePublish() {
		// 如果既没有录音又没有文字
		if (fileAudio == null && "".equals(publishText.getText().toString())) {
			Toast.makeText(this, "先来句家乡话咯~", Toast.LENGTH_SHORT).show();
		} else {
			// 上传发布数据
			if ("".equals(publishText.getText().toString())) {
				publishText.setText("");
			}
			new PublishInvitation().execute();
		}
	}
	/**
	 * 发布帖子
	 *
	 */
	class PublishInvitation extends AsyncTask<Object, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = new OnloadDialog(PublishActivity.this, R.style.LoadDialog,
					true);
			dialog.show();
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			SyncHttp client = new SyncHttp(Global.PUBLISH_INVITATION);
			try {
				String response;
				if (fileAudio != null && fileAudio.exists()) {
					response = client.filePost(getParams(),fileAudio,"voice");
				}else{
					response = client.post(getParams());
				}
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == 100){//成功
					return Global.code.SUCCESS;
				}else if(code == 101){//失败
					return Global.code.FAILURE;
				}else{//code == 801用户失效
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
				// 提示发布成功
				closeProgressDialog();
				// 主列表需要刷新
				App.pre.edit().putBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, true)
						.commit();
				Toast.makeText(PublishActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
				finish();
			}
				break;
			case Global.code.FAILURE: {
			}
				break;
			case Global.code.ERROR: {
			}
				break;
			case Global.code.TOKEN_DISABLE: {
				//返回登录界面
			}
				break;
			}
			closeProgressDialog();
			super.onPostExecute(result);
		}
		private HashMap<String, Object> getParams(){
			HashMap<String, Object> urlParams = new HashMap<String, Object>();
			urlParams.put("u_id", App.pre.getInt(Global.UID, -1));
			urlParams.put("u_divice_id", AppInfoUtil.getDeviceId(PublishActivity.this));
			urlParams.put("u_token", App.pre.getString(Global.TOKEN, ""));
			urlParams.put("home", new CityDao(PublishActivity.this).getCityIdByName(currentCity));
			urlParams.put("position", publishTitleLocation.getText().toString());
			urlParams.put("words", publishText.getText().toString());
			urlParams.put("voice_duration", mRecordTime);
			urlParams.put("type", type);
			return urlParams;
		}
	}


	private void closeProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private AnimationDrawable animationDrawable;

	/*
	 * 
	 * 开始录音
	 */
	private void startAudio() {
		publishRecordTime.setVisibility(View.VISIBLE);
		publishRecordingLinear.setVisibility(View.VISIBLE);// 显示出录音时候的动画
		publishRecordingImg.setBackgroundResource(R.anim.frame_comment_anim);// 正在录音的动画
		publishHint.setText("再点一下结束录音");
		animationDrawable = (AnimationDrawable) publishRecordingImg
				.getBackground();
		animationDrawable.start();
		progress.setBackgroundResource(R.drawable.comment_record_press);
		publishRerecordButton.setVisibility(View.INVISIBLE);
		publishButton.setTextColor(getResources().getColor(
				R.color.publish_btn_no_color));
		publishButton.setClickable(false);
		// 初始化计时器
		mCountUpTimer = new Timer();
		// 开始计时
		mCountUpTimer.schedule(new CountTask(), 1000l, 1000l);

		// 创建录音频文件
		// 这种创建方式生成的文件名是随机的
		fileAudioName = "audio" + GetSystemDateTime.now()
				+ StringTools.getRandomString(2) + ".mar";
		mediaRecorder = new MediaRecorder();
		// 设置录音的来源为麦克风
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setOutputFile(filePath + "/" + fileAudioName);
		try {
			mediaRecorder.setOnErrorListener(new OnErrorListener() {

				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
					// TODO Auto-generated method stub
					stopAudio();
					// Toast.makeText(PublishActivity.this, "录音错误，请稍候再试",
					// Toast.LENGTH_SHORT)
					// .show();
					Log.i("PublishActivity", "录音错误");
				}
			});
			mediaRecorder.prepare();
			mediaRecorder.start();

			fileAudio = new File(filePath + "/" + fileAudioName);
			isLuYin = true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * 
	 * 停止录制
	 */
	private void stopAudio() {
		animationDrawable.stop();
		publishRecordingLinear.setVisibility(View.GONE);// 显示出录音时候的动画
		publishHint.setText("点击试听");
		publishButton.setTextColor(getResources().getColor(R.color.white));
		publishButton.setClickable(true);
		if (null != mediaRecorder) {
			// 停止录音
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;

			// 停止计时
			mCountUpTimer.cancel();
			// 设置UI
			publishPlayRecordImgbutton.setVisibility(View.VISIBLE);// 播放和重录的功能按钮显示
			publishPlayRecordImgbutton
					.setBackgroundResource(R.drawable.comment_record_play);// 点击播放
			publishRerecordButton.setVisibility(View.VISIBLE);
		}
	}

	// 播放已经录好的音
	private void playAudio() {
		// 设置ui
		publishPlayRecordImgbutton
				.setBackgroundResource(R.drawable.comment_record_pause);// 点击暂停
		publishRerecordButton.setVisibility(View.INVISIBLE);
		isReplay = true;
		// 点击播放而已
		try {
			mp.reset();
			mp.setDataSource(filePath + "/" + fileAudioName);
			mp.prepare();
			mp.seekTo(0);
			mp.start();
			publishHint.setText("录音正在播放");
			// //实现倒计时
			// mCountDownTimer = new MyCount(mp.getDuration(), 1000);
			mCountDownTimer = new MyCount((mRecordTime) * 1000 + 50, 1000);
			mCountDownTimer.start();
			// 显示进度条
			// showIndeterDialog(mp.getDuration() / 1000);
			// //
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

	// 停止当前播放的录音
	private void pauseAudio() {
		Log.i("tangpeng", "停止录音的");
		// TODO Auto-generated method stub
		if (mp.isPlaying()) {
			mp.stop();
		}
		// 倒计时停止
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		// 进度条进度停止并归0，重录按钮可见
		if (mProgressTimer != null) {
			mProgressTimer.cancel();
		}

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// progress.setProgress(0);
			}
		});
		// Log.i("progress", "progress:"+progress.getProgress());
		publishHint.setText("点击播放");
		publishRecordTime.setText(mRecordTime + "\"");
		publishRerecordButton.setVisibility(View.VISIBLE);
		publishPlayRecordImgbutton
				.setBackgroundResource(R.drawable.comment_record_play);
		isReplay = false;
	}

	// 重新录音
	private void recordAudio() {
		publishRecordTime.setVisibility(View.GONE);
		publishHint.setText("点一下开始录音");
		publishHint.setVisibility(View.VISIBLE);
		publishPlayRecordImgbutton.setVisibility(View.GONE);
		publishRerecordButton.setVisibility(View.INVISIBLE);
		progress.setBackgroundResource(R.drawable.comment_record_no);
		// 初始化录音
		mRecordTime = 0;
		publishRecordTime.setText(mRecordTime + "\"");
		if (fileAudio != null) {
			fileAudio.delete();// 文件删除
			fileAudio = null;
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
			isReplay = false;
			publishPlayRecordImgbutton
					.setBackgroundResource(R.drawable.comment_record_play);// 点击播放
			publishRerecordButton.setVisibility(View.VISIBLE);
			publishRecordTime.setText(mRecordTime + "\"");
			publishHint.setText("点击试听");
			Log.i("tangpeng", "试听完成的时候");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			Log.i("countdown", millisUntilFinished + "");
			publishRecordTime.setText(millisUntilFinished / 1000 + "\"");
		}
	}

	/**
	 * 计时器
	 */
	class CountTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(1);
		}

	}

	/**
	 * Handler消息处理
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {// 录音计时器
				mRecordTime++;
				publishRecordTime.setText(mRecordTime + "\"");
			} else if (msg.what == 0) {
				mProgressTimer.cancel();
				publishRerecordButton.setVisibility(View.VISIBLE);
				publishRecordTime.setText(mRecordTime + "\"");
				// publishPlayRecordImgbutton
				// .setBackgroundResource(R.drawable.play_ico);
			}
			super.handleMessage(msg);
		}
	};

	protected void onDestroy() {
		if (mp != null) {
			mp.release();
		}
		if (mCountUpTimer != null) {
			mCountUpTimer.cancel();
		}
		if (mProgressTimer != null) {
			mProgressTimer.cancel();
		}
		locationBaidu.stop();
		if (null != mediaRecorder && isLuYin) {
			mediaRecorder.release();
		}
		if (fileAudio != null) {
			fileAudio.delete();
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("PublishActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("PublishActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

}
