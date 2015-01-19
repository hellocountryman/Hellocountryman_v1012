package com.feytuo.laoxianghao.view;

import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.share_qq.Share_QQ;
import com.feytuo.laoxianghao.share_sina.Share_Weibo;
import com.feytuo.laoxianghao.util.SyncHttp;
import com.feytuo.laoxianghao.wxapi.Share_Weixin;

public class MyDialog extends Dialog {
	Context context;
//	private final String targetUrl = "http://182.254.140.92:8080/xiangxiang/xiangxiang.apk";// app下载地址
	private final int imageResource = R.drawable.ic_launcher;
	private final String voiceTitle = "乡乡,熟悉的才是好玩的";
	private final String voiceDes = "远在异乡的你，是否怀念家乡的声音？";
	private String words;// 分享的文字内容
	private String audioUrl;// 分享的声音
	private int invId;

	private ImageView shareWeixinArea;
	private ImageView shareSina;
	private ImageView shareWeixinFriend;
	private ImageView shareQQFriend;
	private ImageView shareQzone;
	private ImageView shareSms;
	private Share_QQ shareQQ;
	private Share_Weibo shareWeibo;
	private Share_Weixin shareWeixin;

	public MyDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public MyDialog(Context context, Map<String, Object> map, int theme) {
		super(context, theme);
		this.context = context;
		this.words = map.get("words") + "";
		this.audioUrl = map.get("voice") + "";
		this.invId = (int)map.get("inv_id");
	}

	public MyDialog(Context context, Invitation inv, int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);
		this.context = context;
		this.words = inv.getWords();
		this.audioUrl = inv.getVoice();
		this.invId = inv.getInvId();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog);
		shareQQ = new Share_QQ(context);
		shareWeibo = new Share_Weibo(context);
		shareWeixin = new Share_Weixin(context);
		shareWeixinArea = (ImageView) findViewById(R.id.share_weixin_area);
		shareSina = (ImageView) findViewById(R.id.share_sina);
		shareWeixinFriend = (ImageView) findViewById(R.id.share_weixin_friend);
		shareQQFriend = (ImageView) findViewById(R.id.share_qq_friend);
		shareQzone = (ImageView) findViewById(R.id.share_qzone);
		shareSms = (ImageView) findViewById(R.id.share_sms);

		shareWeixinArea.setOnClickListener(loginListener);
		shareSina.setOnClickListener(loginListener);
		shareWeixinFriend.setOnClickListener(loginListener);
		shareQQFriend.setOnClickListener(loginListener);
		shareQzone.setOnClickListener(loginListener);
		shareSms.setOnClickListener(loginListener);

	}

	private View.OnClickListener loginListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new GetShareUrl().execute(invId,v.getId());
		}

	};

	// 新浪分享
	private void shareSina(String shareUrl) {
		// TODO Auto-generated method stub
		// String voiceUrl =
		// "http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
		if (shareWeibo.getmWeiboShareAPI().isWeiboAppInstalled()) {
			// 安装了客户端正常分享
			shareWeibo.sendMessage(words, shareUrl,
					imageResource, voiceTitle, voiceDes, audioUrl,
					imageResource);
		} else {
			// 未安装客户端调用openapi分享
			shareWeibo.sendMessage((Activity) context,words + shareUrl,
					imageResource);
		}
	}

	private void shareQzone(String shareUrl) {
		// TODO Auto-generated method stub
		shareQQ.shareToQQOrQzone(context,words,voiceTitle, shareUrl,
				Global.ICON, audioUrl, 1);
	}

	// 分享QQ好友
	private void shareQFriend(String shareUrl) {
		// TODO Auto-generated method stub
		shareQQ.shareToQQOrQzone(context,words,voiceTitle, shareUrl,
				Global.ICON, audioUrl, 2);
	}

	/**
	 * 分享给微信好友
	 * @param shareUrl 
	 * 
	 * @param v
	 */
	private void toWeixinFriend(String shareUrl) {
		shareWeixin.wechatShare(0, words,voiceTitle, shareUrl,
				audioUrl, R.drawable.ic_launcher);
	}

	/**
	 * 分享到朋友圈
	 * @param shareUrl 
	 * 
	 * @param v
	 */
	private void toFriendGroup(String shareUrl) {
		shareWeixin.wechatShare(1,words,voiceTitle, shareUrl,
				audioUrl, R.drawable.ic_launcher);
	}
	
	//短信分享
	private void shareSms(String shareUrl) {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse("smsto:");
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", words + "(分享自 *乡乡* ,身在异乡的你是否怀念家乡话的味道？)"+shareUrl);//短信内容
		context.startActivity(intent);
	}
	
	/**
	 * 获取当前帖子的分享url
	 * params[0] invId
	 * params[1] viewId
	 */
	class GetShareUrl extends AsyncTask<Object, Void, Integer>{

		private int viewId;
		private String shareUrl;
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			viewId = (int)params[1];
			String param = App.getCommonParams()
					+"&inv_id="+(int)params[0];
			SyncHttp client = new SyncHttp(Global.SHARE_URL);
			try {
				String response = client.get(param);
				JSONObject jsonObject = new JSONObject(response);
				int code = jsonObject.getInt("code");
				if(code == Global.code.SUCCESS){//成功
					JSONObject dataObject = jsonObject.getJSONObject("data");
					shareUrl = dataObject.getString("url");
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
				switch (viewId) {
				case R.id.share_weixin_area:
					toFriendGroup(shareUrl);
					break;
				case R.id.share_sina:
					shareSina(shareUrl);
					break;
				case R.id.share_weixin_friend:
					toWeixinFriend(shareUrl);
					break;
				case R.id.share_qq_friend:
					shareQFriend(shareUrl);
					break;
				case R.id.share_qzone:
					shareQzone(shareUrl);
					break;
				case R.id.share_sms:
					shareSms(shareUrl);
					break;
				}
				if (isShowing()) {
					dismiss();
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
}