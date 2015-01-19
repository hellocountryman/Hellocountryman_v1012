package com.feytuo.laoxianghao.global;

import org.json.JSONObject;

import android.os.AsyncTask;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.util.SyncHttp;

/**
 * 改变点赞数
 *params 0代表inv_id，1是点赞（0）还是取消点赞（1）
 */
public class ChangePraiseNumTask extends AsyncTask<Object, Void, Integer>{

	@Override
	protected Integer doInBackground(Object... params) {
		// TODO Auto-generated method stub
		String param = App.getCommonParams()
				+"&inv_id="+(int)params[0]
				+"&type="+(int)params[1];
		SyncHttp client = new SyncHttp(Global.CHANGE_PRAISE_NUM);
		try {
			String response = client.get(param);
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
