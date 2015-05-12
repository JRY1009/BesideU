package com.besideu.source.sqlite;

import java.util.List;

import com.besideu.source.util.FileHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StorageCenter {

	private SQLiteDatabase mDB;
	
	public boolean setStorageFile(Context context, String file, int version)
	{
		FileHelper fileHp = new FileHelper(context);
		if (!fileHp.isHaveSD())
			return false;
		
		DBHelper dbHelper = new DBHelper(context, file, null, version);
		mDB = dbHelper.getWritableDatabase();

		return mDB.isOpen();
	}
	
	public String getStorageFile()
	{
		return mDB.getPath();
	}
	
	public void createDomain(String domain)
	{
		final String strTable = "CREATE TABLE IF NOT EXISTS ";
		final String strColumn = " (KeyPath TEXT PRIMARY KEY NOT NULL UNIQUE, Text TEXT, Int64 INTEGER, Float REAL)";
		String sql = strTable + domain + strColumn;
		mDB.execSQL(sql);
	}
	
	public void deleteDomain(String domain)
	{
		final String strTable = "DROP TABLE IF EXISTS ";
		String sql = strTable + domain;
		mDB.execSQL(sql);
	}
	
	public boolean getRootNode(String domain, StorageNode node)
	{
		if (node == null)
			return false;
		
		try
		{
			Cursor root = mDB.query(domain, new String[]{"*"}, "KeyPath=?", new String[]{"\\root\\"}, null, null, null);
			if (root.moveToFirst() == false)
			{
				ContentValues values = new ContentValues();
				values.put("KeyPath", "\\root\\");
				mDB.insert(domain, null, values);
				
				root = mDB.query(domain, new String[]{"*"}, "KeyPath=?", new String[]{"\\root\\"}, null, null, null);
			}
			
			if (root.moveToFirst() == false)
				return false;
			
			return node.Init(domain, mDB, root);
		}
		catch (Exception e)
		{
			Log.e("Sqlite", e.toString());
		}
		
		return false;
	}
	
	public boolean createSubNode(String domain, String keyFullPath, StorageNode node)
	{
		if (node == null)
			return false;
		
		if (querySubNode(domain, keyFullPath, node))
			return true;
		
		ContentValues values = new ContentValues();
		values.put("KeyPath", keyFullPath);
		mDB.insert(domain, null, values);
		
		return querySubNode(domain, keyFullPath, node);
	}
	
	public boolean createSubNode(StorageNode parent, String keyRelPath, StorageNode node)
	{
		if (parent == null || node == null)
			return false;
		
		String domain = parent.getDomain();
		String keyPath = parent.getKeyPath() + keyRelPath + "\\";
		
		return createSubNode(domain, keyPath, node);
	}
	
	public boolean querySubNode(String domain, String keyFullPath, StorageNode node)
	{
		if (node == null)
			return false;
		
		Cursor cursor = mDB.query(domain, new String[]{"*"},  "KeyPath=?", new String[]{keyFullPath}, null, null, null);
		if (cursor.moveToFirst() == false)
			return false;
		
		node.Init(domain, mDB, cursor);
		return true;
	}
	
	public boolean querySubNode(StorageNode parent, String keyRelPath, StorageNode node)
	{
		if (parent == null || node == null)
			return false;
		
		if (parent.getKeyPath() == null || keyRelPath == null)
			return false;
		
		String domain = parent.getDomain();
		String keyPath = "";
		if (parent.getKeyPath().equals("\\root\\") && keyRelPath.equals("\\"))
			keyPath = "\\root\\";
		else
			keyPath = parent.getKeyPath() + keyRelPath + "\\";
		
		return querySubNode(domain, keyPath, node);
	}
	
	public boolean querySubNodeList(String domain, String keyFullPath, boolean recursive, List<StorageNode> listNode)
	{
		if (listNode == null)
			return false;
		
		listNode.clear();
		
		Cursor cursor = mDB.query(domain, new String[]{"*"}, "KeyPath <> ? AND KeyPath LIKE ?", new String[]{keyFullPath, keyFullPath + "%"}, null, null, null);
		
		int nCur = 0;
		int nCount = cursor.getCount();
		
		while (cursor.moveToNext())
		{
			if (recursive == false)
			{
				String keyPath = cursor.getString(cursor.getColumnIndex("KeyPath"));
				
				int countK = 0;
				for(int i=0; i<keyPath.length(); i++)
					if(keyPath.charAt(i) == '\\')
						countK ++;
				
				int countKF = 0;
				for(int i=0; i<keyFullPath.length(); i++)
					if(keyFullPath.charAt(i) == '\\')
						countKF ++;
				
				if (countK - countKF > 1)
					continue;
			}
			
			StorageNode node = new StorageNode();
			node.Init(domain, mDB, cursor);
			
			listNode.add(node);
			
			if (++nCur >= nCount)
				break;
		}
		return true;
	}
	
	public boolean querySubNodeList(StorageNode parent, String keyRelPath, boolean recursive, List<StorageNode> listNode)
	{
		if (listNode == null)
			return false;
		
		if (parent.getKeyPath() == null || keyRelPath == null)
			return false;
		
		listNode.clear();
		
		String domain = parent.getDomain();
		String keyPath = "";
		if (parent.getKeyPath().equals("\\root\\") && keyRelPath.equals("\\"))
			keyPath = "\\root\\";
		else
			keyPath = parent.getKeyPath() + keyRelPath + "\\";
		
		return querySubNodeList(domain, keyPath, recursive, listNode);
	}
	
	public boolean deleteSubNode(String domain, String keyFullPath) {
		
		int result = mDB.delete(domain, "KeyPath=?", new String[]{keyFullPath});
		return result != 0;
	}
	
	public boolean deleteSubNode(StorageNode parent, String keyRelPath) {
		
		if (parent == null)
			return false;
		
		if (parent.getKeyPath() == null || keyRelPath == null)
			return false;
		
		String domain = parent.getDomain();
		String keyPath = "";
		if (parent.getKeyPath().equals("\\root\\") && keyRelPath.equals("\\"))
			keyPath = "\\root\\";
		else
			keyPath = parent.getKeyPath() + keyRelPath + "\\";
		
		return deleteSubNode(domain, keyPath);
	}
}
