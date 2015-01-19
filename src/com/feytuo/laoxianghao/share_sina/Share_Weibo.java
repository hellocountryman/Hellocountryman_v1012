package com.feytuo.laoxianghao.share_sina;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.Utility;

/**
 * 微博分享
 * 1、判断是否安装微博客户端
 * 2、安装，直接分享，app下载链接可以用url形式跟内容一起
 * 3、没安装，需要授权，然后调用openAPI接口进行分享，只能分享图文
 * 
 * 微博登录
 * sso授权登录，可以在回调AuthListener获取uid信息
 * @author feytuo
 *
 */
public class Share_Weibo {

	private IWeiboShareAPI mWeiboShareAPI;
	private SsoHandler mSsoHandler;
	private Context context;

	public Share_Weibo(Context context) {
		// TODO Auto-generated constructor stub
		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context,
				Constants.APP_KEY);
		if (mWeiboShareAPI.isWeiboAppInstalled()) {
			// 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
			// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
			// NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
			// 没有安装微博客户端的会弹出对话框提示
			mWeiboShareAPI.registerApp();
		}
		this.context = context;
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。
	 * 
	 * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
	 */
	public void sendMessage(String words, String targetUrl, int imageResource,
			String voiceTitle, String voiceDes, String voiceUrl,
			int voiceImageRes) {

		if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
			int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
			if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
				sendMultiMessage(words, targetUrl, imageResource, voiceTitle,
						voiceDes, voiceUrl, voiceImageRes);
			} else {
				sendSingleMessage(words, targetUrl);
			}
		} else {
			// Toast.makeText(this, R.string.weibosdk_demo_not_support_api_hint,
			// Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当
	 * {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
	 * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
	 * 
	 * @param hasText
	 *            分享的内容是否有文本
	 * @param hasImage
	 *            分享的内容是否有图片
	 * @param hasWebpage
	 *            分享的内容是否有网页
	 * @param hasMusic
	 *            分享的内容是否有音乐
	 * @param hasVideo
	 *            分享的内容是否有视频
	 * @param hasVoice
	 *            分享的内容是否有声音
	 */
	private void sendMultiMessage(String words, String targetUrl,
			int imageResource, String voiceTitle, String voiceDes,
			String voiceUrl, int voiceImageRes) {

		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		weiboMessage.textObject = getTextObj(words, targetUrl);
		weiboMessage.imageObject = getImageObj(imageResource);
		weiboMessage.mediaObject = getVoiceObj(voiceTitle, voiceDes, voiceUrl,
				voiceImageRes);
		// // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
		// if (hasWebpage) {
		// weiboMessage.mediaObject = getWebpageObj();
		// }
		// if (hasMusic) {
		// weiboMessage.mediaObject = getMusicObj();
		// }
		// if (hasVideo) {
		// weiboMessage.mediaObject = getVideoObj();
		// }
		// if (hasVoice) {
		// weiboMessage.mediaObject = getVoiceObj();
		// }

		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(request);
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()}
	 * < 10351 时，只支持分享单条消息，即 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
	 * 
	 * @param hasText
	 *            分享的内容是否有文本
	 * @param hasImage
	 *            分享的内容是否有图片
	 * @param hasWebpage
	 *            分享的内容是否有网页
	 * @param hasMusic
	 *            分享的内容是否有音乐
	 * @param hasVideo
	 *            分享的内容是否有视频
	 */
	private void sendSingleMessage(String words, String targetUrl) {

		// 1. 初始化微博的分享消息
		// 用户可以分享文本、图片、网页、音乐、视频中的一种
		WeiboMessage weiboMessage = new WeiboMessage();
		weiboMessage.mediaObject = getTextObj(words, targetUrl);
		// if (hasText) {
		// weiboMessage.mediaObject = getTextObj();
		// }
		// if (hasImage) {
		// weiboMessage.mediaObject = getImageObj();
		// }
		// if (hasWebpage) {
		// weiboMessage.mediaObject = getWebpageObj();
		// }
		// if (hasMusic) {
		// weiboMessage.mediaObject = getMusicObj();
		// }
		// if (hasVideo) {
		// weiboMessage.mediaObject = getVideoObj();
		// }
		/*
		 * if (hasVoice) { weiboMessage.mediaObject = getVoiceObj(); }
		 */

		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(request);
	}

	/**
	 * 创建文本消息对象。
	 * 
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj(String words, String targetUrl) {
		TextObject textObject = new TextObject();
		textObject.text = getSharedText(words, targetUrl);
		return textObject;
	}

	/**
	 * 获取分享的文本模板。
	 * 
	 * @return 分享的文本模板
	 */
	private String getSharedText(String words, String targetUrl) {
		String format = " %1$s（分享自 \"乡乡\",点击下载app: %2$s）";
		// String demoUrl = "http://www.baidu.com";
		String text = String.format(format, words, targetUrl);
		return text;
	}

	/**
	 * 创建图片消息对象。
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj(int imageResource) {
		ImageObject imageObject = new ImageObject();
		Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(
				imageResource)).getBitmap();
		imageObject.setImageObject(bitmap);
		return imageObject;
	}

	// /**
	// * 创建多媒体（网页）消息对象。
	// *
	// * @return 多媒体（网页）消息对象。
	// */
	// private WebpageObject getWebpageObj() {
	// WebpageObject mediaObject = new WebpageObject();
	// mediaObject.identify = Utility.generateGUID();
	// mediaObject.title = "网页title";
	// mediaObject.description = "网页描述";
	//
	// // 设置 Bitmap 类型的图片到视频对象里
	// Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(
	// R.drawable.ic_launcher)).getBitmap();
	// mediaObject.setThumbImage(bitmap);
	// mediaObject.actionUrl =
	// "http://news.sina.com.cn/c/2013-10-22/021928494669.shtml";
	// mediaObject.defaultText = "Webpage 默认文案";
	// return mediaObject;
	// }
	//
	// /**
	// * 创建多媒体（音乐）消息对象。
	// *
	// * @return 多媒体（音乐）消息对象。
	// */
	// private MusicObject getMusicObj() {
	// // 创建媒体消息
	// MusicObject musicObject = new MusicObject();
	// musicObject.identify = Utility.generateGUID();
	// musicObject.title = "音乐title";
	// musicObject.description = "音乐描述";
	//
	// // 设置 Bitmap 类型的图片到视频对象里
	// Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(
	// R.drawable.share_sina_normal)).getBitmap();
	// musicObject.setThumbImage(bitmap);
	// musicObject.actionUrl =
	// "http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
	// musicObject.dataUrl = "www.weibo.com";
	// musicObject.dataHdUrl = "www.weibo.com";
	// musicObject.duration = 10;
	// musicObject.defaultText = "Music 默认文案";
	// return musicObject;
	// }
	//
	// /**
	// * 创建多媒体（视频）消息对象。
	// *
	// * @return 多媒体（视频）消息对象。
	// */
	// private VideoObject getVideoObj() {
	// // 创建媒体消息
	// VideoObject videoObject = new VideoObject();
	// videoObject.identify = Utility.generateGUID();
	// videoObject.title = "视频title";
	// videoObject.description = "视频描述";
	//
	// // 设置 Bitmap 类型的图片到视频对象里
	// Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(
	// R.drawable.share_sina_normal)).getBitmap();
	// videoObject.setThumbImage(bitmap);
	// videoObject.actionUrl =
	// "http://video.sina.com.cn/p/sports/cba/v/2013-10-22/144463050817.html";
	// videoObject.dataUrl = "www.weibo.com";
	// videoObject.dataHdUrl = "www.weibo.com";
	// videoObject.duration = 10;
	// videoObject.defaultText = "Vedio 默认文案";
	// return videoObject;
	// }

	/**
	 * 创建多媒体（音频）消息对象。
	 * 
	 * @return 多媒体（音乐）消息对象。
	 */
	private VoiceObject getVoiceObj(String voiceTitle, String voiceDes,
			String voiceUrl, int voiceImageRes) {
		// 创建媒体消息
		VoiceObject voiceObject = new VoiceObject();
		voiceObject.identify = Utility.generateGUID();
		voiceObject.title = voiceTitle;
		voiceObject.description = voiceDes;

		// 设置 Bitmap 类型的图片到视频对象里
		Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(
				voiceImageRes)).getBitmap();
		voiceObject.setThumbImage(bitmap);
		voiceObject.actionUrl = voiceUrl;
		voiceObject.dataUrl = "http://baike.baidu.com/view/15208269.htm?fr=aladdin";
		voiceObject.dataHdUrl = "http://baike.baidu.com/view/15208269.htm?fr=aladdin";
		voiceObject.duration = 10;
		voiceObject.defaultText = "乡乡分享";
		return voiceObject;
	}

	/**
	 * 没有安装客户端的通过openAPI进行分享 三种形式：文字；本地图+文字；网络图+文字，只能任选一种
	 */
	public void sendMessage(Activity activity,String words, int resource) {
		// TODO Auto-generated method stub-----------
		// 获取当前已保存过的 Token
		Oauth2AccessToken mAccessToken = AccessTokenKeeper
				.readAccessToken(context);

		if (mAccessToken == null || !mAccessToken.isSessionValid()) {// token不可用，先sso授权，在发送
//			Toast.makeText(context,
//					R.string.weibosdk_demo_access_token_is_empty,
//					Toast.LENGTH_LONG).show();
			SSOAuthorize(activity,false, words, resource);
		} else {// token可用，直接分享
			sendMessageByOpenAPI(words, resource);
		}

	}

	/**
	 * 没有客户端通过openAPI发送消息
	 * 
	 * @param words
	 *            文字
	 * @param resource
	 *            图片资源
	 */
	private void sendMessageByOpenAPI(String words, int resource) {
		// 获取当前已保存过的 Token
		Oauth2AccessToken mAccessToken = AccessTokenKeeper
				.readAccessToken(context);
		// 对statusAPI实例化/** 用于获取微博信息流等操作的API */
		StatusesAPI mStatusesAPI = new StatusesAPI(mAccessToken);
		// /***********一条文字***********/
		// mStatusesAPI.update("发送一条纯文字微博", null, null, mListener);

		/*********** 本地图片 ***********/
		Drawable drawable = context.getResources().getDrawable(resource);
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		mStatusesAPI.upload(words, bitmap, null, null, mListener);

	}

	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				// LogUtil.i(TAG, response);
				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.total_number > 0) {
						// Toast.makeText(context,
						// "获取微博信息流成功, 条数: " + statuses.statusList.size(),
						// Toast.LENGTH_LONG).show();
					}
				} else if (response.startsWith("{\"created_at\"")) {
					// 调用 Status#parse 解析字符串成微博对象
					// Status status = Status.parse(response);
					Toast.makeText(context, "成功分享至微博", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(context, response, Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			// LogUtil.e(TAG, e.getMessage());
//			ErrorInfo info = ErrorInfo.parse(e.getMessage());
//			Toast.makeText(context, info.toString(), Toast.LENGTH_LONG).show();
		}
	};

	/**
	 * SSO授权
	 */
	public void SSOAuthorize(Context activity,boolean isLogin, String words, int resource) {
		// 创建微博实例/** 微博 Web 授权类，提供登陆等功能 */
		WeiboAuth mWeiboAuth = new WeiboAuth(activity, Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);
		mSsoHandler = new SsoHandler((Activity)activity, mWeiboAuth);
		mSsoHandler.authorize(new AuthListener(activity, isLogin, words,
				resource));
	}
	
	public IWeiboShareAPI getmWeiboShareAPI() {
		return mWeiboShareAPI;
	}

	public SsoHandler getmSsoHandler() {
		return mSsoHandler;
	}

}
