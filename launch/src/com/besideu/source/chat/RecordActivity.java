package com.besideu.source.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.besideu.source.MainActivity;
import com.besideu.source.R;
import com.besideu.source.setting.SettingActivity;
import com.besideu.source.share.SharePopWnd;
import com.besideu.source.util.UtilLocationData;
import com.besideu.source.util.UtilUserData;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class RecordActivity extends Activity {

	protected static final int CONTEXTMENU_DELETEITEM = 0;
	
	private TextView mTxtMap;
	
	private List<RecordItem> mLogArrays = new ArrayList<RecordItem>();
	private ListView mListView;
	private RecordAdapter mAdapter;
	private List<RecordItem> mDataArrays = new ArrayList<RecordItem>();
	
	private OnClickListener mMapListener = new View.OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		
		mTxtMap = (TextView)findViewById(R.id.txt_map);
		mTxtMap.setOnClickListener(mMapListener);
		
		mListView = (ListView) findViewById(R.id.lv_record);
		mAdapter = new RecordAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				   
				menu.add(0, CONTEXTMENU_DELETEITEM, 0, "É¾³ý¸ÃÎ»ÖÃ");    
			}
		});
		
		mLogArrays = ChatLogMgr.getInstance().loadRecord(this, 0);
		int count = mLogArrays.size();
		for (int i=0; i<count; i++) {
			RecordItem item = mLogArrays.get(i);
			if (item == null)
				continue;
			
			mDataArrays.add(item);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		
		switch (item.getItemId()) {
		case CONTEXTMENU_DELETEITEM:
			RecordItem rItem = (RecordItem) mAdapter.getItem(position);
			if (rItem != null) {
				ChatLogMgr.getInstance().deleteRecord(this, 0, rItem);
				mDataArrays.remove(rItem);
				mAdapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void onRecordItem(View view) {
		
		TextView txtName = (TextView) view.findViewById(R.id.txt_record_name);
		
		int count = mLogArrays.size();
		for (int i=0; i<count; i++) {
			RecordItem item = mLogArrays.get(i);
			if (item == null)
				continue;
			
			if (item.getLocDesc() == txtName.getText())
			{
				UtilUserData.leaveGroup(this, UtilUserData.getGid());
				
				item.setDate(new Date());
				UtilLocationData.setGeoLat(item.getGeoLat());
				UtilLocationData.setGeoLng(item.getGeoLng());
				UtilLocationData.setLocDescFull(item.getLocDescFull());
				UtilLocationData.setLocDesc(item.getLocDesc());
				UtilUserData.setGid(item.getGroupId());
				
				ChatLogMgr.getInstance().pushRecordPlace(this, 0, item);
			}
		}
		
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public void onRecordBack(View view) {
		finish();
	}
	
	public void onShare(View view) {
		Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
		SharePopWnd.showSharePopWnd(this, bmp, null, false);
	}
	
	public void onSetting(View view) {
		startActivity(new Intent(RecordActivity.this, SettingActivity.class));
	}
}
