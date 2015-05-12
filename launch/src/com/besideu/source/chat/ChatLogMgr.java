package com.besideu.source.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.text.Editable.Factory;

import com.besideu.source.sqlite.DBCommonDef;
import com.besideu.source.sqlite.StorageCenter;
import com.besideu.source.sqlite.StorageNode;

public class ChatLogMgr {


	private StorageCenter mSC = new StorageCenter();
	private boolean mInited = false;
	
	private volatile static ChatLogMgr uniqueInstance;

	private ChatLogMgr() {
	}

	public static ChatLogMgr getInstance() {
		if (uniqueInstance == null) {
			synchronized (ChatLogMgr.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new ChatLogMgr();
				}
			}
		}
		return uniqueInstance;
	}


	public boolean Init(Context context)
	{
		if (mInited)
			return true;
		
		if (mSC.setStorageFile(context, DBCommonDef.DB_NAME, DBCommonDef.DB_VERSION))
			mInited = true;
		
		return mInited;
	}
	
	public void update2DB(Context context, String domain, String key, String json)
	{
		if (!Init(context))
			return ;
		
		StorageNode root = new StorageNode();
		if (!mSC.getRootNode(domain, root))
		{
			mSC.createDomain(domain);
			mSC.getRootNode(domain, root);
		}
		
		if (!root.isValid())
			return ;
		
		StorageNode node = new StorageNode();
		mSC.querySubNode(root, key, node);
		if (node.isValid())
		{
			node.setString(json);
			node.saveNodeData();
		}
		else
		{
			mSC.createSubNode(root, key, node);
			node.setString(json);
			node.saveNodeData();
		}
	}
	
	public boolean pushChatLog(Context context, String gid, ChatMsgItem item)
	{
		if (!Init(context))
			return false;
		
		StorageNode root = new StorageNode();
		if (!mSC.getRootNode("chat_log_0", root))
		{
			mSC.createDomain("chat_log_0");
			mSC.getRootNode("chat_log_0", root);
		}
		
		if (!root.isValid())
			return false;
		
		StorageNode node_gid = new StorageNode();
		mSC.querySubNode(root, gid, node_gid);
		if (node_gid.isValid())
		{
			StorageNode node_item = new StorageNode();
			mSC.querySubNode(node_gid, item.getCid(), node_item);
			if (node_item.isValid())
			{
				node_item.setString(data2Json_chat(item));
				node_item.saveNodeData();
			}
			else
			{
				mSC.createSubNode(node_gid, item.getCid(), node_item);
				if (node_item.isValid()) {
					node_item.setString(data2Json_chat(item));
					node_item.saveNodeData();
				}
			}
			
		}
		else
		{
			mSC.createSubNode(root, gid, node_gid);
			if (node_gid.isValid()) 
			{
				StorageNode node_item = new StorageNode();
				mSC.createSubNode(node_gid, item.getCid(), node_item);
				if (node_item.isValid()) {
					node_item.setString(data2Json_chat(item));
					node_item.saveNodeData();
				}
			}
		}
	
		return true;
	}
	
	public List<ChatMsgItem> loadChatLog(Context context, String gid)
	{
		List<StorageNode> list_node = new ArrayList<StorageNode>();
		List<ChatMsgItem> list_chat = new ArrayList<ChatMsgItem>();
		
		if (!Init(context))
			return list_chat;
		
		String domain = "chat_log_0";
		StorageNode root = new StorageNode();
		if (!mSC.getRootNode(domain, root))
			return list_chat;
		
		mSC.querySubNodeList(root, gid, false, list_node);
		
		list_chat.clear();
		
		int nCount = list_node.size();
		for (int i=0; i<nCount; ++i)
		{
			String json = list_node.get(i).getString();
			ChatMsgItem item = json2Data_chat(json);
			if (item != null) {
				list_chat.add(item);
			}
		}
		
		return list_chat;
	}
	
	public String data2Json_chat(ChatMsgItem item)
	{
		try
		{
			JSONObject param = new JSONObject(); 
			param.put("cid", item.getCid());
			param.put("name", item.getName());
			param.put("logo", item.getLogo());
			
			if (item.getDate() != null) {
				param.put("date", item.getDate().getTime()); 
			}
			if (item.getText() != null) {
				param.put("text", item.getText().toString());
			}
				
			param.put("url", item.getUrl());
			param.put("thumb", item.getThumb());
			param.put("extinfo", item.getExtinfo());
			param.put("showDate", item.getShowDate());
			param.put("contentType", item.getContentType());
			param.put("bySelf", item.getBySelf());
			
			return param.toString();
		}
		catch (JSONException ex)
		{
		}
		
		return null;
	}
	
	public ChatMsgItem json2Data_chat(String json) {
		try {
			JSONTokener jsonParser = new JSONTokener(json);
			JSONObject obj = (JSONObject) jsonParser.nextValue();

			ChatMsgItem item = new ChatMsgItem();
			
			if (obj.has("cid")) {
				item.setCid(obj.getString("cid"));
			}
			if (obj.has("date")) {
				item.setDate(new Date(obj.getLong("date")));
			}
			if (obj.has("name")) {
				item.setName(obj.getString("name"));
			}
			if (obj.has("logo")) {
				item.setLogo(obj.getString("logo"));
			}
			if (obj.has("text")) {
				item.setText(Factory.getInstance().newEditable(obj.getString("text")));
			}
			if (obj.has("url")) {
				item.setUrl(obj.getString("url"));
			}
			if (obj.has("thumb")) {
				item.setThumb(obj.getString("thumb"));
			}
			if (obj.has("extinfo")) {
				item.setExtinfo(obj.getString("extinfo"));
			}
			if (obj.has("showDate")) {
				item.setShowDate(obj.getBoolean("showDate"));
			}
			if (obj.has("bySelf")) {
				item.setBySelf(obj.getBoolean("bySelf"));
			}
			if (obj.has("contentType")) {
				item.setContentType(obj.getInt("contentType"));
			}

			return item;
		} catch (JSONException ex) {
		}

		return null;
	}
	
	public boolean pushRecordPlace(Context context, int type, RecordItem item)
	{
		if (!Init(context))
			return false;
		
		StorageNode root = new StorageNode();
		if (!mSC.getRootNode("record_log_0", root))
		{
			mSC.createDomain("record_log_0");
			mSC.getRootNode("record_log_0", root);
		}
		
		if (!root.isValid())
			return false;
		
		String sub_string = (type == 0) ? "group" : "single";
		String id = (type == 0) ? item.getGroupId() : item.getUserId();
		
		StorageNode node_gid = new StorageNode();
		mSC.querySubNode(root, sub_string, node_gid);
		if (node_gid.isValid())
		{
			StorageNode node_item = new StorageNode();
			mSC.querySubNode(node_gid, id, node_item);
			if (node_item.isValid())
			{
				node_item.setString(data2Json_record(item));
				node_item.saveNodeData();
			}
			else
			{
				mSC.createSubNode(node_gid, id, node_item);
				if (node_item.isValid()) {
					node_item.setString(data2Json_record(item));
					node_item.saveNodeData();
				}
			}
			
		}
		else
		{
			mSC.createSubNode(root, sub_string, node_gid);
			if (node_gid.isValid()) 
			{
				StorageNode node_item = new StorageNode();
				mSC.createSubNode(node_gid, id, node_item);
				if (node_item.isValid()) {
					node_item.setString(data2Json_record(item));
					node_item.saveNodeData();
				}
			}
		}
	
		return true;
	}
	
	public boolean deleteRecord(Context context, int type, RecordItem item) {
		
		if (!Init(context))
			return false;
		
		StorageNode root = new StorageNode();
		if (!mSC.getRootNode("record_log_0", root))
		{
			mSC.createDomain("record_log_0");
			mSC.getRootNode("record_log_0", root);
		}
		
		if (!root.isValid())
			return false;
		
		String sub_string = (type == 0) ? "group" : "single";
		String id = (type == 0) ? item.getGroupId() : item.getUserId();
		
		StorageNode node_gid = new StorageNode();
		mSC.querySubNode(root, sub_string, node_gid);
		if (node_gid.isValid())
		{
			return mSC.deleteSubNode(node_gid, id);
		}
		
		return true;
	}
	
	public String data2Json_record(RecordItem item)
	{
		try
		{
			JSONObject param = new JSONObject(); 
			param.put("gid", item.getGroupId());
			param.put("lng", item.getGeoLng());
			param.put("lat", item.getGeoLat());
			
			if (item.getDate() != null) {
				param.put("date", item.getDate().getTime()); 
			}
				
			param.put("desc", item.getLocDesc());
			param.put("fulldesc", item.getLocDescFull());
			param.put("imgurl", item.getImgUrl());
			param.put("type", item.getType());
			param.put("username", item.getUserName());
			param.put("userid", item.getUserId());
			
			return param.toString();
		}
		catch (JSONException ex)
		{
		}
		
		return null;
	}
	
	public RecordItem json2Data_record(String json) {
		try {
			JSONTokener jsonParser = new JSONTokener(json);
			JSONObject obj = (JSONObject) jsonParser.nextValue();

			RecordItem item = new RecordItem();
			
			if (obj.has("gid")) {
				item.setGroupId(obj.getString("gid"));
			}
			if (obj.has("date")) {
				item.setDate(new Date(obj.getLong("date")));
			}
			if (obj.has("lng")) {
				item.setGeoLng(obj.getDouble("lng"));
			}
			if (obj.has("lat")) {
				item.setGeoLat(obj.getDouble("lat"));
			}
			if (obj.has("desc")) {
				item.setLocDesc(obj.getString("desc"));
			}
			if (obj.has("fulldesc")) {
				item.setLocDescFull(obj.getString("fulldesc"));
			}
			if (obj.has("imgurl")) {
				item.setImgUrl(obj.getString("imgurl"));
			}
			if (obj.has("username")) {
				item.setUserName(obj.getString("username"));
			}
			if (obj.has("userid")) {
				item.setUserId(obj.getString("userid"));
			}
			if (obj.has("type")) {
				item.setType(obj.getInt("type"));
			}

			return item;
		} catch (JSONException ex) {
		}

		return null;
	}
	
	public List<RecordItem> loadRecord(Context context, int type)
	{
		List<StorageNode> list_node = new ArrayList<StorageNode>();
		List<RecordItem> list_record = new ArrayList<RecordItem>();
		
		if (!Init(context))
			return list_record;
		
		String domain = "record_log_0";
		StorageNode root = new StorageNode();
		if (!mSC.getRootNode(domain, root))
			return list_record;
		
		String sub_string = (type == 0) ? "group" : "single";
		mSC.querySubNodeList(root, sub_string, false, list_node);
		
		list_record.clear();
		
		int nCount = list_node.size();
		for (int i=0; i<nCount; ++i)
		{
			String json = list_node.get(i).getString();
			RecordItem item = json2Data_record(json);
			if (item != null) {
				list_record.add(item);
			}
		}
		
		Comparator<Object> comp = new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				RecordItem r1 = (RecordItem) o1;
				RecordItem r2 = (RecordItem) o2;
				long t1 = r1.getDate().getTime();
				long t2 = r2.getDate().getTime();
				if (t1 < t2)
					return 1;
				else
					return -1;
			}
		};
		Collections.sort(list_record, comp);
		return list_record;
	}
}
