package com.besideu.source.chat;

import java.util.Date;

public class RecordItem {

	private Date date;
	private String groupId;
	private double geoLng;
	private double geoLat;
	private String locDesc;
	private String locDescFull;
	private String imgUrl;
	private String userName;
	private String userId;
	private int type = 0;	// 0ÈºÁÄ£¬1µ¥ÈËÁÄ
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date mDate) {
		this.date = mDate;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public double getGeoLng() {
		return geoLng;
	}

	public void setGeoLng(double geoLng) {
		this.geoLng = geoLng;
	}

	public double getGeoLat() {
		return geoLat;
	}

	public void setGeoLat(double geoLat) {
		this.geoLat = geoLat;
	}

	public String getLocDesc() {
		return locDesc;
	}

	public void setLocDesc(String locDesc) {
		this.locDesc = locDesc;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String url) {
		this.imgUrl = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getLocDescFull() {
		return locDescFull;
	}

	public void setLocDescFull(String locDescFull) {
		this.locDescFull = locDescFull;
	}

}
