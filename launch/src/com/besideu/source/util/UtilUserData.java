package com.besideu.source.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class UtilUserData {
	
	public interface UserDataCallBackEvent
	{
		public void onGetRegistComplete(boolean bSucceed, boolean registed);
	    public void onRegisterComplete(boolean bSucceed);
	    public void onGetInfoComplete(boolean bSucceed);
	    public void onFindAreaComplete(boolean bSucceed);
	    public void onFindGroupComplete(boolean bSucceed);
	    public void onJoinGroupComplete(boolean bSucceed);
	    public void onLeaveGroupComplete(boolean bSucceed);
	}
	
	private static Set<UserDataCallBackEvent> mEventHandler = new HashSet<UserDataCallBackEvent>();
	private static Context mContext;
	private static String mUid;
	private static String mTele;
	private static String mName;
	private static String mGid;
	private static String mGname;
	private static String mLogo;
	private static int mUserNum;
	
	public static void addEvent(UserDataCallBackEvent event)
	{
		mEventHandler.add(event);
	}
	
	public static void removeEvent(UserDataCallBackEvent event)
	{
		mEventHandler.remove(event);
	}
	
	public static void getRegist(Context context, String imei) {
		mContext = context;
		
		String urlString = "http://www.imshenbian.com/user/exists?";
		RequestParams params = new RequestParams();
		params.put("imei", imei);
		
		UtilHttp.get(urlString, params, new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					int nError = response.getInt("Errno");
					if (nError != 0) {
						for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
							UserDataCallBackEvent event = (UserDataCallBackEvent) it.next();
							event.onGetRegistComplete(true, false);
						}
						return;
					}
					
					String uid = response.getJSONObject("Data").getString("Uid");
					boolean bRegist = uid.length() > 0;

					for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
						UserDataCallBackEvent event = (UserDataCallBackEvent) it.next();
						event.onGetRegistComplete(true, bRegist);
					}

					Log.e("UserData", response.getString("Errmsg"));

				} catch (Exception e) {
					Log.e("UserData", e.toString());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(mContext, "获取信息失败，请检查您的网络连接",Toast.LENGTH_LONG).show();
				
				for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
					UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
					event.onGetRegistComplete(false, false);
				}
			}
		});
	}

	public static void register(Context context, String imei, String mobile, String name, String logo) {
		
		mContext = context;
		
		String urlString = "http://www.imshenbian.com/user/reg?";
		RequestParams params = new RequestParams();
		params.put("imei", imei);
		params.put("mobile", mobile);
		params.put("name", name);
		params.put("logo", logo);
		
		UtilHttp.post(urlString, params, new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					int nError = response.getInt("Errno");
					if (nError == 0) {
						mUid = response.getJSONObject("Data").getString("Uid");
						
						for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
							UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
							event.onRegisterComplete(true);
						}

						Log.e("UserData", "注册成功！！！！！！");
					} else {
						Log.e("UserData", response.getString("Errmsg"));
					}

				} catch (Exception e) {
					Log.e("UserData", e.toString());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(mContext, "注册失败，请检查您的网络连接",Toast.LENGTH_LONG).show();
				
				for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
					UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
					event.onRegisterComplete(false);
				}
			}
		});
	}
	
	public static void getinfo(Context context, String uid) {
		mContext = context;
		
		String urlString = "http://www.imshenbian.com/user/getinfo?";
		RequestParams params = new RequestParams();
		params.put("uid", uid);

		
		UtilHttp.post(urlString, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					int nError = response.getInt("Errno");
					if (nError == 0) {
						mTele = response.getJSONObject("Data").getString("Mobile");
						mName = response.getJSONObject("Data").getString("Name");
						mLogo = response.getJSONObject("Data").getString("Logo");
						
						for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
							UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
							event.onGetInfoComplete(true);
						}

						Log.e("UserData", "获取用户信息成功！！！！！！");
					} else {
						Log.e("UserData", response.getString("Errmsg"));
					}

				} catch (Exception e) {
					Log.e("UserData", e.toString());
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(mContext, "获取用户信息失败，请检查您的网络连接",Toast.LENGTH_LONG).show();
				
				for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
					UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
					event.onGetInfoComplete(false);
				}
			}
		});
	}
	
	public static void findArea(Context context, String lng, String lat) {
		mContext = context;

		String urlString = "http://restapi.map.so.com/api/simple.php?";
		RequestParams params = new RequestParams();
		params.put("sid", "7001");
		params.put("number", "0");
		params.put("addr_desc", "true");
		params.put("show_addr", "true");
		params.put("x", lng);
		params.put("y", lat);
		UtilHttp.get2(urlString, params, new JsonHttpResponseHandler() {
			
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(mContext, "查找区域失败，请检查您的网络连接", Toast.LENGTH_LONG).show();
				
				for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
					UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
					event.onFindAreaComplete(false);
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					String aoi = response.getString("aoi");

					if (aoi.length() > 3) {
						UtilLocationData.setLocDesc(aoi);
						UtilLocationData.setLocDescFull(UtilLocationData.calcFullDesc());
					}

					Log.e("UserData", "查找区域成功！！！！！！！");
				} catch (Exception e) {
					Log.e("UserData", e.toString());
				}
				
				for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
					UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
					event.onFindAreaComplete(true);
				}
			}
		});
	}
	
	public static void findGroup(Context context, String lng, String lat, String loc) {
		mContext = context;

		String urlString = "http://www.imshenbian.com/group/find?";
		RequestParams params = new RequestParams();
		params.put("lng", lng);
		params.put("lat", lat);
		params.put("loc", loc);

		UtilHttp.get(urlString, params, new JsonHttpResponseHandler() {
			
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(mContext, "查找组失败，请检查您的网络连接", Toast.LENGTH_LONG).show();
				
				for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
					UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
					event.onFindGroupComplete(false);
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					int nError = response.getInt("Errno");
					if (nError == 0) {
						
						mGid = response.getJSONObject("Data").getJSONObject("Group").getString("Id");
						mGname = response.getJSONObject("Data").getJSONObject("Group").getString("Name");
						mUserNum = response.getJSONObject("Data").getJSONObject("Group").getInt("UserNum");
						
						for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
							UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
							event.onFindGroupComplete(true);
						}
						
						Log.e("UserData", "查找组成功！！！！！！！");
					} else {
						Log.e("UserData", response.getString("Errmsg"));
					}

				} catch (Exception e) {
					Log.e("UserData", e.toString());
				}
			}
		});
	}
	
	public static void joinGroup(Context context, String gid)
	{
		mContext = context;
		
		String urlString = "http://www.imshenbian.com/group/join?";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("gid", gid);
		
		UtilHttp.get(urlString, params, new JsonHttpResponseHandler() 
		{
			
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(mContext, "加入组失败，请检查您的网络连接", Toast.LENGTH_LONG).show();
				
				for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
					UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
					event.onFindGroupComplete(false);
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try 
				{	
					int nError = response.getInt("Errno");
					if (nError == 0) {
						
						mUserNum = response.getJSONObject("Data").getInt("UserNum");
						
						for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
							UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
							event.onJoinGroupComplete(true);
						}
						
						Log.e("UserData", "加入组成功！！！！！！");
					} else {
						Log.e("UserData", response.getString("Errmsg"));
					}
				} 
				catch (Exception e) 
				{
					Log.e("UserData", e.toString());
				}
			}
		});
	}
	
	public static void leaveGroup(Context context, String gid) {
		mContext = context;
		
		String urlString = "http://www.imshenbian.com/group/leave?";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("gid", gid);
		
		UtilHttp.get(urlString, params, new JsonHttpResponseHandler() 
		{
			
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(mContext, "离开组失败，请检查您的网络连接", Toast.LENGTH_LONG).show();
				
				for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
					UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
					event.onLeaveGroupComplete(false);
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try 
				{	
					int nError = response.getInt("Errno");
					if (nError == 0) {
						
						for (Iterator<UserDataCallBackEvent> it = mEventHandler.iterator(); it.hasNext();) {
							UserDataCallBackEvent event = (UserDataCallBackEvent)it.next();
							event.onLeaveGroupComplete(true);
						}
						
						Log.e("UserData", "离开组成功！！！！！！");
					} else {
						Log.e("UserData", response.getString("Errmsg"));
					}
				} 
				catch (Exception e) 
				{
					Log.e("UserData", e.toString());
				}
			}
		});
	}

	public static String getUid() {
		return mUid;
	}

	public static void setUid(String mUid) {
		UtilUserData.mUid = mUid;
	}

	public static String getTele() {
		return mTele;
	}

	public static void setTele(String mTele) {
		UtilUserData.mTele = mTele;
	}

	public static String getName() {
		return mName;
	}

	public static void setName(String mName) {
		UtilUserData.mName = mName;
	}

	public static String getGid() {
		return mGid;
	}

	public static void setGid(String mGid) {
		UtilUserData.mGid = mGid;
	}

	public static String getGname() {
		return mGname;
	}

	public static void setGname(String mGname) {
		UtilUserData.mGname = mGname;
	}

	public static String getLogo() {
		return mLogo;
	}

	public static void setLogo(String mLogo) {
		UtilUserData.mLogo = mLogo;
	}

	public static int getUserNum() {
		return mUserNum;
	}

	public static void setUserNum(int mUserNum) {
		UtilUserData.mUserNum = mUserNum;
	}
}
