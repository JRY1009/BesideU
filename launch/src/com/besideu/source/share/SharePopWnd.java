package com.besideu.source.share;

import com.besideu.source.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
public class SharePopWnd {
	
	@SuppressWarnings("unused")
	private static final String TAG     = "SHARE";
	private static Context mContext     = null;
	private static Bitmap  mBitmap2Share= null;
	private static Handler mHandler		= null;
	private static boolean mBInvite     = true;			// ���룿����
	public static void showSharePopWnd(Context context, Bitmap bitmap, Handler handler, boolean bInvite){
		mContext      = context;
		mBitmap2Share = bitmap;
		mHandler	  = handler;
		mBInvite	  = bInvite;
		
		ShareDialog selectDlg = new ShareDialog(context) {

			@Override
			public void doGoWeixin() {
				this.dismiss();
				sharetoWx(false);
				notifyHideSharePage();
			}

			@Override
			public void doGoFriends() {
				this.dismiss();
				sharetoWx(true);
				notifyHideSharePage();
			}

			@Override
			public void doGoMessage() {
				this.dismiss();
				Uri uri = Uri.parse("smsto:");
				Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
				intent.putExtra("sms_body", "����һ���ڡ���ߡ���ɣ�����ע�ᣬ�����������Ⱥ����ʱ��غ���ߵ��˿��ģ�������ǻ���黶ӭ�����氡����Ҫ����ʧ���������ַ��װ��http://www.imshenbian.com");
				mContext.startActivity(intent);
			}
		};
		
		selectDlg.setTitle("����");
		selectDlg.show();
	}
	
	@SuppressLint("UseValueOf")
	private static void notifyHideSharePage(){
		Message msg = new Message();
		msg.obj = new Boolean(mBInvite);
		msg.what= 10086;
		if(null != mHandler) mHandler.sendMessage(msg);
	}

	private static void sharetoWx(boolean bTimeline){
		//bTimeline=true ����Ȧ
		if(mBInvite)  ShareByWX.shareToWX_page(mContext, bTimeline);
		else 		  ShareByWX.sharetoWX(mContext, mBitmap2Share, bTimeline);
	}
}
