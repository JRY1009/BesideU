package com.besideu.source.chat;

import com.besideu.source.R;
import com.besideu.source.util.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class PicPreviewActivity extends Activity {

	private Bitmap mBitmap;
	private Button mBtnCancel;
	private Button mBtnSend;
	private ImageView mImageView;
	private LinearLayout ll_bottom;
	
	private String mStrPath;
	ImageLoader mImageLoader;
	
	private OnClickListener mBtnListener = new View.OnClickListener()
	{
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
			case R.id.btn_preview_cancel:
				finish();
				break;
			case R.id.btn_preview_send:
				Intent intent = new Intent();
				intent.putExtra("path", mStrPath);
				setResult(RESULT_OK, intent);
				finish();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_preview);
		
		mImageLoader = new ImageLoader(this);
		init();
	}

	@Override
	protected void onStop() 
	{
		super.onStop();
		if (mBitmap != null)
		{
			mBitmap = null;
		}
	}

	@SuppressLint("ShowToast")
	private void init()
	{
		mStrPath = getIntent().getStringExtra("path");
		
		String strBottom = getIntent().getStringExtra("bottom");
		ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
		
		if (strBottom != null && strBottom.equals("hide"))
		{
			ll_bottom.setVisibility(View.GONE);
		}
		
		mBtnCancel = (Button) findViewById(R.id.btn_preview_cancel);
		mBtnCancel.setOnClickListener(mBtnListener);
		
		mBtnSend = (Button) findViewById(R.id.btn_preview_send);
		mBtnSend.setOnClickListener(mBtnListener);
		
		mImageView = (ImageView) findViewById(R.id.img_preview);
		
		//Bitmap bmp = BitmapFactory.decodeFile(mStrPath);
			
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int nScreenWidth = dm.widthPixels;
		int nScreenHeight = dm.heightPixels;
		
        try
        {
        	if (ll_bottom.getVisibility() == View.GONE) {
        		mImageLoader.DisplayImage(mStrPath, mImageView, R.drawable.map_icon);
        	}
        	else {
              mBitmap = createBitmap(mStrPath, nScreenWidth, nScreenHeight);
              if(mBitmap==null)
              {
              	Toast.makeText(this, "没有找到图片", 0).show();
      			finish();
              }
              mImageView.setImageBitmap(mBitmap);
        	}
        }
        catch (Exception e) 
        {
        	Toast.makeText(this, "没有找到图片", 0).show();
			finish();
		}
	}
	
	public void onImageClick(View view) {
		if (ll_bottom.getVisibility() == View.GONE) {
			finish();
		}
	}
	
    public Bitmap createBitmap(String path,int w,int h)
    {
    	try
    	{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h)
			{
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} 
			else if (srcWidth > srcHeight) 
			{// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} 
			else 
			{
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			return BitmapFactory.decodeFile(path, newOpts);
		} 
    	catch (Exception e)
    	{
			return null;
		}
    }
}
