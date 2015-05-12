package com.besideu.source.chat;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.besideu.source.chat.ChatMsgItem.IContentType;
import com.besideu.source.chat.ChatMsgItem.MsgItemEvent;
import com.besideu.source.ctrl.PullToRefreshListView;
import com.besideu.source.ctrl.PullToRefreshListView.OnRefreshListener;
import com.besideu.source.util.ImageLoader;
import com.besideu.source.util.UtilHttp;
import com.besideu.source.util.UtilLocationData;
import com.besideu.source.util.UtilUserData;
import com.besideu.source.util.ImageLoader.ImageSize;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Editable.Factory;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class ChatMsgMgr implements MsgItemEvent{
	
	public interface MsgMgrEvent
	{
		public void onReciveMessage(String place, String name, String content);
	}
	
	private static Set<MsgMgrEvent> mEventHandler = new HashSet<MsgMgrEvent>();
	
	public static interface IElapseState
	{
		int STATE_RESET 		= 0;
		int STATE_INCREASE 		= 1;
	}
	
	Context _context;
	private static final int MSG_REQUSET_COMMENTS = 10086;
	private static final String CID_SHARE = "12580";
	private static final String CID_SKYCITY = "12581";
	
	private int mLogStep = -1;
	private List<ChatMsgItem> mLogArrays = new ArrayList<ChatMsgItem>();
	private List<ChatMsgItem> mDataArrays = new ArrayList<ChatMsgItem>();
	private List<String> mCommentIds = new ArrayList<String>();
	
	private PullToRefreshListView mListView;
	private ChatMsgAdapter mAdapter;
	private EditText mEdit;

	private Timer mTimer = new Timer();
	private long mDelay = 1000;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_REQUSET_COMMENTS) {
				getlist();
				super.handleMessage(msg);
			}
		}
	};
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                ;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

        	pushLog(10);
        	
        	mListView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }
	
	private volatile static ChatMsgMgr uniqueInstance;

	private ChatMsgMgr() {
	}

	public static ChatMsgMgr getInstance() {
		if (uniqueInstance == null) {
			synchronized (ChatMsgMgr.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new ChatMsgMgr();
				}
			}
		}
		return uniqueInstance;
	}
	
	public void addEvent(MsgMgrEvent event)
	{
		mEventHandler.add(event);
	}
	
	public void removeEvent(MsgMgrEvent event)
	{
		mEventHandler.remove(event);
	}

	public void init(Context context, PullToRefreshListView listView, EditText edit) {
		_context = context;
		
		mAdapter = new ChatMsgAdapter(context, mDataArrays);

		mEdit = edit;
		mListView = listView;
		mListView.setAdapter(mAdapter);
		mListView.setonRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetDataTask().execute();
            }
        });
		
		refreshState();
	}
	
	public void destroy() {
		
	}
	
	public void refreshState() {
		mLogStep = -1;
		mLogArrays.clear();
		mDataArrays.clear();
		mCommentIds.clear();

		mLogArrays = ChatLogMgr.getInstance().loadChatLog(_context, UtilUserData.getGid());
		
		pushLog(10);
		requestComments(IElapseState.STATE_RESET);
	}
	
	public void setTimerTask(int what, long delay) {
		
		switch (what) {
		case MSG_REQUSET_COMMENTS:
			mTimer.schedule(new TimerTask() {
				public void run() {
					mHandler.sendEmptyMessage(MSG_REQUSET_COMMENTS);
				}
			}, delay);
			break;
		default:
			break;
		}
	}
	
	public void requestComments(int nState) {
		switch (nState) {
		case IElapseState.STATE_RESET:
			mDelay = 1000;
			setTimerTask(MSG_REQUSET_COMMENTS, mDelay);
			break;
		case IElapseState.STATE_INCREASE:
			mDelay = (long) Math.min(mDelay + 1000, 1000 * 10);
			setTimerTask(MSG_REQUSET_COMMENTS, mDelay);
			break;
		default:
			break;
		}
	}
	
	public void pushLog(int count) {
		if (mLogStep > mLogArrays.size() - 1 || mLogStep < 0) {
			mLogStep = mLogArrays.size() - 1;
		}
		
		int index = 0;
		int i = mLogStep;
		for (; i >= 0 && index < count; i--) {
			
			ChatMsgItem item = mLogArrays.get(i);
			if (item == null)
				continue;

			String cid = item.getCid();
			if (mCommentIds.contains(cid) == false) {
				index ++;
				
				Editable e = item.getText();
				if (e != null) {
					UtilBiaoqing.parseEditable(_context, e, mEdit);
					item.setText(e);
				}
				
				mCommentIds.add(cid);
				mDataArrays.add(0, item);
			}
		}
		
		mLogStep = i;
		if (mLogStep == -1) {
			if (true == addSkyCityItem())	index++ ;
			if (true == addShareItem())		index++ ;
		}
		
		mAdapter.notifyDataSetChanged();
		if (index == 0) {
			return ;
		}
		
		if (index < mListView.getCount() - 1) {
			mListView.setSelectionFromTop(index + 1, 60); 
		} else {
			mListView.setSelectionFromTop(index, 0);
		}
		
	}

	private boolean addShareItem() {
		
		if (mCommentIds.contains(CID_SHARE) == false) {
			mCommentIds.add(CID_SHARE);

			ChatMsgItem Item = new ChatMsgItem();
			String name = "身边小帮手";
			Item.setName(name);

			Item.setDate(new Date());
			Item.setCid(CID_SHARE);
			Item.setBySelf(false);
			Item.setContentType(IContentType.ICONTENT_SHARE);

			Item.setText(Factory
					.getInstance()
					.newEditable(
							Html.fromHtml("<font color=#000000>在公司<br>和身边的同事火热开聊，无论八卦、工作<br><br>在小区<br>和同一个小区的伙伴减少隔阂，成为好朋友<br><br>在学校<br>和学姐、学弟打成一片，之后，嘿嘿，您懂得！<br><br></font> "
									+ "<u><font color=#1111EE>快点把 身边 分享给周边的朋友吧！</font></u>")));
			Item.setShowDate(true);

			mDataArrays.add(0, Item);
			return true;
		}

		return false;
	}
	
	private boolean addSkyCityItem() {
		
		String desc = UtilLocationData.getLocDescFull();
		if (desc != null && desc.contains("天空之城")) {
			return false;
		}
		
		String city = UtilLocationData.getCity();
		if (city == null || city.length() == 0) {
			return false;
		}
		
		if (mCommentIds.contains(CID_SKYCITY) == false) {
			mCommentIds.add(CID_SKYCITY);
			
			String text = "<font color=#000000>这个地方没有人？<br></font> "
					+ "<u><font color=#1111EE>先和  " + city + " 的朋友一起聊吧！</font></u>";
			
			ChatMsgItem Item = new ChatMsgItem();
			String name = "身边小帮手";
			Item.setName(name);
	
			Item.setDate(new Date());
			Item.setCid(CID_SKYCITY);
			Item.setBySelf(false);
			Item.setContentType(IContentType.ICONTENT_SKYCITY);
			
			Item.setText(Factory.getInstance().newEditable(Html.fromHtml(text)));
			Item.setShowDate(true);
			
			mDataArrays.add(0, Item);
			
			return true;
		}
		return false;
	}
	
	private boolean isShowDate(Date date) {
		if (mDataArrays.size() > 0) {
			ChatMsgItem lastItem = mDataArrays.get(mDataArrays.size() - 1);
			long span = Math.abs(lastItem.getDate().getTime() - date.getTime());
			return span > 5 * 60 * 1000;
		} 

		return true;
	}
	
	private void onRecvSucceed(JSONObject arg0) {
		
		try {
			int nError = arg0.getInt("Errno");
			if (nError != 0) {
				Log.e("Comment", arg0.getString("Errmsg"));
				requestComments(IElapseState.STATE_INCREASE);
				return ;
			}
				
			int type = 0;
			String cid = "", uid = "", name = "", logo = "", content = "", extinfo = "";
			boolean update = false;
			
			JSONArray result = arg0.getJSONObject("Data").getJSONArray("Result");
			for (int i = result.length() - 1; i >= 0; i--) {

				JSONObject obj = ((JSONObject) result.opt(i));
				type = 0; cid = ""; uid = ""; name = ""; logo = ""; content = ""; extinfo = "";

				if (obj.has("Cid")) 	cid = obj.getString("Cid");
				if (obj.has("Uid")) 	uid = obj.getString("Uid");
				if (obj.has("Name")) 	name = obj.getString("Name");
				if (obj.has("Logo")) 	logo = obj.getString("Logo");
				if (obj.has("Content")) content = obj.getString("Content");
				if (obj.has("ExtInfo")) extinfo = obj.getString("ExtInfo");
				if (obj.has("Type")) 	type = Integer.parseInt(obj.getString("Type"));
				
				ChatMsgItem item = getSended(extinfo);
				if (item != null) {
					if (mCommentIds.contains(cid))
						continue;
					
					mCommentIds.add(cid);
					item.setCid(cid);
					
					ChatMsgItem item2 = new ChatMsgItem();
					item2.setCid(item.getCid());
					item2.setName(item.getName());
					item2.setLogo(item.getLogo());
					item2.setExtinfo(item.getExtinfo());
					item2.setBySelf(item.getBySelf());
					item2.setContentType(item.getContentType());
					item2.setDate(item.getDate());
					item2.setShowDate(item.getShowDate());
					if (type == IContentType.ICONTENT_TEXT || type == IContentType.ICONTENT_NONE) {
						Editable e = Factory.getInstance().newEditable(content);
						UtilBiaoqing.parseEditable(_context, e, mEdit);
						item2.setText(e);
					}
					if (type == IContentType.ICONTENT_PICTURE) {
						int in_width = 460, in_height = 800;
						if (extinfo.length() > 0) {
							JSONTokener jsonParser = new JSONTokener(extinfo);
							JSONObject ext_obj = (JSONObject) jsonParser.nextValue();
							if (ext_obj.has("w"))	in_width = ext_obj.getInt("w");
							if (ext_obj.has("h"))	in_height = ext_obj.getInt("h");
						}

						ImageSize size = ImageLoader.getThumbSize(in_width, in_height);
						String key = content.substring(content.lastIndexOf("key=") + 4);
						String thumb = "http://www.imshenbian.com/image/scale?key="
								+ key + "&width="
								+ Integer.toString(size.width) + "&height="
								+ Integer.toString(size.height);
						item2.setUrl(content);
						item2.setThumb(thumb);
					}
					ChatLogMgr.getInstance().pushChatLog(_context, UtilUserData.getGid(), item2);
				}
				else {
					if (mCommentIds.contains(cid))
						continue;
					
					mCommentIds.add(cid);
					item = new ChatMsgItem();
					item.setCid(cid);
					item.setName(name);
					item.setLogo(logo);
					item.setExtinfo(extinfo);
					item.setBySelf(uid.equals(UtilUserData.getUid()));
					item.setContentType(type);

					if (obj.has("Ctime")) {
						item.setDate(new Date(obj.getLong("Ctime") * 1000));
						item.setShowDate(isShowDate(item.getDate()));
					}

					if (type == IContentType.ICONTENT_TEXT || type == IContentType.ICONTENT_NONE) {
						Editable e = Factory.getInstance().newEditable(content);
						UtilBiaoqing.parseEditable(_context, e, mEdit);
						item.setText(e);
					} 
					else if (type == IContentType.ICONTENT_PICTURE) {

						int in_width = 460, in_height = 800;
						if (extinfo.length() > 0) {
							JSONTokener jsonParser = new JSONTokener(extinfo);
							JSONObject ext_obj = (JSONObject) jsonParser.nextValue();
							if (ext_obj.has("w"))	in_width = ext_obj.getInt("w");
							if (ext_obj.has("h"))	in_height = ext_obj.getInt("h");
						}

						ImageSize size = ImageLoader.getThumbSize(in_width, in_height);
						String key = content.substring(content.lastIndexOf("key=") + 4);
						String thumb = "http://www.imshenbian.com/image/scale?key="
								+ key + "&width="
								+ Integer.toString(size.width) + "&height="
								+ Integer.toString(size.height);
						item.setUrl(content);
						item.setThumb(thumb);
					}

					mDataArrays.add(item);
					ChatLogMgr.getInstance().pushChatLog(_context, UtilUserData.getGid(), item);
					update = true;
				}
			}

			if (update) 
			{
				for (Iterator<MsgMgrEvent> it = mEventHandler.iterator(); it.hasNext();) {
					MsgMgrEvent event = (MsgMgrEvent)it.next();
					event.onReciveMessage(UtilLocationData.getLocDesc(), name,
							(type == IContentType.ICONTENT_PICTURE) ? "[图片]":content);
				}
				
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getCount() - 1);
				requestComments(IElapseState.STATE_RESET);
			} 
			else 
				requestComments(IElapseState.STATE_INCREASE);
		}
		catch (Exception e) {
			requestComments(IElapseState.STATE_INCREASE);
			Log.e("Comment", e.toString());
		}
	}

	public void getlist() {
		final String gid = UtilUserData.getGid();
		
		String urlString = "http://www.imshenbian.com/comment/getlist?";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("gid", gid);

		UtilHttp.get(urlString, params, new JsonHttpResponseHandler() {
			
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				requestComments(IElapseState.STATE_INCREASE);
				Log.e("Comment", "接收信息失败" + throwable.toString());
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				if (gid !=null && gid.equals(UtilUserData.getGid())) {
					onRecvSucceed(response);
				}
			}
		});
	}
	
	public ChatMsgItem getSended(String extinfo) throws JSONException {
		
		if (extinfo == null || extinfo.length() == 0)
			return null;
		
		JSONTokener jsonParser = new JSONTokener(extinfo);
		JSONObject ext_obj = (JSONObject) jsonParser.nextValue();
		if (ext_obj.has("local_id") == false)
			return null;
		
		int count = mDataArrays.size();
		for (int i=0; i<count; ++i) {
			ChatMsgItem item = mDataArrays.get(i);
			if (item == null)
				continue;
			
			if (extinfo.equals(item.getExtinfo())) {
				return item;
			}
		}
		return null;
	}
	
	public void sendText() {
		Editable etContent = mEdit.getText();
		if (etContent.length() == 0)
			return;

		ChatMsgItem item = new ChatMsgItem();
		item.setName(UtilUserData.getName());
		item.setDate(new Date());
		item.setShowDate(isShowDate(item.getDate()));
		item.setLogo(UtilUserData.getLogo());
		item.setBySelf(true);
		item.setContentType(IContentType.ICONTENT_TEXT);
		
		Editable e = mEdit.getText();
		UtilBiaoqing.parseEditable(_context, e, mEdit);
		item.setText(e);
		item.sendText(false);
		item.setEvent(this);

		mDataArrays.add(item);
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mListView.getCount() - 1);
		mEdit.setText("");
		
		requestComments(IElapseState.STATE_RESET);
	}
	
	public void uploadPicture(String srcPath) throws FileNotFoundException {

		ChatMsgItem item = new ChatMsgItem();
		item.setName(UtilUserData.getName());
		item.setDate(new Date());
		item.setShowDate(isShowDate(item.getDate()));
		item.setLogo(UtilUserData.getLogo());
		item.setBySelf(true);
		item.setContentType(IContentType.ICONTENT_PICTURE);
		item.sendPicture(srcPath, false);
		item.setEvent(this);
		
		mDataArrays.add(item);
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mListView.getCount() - 1);
		
		requestComments(IElapseState.STATE_RESET);
	}

	@Override
	public void onSendComplete(boolean bSucceed) {
		if (bSucceed == false) {
			Toast.makeText(_context, "消息发送失败",Toast.LENGTH_LONG).show();
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onResendBegin() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResendComplete(boolean bSucceed) {
		if (bSucceed == false) {
			Toast.makeText(_context, "消息发送失败",Toast.LENGTH_LONG).show();
		}
		mAdapter.notifyDataSetChanged();
	}
}
