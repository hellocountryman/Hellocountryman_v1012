package com.feytuo.laoxianghao.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.feytuo.laoxianghao.global.Global;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信分享
 * @author feytuo
 *
 */
public class Share_Weixin {

	private IWXAPI wxApi;
	private Context context;

	public Share_Weixin(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		// 实例化
		wxApi = WXAPIFactory.createWXAPI(context, Global.WEIXIN_APPID, true);
		wxApi.registerApp(Global.WEIXIN_APPID);
	}

	/**
	 * 微信分享
	 * 
	 * @param flag
	 *            (0:分享到微信好友，1：分享到微信朋友圈)
	 */
	public void wechatShare(int flag, String title, String words,
			String targetUrl, String voiceUrl, int imageResource) {
		// WXWebpageObject object = new WXWebpageObject();
		// object.webpageUrl = "www.hao123.com";
		WXMusicObject object = new WXMusicObject();
		object.musicUrl = targetUrl;// 分享链接，可以提供下载app链接
		// 音乐链接
		object.musicDataUrl = voiceUrl;
		WXMediaMessage msg = new WXMediaMessage(object);
		msg.title = title;
		msg.description = words;
		// 这里替换一张自己工程里的图片资源
		Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),
				imageResource);
		msg.setThumbImage(thumb);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}
}
