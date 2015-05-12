package com.besideu.source.setting;

import java.util.Timer;
import java.util.TimerTask;


import com.besideu.source.R;

import android.os.Bundle;
import android.text.Editable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.app.Activity;

public class SetiingMynameActivity extends Activity {

	private EditText mEditName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setiing_myname);

		mEditName = (EditText) findViewById(R.id.et_myname);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) SetiingMynameActivity.this
						.getSystemService(INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 500); // 在一秒后打开
	}

	@SuppressWarnings("unused")
	private void onSaveMyname() {
		Editable etContent = mEditName.getText();
		if (etContent.length() == 0)
			return;
	}

}
