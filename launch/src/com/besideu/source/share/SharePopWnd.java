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
	private static boolean mBInvite     = true;			// 邀请？分享
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
				intent.putExtra("sms_body", "和我一起在【身边】玩吧！不用注册，不用申请加入群，随时随地和身边的人开聊！我用满腔热情欢迎你来玩啊，不要让我失望，点击地址安装：http://www.imshenbian.com");
				mContext.startActivity(intent);
			}
		};
		
		selectDlg.setTitle("分享");
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
		//bTimeline=true 朋友圈
		if(mBInvite)  ShareByWX.shareToWX_page(mContext, bTimeline);
		else 		  ShareByWX.sharetoWX(mContext, mBitmap2Share, bTimeline);
	}
}
