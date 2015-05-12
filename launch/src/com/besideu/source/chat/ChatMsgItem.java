package com.besideu.source.chat;

import java.io.ByteArrayInputStream;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.besideu.source.util.ImageCompress;
import com.besideu.source.util.UtilHttp;
import com.besideu.source.util.UtilUserData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.graphics.BitmapFactory;
import android.text.Editable;
import android.util.Log;

public class ChatMsgItem {

	public static interface IContentType
	{
		int ICONTENT_NONE 		= 0;
		int ICONTENT_TEXT 		= 1;
		int ICONTENT_PICTURE	= 2;
		int ICONTENT_VOICE		= 3;
		int ICONTENT_SHARE		= 4;
		int ICONTENT_SKYCITY	= 5;
	}
	
	public interface MsgItemEvent
	{
		public void onSendComplete(boolean bSucceed);
		public void onResendBegin();
		public void onResendComplete(boolean bSucceed);
	}
	
	private MsgItemEvent mEventHandle;
	
	private Date mDate;
	private String mCid;
	private String mName;
	private String mLogo;
	private String mThumb;	// 缩略图路径
	private String mUrl;		// 资源url
	private String mExtinfo;	// 额外信息，比如图片的宽高
	private Editable mEditable;
	private boolean mShowDate = true;
	private boolean mBySelf = false;
	private int mContentType = IContentType.ICONTENT_TEXT;
	
	private int mWidth;
	private int mHeight;
	private int mState = 0;	//0 normal, 1 failed, 2 sending

	public ChatMsgItem(){}
	
	public void setEvent(MsgItemEvent event) {
		if (mEventHandle == null) {
			mEventHandle = event;
		}
	}
	
	public String getCid() {
		return mCid;
	}

	public void setCid(String strCid) {
		mCid = strCid;
	}

	public String getName() {
		return mName;
	}

	public void setName(String strName) {
		mName = strName;
	}

	public String getLogo() {
		return mLogo;
	}

	public void setLogo(String mLogo) {
		this.mLogo = mLogo;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public Editable getText() {
		return mEditable;
	}

	public void setText(Editable etText) {
		mEditable = etText;
	}

	public String getThumb() {
		return mThumb;
	}

	public void setThumb(String thumb) {
		mThumb = thumb;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public boolean getBySelf() {
		return mBySelf;
	}

	public void setBySelf(boolean bySelf) {
		mBySelf = bySelf;
	}

	public int getContentType() {
		return mContentType;
	}

	public void setContentType(int nContentType) {
		mContentType = nContentType;
	}

	public boolean getShowDate() {
		return mShowDate;
	}

	public void setShowDate(boolean bShow) {
		mShowDate = bShow;
	}

	public String getExtinfo() {
		return mExtinfo;
	}

	public void setExtinfo(String mExtinfo) {
		this.mExtinfo = mExtinfo;
	}
	
	public int getState() {
		return mState;
	}

	public void setState(int state) {
		mState = state;
	}
	
	public void sendText(boolean bResend) {
		final boolean bRE = bResend;
		
		String urlString = "http://www.imshenbian.com/comment/add?";
		RequestParams params = new RequestParams();
		params.put("uid", UtilUserData.getUid());
		params.put("gid", UtilUserData.getGid());
		params.put("type", "1");
		params.put("content", mEditable.toString());
		
		try {
			JSONObject obj = new JSONObject(); 
			obj.put("local_id", java.util.UUID.randomUUID().toString().replace("-", ""));
			params.put("extinfo", obj.toString());
			
			this.setExtinfo(obj.toString());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		UtilHttp.post(urlString, params, new JsonHttpResponseHandler() {
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				if (mEventHandle != null) {
					setState(1);
					if (!bRE)	mEventHandle.onSendComplete(false);
					else		mEventHandle.onResendComplete(false);
				}
				Log.e("Comment", "发送消息失败" + throwable.toString());
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				if (mEventHandle != null) {
					setState(0);
					if (!bRE)	mEventHandle.onSendComplete(true);
					else		mEventHandle.onResendComplete(true);
				}
			}
		});
	}
	
	// 第一步：上传图片
	public void sendPicture(String srcPath, boolean bResend) {
		final boolean bRE = bResend;
		
		this.setUrl(srcPath);
		this.setThumb(srcPath);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, options);
		mWidth = options.outWidth;
		mHeight = options.outHeight;
        ByteArrayInputStream stream = ImageCompress.getImage(srcPath, 480, 800);

		String urlString = "http://www.imshenbian.com/image/upload?";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("upload_img", stream, "uploadimg.jpg");
		
		UtilHttp.post(urlString, params, new JsonHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				if (mEventHandle != null) {
					setState(1);
					if (!bRE)	mEventHandle.onSendComplete(false);
					else		mEventHandle.onResendComplete(false);
				}
				Log.e("Comment", "上传图片失败" + throwable.toString());
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					int nError = response.getInt("Errno");
					if (nError != 0) {
						Log.e("Comment", response.getString("Errmsg"));
						if (mEventHandle != null) {
							setState(1);
							if (!bRE)	mEventHandle.onSendComplete(false);
							else		mEventHandle.onResendComplete(false);
						}
						return ;
					}
					
					sendPicture2(response.getJSONObject("Data").getString("Url"), bRE);
					
				} catch (Exception e) {
					Log.e("Comment", e.toString());
				}
			}
		});
	}
	
	// 第二部：添加到评论列表
	private void sendPicture2(String url, boolean bResend) {
		final boolean bRE = bResend;
		
		String urlString = "http://www.imshenbian.com/comment/add?";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("uid", UtilUserData.getUid());
		params.put("gid", UtilUserData.getGid());
		params.put("type", "2");
		params.put("content", url);
		
		try {
			JSONObject obj = new JSONObject(); 
			obj.put("w", mWidth);
			obj.put("h", mHeight);
			obj.put("local_id", java.util.UUID.randomUUID().toString().replace("-", ""));
			params.put("extinfo", obj.toString());
			this.setExtinfo(obj.toString());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		UtilHttp.post(urlString, params, new JsonHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				if (mEventHandle != null) {
					setState(1);
					if (!bRE)	mEventHandle.onSendComplete(false);
					else		mEventHandle.onResendComplete(false);
				}
				Log.e("Comment", "发送图片失败" + throwable.toString());
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				if (mEventHandle != null) {
					setState(0);
					if (!bRE)	mEventHandle.onSendComplete(true);
					else		mEventHandle.onResendComplete(true);
				}
			}
		});
	}

	public void reSend() {
		
		int type = getContentType();
		switch(type) {
		case IContentType.ICONTENT_NONE:
		case IContentType.ICONTENT_TEXT:
			sendText(true);
			break;
		case IContentType.ICONTENT_PICTURE:
			sendPicture(getUrl(), true);
			break;
		default:
			break;
		}
		
		if (mEventHandle != null) {
			setState(2);
			mEventHandle.onResendBegin();
		}
	}
}
