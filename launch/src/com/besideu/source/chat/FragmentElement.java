package com.besideu.source.chat;

import java.io.File;
import java.util.Date;

import com.besideu.source.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;


public class FragmentElement extends Fragment {

	public interface EmCallBackEvent
	{
	    public void OnPreviewOKEvent(String strPath);
	}
	
	EmCallBackEvent mEmCallBackEvent = null;
	
	private ImageView mImgPicture;
	private ImageView mImgCamera;
	
	String mStrTempImageName;
	private static final int FLAG_LOCALIMG = 10086;
	private static final int FLAG_CAMERA = 10087;
	private static final int FLAG_PREVIEW = 10088;
	
	public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
	public static final File FILE_LOCAL = new File(FILE_SDCARD, "BesideU");
	public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL, "images/screenshots");
	
	private OnClickListener mBtnListener = new View.OnClickListener()
	{
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
			case R.id.img_camera:
				goCamera();
				break;
			case R.id.img_picture:
				goLocalImg();
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.chat_bottom_element, container, false);

		mImgPicture = (ImageView) view.findViewById(R.id.img_picture);
		mImgPicture.setOnClickListener(mBtnListener);
		
		mImgCamera = (ImageView) view.findViewById(R.id.img_camera);
		mImgCamera.setOnClickListener(mBtnListener);

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == FLAG_LOCALIMG && resultCode == Activity.RESULT_OK)
		{
			if (data != null)
			{
				Uri uri = data.getData(); 
				if (!TextUtils.isEmpty(uri.getAuthority()))
				{
					Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{ MediaStore.Images.Media.DATA}, null, null, null);
					if (null == cursor)
					{
						Toast.makeText(getActivity(), "图片没找到", Toast.LENGTH_SHORT).show();
						return ;
					}
					
					cursor.moveToFirst();
					String strPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					
					Intent intent = new Intent(getActivity(), PicPreviewActivity.class);
					intent.putExtra("path", strPath);
					startActivityForResult(intent, FLAG_PREVIEW);
				}
				else
				{
					Intent intent = new Intent(getActivity(), PicPreviewActivity.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, FLAG_PREVIEW);
				}
			}
		}
		else if (requestCode == FLAG_CAMERA && resultCode == Activity.RESULT_OK)
		{
			if (mStrTempImageName == null) {
				Toast.makeText(getActivity(), "图片没找到", Toast.LENGTH_SHORT).show();
				return ;
			}
			
			File file = new File(FILE_PIC_SCREENSHOT, mStrTempImageName);
			
			Intent intent = new Intent(getActivity(), PicPreviewActivity.class);
			intent.putExtra("path", file.getAbsolutePath());
			startActivityForResult(intent, FLAG_PREVIEW);
		}
		else if (requestCode == FLAG_PREVIEW && resultCode == Activity.RESULT_OK)
		{
			final String strPath = data.getStringExtra("path");
			if (mEmCallBackEvent != null)	mEmCallBackEvent.OnPreviewOKEvent(strPath);
		}
	}
	
	public void setListener(EmCallBackEvent event)
	{
		mEmCallBackEvent = event;
	}
	
	private void goCamera()
	{
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED))
		{
			try
			{
				mStrTempImageName = "";
				mStrTempImageName = String.valueOf((new Date()).getTime()) + ".png";
				File file = FILE_PIC_SCREENSHOT;
				if (!file.exists())
				{
					file.mkdirs();
				}
				
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				File f = new File(file, mStrTempImageName);
				
				Uri uri = Uri.fromFile(f);
				intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent, FLAG_CAMERA);
			}
			catch (ActivityNotFoundException e)
			{
				
			}
		}
	}
	
	private void goLocalImg()
	{
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, FLAG_LOCALIMG);
	}
}
