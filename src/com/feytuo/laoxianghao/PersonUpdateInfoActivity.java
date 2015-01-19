package com.feytuo.laoxianghao;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.util.EMLog;
import com.feytuo.chat.widget.PasteEditText;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.umeng.analytics.MobclickAgent;

public class PersonUpdateInfoActivity extends Activity {
	private TextView titleTypeText;//显示昵称还是个性签名
	private TextView typeTint;//文字输入提示
	private PasteEditText mEditText;// 设置昵称的edit
	private RelativeLayout rela;// 设置昵称的底部横线
	private TextView wordnumText;//提示还可以输入多少字
	private String type;//判断是修改昵称，还是签名
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_person_info_activity);
		type=getIntent().getStringExtra("type");
		initview();

	}

	public void initview()
	{
		titleTypeText=(TextView)findViewById(R.id.title_type_text);
		typeTint=(TextView)findViewById(R.id.type_hint);
		// //昵称
		mEditText = (PasteEditText) findViewById(R.id.person_edit);
		if(type.equals("nick"))//如果是修改昵称
		{
			titleTypeText.setText("修改昵称");
			typeTint.setText("好名字可以让你的朋友更加容易记住你");
		}
		else
		{
			titleTypeText.setText("修改签名");
			typeTint.setText("签名即个性，秀出你的个性吧");
		}
		rela = (RelativeLayout) findViewById(R.id.edittext_rela);
		wordnumText=(TextView)findViewById(R.id.wordnumtext);
		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					rela.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					rela.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}
			}
		});
		
		mEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				wordnumText.setText(20-s.length()+"/20");	
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	//点击修改完成按钮
	public void updateInfoSuccess(View v)
	{
		updateUserInfo(mEditText.getText().toString().trim());
	}
	

	/**
	 * 修改个性签名
	 * @param et
	 */
	private void updateUserInfo(final String et) {
		// TODO Auto-generated method stub
		LXHUser user = new LXHUser();
		if("nick".equals(type)){//昵称
			user.setNickName(et);
			new UpdateUserInfoTask().execute(et,2);
		}else{//个性签名
			user.setPersonSign(et);
			new UpdateUserInfoTask().execute(et,3);
		}
	}
	/**
	 * 修改用户信息
	 * @author hand
	 *
	 */
	class UpdateUserInfoTask extends AsyncTask<Object, Void, Integer>{

		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			SyncHttp client = new SyncHttp(Global.UPDATE_USER_INFO);
			try {
				String response = client.post(getParams(params));
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == Global.code.SUCCESS){//成功
					int type = (int)params[1];
					if(type == 2){//昵称
						new LXHUserDao(App.applicationContext).updateUserNickName(App.pre.getInt(Global.UID, -1), (String)params[0]);
						//更新环信上的昵称
						boolean updatenick = EMChatManager.getInstance()
								.updateCurrentUserNick((String)params[0]);
						if (!updatenick) {
							EMLog.e("PersonUpdateInfo", "update current user nick fail");
						}
					}else if(type == 3){//个性签名
						new LXHUserDao(App.applicationContext).updateUserPersonSign(App.pre.getInt(Global.UID, -1), (String)params[0]);
					}
					Intent intent = new Intent();
					intent.putExtra("data", (String)params[0]);
					setResult(Global.RESULT_OK,intent);
					finish();
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
		
		private HashMap<String, Object> getParams(Object... params){
			HashMap<String, Object> urlParams = new HashMap<String, Object>();
			urlParams.put("u_id", App.pre.getInt(Global.UID, -1));
			urlParams.put("u_token", App.pre.getString(Global.TOKEN, ""));
			urlParams.put("u_divice_id", AppInfoUtil.getDeviceId(App.applicationContext));
			urlParams.put("content",(String)params[0]);
			urlParams.put("type",(int)params[1]);
			return urlParams;
		}
	}
	public void personDetailsRetImg(View v) {
		setResult(Global.RESULT_RETURN);
		finish();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("PersonUpdateInfoActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("PersonUpdateInfoActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
