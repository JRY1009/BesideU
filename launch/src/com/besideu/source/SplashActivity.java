package com.besideu.source;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONObject;


import com.amap.api.location.AMapLocation;
import com.besideu.source.R;
import com.besideu.source.chat.ChatLogMgr;
import com.besideu.source.chat.RecordItem;
import com.besideu.source.setting.CropImageActivity;
import com.besideu.source.setting.HeadSelectDialog;
import com.besideu.source.sqlite.DBCommonDef;
import com.besideu.source.util.ImageCompress;
import com.besideu.source.util.UtilHttp;
import com.besideu.source.util.UtilLocationData;
import com.besideu.source.util.UtilPreference;
import com.besideu.source.util.UtilSystem;
import com.besideu.source.util.UtilTelephone;
import com.besideu.source.util.UtilUserData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SplashActivity extends BaseLocationActivity
{
	private static final int FLAG_CHOOSE_LOCALIMG = 10089;
	private static final int FLAG_CHOOSE_CAMERA = 10090;
	private static final int FLAG_PICTURE_MODIFY = 10091;
	
	public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
	public static final File FILE_LOCAL = new File(FILE_SDCARD, DBCommonDef.APP_PATH);
	public static final File FILE_SCREENSHOT = new File(FILE_LOCAL,"images/screenshots");
	private static String gLocalTempImage = "";
	
	private Bitmap mBitmap;
	private String mUid;
	private String mLogo;
	private String mName;
	private RelativeLayout mRlRegister;
	private EditText mEtMyname;
	private ImageView mImgMyHead;
	
	private boolean mJoinGroup = false;
	private boolean mNewUser = false;	// 是否是新用户
	
	private Timer m_timer;
	
	@SuppressLint("HandlerLeak")
	private Handler m_handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == 10086)
			{
				m_timer.cancel();
				
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				super.handleMessage(msg);
			}
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		
		// UtilPreference.clearPreference(this);
		if (UtilPreference.getPrefBoolean(this, "bsdu_shortcut", true)) {
			UtilSystem.createShortCut(this, R.drawable.ic_launcher, R.string.app_name);
			UtilPreference.setPrefBoolean(this, "bsdu_shortcut", false);
		}
		
		mUid = UtilPreference.getPrefString(this, "bsdu_userid", "");
		mName = UtilPreference.getPrefString(this, "bsdu_username", "");
		mLogo = UtilPreference.getPrefString(this, "bsdu_userhead", "");
		
		UtilUserData.setUid(mUid);
		UtilUserData.setName(mName);
		UtilUserData.setLogo(mLogo);
		
		mNewUser = mName.length() == 0 ? true : false;
		
		mRlRegister = (RelativeLayout) findViewById(R.id.rl_splash_register);
		mEtMyname = (EditText) findViewById(R.id.et_splash_myname);
		mImgMyHead = (ImageView) findViewById(R.id.img_splash_myhead_content);
		
		
		if (mNewUser) {
			UtilUserData.getRegist(this, UtilTelephone.getIMEI(this));
		}
		else {
			startLocation();
		}
		
		m_timer = new Timer();
		m_timer.schedule(new TimerTask()
		{
			public void run()
			{
				if (mJoinGroup)
					m_handler.sendEmptyMessage(10086);
			}
		}, 1000, 1000);
	}
	
	

	@Override
	protected void onDestroy() {
		m_timer.cancel();
		super.onDestroy();
	}
	
	@Override
	public void run() {
		if (UtilLocationData.getLocateSucceed() == false) {
			Toast.makeText(this, "10秒内还没有定位成功，请检查您的网络", Toast.LENGTH_LONG).show();
			stopLocation();// 销毁掉定位
			
			onJoinGroupFailed();
		}
	}

	private void onJoinGroupFailed() {
		if (mNewUser == true)
			return ;
		
		List<RecordItem> list_record = ChatLogMgr.getInstance().loadRecord(this, 0);
		if (list_record == null || list_record.size() == 0)
			return ;
		
		RecordItem item = list_record.get(0);
		if (item == null)
			return ;
		
		UtilLocationData.setGeoLat(item.getGeoLat());
		UtilLocationData.setGeoLng(item.getGeoLng());
		UtilLocationData.setLocDescFull(item.getLocDescFull());
		UtilLocationData.setLocDesc(item.getLocDesc());
		UtilUserData.setGid(item.getGroupId());
		ChatLogMgr.getInstance().pushRecordPlace(this, 0, item);
		
		mJoinGroup = true;
	}
	
	@Override
	public void onLocationChanged(AMapLocation location) {
		
		super.onLocationChanged(location);
		
		if (location != null) {
			
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
		if (mNewUser == false) {
			if (UtilUserData.getUid().length() == 0)
				UtilUserData.register(SplashActivity.this,
						UtilTelephone.getIMEI(SplashActivity.this),
						UtilTelephone.getPhoneNum(SplashActivity.this),
						mName, mLogo);
			else
				UtilUserData.findGroup(this,
						Double.toString(UtilLocationData.getGeoLng()),
						Double.toString(UtilLocationData.getGeoLat()),
						UtilLocationData.getLocDescFull());

		}
	}
	
	@Override
	public void onGetRegistComplete(boolean bSucceed, boolean registed) {
		if (bSucceed) {
			if (registed) {
				mNewUser = false;
				startLocation();
			} 
			else {
				mRlRegister.setVisibility(View.VISIBLE);
				startLocation();
			}
		}
	}
	
	@Override
	public void onRegisterComplete(boolean bSucceed) {
		if (bSucceed) {
			UtilPreference.setPrefString(this, "bsdu_userid", UtilUserData.getUid());
			UtilUserData.getinfo(this, UtilUserData.getUid());
		}
	}

	@Override
	public void onGetInfoComplete(boolean bSucceed) {
		if (bSucceed) {
			UtilPreference.setPrefString(this, "bsdu_username", UtilUserData.getName());
			UtilPreference.setPrefString(this, "bsdu_userhead", UtilUserData.getLogo());
			
			UtilUserData.findGroup(this,
					Double.toString(UtilLocationData.getGeoLng()),
					Double.toString(UtilLocationData.getGeoLat()),
					UtilLocationData.getLocDescFull());
		}
	}
	
	@Override
	public void onFindGroupComplete(boolean bSucceed) {
		if (bSucceed) {
			RecordItem item = new RecordItem();
			item.setType(0);
			item.setDate(new Date());
			item.setGroupId(UtilUserData.getGid());
			item.setGeoLat(UtilLocationData.getGeoLat());
			item.setGeoLng(UtilLocationData.getGeoLng());
			item.setLocDesc(UtilLocationData.getLocDesc());
			item.setLocDescFull(UtilLocationData.getLocDescFull());
			ChatLogMgr.getInstance().pushRecordPlace(this, 0, item);
			
			UtilUserData.joinGroup(this, UtilUserData.getGid());
		} else {
			onJoinGroupFailed();
		}
	}

	@Override
	public void onJoinGroupComplete(boolean bSucceed) {
		if (bSucceed) {
			mJoinGroup = true;
		} else {
			onJoinGroupFailed();
		}
	}
	
	public void OnSplashExperience(View v) {
		if (UtilLocationData.getLocateSucceed() == false) {
			Toast.makeText(this, "定位失败，请检查您的网络", Toast.LENGTH_LONG).show();
			return ;
		}
		
		Editable edt = mEtMyname.getText();
		mName = edt.toString();
		if (edt.length() == 0) {
			Dialog alertDialog = new AlertDialog.Builder(this)
					.setMessage("请先输入您的昵称")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
								}
							}).create();
			alertDialog.show();
		} else {
			UtilUserData.register(SplashActivity.this, UtilTelephone.getIMEI(SplashActivity.this), UtilTelephone.getPhoneNum(SplashActivity.this), mName, mLogo);
		}
	}
	
	public void OnSplashHead(View v) {
		
		HeadSelectDialog selectDlg = new HeadSelectDialog(this) {

			@Override
			public void doGoLocalImg() {
				this.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, FLAG_CHOOSE_LOCALIMG);
			}

			@Override
			public void doGoCamera() {
				this.dismiss();
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					try {
						gLocalTempImage = "";
						gLocalTempImage = String.valueOf((new Date()).getTime()) + ".png";
						File filePath = FILE_SCREENSHOT;
						if (!filePath.exists())
							filePath.mkdirs();
						
						Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						File f = new File(filePath, gLocalTempImage);
						
						Uri u = Uri.fromFile(f);
						intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
						startActivityForResult(intent, FLAG_CHOOSE_CAMERA);
						
					} catch (ActivityNotFoundException e) {
						//
					}
				}
			}
		};
		
		selectDlg.setTitle("头像选择");
		selectDlg.show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == FLAG_CHOOSE_LOCALIMG && resultCode == RESULT_OK) {
			if (data == null)
				return;

			Uri uri = data.getData();
			if (!TextUtils.isEmpty(uri.getAuthority()))
			{
				Cursor cursor = getContentResolver().query(uri,
						new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
				
				if (null == cursor) {
					Toast.makeText(this, "图片没找到", Toast.LENGTH_SHORT).show();
					return;
				}
				
				cursor.moveToFirst();
				String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				cursor.close();
				Intent intent = new Intent(this, CropImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, FLAG_PICTURE_MODIFY);
			} 
			else 
			{
				Intent intent = new Intent(this, CropImageActivity.class);
				intent.putExtra("path", uri.getPath());
				startActivityForResult(intent, FLAG_PICTURE_MODIFY);
			}
		}
		else if (requestCode == FLAG_CHOOSE_CAMERA && resultCode == RESULT_OK) {
			File f = new File(FILE_SCREENSHOT, gLocalTempImage);
			Intent intent = new Intent(this, CropImageActivity.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, FLAG_PICTURE_MODIFY);
		} 
		else if (requestCode == FLAG_PICTURE_MODIFY && resultCode == RESULT_OK) {
			if (data == null)
				return;

			final String srcPath = data.getStringExtra("path");
			mBitmap = BitmapFactory.decodeFile(srcPath);
	        
			ByteArrayInputStream stream = ImageCompress.getImage(srcPath, 400, 400);

			String urlString = "http://www.imshenbian.com/image/upload?";
			RequestParams params = new RequestParams(); // 绑定参数
			params.put("upload_img", stream, "uploadimg.jpg");
			
			UtilHttp.post(urlString, params, new JsonHttpResponseHandler() {
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					Log.e("Comment", "上传图片失败" + throwable.toString());
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {
						int nError = response.getInt("Errno");
						if (nError == 0) {
							mLogo = response.getJSONObject("Data").getString("Url");
							mImgMyHead.setImageBitmap(mBitmap);
						}

						Log.e("Comment", response.getString("Errmsg"));

					} catch (Exception e) {
						Log.e("Comment", e.toString());
					}
				}
			});
		}
	}



}
