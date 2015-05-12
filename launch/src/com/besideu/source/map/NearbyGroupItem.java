package com.besideu.source.map;

public class NearbyGroupItem {

	private double geoLng;
	private double geoLat;
	private String locDesc;
	private String locDescFull;
	private String imgUrl;


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
	
	public String getLocDescFull() {
		return locDescFull;
	}

	public void setLocDescFull(String locDescFull) {
		this.locDescFull = locDescFull;
	}

}
