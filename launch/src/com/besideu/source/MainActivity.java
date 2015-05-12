package com.besideu.source;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;


import com.amap.api.location.AMapLocation;
import com.besideu.source.R;
import com.besideu.source.chat.ChatLogMgr;
import com.besideu.source.chat.ChatMsgMgr;
import com.besideu.source.chat.ChatMsgMgr.MsgMgrEvent;
import com.besideu.source.chat.FragmentBiaoqing.BqCallBackEvent;
import com.besideu.source.chat.FragmentElement.EmCallBackEvent;
import com.besideu.source.chat.FragmentBiaoqing;
import com.besideu.source.chat.FragmentElement;
import com.besideu.source.chat.FragmentEmpty;
import com.besideu.source.chat.RecordItem;
import com.besideu.source.chat.RecordActivity;
import com.besideu.source.chat.UtilBiaoqing;
import com.besideu.source.ctrl.PullToRefreshListView;
import com.besideu.source.map.MapViewActivity;
import com.besideu.source.map.NearbyGroupActivity;
import com.besideu.source.setting.SettingActivity;
import com.besideu.source.util.UpdateManager;
import com.besideu.source.util.UtilLocationData;
import com.besideu.source.util.UtilPreference;
import com.besideu.source.util.UtilTelephone;
import com.besideu.source.util.UtilUserData;

