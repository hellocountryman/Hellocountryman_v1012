package com.feytuo.laoxianghao;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.umeng.analytics.MobclickAgent;

public class FeedbackActivity extends Activity {

	private Button  feedbackSetButton;
	private EditText publishText;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		listener listenerlist = new listener();
		feedbackSetButton = (Button) findViewById(R.id.feedback_set_button);
		feedbackSetButton.setOnClickListener(listenerlist);
		publishText = (EditText)findViewById(R.id.feedback_publish_text);
	}

	public void feedbackReturnRelative(View v) {
		finish();
	}

	class listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.feedback_set_button:
				//发送反馈信息
				sendFeedBack();
				break;
			}
		}
	}
	
	/**
	 * 发送反馈数据
	 */
	public void sendFeedBack(){
		if("".equals(publishText.getText().toString())){
			Toast.makeText(this, "请尽情吐槽吧~", Toast.LENGTH_SHORT).show();
		}else{
			new FeedbackTask().execute();
		}
	}
	/**
	 * 获取首页帖子
	 *params 0代表page，1代表是否刷新（否加载更多）
	 */
	class FeedbackTask extends AsyncTask<Object, Void, Integer>{

		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			SyncHttp client = new SyncHttp(Global.FEEDBACK);
			try {
				String response = client.post(getParams());
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == Global.code.SUCCESS){//成功
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
			}
				break;
			}
			super.onPostExecute(result);
		}
		private HashMap<String, Object> getParams(){
			HashMap<String, Object> urlParams = new HashMap<String, Object>();
			urlParams.put("u_id", App.pre.getInt(Global.UID, -1));
			urlParams.put("u_divice_id", AppInfoUtil.getDeviceId(FeedbackActivity.this));
			urlParams.put("u_token", App.pre.getString(Global.TOKEN, ""));
			urlParams.put("content", publishText.getText().toString());
			urlParams.put("version", AppInfoUtil.getAppVersionName(FeedbackActivity.this));
			return urlParams;
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("FeedbackActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("FeedbackActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
