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
              	Toast.makeText(this, "û���ҵ�ͼƬ", 0).show();
      			finish();
              }
              mImageView.setImageBitmap(mBitmap);
        	}
        }
        catch (Exception e) 
        {
        	Toast.makeText(this, "û���ҵ�ͼƬ", 0).show();
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
			// ���������������Ĺؼ���inJustDecodeBounds��Ϊtrueʱ����ΪͼƬ�����ڴ档
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// ��ȡͼƬ��ԭʼ���
			int srcHeight = opts.outHeight;// ��ȡͼƬԭʼ�߶�
			int destWidth = 0;
			int destHeight = 0;
			// ���ŵı���
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h)
			{
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} 
			else if (srcWidth > srcHeight) 
			{// �������������ź��ͼƬ��С��maxLength�ǳ�����������󳤶�
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
			// ���ŵı����������Ǻ��Ѱ�׼���ı����������ŵģ�Ŀǰ��ֻ����ֻ��ͨ��inSampleSize���������ţ���ֵ�������ŵı�����SDK�н�����ֵ��2��ָ��ֵ
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds��Ϊfalse��ʾ��ͼƬ�����ڴ���
			newOpts.inJustDecodeBounds = false;
			// ���ô�С�����һ���ǲ�׼ȷ�ģ�����inSampleSize��Ϊ׼���������������ȴ��������
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// ��ȡ���ź�ͼƬ
			return BitmapFactory.decodeFile(path, newOpts);
		} 
    	catch (Exception e)
    	{
			return null;
		}
    }
}
