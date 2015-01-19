/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feytuo.chat.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feytuo.laoxianghao.AboutUsActivity;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.PersonInvitationActivity;
import com.feytuo.laoxianghao.PersonUpdateInfoActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.SelsectedCountry;
import com.feytuo.laoxianghao.SetActivity;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.GetUserInfoTask;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.feytuo.laoxianghao.util.CommonUtils;
import com.feytuo.laoxianghao.util.ImageLoader;
import com.feytuo.laoxianghao.util.SDcardTools;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.umeng.analytics.MobclickAgent;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class SettingsFragment extends Fragment {
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	private static final int PHOTO_REQUEST_GALLERY = 2;
	private static final int PHOTO_REQUEST_CUT = 3;
	private static final int UPDATE_NICK_NAME = 4;
	private static final int UPDATE_PERSON_SIGN = 5;
	private static final int UPDATE_HOME = 6;
	private final String TEMP_HEAD_IMAGE = "temp_head_mage.png";
	
	private RelativeLayout personNickRela;// 修改昵称
	private TextView personNickText;// 用于显示昵称

	private RelativeLayout personSignRela;// 修改个性签名
	private TextView personSignText;// 用于显示个性签名
	
	private RelativeLayout personHomeRela;//修改家乡
	private TextView personHomeText;//显示家乡

	private RelativeLayout personTieziRela;
	private RelativeLayout personSetRela;
	private RelativeLayout personAboutRela;//个人中心出现关于乡乡

	private ImageView personHeadImg;// 个人中心的头像
	private TextView personHeadNick;// 个人中心头像下面的昵称
	
	private ImageView redPoint;//我的帖子红点
	
	private AlertDialog dialog;
	File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());
	private int crop = 180;
	
	private LXHUser user;
	private ImageLoader mImageLoader;
	private File uploadFile;
	private Bitmap photo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.person_activity, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mImageLoader = new ImageLoader(getActivity());
		initview();
		setViewContent();
	}

	public void initview() {

		redPoint = (ImageView) getView().findViewById(R.id.person_red_point);
		personHeadImg = (ImageView) getView()
				.findViewById(R.id.person_head_img);
		personHeadNick = (TextView) getView().findViewById(
				R.id.person_head_nick);
		personNickText = (TextView)getView().findViewById(R.id.person_nick_text);
		personSignText = (TextView)getView().findViewById(R.id.person_sign_text);
		personHomeText = (TextView)getView().findViewById(R.id.person_home_text);
		personNickRela = (RelativeLayout) getView().findViewById(
				R.id.person_nick_rela);
		personSignRela = (RelativeLayout) getView().findViewById(
				R.id.person_sign_rela);
		personTieziRela = (RelativeLayout) getView().findViewById(
				R.id.person_tiezi_rela);
		personSetRela = (RelativeLayout) getView().findViewById(
				R.id.person_set_rela);
		personHomeRela = (RelativeLayout) getView().findViewById(
				R.id.person_home_rela);
		personAboutRela= (RelativeLayout) getView().findViewById(
				R.id.person_about_rela);
		Linstener linstener = new Linstener();
		personHeadImg.setOnClickListener(linstener);
		personNickRela.setOnClickListener(linstener);
		personSignRela.setOnClickListener(linstener);
		personTieziRela.setOnClickListener(linstener);
		personAboutRela.setOnClickListener(linstener);
		personSetRela.setOnClickListener(linstener);
		personHomeRela.setOnClickListener(linstener);
	}

	private void setViewContent() {
		// TODO Auto-generated method stub
		user = new LXHUserDao(getActivity()).getCurrentUserInfo(App.pre.getInt(Global.UID, -1));
		CommonUtils.corner(getActivity(), R.drawable.default_avatar, personHeadImg);// 设置圆角
		if(user != null){
			setContent(user);
		}else{
			//从服务器获取
			Log.i("personInfo", "userId:"+App.pre.getInt(Global.UID, -1));
			new GetUserInfo().execute(App.pre.getInt(Global.UID, -1));
		}
	}
	private void setContent(LXHUser user){
		if(user != null){
			personHeadNick.setText(user.getNickName());
			personNickText.setText(user.getNickName());
			personSignText.setText(user.getPersonSign());
			personHomeText.setText(user.getHome());
			setHead(user.getuId(), Global.FILE_HOST+user.getHeadUrl(), personHeadImg);
		}
	}
	//设置头像
	private void setHead(int userId,String headUrl ,ImageView head_iv) {
		// TODO Auto-generated method stub
		if(!TextUtils.isEmpty(headUrl)){//如果本地数据库存在该用户
			mImageLoader.loadCornerImage(headUrl, null, head_iv);
		}else{
			new GetUserInfoTask().execute(userId, null, head_iv,null);
		}
	}

	class Linstener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.person_head_img:
				// 弹出对话框，选择照相还是相册
				if (dialog == null) {
					dialog = new AlertDialog.Builder(getActivity()).setItems(
							new String[] { "相机", "相册" },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										Intent intent = new Intent(
												MediaStore.ACTION_IMAGE_CAPTURE);
										intent.putExtra(
												MediaStore.EXTRA_OUTPUT,
												Uri.fromFile(tempFile));
										Log.e("file", tempFile.toString());
										startActivityForResult(intent,
												PHOTO_REQUEST_TAKEPHOTO);

									} else {
										Intent intent = new Intent(
												Intent.ACTION_PICK, null);
										intent.setDataAndType(
												MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
												"image/*");
										startActivityForResult(intent,
												PHOTO_REQUEST_GALLERY);
									}
								}
							}).create();
				}
				if (!dialog.isShowing()) {
					dialog.show();
				}
				break;
			case R.id.person_nick_rela:
				intent.setClass(getActivity(), PersonUpdateInfoActivity.class);
				intent.putExtra("type", "nick");//
				startActivityForResult(intent, UPDATE_NICK_NAME);
				break;
			case R.id.person_sign_rela:
				intent.setClass(getActivity(), PersonUpdateInfoActivity.class);
				intent.putExtra("type", "sign");
				startActivityForResult(intent, UPDATE_PERSON_SIGN);
				break;
			case R.id.person_tiezi_rela:
				redPoint.setVisibility(View.GONE);
				intent.setClass(getActivity(), PersonInvitationActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.person_about_rela:
				intent.setClass(getActivity(), AboutUsActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.person_set_rela:
				intent.setClass(getActivity(), SetActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.person_home_rela:
				intent.setClass(getActivity(), SelsectedCountry.class);
				intent.putExtra("isfromtocity", 2);
				startActivityForResult(intent, UPDATE_HOME);
				break;
			default:
				break;
			}
		}

	}

	// 接收data返回的值
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(tempFile), 150);
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData(), 150);
			break;

		case PHOTO_REQUEST_CUT:
			Log.e("zoom", "begin2");
			if (data != null)
				setPicToView(data);
			break;
		}
		if(resultCode == Global.RESULT_OK){
			String resultData = data.getStringExtra("data").toString().trim();
			switch(requestCode){
			case UPDATE_NICK_NAME:
				personNickText.setText(resultData);
				personHeadNick.setText(resultData);
				break;
			case UPDATE_PERSON_SIGN:
				personSignText.setText(resultData);
				break;
			case UPDATE_HOME:
				personHomeText.setText(resultData);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	// 使用默认的裁切工具进行图的裁切
	private void startPhotoZoom(Uri uri, int size) {
		Log.e("zoom", "begin");
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", crop);// 输出图片大小
		intent.putExtra("outputY", crop);
		intent.putExtra("return-data", true);
		Log.e("zoom", "begin1");
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	private ProgressDialog pd;
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			pd = new ProgressDialog(getActivity());
			pd.setMessage("正在上传头像...");
			pd.setCanceledOnTouchOutside(false);
			pd.show();
			photo = bundle.getParcelable("data");
			if (!SDcardTools.isHaveSDcard()) {
				Toast.makeText(getActivity(), "请插入SD卡以便存储头像",
						Toast.LENGTH_LONG).show();
				return;
			}
			File dir = new File(SDcardTools.getSDPath()+"/laoxianghaoAudio");
			if(!dir.exists()){
				dir.mkdir();
			}
			uploadFile = new File(SDcardTools.getSDPath()+"/laoxianghaoAudio/"+TEMP_HEAD_IMAGE);
			try {
				FileOutputStream ous = new FileOutputStream(uploadFile);
				photo.compress(CompressFormat.PNG, 100, ous);
				ous.flush();
				ous.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//上传到服务器
			if (uploadFile != null && uploadFile.exists()) {
				new UpdateUserHeadTask().execute(uploadFile,1);
			}
		}
	}
	class UpdateUserHeadTask extends AsyncTask<Object, Void, Integer>{

		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			SyncHttp client = new SyncHttp(Global.UPDATE_USER_INFO);
			try {
				String response = client.filePost(getParams(params),(File)params[0],"content");
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				String headUrl = jsonObject.getString("data");
				if(code == Global.code.SUCCESS){//成功
					new LXHUserDao(getActivity()).updateUserHeadUrl(App.pre.getString(Global.UNAME, ""), headUrl);
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
				//设置头像
				CommonUtils.corner(getActivity(), photo, personHeadImg);
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
			pd.dismiss();
			super.onPostExecute(result);
		}
		
		private HashMap<String, Object> getParams(Object... params){
			HashMap<String, Object> urlParams = new HashMap<String, Object>();
			urlParams.put("u_id", App.pre.getInt(Global.UID, -1));
			urlParams.put("u_token", App.pre.getString(Global.TOKEN, ""));
			urlParams.put("u_divice_id", AppInfoUtil.getDeviceId(App.applicationContext));
			urlParams.put("type",(int)params[1]);
			return urlParams;
		}
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
	
	class GetUserInfo extends AsyncTask<Object, Void, Integer>{

		private LXHUser mUser;
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String param = "u_id="+(int)params[0]
					+"&u_divice_id="+AppInfoUtil.getDeviceId(App.applicationContext)
					+"&u_token="+App.pre.getString(Global.TOKEN, "");
			SyncHttp client = new SyncHttp(Global.GET_USER_INFO);
			try {
				String response = client.get(param);
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == Global.code.SUCCESS){//成功
					if(jsonObject.isNull("data")){
						return Global.code.FAILURE;
					}
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
					setContent(mUser);
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
	/**
	 * 获取用户帖子判断评论数是否有更新
	 * "我的帖子"的评论数是否有更新
	 */
	class GetMyCommentNotice extends AsyncTask<Object, Void, Integer>{

		private List<Invitation> invList;
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String param = "u_id="+App.pre.getInt(Global.UID, -1)
					+"&u_divice_id="+AppInfoUtil.getDeviceId(getActivity())
					+"&u_token="+App.pre.getString(Global.TOKEN, "");
			SyncHttp client = new SyncHttp(Global.GET_USER_INVITATION);
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
				final List<Integer> localCommentNum = new ArrayList<Integer>();
				final List<Integer> netCommentNum = new ArrayList<Integer>();
				final List<Integer> myInvIds = new ArrayList<Integer>();
				new InvitationDao(getActivity()).getAllCommentNum(localCommentNum, myInvIds);
				for (Invitation inv : invList) {
					netCommentNum.add(inv.getCommentNum());
				}
				// 对比本地和服务器评论数是否有不同
				if(myInvIds.size() > 0){
					for (int i = 0; i < netCommentNum.size(); i++) {
						int idIndex = 0;
						for (int j = 0; j < myInvIds.size(); j++) {
							if (invList.get(i).getInvId() == myInvIds.get(j)) {
								idIndex = j;
								break;
							}
						}
						Log.i("commentNumber", ""+i+"---"+idIndex);
						Log.i("commentNumber", ""+netCommentNum.get(i)+"***"+localCommentNum.get(idIndex));
						if (netCommentNum.get(i) != localCommentNum.get(idIndex)) {
							redPoint.setVisibility(View.VISIBLE);
							break;
						}
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
		private void updateData(JSONObject jsonObject) {
			// TODO Auto-generated method stub
			invList = new ArrayList<Invitation>();
			if(jsonObject.isNull("data")){
				return ;
			}
			try {
				JSONArray dataObject = jsonObject.getJSONArray("data");
				for(int i = 0; i < dataObject.length(); i++){
					JSONObject itemObject = dataObject.optJSONObject(i);
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
					invList.add(inv);
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
		new GetMyCommentNotice().execute();
		MobclickAgent.onPageStart("SettingsFragment"); // 统计页面
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("SettingsFragment");
	}
}
