package com.besideu.source.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.besideu.source.R;
import com.besideu.source.chat.ChatMsgItem.IContentType;
import com.besideu.source.share.SharePopWnd;
import com.besideu.source.util.ImageLoader;
import com.besideu.source.util.UtilLocationData;
import com.besideu.source.util.UtilUserData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChatMsgAdapter extends BaseAdapter {

	public static interface IMsgType
	{
		int IMSG_COME = 0;
		int IMSG_TO	= 1;
	}
	
	static class ViewHolder
	{
		public TextView txt_Date;
		public TextView txt_Name;
		public TextView txt_Content;
		public ImageView img_Content;
		public ImageView img_Head;
		public ImageView img_Invalid;
		public ProgressBar prog_Sending;
		
		public int type = IContentType.ICONTENT_TEXT;
	}
	
	private List<ChatMsgItem> m_listItem;
	private Context m_context;
	
	private LayoutInflater m_layoutInflater;
	ImageLoader mImageLoader;
	
	public ChatMsgAdapter(Context context, List<ChatMsgItem> listItem)
	{
		m_context = context;
		m_listItem = listItem;
		m_layoutInflater = LayoutInflater.from(context);	
		
		mImageLoader = new ImageLoader(context);
	}
	
	@Override
	public int getCount() 
	{
		return m_listItem.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return m_listItem.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public int getItemViewType(int position) 
	{
		ChatMsgItem Item = m_listItem.get(position);
		if (Item.getBySelf() == true)
		{
			return IMsgType.IMSG_TO;
		}
		else
		{
			return IMsgType.IMSG_COME;
		}
	}

	@Override
	public int getViewTypeCount() 
	{
		return 2;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final ChatMsgItem item = m_listItem.get(position);
		int 	type 	= item.getContentType();
		boolean bySelf 	= item.getBySelf();
		
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = m_layoutInflater.inflate(bySelf ? R.layout.chat_item_right : R.layout.chat_item_left, null);
			
			viewHolder = new ViewHolder();
			viewHolder.txt_Date = (TextView) convertView.findViewById(R.id.txt_chat_sendtime);
			viewHolder.txt_Name = (TextView) convertView.findViewById(R.id.txt_username);
			viewHolder.txt_Content = (TextView) convertView.findViewById(R.id.txt_chatcontent);
			viewHolder.img_Content = (ImageView) convertView.findViewById(R.id.img_chatcontent);
			viewHolder.img_Head = (ImageView) convertView.findViewById(R.id.img_userhead);
			viewHolder.img_Invalid = (ImageView) convertView.findViewById(R.id.img_invalid);
			viewHolder.prog_Sending = (ProgressBar) convertView.findViewById(R.id.progress_sending);
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolder) convertView.getTag();
		
		if (item.getState() == 0) {
			if (viewHolder.img_Invalid != null)		viewHolder.img_Invalid.setVisibility(View.GONE);
			if (viewHolder.prog_Sending != null)	viewHolder.prog_Sending.setVisibility(View.GONE);
		} else if (item.getState() == 1) {
			if (viewHolder.img_Invalid != null)		viewHolder.img_Invalid.setVisibility(View.VISIBLE);
			if (viewHolder.prog_Sending != null)	viewHolder.prog_Sending.setVisibility(View.GONE);
		} else if (item.getState() == 2) {
			if (viewHolder.img_Invalid != null)		viewHolder.img_Invalid.setVisibility(View.GONE);
			if (viewHolder.prog_Sending != null)	viewHolder.prog_Sending.setVisibility(View.VISIBLE);
		}

		mImageLoader.DisplayImage(item.getLogo(), viewHolder.img_Head, R.drawable.head_default);
		viewHolder.txt_Date.setText(getDate(item.getDate()));
		viewHolder.txt_Date.setVisibility(item.getShowDate() ? View.VISIBLE : View.GONE);
		viewHolder.txt_Name.setText(item.getName());
		if (viewHolder.img_Invalid != null) {
			viewHolder.img_Invalid.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Dialog alertDialog = new AlertDialog.Builder(m_context)
					.setMessage("重发该消息？")
					.setPositiveButton("重发",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									item.reSend();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {}
							}).create();
					alertDialog.show();
				}
			});
		}
		
		switch (type) {
		case IContentType.ICONTENT_NONE:
		case IContentType.ICONTENT_TEXT:
			doText(viewHolder, item);
			break;
		case IContentType.ICONTENT_PICTURE:
			doPicture(viewHolder, item);
			break;
		case IContentType.ICONTENT_SHARE:
			doShare(viewHolder, item);
			break;
		case IContentType.ICONTENT_SKYCITY:
			doSkyCity(viewHolder, item);
			break;
		default:
			break;
		}

		return convertView;
	}
	
	private void doText(ViewHolder holder, ChatMsgItem item) {
		holder.img_Content.setVisibility(View.GONE);
		holder.txt_Content.setVisibility(View.VISIBLE);
		
		holder.txt_Content.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
		holder.txt_Content.setText(item.getText());
		holder.txt_Content.setTextColor(item.getBySelf() ? 0xFFFFFFFF : 0xFF000000);
		holder.txt_Content.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
			
				final String text = ((TextView)v).getText().toString();
				ChatSelectDialog selectDlg = new ChatSelectDialog((Activity)m_context) {
					
					@SuppressLint("NewApi")
					@SuppressWarnings("deprecation")
					@Override
					public void doCopy() {
						this.dismiss();
						ClipboardManager cmb = (ClipboardManager) m_context.getSystemService(Context.CLIPBOARD_SERVICE);
						cmb.setText(text);
					}
				};
				
				selectDlg.show();
				return true;
			}
		});
		holder.img_Content.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) {
			}
		});
	}
	
	private void doPicture(ViewHolder holder, ChatMsgItem item) {
		final String url = item.getUrl();
		holder.img_Content.setVisibility(View.VISIBLE);
		holder.txt_Content.setVisibility(View.GONE);
		mImageLoader.DisplayImage(item.getThumb(), holder.img_Content, R.drawable.map_icon_small);
		holder.img_Content.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (m_context instanceof Activity)
				{
					Intent intent = new Intent((Activity)m_context, PicPreviewActivity.class);
					intent.putExtra("path", url);
					intent.putExtra("bottom", "hide");
					((Activity)m_context).startActivityForResult(intent, 10088);
				}
			}
		});
	}
	
	private void doShare(ViewHolder holder, ChatMsgItem item) {
		holder.img_Content.setVisibility(View.GONE);
		holder.txt_Content.setVisibility(View.VISIBLE);
		
		holder.txt_Content.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
		holder.txt_Content.setText(item.getText());
		
		holder.txt_Content.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bitmap bmp = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ic_launcher);
				SharePopWnd.showSharePopWnd(m_context, bmp, null, false);
				
			}
		});
	}
	
	private void doSkyCity(ViewHolder holder, ChatMsgItem item) {
		holder.img_Content.setVisibility(View.GONE);
		holder.txt_Content.setVisibility(View.VISIBLE);
		
		holder.txt_Content.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
		holder.txt_Content.setText(item.getText());
		
		holder.txt_Content.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String city = UtilLocationData.getCity();
				
				Dialog alertDialog = new AlertDialog.Builder(m_context)
				.setMessage("确定前往" + city + "？")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								UtilUserData.leaveGroup(m_context, UtilUserData.getGid());
								
								UtilUserData.findGroup(m_context,
										Double.toString(UtilLocationData.getGeoLng()),
										Double.toString(UtilLocationData.getGeoLat()),
										UtilLocationData.getCityGroup());
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {}
						}).create();
				alertDialog.show();
			}
		});
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getDate(Date date) 
	{
		SimpleDateFormat simDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return simDate.format(date);
	}
}
