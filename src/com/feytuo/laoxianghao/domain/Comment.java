package com.feytuo.laoxianghao.domain;


public class Comment{
	private int comId;
	private int uId;
	private int invId;
	private String comWords;
	private String comVoice;
	private String comTime;
	private String comPosition;
	
	public int getComId() {
		return comId;
	}
	public void setComId(int comId) {
		this.comId = comId;
	}
	public int getuId() {
		return uId;
	}
	public void setuId(int uId) {
		this.uId = uId;
	}
	public int getInvId() {
		return invId;
	}
	public void setInvId(int invId) {
		this.invId = invId;
	}
	public String getComWords() {
		return comWords;
	}
	public void setComWords(String comWords) {
		this.comWords = comWords;
	}
	public String getComVoice() {
		return comVoice;
	}
	public void setComVoice(String comVoice) {
		this.comVoice = comVoice;
	}
	public String getComTime() {
		return comTime;
	}
	public void setComTime(String comTime) {
		this.comTime = comTime;
	}
	public String getComPosition() {
		return comPosition;
	}
	public void setComPosition(String comPosition) {
		this.comPosition = comPosition;
	}
	
}
