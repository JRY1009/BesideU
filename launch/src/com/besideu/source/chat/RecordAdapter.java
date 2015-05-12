package com.besideu.source.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.besideu.source.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordAdapter extends BaseAdapter {

	private List<RecordItem> mListItem;
	private LayoutInflater mLayoutInflater;
	
	static class RecordHolder {
		public TextView txtName;
		public TextView txtTime;
		public int type;
	}
	
	public RecordAdapter(Context context, List<RecordItem> list) {
		mListItem = list;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mListItem.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mListItem.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public int getItemViewType(int position) {
		RecordItem item = mListItem.get(position);
		return item.getType();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final RecordItem item = mListItem.get(position);
		int type = item.getType();
		
		RecordHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.record_item, null);
			holder = new RecordHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.txt_record_name);
			holder.txtTime = (TextView) convertView.findViewById(R.id.txt_record_time);
			holder.type = type;
			convertView.setTag(holder);
		} else {
			holder = (RecordHolder) convertView.getTag();
		}
		
		holder.txtName.setText(item.getLocDesc());
		holder.txtTime.setText(getDate(item.getDate()));
		return convertView;
	}

	@SuppressLint("SimpleDateFormat")
	private String getDate(Date date) 
	{
		SimpleDateFormat simDate = new SimpleDateFormat("MM-dd");
		return simDate.format(date);
	}
}