import android.os.Bundle;
import android.os.Handler;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseLocationActivity implements 
		MsgMgrEvent, EmCallBackEvent, BqCallBackEvent{

	//----------------------------------------------------------------
	private static final int FLAG_CHOOSE_RECORD = 20086;
	private static final int FLAG_CHOOSE_NEARBY_GROUP = 20087;
	private static final int FLAG_CHOOSE_MAPVIEW_GROUP = 20088;
	
	private TextView mTxtLocation;
	private ProgressDialog mDialog;
	
	private Button mBtnSend;
	private EditText mEditContent;

	private ImageView mImgElement;
	private ImageView mImgFace;

	private PullToRefreshListView mListView;
	private RelativeLayout mRlFmContent;
	private RelativeLayout mRlBottomRBtn;

	private Fragment mFmContent;
	private FragmentBiaoqing mFmBiaoqing;
	private FragmentElement mFmElement;
	private FragmentEmpty mFmEmpty;
	private FragmentManager mFmManager;
	
	private PopupWindow mPopupLocation;
	private TextView mReLocate;
	private TextView mMapView;
	private TextView mNearbyGroup;
	
	Context _context;
	
	private OnClickListener mBtnListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_send:
				ChatMsgMgr.getInstance().sendText();
				break;
			case R.id.img_element:
				showFm(mFmElement);
				break;
			case R.id.img_face:
				showFm(mFmBiaoqing);
				break;
			case R.id.txt_relocate:
				reLocate();
				break;
			case R.id.txt_mapview:
				mapView();
				break;
			case R.id.txt_nearby_group:
				nearbyGroup();
				break;
			}
		}
	};
	
	private Handler mHandler = new Handler();  
	private OnClickListener mEditClick = new View.OnClickListener() {
		public void onClick(View v) {
			
	        mHandler.postDelayed(new Runnable() {
	            @Override  
	            public void run() {  
	            	mListView.setSelection(mListView.getBottom());
	            }  
	        }, 100); 
		}
	};
	
	
	private TextWatcher mWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable arg0) {
			if (mEditContent.getText().length() > 0) {
				mBtnSend.setVisibility(View.VISIBLE);
				mImgElement.setVisibility(View.GONE);
			} else {
				mBtnSend.setVisibility(View.GONE);
				mImgElement.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Log.v("MainActivity", "onCreate");
		
		super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
        	String uid = savedInstanceState.getString("uid");
        	if (uid != null) 	UtilUserData.setUid(uid);
        	
        	String name = savedInstanceState.getString("name");
        	if (name != null) 	UtilUserData.setName(name);
        	
        	String gid = savedInstanceState.getString("gid");
        	if (gid != null) 	UtilUserData.setGid(gid);
        	
        	String gname = savedInstanceState.getString("gname");
        	if (gname != null) 	UtilUserData.setGname(gname);
        	
        	String logo = savedInstanceState.getString("logo");
        	if (logo != null) 	UtilUserData.setLogo(logo);
        	
        	String loc = savedInstanceState.getString("loc");
        	if (loc != null) 	UtilLocationData.setLocDesc(loc);
        	
        	String locfull = savedInstanceState.getString("locfull");
        	if (locfull != null) 	UtilLocationData.setLocDescFull(locfull);
        	
        	String city = savedInstanceState.getString("city");
        	if (city != null) 	UtilLocationData.setCity(city);
        	
        	String province = savedInstanceState.getString("province");
        	if (province != null) 	UtilLocationData.setProvince(province);
        	
        	String district = savedInstanceState.getString("district");
        	if (district != null) 	UtilLocationData.setDistrict(district);
        }  
        
		setContentView(R.layout.activity_main);
		_context = this;
		
		UpdateManager.getInstance().checkUpdate(_context);
		
		UtilUserData.addEvent(this);
		
		InitNavigate();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		outState.putString("uid", UtilUserData.getUid());
		outState.putString("name", UtilUserData.getName());
		outState.putString("gid", UtilUserData.getGid());
		outState.putString("gname", UtilUserData.getGname());
		outState.putString("logo", UtilUserData.getLogo());
		
		outState.putDouble("lng", UtilLocationData.getGeoLng());
		outState.putDouble("lat", UtilLocationData.getGeoLat());
		outState.putString("loc", UtilLocationData.getLocDesc());
		outState.putString("locfull", UtilLocationData.getLocDescFull());
		outState.putString("city", UtilLocationData.getCity());
		outState.putString("province", UtilLocationData.getProvince());
		outState.putString("district", UtilLocationData.getDistrict());
		
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		
		UtilUserData.leaveGroup(this, UtilUserData.getGid());
		UtilUserData.removeEvent(this);
		
		ChatMsgMgr.getInstance().removeEvent(this);
		ChatMsgMgr.getInstance().destroy();
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		clearNotification();
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
        switch(item.getItemId())//得到被点击的item的itemId
        {
        case R.id.action_settings:
        	startActivity(new Intent(MainActivity.this, SettingActivity.class));
            break;
        case R.id.action_quit:
        	finish();
            break;
        }
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
        }  
		return super.onKeyDown(keyCode, event);
	}

	// 点击空白处隐藏软键盘
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) 
	{
        if (ev.getAction() == MotionEvent.ACTION_DOWN) 
        {
            View v = getCurrentFocus();
            
            OnDispatchTouch(v, ev);
        }
        
        return super.dispatchTouchEvent(ev);
	}

	private void InitNavigate()
	{
		mTxtLocation = (TextView) findViewById(R.id.txt_location);
		int num = UtilUserData.getUserNum();
		String text = UtilLocationData.getLocDesc() + "(" + Integer.toString(num) + ")";
		mTxtLocation.setText(text);
		
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(mBtnListener);

		mImgElement = (ImageView) findViewById(R.id.img_element);
		mImgElement.setOnClickListener(mBtnListener);
		
		mImgFace = (ImageView) findViewById(R.id.img_face);
		mImgFace.setOnClickListener(mBtnListener);

		mEditContent = (EditText) findViewById(R.id.et_sendmessage);
		mEditContent.addTextChangedListener(mWatcher);
		mEditContent.setOnClickListener(mEditClick);

		mListView = (PullToRefreshListView) findViewById(R.id.listview);

		mRlFmContent = (RelativeLayout) findViewById(R.id.rl_fragment_content);
		mRlBottomRBtn = (RelativeLayout) findViewById(R.id.rl_bottom_rbtn);
		// ---------------------------------------------------------------------
		mFmManager = getSupportFragmentManager();
		mFmElement = new FragmentElement();
		mFmElement.setListener(this);

		mFmBiaoqing = new FragmentBiaoqing();
		mFmBiaoqing.setListener(this);

		mFmEmpty = new FragmentEmpty();
		switchContent(mFmEmpty);
		
		ChatMsgMgr.getInstance().addEvent(this);
		ChatMsgMgr.getInstance().init(this, mListView, mEditContent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == FLAG_CHOOSE_RECORD && resultCode == RESULT_OK) {
			ChatMsgMgr.getInstance().refreshState();
			mTxtLocation.setText(UtilLocationData.getLocDesc());
			UtilUserData.joinGroup(this, UtilUserData.getGid());
		}
		else if (requestCode == FLAG_CHOOSE_NEARBY_GROUP && resultCode == RESULT_OK) {
			
		}
		else if (requestCode == FLAG_CHOOSE_MAPVIEW_GROUP && resultCode == RESULT_OK) {
			
		}
		
		int index = requestCode>>16;
		if (index != 0) {
			if (mFmElement != null) {
				mFmElement.onActivityResult(requestCode&0xffff, resultCode, data);
			}
			return;
		}
		
		super.onActivityResult(resultCode, resultCode, data);
	}

	public void onRecord(View view) {
		startActivityForResult(new Intent(MainActivity.this, RecordActivity.class), FLAG_CHOOSE_RECORD);
	}
	
	public void mapView() {
		mPopupLocation.dismiss();
		startActivityForResult(new Intent(MainActivity.this, MapViewActivity.class), FLAG_CHOOSE_MAPVIEW_GROUP);
	}
	
	public void nearbyGroup() {
		mPopupLocation.dismiss();
		startActivityForResult(new Intent(MainActivity.this, NearbyGroupActivity.class), FLAG_CHOOSE_NEARBY_GROUP);
	}
	
	public void onLocate(View view) {
		if (mPopupLocation == null) {  
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
  
            View popupmenu = layoutInflater.inflate(R.layout.popup_location, null);  
  
            mReLocate = (TextView) popupmenu.findViewById(R.id.txt_relocate);
            mMapView = (TextView) popupmenu.findViewById(R.id.txt_mapview);
            mNearbyGroup = (TextView) popupmenu.findViewById(R.id.txt_nearby_group);
            
            mReLocate.setOnClickListener(mBtnListener);
            mMapView.setOnClickListener(mBtnListener);
            mNearbyGroup.setOnClickListener(mBtnListener);
            
            // 创建一个PopuWidow对象  
            mPopupLocation = new PopupWindow(popupmenu);  
            mPopupLocation.setWidth(LayoutParams.WRAP_CONTENT);                
            mPopupLocation.setHeight(LayoutParams.WRAP_CONTENT);    
        }  
  
        // 使其聚集  
		mPopupLocation.setFocusable(true);  
        // 设置允许在外点击消失  
		mPopupLocation.setOutsideTouchable(true);  
  
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
		mPopupLocation.setBackgroundDrawable(new BitmapDrawable());  
  
        mPopupLocation.showAsDropDown(view);  
	}
	
	public void reLocate() {
		mPopupLocation.dismiss();
		
		if (mDialog == null) {
			mDialog = new ProgressDialog(this);
		}

		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setCancelable(true);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setTitle("请稍等");
		mDialog.setMessage("正在重新定位...");
		mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDialog.dismiss();
						stopLocation();
					}
				});

		mDialog.show();
		startLocation();
	}
	
	@Override
	public void run() {
		if (UtilLocationData.getLocateSucceed() == false) {
			mDialog.dismiss();
			stopLocation();// 销毁掉定位
			Toast.makeText(this, "10秒内还没有定位成功，请检查您的网络", Toast.LENGTH_LONG).show();
		}
	}
	
    //删除通知    
    private void clearNotification(){
        // 启动后删除之前我们定义的通知   
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);   
        notificationManager.cancel(10);  
  
    }

	@SuppressWarnings("deprecation")
	@Override
	public void onReciveMessage(String place, String name, String content) {
		boolean bChecked = UtilPreference.getPrefBoolean(this, "bsdu_auto_notify", true);
		if (bChecked == false) {
			return ;
		}
		
		if (isTopActivity("com.besideu.source")) {
			return ;
		}
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = name + "：" + content;

		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent notificationIntent = new Intent(MainActivity.this,MainActivity.class);
		PendingIntent intent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(this, place, name + "：" + content, intent);

		notificationManager.notify(10, notification);
	}
	
	private boolean isTopActivity(String $packageName) 
	{
	    //_context是一个保存的上下文
	    ActivityManager __am = (ActivityManager) _context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
	    List<ActivityManager.RunningAppProcessInfo> __list = __am.getRunningAppProcesses();
	    if(__list.size() == 0) return false;
	    for(ActivityManager.RunningAppProcessInfo __process:__list)
	    {
	        if(__process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
	                __process.processName.equals($packageName))
	        {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override
	public void onLocationChanged(AMapLocation location) {
		
		super.onLocationChanged(location);
		
		if (location != null) {
			
			mDialog.dismiss();
			stopLocation();
			
			UtilLocationData.setGeoLat(location.getLatitude());
			UtilLocationData.setGeoLng(location.getLongitude());
			
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				
				String strDescFull = locBundle.getString("desc");
				strDescFull = strDescFull.replaceAll("\\(.*\\)|\\（.*\\）","");
				String strLoc = strDescFull;
				if (strLoc != null) {
					strLoc = strLoc.substring(strLoc.lastIndexOf(" "));
					strLoc = strLoc.replaceAll("靠近", "");
					strLoc = strLoc.trim();
					UtilLocationData.setLocDesc(strLoc);
				}
			}
			
			UtilLocationData.setProvince(location.getProvince());
			UtilLocationData.setCity(location.getCity());
			UtilLocationData.setDistrict(location.getDistrict());
			UtilLocationData.setLocDescFull(UtilLocationData.calcFullDesc());
			
			UtilUserData.findArea(this,
					Double.toString(UtilLocationData.getGeoLng()),
					Double.toString(UtilLocationData.getGeoLat()));
		}
	}

	@Override
	public void onFindAreaComplete(boolean bSucceed) {
		
		UtilUserData.leaveGroup(this, UtilUserData.getGid());
		
		UtilUserData.findGroup(this,
				Double.toString(UtilLocationData.getGeoLng()),
				Double.toString(UtilLocationData.getGeoLat()),
				UtilLocationData.getLocDescFull());
	}
	
	@Override
	public void onFindGroupComplete(boolean bSucceed) {
		if (bSucceed) {
			
			UtilLocationData.setLocDescFull(UtilUserData.getGname());
			String strLoc = UtilUserData.getGname();
			if (strLoc != null) {
				strLoc = strLoc.substring(strLoc.lastIndexOf(" "));
				strLoc = strLoc.replaceAll("靠近", "");
				UtilLocationData.setLocDesc(strLoc);
			}
			
			mTxtLocation.setText(UtilLocationData.getLocDesc());
			
			RecordItem item = new RecordItem();
			item.setType(0);
			item.setDate(new Date());
			item.setGroupId(UtilUserData.getGid());
			item.setGeoLat(UtilLocationData.getGeoLat());
			item.setGeoLng(UtilLocationData.getGeoLng());
			item.setLocDesc(UtilLocationData.getLocDesc());
			item.setLocDescFull(UtilLocationData.getLocDescFull());
			ChatLogMgr.getInstance().pushRecordPlace(this, 0, item);
			
			ChatMsgMgr.getInstance().refreshState();
			
			UtilUserData.joinGroup(this, UtilUserData.getGid());
		}
	}

	@Override
	public void onJoinGroupComplete(boolean bSucceed) {
		if (bSucceed) {
			int num = UtilUserData.getUserNum();
			String text = UtilLocationData.getLocDesc() + "(" + Integer.toString(num) + ")";
			mTxtLocation.setText(text);
		}
	}
	
	public void OnDispatchTouch(View v, MotionEvent ev) {
		if (isHideKeyboard((int) ev.getX(), (int) ev.getY())) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(mEditContent.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		} else {
			mEditContent.requestFocus();
		}

		if (isHideElement((int) ev.getX(), (int) ev.getY())) {
			switchContent(mFmEmpty);
			mRlFmContent.setVisibility(View.GONE);
		}
	}

	private void showFm(Fragment fragment) {
		if (mRlFmContent.getVisibility() == View.GONE) {
			switchContent(fragment);
			mRlFmContent.setVisibility(View.VISIBLE);
		} else {
			if (mFmContent == fragment) {
				switchContent(mFmEmpty);
				mRlFmContent.setVisibility(View.GONE);
			} else
				switchContent(fragment);
		}
	}

	private boolean isHideKeyboard(int x, int y) {
		if (mImgElement.getVisibility() == View.GONE) {
			return (!isPtInView(x, y, mEditContent) && !isPtInView(x, y,
					mBtnSend));
		} else
			return !isPtInView(x, y, mEditContent);
	}

	private boolean isHideElement(int x, int y) {
		return (!isPtInView(x, y, mRlFmContent)
				&& !isPtInView(x, y, mRlBottomRBtn)
				&& !isPtInView(x, y, mBtnSend) && !isPtInView(x, y, mImgFace));
	}

	private boolean isPtInView(int x, int y, View view) {
		int[] l = { 0, 0 };
		view.getLocationInWindow(l);
		Rect rcView = new Rect(l[0], l[1], l[0] + view.getWidth(), l[1]
				+ view.getHeight());

		return rcView.contains(x, y);
	}

	public void switchContent(Fragment fragment) {
		if (mFmContent != fragment) {
			if (fragment.isAdded()) {
				if (mFmContent != null && mFmContent.isAdded()) {
					mFmManager.beginTransaction().hide(mFmContent)
							.show(fragment).commit();
				} else {
					mFmManager.beginTransaction().show(fragment).commit();
				}
			} else {
				if (mFmContent != null && mFmContent.isAdded()) {
					mFmManager.beginTransaction().hide(mFmContent)
							.add(R.id.rl_fragment_content, fragment).commit();
				} else {
					mFmManager.beginTransaction()
							.add(R.id.rl_fragment_content, fragment).commit();
				}
			}

			mFmContent = fragment;
		}
	}

	@Override
	public void OnPreviewOKEvent(String strPath) {
		try {
			ChatMsgMgr.getInstance().uploadPicture(strPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnBiaoqingSelectEvent(int nId, int nIndex) {
		if (nId == R.drawable.chatting_biaoqing_del_btn) {
			UtilBiaoqing.backSpace(mEditContent);
		} else {
			UtilBiaoqing.inputBiaoqing(this, nId, nIndex, mEditContent);
		}
	}
}
