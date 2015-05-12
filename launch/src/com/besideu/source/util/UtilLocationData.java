package com.besideu.source.util;

public class UtilLocationData {
	
	private static boolean mLocateSucceed = false;
	private static double geoLng;
	private static double geoLat;
	private static String locDesc;
	private static String locDescFull;
	
	private static String province;
	private static String city;
	private static String district;
	
	public static boolean getLocateSucceed() {
		return mLocateSucceed;
	}
	
	public static void setLocateSucceed(boolean bSucceed) {
		UtilLocationData.mLocateSucceed = bSucceed;
	}
	
	public static double getGeoLng() {
		return geoLng;
	}

	public static void setGeoLng(double geoLng) {
		UtilLocationData.geoLng = geoLng;
	}

	public static double getGeoLat() {
		return geoLat;
	}

	public static void setGeoLat(double geoLat) {
		UtilLocationData.geoLat = geoLat;
	}

	public static String getLocDesc() {
		return locDesc;
	}

	public static void setLocDesc(String locDesc) {
		UtilLocationData.locDesc = locDesc;
	}

	public static String getLocDescFull() {
		return locDescFull;
	}
	
	public static String calcFullDesc() {
		String province = UtilLocationData.province;
		String city = UtilLocationData.city;
		String ad = UtilLocationData.district;
		String fulldesc = "神州大地";
		if (province != null && province.length() > 0 && !province.equals(city)) {
			fulldesc = fulldesc + " " + province;
		}
		
		if (city != null && city.length() > 0) {
			fulldesc = fulldesc + " " + city;
		}
		
		if (ad != null && ad.length() > 0) {
			fulldesc = fulldesc + " " + ad;
		}
		
		fulldesc = fulldesc + " " + locDesc;
		
		return fulldesc;
	}

	public static void setLocDescFull(String locDescFull) {
		UtilLocationData.locDescFull = locDescFull;
	}

	public static String getProvince() {
		return province;
	}

	public static void setProvince(String province) {
		UtilLocationData.province = province;
	}

	public static String getCity() {
		return city;
	}

	public static void setCity(String city) {
		UtilLocationData.city = city;
	}

	public static String getDistrict() {
		return district;
	}

	public static void setDistrict(String district) {
		UtilLocationData.district = district;
	}

	public static String getCityGroup() {
		String group = "天空之城";
		if (province != null && province.length() > 0) {
			group = group + " " + province;
		}
		
		if (city != null && city.length() > 0) {
			group = group + " " + city;
		}
		return group;
	}
}
