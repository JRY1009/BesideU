package com.besideu.source.setting;

import com.besideu.source.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HeadSelectDialog extends Dialog implements OnClickListener {

	private LayoutInflater mInflater;
	
	private Button mLocalImg;
	private Button mCamera;
	private Button mCancel;
	
	public HeadSelectDialog(Context context) {
		super(context);
		mInflater = LayoutInflater.from(context);
	}

	public HeadSelectDialog(Context context, int theme) {
		super(context, theme);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(mInflater.inflate(R.layout.dialog_head_select, null));
		
		mLocalImg = (Button) this.findViewById(R.id.btn_head_select_local);
		mCamera = (Button) this.findViewById(R.id.btn_head_select_carema);
		mCancel = (Button) this.findViewById(R.id.btn_head_select_cancle);
		
		mLocalImg.setOnClickListener(this);
		mCamera.setOnClickListener(this);
		mCancel.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_head_select_local:
			doGoLocalImg();
			break;
		case R.id.btn_head_select_carema:
			doGoCamera();
			break;
		case R.id.btn_head_select_cancle:
			dismiss();
			break;
		}
	}
	
	public void doGoLocalImg() {}
	public void doGoCamera() {}
}
