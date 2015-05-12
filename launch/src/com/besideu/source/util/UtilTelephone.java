package com.besideu.source.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import android.content.Context;
import android.telephony.TelephonyManager;

public class UtilTelephone {

	/* IMEIҲ��һ��Ψһ�����֣� ��ʶ��GSM �� UMTS�������Ψһһ���ֻ�.
	 * ��ͨ������ӡ���ֻ������������һ�棬�� *#06# Ҳ�ܿ�����. IMEI �� �豸Ψһ��Ӧ.*/
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
	
	/* IMSI��һ��Ψһ�����֣� ��ʶ��GSM��UMTS �������Ψһһ���û�. ���洢���ֻ���SIM�������ͨ���ֻ����͵�������. 
	 * IMSI �� SIMΨһ��Ӧ���Ӽ���������ԣ��ֻ���SIM���ϲ�����洢�ֻ�������Ϣ��
	 * ֻ��洢IMSI��International Mobile Subscriber Identification Number����
	 * �ֻ����루MSISDN�����ǵǼ���HLR��Home Location Register���еģ���HLR�л��IMSI��MSISDN������һ��*/
	public static String getIMSI(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = tm.getSubscriberId();
        return imsi;
	}
	
	//ȡ��ICCID
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
					macSerial = str.trim();// ȥ�ո�
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
