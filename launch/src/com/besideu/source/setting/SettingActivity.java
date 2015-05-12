package com.besideu.source.setting;


import com.besideu.source.R;
import com.besideu.source.util.UtilPreference;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.app.Activity;

public class SettingActivity extends Activity {

	private CheckBox mCheckNotify;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mCheckNotify = (CheckBox) findViewById(R.id.cb_set_notify);
		
		boolean bChecked = UtilPreference.getPrefBoolean(this, "bsdu_auto_notify", true);
		mCheckNotify.setChecked(bChecked);
		
		mCheckNotify.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				UtilPreference.setPrefBoolean(SettingActivity.this, "bsdu_auto_notify", isChecked);
			}
		});
	}

	public void onSetBack(View view) {
		finish();
	}
}
