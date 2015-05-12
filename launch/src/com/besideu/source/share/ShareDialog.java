package com.besideu.source.share;

import com.besideu.source.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class ShareDialog extends Dialog implements OnClickListener {
	
	private LayoutInflater mInflater;
	
	private Button mWeixin;
	private Button mFriends;
	private Button mMessage;
	private Button mCancel;
	public ShareDialog(Context context) {
		super(context);
		mInflater = LayoutInflater.from(context);
	}

	public ShareDialog(Context context, int theme) {
		super(context, theme);
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(mInflater.inflate(R.layout.dialog_share, null));
		
		mWeixin = (Button) this.findViewById(R.id.btn_share_weixin);
		mFriends = (Button) this.findViewById(R.id.btn_share_friends);
		mMessage = (Button) this.findViewById(R.id.btn_share_message);
		mCancel = (Button) this.findViewById(R.id.btn_share_cancle);
		
		mWeixin.setOnClickListener(this);
		mFriends.setOnClickListener(this);
		mMessage.setOnClickListener(this);
		mCancel.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_share_weixin:
			doGoWeixin();
			break;
		case R.id.btn_share_friends:
			doGoFriends();
			break;
		case R.id.btn_share_message:
			doGoMessage();
			break;
		case R.id.btn_share_cancle:
			dismiss();
			break;
		}
	}
	
	public void doGoWeixin() {}
	public void doGoFriends() {}
	public void doGoMessage() {}
}
