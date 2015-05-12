package com.besideu.source.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import android.content.Context;
import android.telephony.TelephonyManager;

public class UtilTelephone {

	/* IMEI也是一串唯一的数字， 标识了GSM 和 UMTS网络里的唯一一个手机.
	 * 它通常被打印在手机里电池下面的那一面，拨 *#06# 也能看到它. IMEI 与 设备唯一对应.*/
	public static String getIMEI(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();
        return deviceid;
	}
	
	public static String getPhoneNum(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNum = tm.getLine1Number();
        return phoneNum;
	}
	
	/* IMSI是一个唯一的数字， 标识了GSM和UMTS 网络里的唯一一个用户. 它存储在手机的SIM卡里，它会通过手机发送到网络上. 
	 * IMSI 与 SIM唯一对应。从技术层面而言，手机的SIM卡上并不会存储手机号码信息，
	 * 只会存储IMSI（International Mobile Subscriber Identification Number）。
	 * 手机号码（MSISDN）都是登记在HLR（Home Location Register）中的，在HLR中会把IMSI和MSISDN关联在一起。*/
	public static String getIMSI(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = tm.getSubscriberId();
        return imsi;
	}
	
	//取出ICCID
	public static String getSerialNumber(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getSimSerialNumber();
        return imei;
	}
	
	public static String getMac() {
		String macSerial = null;
		String str = "";
		try
		{
			Process pp = Runtime.getRuntime().exec("cat/sys/class/net/wlan0/address");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) 
			{
				str = input.readLine();
				if (str != null) 
				{
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} 
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		return macSerial;
	}
}
