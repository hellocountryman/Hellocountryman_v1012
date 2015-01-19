package com.feytuo.laoxianghao.global;

import java.util.HashMap;

import org.json.JSONObject;

import android.os.AsyncTask;

import com.easemob.chat.EMChatManager;
import com.easemob.util.EMLog;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.SyncHttp;

/**
 * 修改用户信息
 * params
 * 0为content
 * 1为type
 */
public class UpdateUserInfoTask extends AsyncTask<Object, Void, Integer>{

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
			//
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
