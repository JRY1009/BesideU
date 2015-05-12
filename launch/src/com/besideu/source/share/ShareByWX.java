package com.besideu.source.share;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.besideu.source.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

public class ShareByWX {
	private static IWXAPI mWXapi;
	 public static final String WX_APP_ID = "wxa07841dec83b20cf";
	 
	public static void register(Context cxt) {
		mWXapi = WXAPIFactory.createWXAPI(cxt, WX_APP_ID, false);
		
		mWXapi.registerApp(WX_APP_ID);
	}

	@SuppressLint("ShowToast")
	public static void sharetoWX(Context cxt, Bitmap bitmap, boolean bTimeline) {

		if (mWXapi == null) {
			register(cxt);
		}
		
		try {

			if (mWXapi.isWXAppInstalled() == false || mWXapi.isWXAppSupportAPI() == false) {
				Toast.makeText(cxt, "请先安装新版微信", Toast.LENGTH_SHORT).show();
				return;
			}
			
			WXWebpageObject txtObj = new WXWebpageObject();
			txtObj.webpageUrl = cxt.getResources().getString(R.string.share_url);
			
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = txtObj;
			msg.title = cxt.getResources().getString(R.string.app_name);
			msg.description = cxt.getResources().getString(R.string.share_descript);
			
			Bitmap thumb = BitmapFactory.decodeResource(cxt.getResources(), R.drawable.ic_share);
			msg.thumbData = Util.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = bTimeline ? SendMessageToWX.Req.WXSceneTimeline
					: SendMessageToWX.Req.WXSceneSession;
			mWXapi.sendReq(req);
			
		} catch (Exception e) {
			
			Toast.makeText(cxt, e.toString(), 500).show();
		}
	}

	public static void shareToWX_page(Context cxt, boolean bTimeline){

		if (mWXapi == null) {
			register(cxt);
		}
		
		if (mWXapi.isWXAppInstalled() == false || mWXapi.isWXAppSupportAPI() == false) {
			Toast.makeText(cxt, "请先安装新版微信", Toast.LENGTH_SHORT).show();
			return;
		}
		
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = cxt.getResources().getString(R.string.share_url);
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.mediaObject = webpage;
		msg.title = cxt.getResources().getString(R.string.app_name);
		msg.description = cxt.getResources().getString(R.string.share_descript);
		Bitmap thumb = BitmapFactory.decodeResource(cxt.getResources(), R.drawable.ic_share);
		msg.thumbData = Util.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = bTimeline ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		mWXapi.sendReq(req);
	}
}