package com.feytuo.laoxianghao.global;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.ImageLoader;
import com.feytuo.laoxianghao.util.SyncHttp;

/**
 * 获取用户信息
 * params[0] uId
 * params[1] nickNameTextView
 * params[2] headImageView
 * params[3] adapter
 */
public class GetUserInfoTask extends AsyncTask<Object, Void, Integer>{

	private LXHUser mUser;
	private TextView nameTV;
	private ImageView headIV;
	private BaseAdapter adapter;
	@Override
	protected Integer doInBackground(Object... params) {
		// TODO Auto-generated method stub
		nameTV = (TextView)params[1];
		headIV = (ImageView)params[2];
		adapter = (BaseAdapter)params[3];
		String param = "u_id="+(int)params[0]
				+"&u_divice_id="+AppInfoUtil.getDeviceId(App.applicationContext)
				+"&u_token="+App.pre.getString(Global.TOKEN, "");
		SyncHttp client = new SyncHttp(Global.GET_USER_INFO);
		try {
			String response = client.get(param);
			JSONObject jsonObject = new JSONObject(response);
			int code = jsonObject.getInt("code");
			if(code == Global.code.SUCCESS){//成功
				JSONObject dataObject = jsonObject.getJSONObject("data");
				mUser = new LXHUser();
				mUser.setuId(dataObject.getInt("u_id"));
				mUser.setuName(dataObject.getString("u_name"));
				mUser.setuKey(dataObject.getString("u_key"));
				mUser.setHeadUrl(dataObject.getString("u_head"));
				mUser.setNickName(dataObject.getString("u_nick"));
				mUser.setHome(dataObject.getString("u_home"));
				mUser.setPersonSign(dataObject.getString("u_person_sign"));
				mUser.setSex(dataObject.getString("u_sex"));
				mUser.setAge(dataObject.getInt("u_age"));
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
			if(mUser != null){
				if(nameTV != null){
					nameTV.setText(mUser.getNickName());
				}
				if(headIV != null){
					new ImageLoader(App.applicationContext).loadCornerImage(
							Global.FILE_HOST+mUser.getHeadUrl(), adapter, headIV);
				}
				if(App.pre.getInt(Global.UID, -1) != mUser.getuId()){//其它用户
					new LXHUserDao(App.applicationContext).insertUser(mUser);
				}else{//用户本人
					new LXHUserDao(App.applicationContext).insertCurrentUser(mUser);
				}
			}
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
}
