package com.besideu.source.map;

import java.util.List;

import com.besideu.source.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NearbyGroupAdapter extends BaseAdapter {
	
	private List<NearbyGroupItem> mListItem;
	private LayoutInflater mLayoutInflater;
	
	static class NearbyGroupHolder {
		public TextView txtName;
	}
	
	public NearbyGroupAdapter(Context context, List<NearbyGroupItem> list) {
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
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final NearbyGroupItem item = mListItem.get(position);
		
		NearbyGroupHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.nearby_group_item, null);
			holder = new NearbyGroupHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.txt_nearby_group_name);
			convertView.setTag(holder);
		} else {
			holder = (NearbyGroupHolder) convertView.getTag();
		}
		
		holder.txtName.setText(item.getLocDesc());
		return convertView;
	}

}
