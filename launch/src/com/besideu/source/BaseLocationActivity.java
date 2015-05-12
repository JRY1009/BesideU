package com.besideu.source;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.besideu.source.util.UtilLocationData;
import com.besideu.source.util.UtilUserData;
import com.besideu.source.util.UtilUserData.UserDataCallBackEvent;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class BaseLocationActivity extends FragmentActivity implements
	AMapLocationListener, Runnable, UserDataCallBackEvent {

	protected LocationManagerProxy aMapLocManager = null;
	private Handler handler = new Handler();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		UtilUserData.addEvent(this);
		
		super.onCreate(savedInstanceState);
	}


	@Override
	protected void onDestroy() {
		
		UtilUserData.removeEvent(this);
		
		super.onDestroy();
	}

	public void startLocation() {
		if (aMapLocManager == null) {
			aMapLocManager = LocationManagerProxy.getInstance(this);
		}
		
		UtilLocationData.setLocateSucceed(false);
		aMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(this, 10 * 1000);
	}

	@SuppressWarnings("deprecation")
	public void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates((AMapLocationListener) this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
		handler.removeCallbacks(this);
	}


	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLocationChanged(AMapLocation location) {
		
		if (location != null) {
			UtilLocationData.setLocateSucceed(true);
		}
	}

	
	@Override
	public void run() {
		if (UtilLocationData.getLocateSucceed() == false) {
			Toast.makeText(this, "10秒内还没有定位成功，请检查您的网络", Toast.LENGTH_LONG).show();
			stopLocation();// 销毁掉定位
		}
	}


	@Override
	public void onRegisterComplete(boolean bSucceed) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onGetInfoComplete(boolean bSucceed) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onFindGroupComplete(boolean bSucceed) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onJoinGroupComplete(boolean bSucceed) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLeaveGroupComplete(boolean bSucceed) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onGetRegistComplete(boolean bSucceed, boolean registed) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onFindAreaComplete(boolean bSucceed) {
		// TODO Auto-generated method stub
		
	}
	
}
