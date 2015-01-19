package com.feytuo.laoxianghao.global;


public interface Global {

	/**
	 * preference string
	 */
	public static final String PREFERENCE_NAME = "xiangxiang";//preference名
	public static final String IS_FIRST_USE = "isFirstUse";//保存在pre中是否首次打开
	public static final String CURRENT_NATIVE = "currentNative";//保存在pre中当前方言地序号
	public static final String USER_HOME = "userHome";//保存在pre中当前用户家乡
	public static final String USER_ID = "userId";//保存在pre中当前用户id（String）
	public static final String IS_MAIN_LIST_NEED_REFRESH = "isNeedRefresh";//主界面列表是否需要刷新
	
	public static final String UID = "u_id";//保存在pre中当前用户id（服务器int）
	public static final String UNAME = "u_name";//保存在pre中当前用户名（手机号或者第三方openid）
	public static final String TOKEN = "token";//登录token值
	
	
	/**
	 * appid
	 * 友盟和百度定位的appid在主文件中设置
	 */
	public static final String BMOB_APPID = "ed1ba04dd59cfa26d2dd3d7565fb4f94";//bmob
	public static final String WEIXIN_APPID = "wx5f6483b251ab08e7";//微信
	public static final String QQ_APPID = "1102486695";//qq
	public static final String WEIBO_APPID = "3750147705";//新浪微博
	
	public static final int RESULT_RETURN = 0;//forResultActivity直接返回标记
	public static final int RESULT_OK = 1;//forResultActivity返回OK标记
	
	/**
	 * 服务器请求异步标示
	 */
	public static final class code{
		public static final int SUCCESS = 100;//成功
		public static final int FAILURE = 101;//失败
		public static final int ERROR = 99;//错误
		public static final int OLD_USER = 102;//老用户
		public static final int TOKEN_DISABLE = 801;//老用户
	}
	/**
	 * 服务器url
	 */
	public static final String ICON = "http://182.254.140.92:8080/xiangxiang/share/ic_launcher.png";
	public static final String FILE_HOST = "http://192.168.1.103/feytuo/Uploads/";
	public static final String BASE_HOST ="http://192.168.1.103/feytuo/index.php";
//	public static final String FILE_HOST = "http://xiangxiang.ileban.cn/Uploads/";
//	public static final String BASE_HOST ="http://xiangxiang.ileban.cn/index.php";
	public static final String BASE_DIR ="/Xiangxiang";
	public static final String BASE_URL = BASE_HOST+BASE_DIR;
	
	public static final String NORMAL_LOGIN = "/User/normalLogin";
	public static final String REGISTER = "/User/register";
	public static final String THREE_LOGIN = "/User/threeLogin";
	public static final String SET_USER_INFO = "/User/setUserInfo";
	public static final String GET_MAIN_INVITATION = "/Invitation/getMainInvitation";
	public static final String GET_LATEST_TOPIC = "/Invitation/getLatestTopic";
	public static final String PUBLISH_INVITATION = "/Invitation/publishInvitation";
	public static final String GET_CLASSIFY_INVITATION = "/Invitation/getClassifyInvitation";
	public static final String GET_COMMENTS = "/Invitation/getComments";
	public static final String PUBLISH_COMMENT = "/Invitation/publishComment";
	public static final String ADD_COMMENT_NUM = "/Invitation/addCommentNum";
	public static final String CHANGE_PRAISE_NUM = "/Invitation/changePraiseNum";
	public static final String GET_USER_INFO = "/User/getUserInfo";
	public static final String GET_USER_INVITATION = "/Invitation/getUserInvitation";
	public static final String UPDATE_USER_INFO = "/User/updateUserInfo";
	public static final String FEEDBACK = "/Service/feedback";
	public static final String DELETE_INVITATION = "/Invitation/deleteInvitation";
	public static final String SEARCH_FRIEND = "/User/searchFriend";
	public static final String GET_FRIEND_INFO = "/User/getFriendInfo";
	public static final String SHARE_URL = "/Share/url";
	
}
