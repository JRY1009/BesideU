package com.besideu.source.share;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.besideu.source.R;
import com.besideu.source.chat.ChatLogMgr;
import com.besideu.source.chat.RecordAdapter;
import com.besideu.source.chat.RecordItem;
import com.besideu.source.util.ImageCompress;
import com.besideu.source.util.UtilHttp;
import com.besideu.source.util.UtilUserData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity extends Activity {

	private List<RecordItem> mLogArrays = new ArrayList<RecordItem>();
	private ListView mListView;
	private RecordAdapter mAdapter;
	private List<RecordItem> mDataArrays = new ArrayList<RecordItem>();
	private String mImagePath;
	private int mWidth;
	private int mHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_share);
		
		mListView = (ListView) findViewById(R.id.lv_share_record);
		mAdapter = new RecordAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		
		mLogArrays = ChatLogMgr.getInstance().loadRecord(this, 0);
		int count = mLogArrays.size();
		for (int i=0; i<count; i++) {
			RecordItem item = mLogArrays.get(i);
			if (item == null)
				continue;
			
			mDataArrays.add(item);
		}
		mAdapter.notifyDataSetChanged();

		
        Intent intent = getIntent();  
        String action = intent.getAction();  
        String type = intent.getType();  
  
        if (Intent.ACTION_SEND.equals(action) && type != null) {  
            
        	if (type.startsWith("image/")) {  
                handleSendImage(intent); 
            }  
        }
	}

	public void onShareBack(View view) {
		finish();
	}
	
	public void handleSendImage(Intent intent) {  
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);  
        if (imageUri != null) {  
        	mImagePath = getPath(imageUri);
        }  
    }
	
	public String getPath(Uri uri)  
    {    
       String[] projection = {MediaStore.Images.Media.DATA };    
       Cursor cursor = managedQuery(uri, projection, null, null, null);    
       int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);    
       cursor.moveToFirst();    
       return cursor.getString(column_index);    
    } 
	
	public void onRecordItem(View view) {
		
		TextView txtName = (TextView) view.findViewById(R.id.txt_record_name);
		String desc = "";
		String gid = "";
		
		int count = mLogArrays.size();
		for (int i=0; i<count; i++) {
			RecordItem item = mLogArrays.get(i);
			if (item == null)
				continue;
			
			if (item.getLocDesc() == txtName.getText())
			{
				desc = item.getLocDesc();
				gid = item.getGroupId();
				break;
			}
		}
		
		final String fGid = gid;
		Dialog alertDialog = new AlertDialog.Builder(this)
		.setMessage("是否发送到 " + desc + "？")
		.setPositiveButton("是",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendPicture(mImagePath, UtilUserData.getUid(), fGid);
						finish();
					}
				})
		.setNegativeButton("否",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
			
					}
				}).create();
		alertDialog.show();
	}
	
	public void sendPicture(String srcPath, String uid, String gid) {
		if (srcPath == null || srcPath.length() == 0) {
			return ;
		}
		
		final String fUid = uid;
		final String fGid = gid;
		
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
				Log.e("Comment", "上传图片失败" + throwable.toString());
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					int nError = response.getInt("Errno");
					if (nError != 0) {
						Log.e("Comment", response.getString("Errmsg"));

						return ;
					}
					
					sendPicture2(response.getJSONObject("Data").getString("Url"), fUid, fGid);
					
				} catch (Exception e) {
					Log.e("Comment", e.toString());
				}
			}
		});
	}
	
	// 第二部：添加到评论列表
	private void sendPicture2(String url, String uid, String gid) {
		String urlString = "http://www.imshenbian.com/comment/add?";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("uid", uid);
		params.put("gid", gid);
		params.put("type", "2");
		params.put("content", url);
		
		try {
			JSONObject obj = new JSONObject(); 
			obj.put("w", mWidth);
			obj.put("h", mHeight);
			obj.put("local_id", java.util.UUID.randomUUID().toString().replace("-", ""));
			params.put("extinfo", obj.toString());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		UtilHttp.post(urlString, params, new JsonHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(ShareActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Toast.makeText(ShareActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
