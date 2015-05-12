package com.besideu.source.chat;

import com.besideu.source.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChatSelectDialog extends Dialog implements OnClickListener {

	private LayoutInflater mInflater;
	
	private Button mCopy;
	private Button mCancel;
	
	public ChatSelectDialog(Context context) {
		super(context);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(mInflater.inflate(R.layout.dialog_chat_select, null));
		
		mCopy = (Button) this.findViewById(R.id.btn_chat_copy);
		mCancel = (Button) this.findViewById(R.id.btn_chat_cancel);
		
		mCopy.setOnClickListener(this);
		mCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_chat_copy:
			doCopy();
			break;
		case R.id.btn_chat_cancel:
			dismiss();
			break;
		}
	}

	public void doCopy() {}
}
