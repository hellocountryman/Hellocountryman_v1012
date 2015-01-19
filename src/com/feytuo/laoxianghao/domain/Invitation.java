package com.feytuo.laoxianghao.domain;


public class Invitation{

	private int invId;//帖子id
	private int uId;// 用户id
	private Integer home;// 方言地
	private String position;// 用户当前所在地
	private String words;// 文字内容
	private String voice;// 语音url
	private int voiceDuration;// 录音时长
	private String time;// 发布时间,用于本地数据库操作
	private int isHot;// 是否热门
	private int praiseNum;// 点赞数
	private int commentNum;// 评论数
	private int type;//分类

	public int getInvId() {
		return invId;
	}

	public void setInvId(int invId) {
		this.invId = invId;
	}

	public int getuId() {
		return uId;
	}

	public void setuId(int uId) {
		this.uId = uId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Integer getHome() {
		return home;
	}

	public void setHome(Integer home) {
		this.home = home;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getIsHot() {
		return isHot;
	}

	public void setIsHot(Integer isHot) {
		this.isHot = isHot;
	}

	public Integer getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(Integer praiseNum) {
		this.praiseNum = praiseNum;
	}

	public Integer getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
	}

	public Integer getVoiceDuration() {
		return voiceDuration;
	}

	public void setVoiceDuration(Integer voiceDuration) {
		this.voiceDuration = voiceDuration;
	}

}
