package com.besideu.source.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StorageNode {

	private String mDomain;
	private String mKeyPath;
	
	private long mInt64;
	private String mBuffer;
	private double mDouble;
	
	private SQLiteDatabase mDB;
	private boolean mDataChanged = false;
	private boolean mValid = false;
	
	public boolean Init(String domain, SQLiteDatabase db, Cursor cursor)
	{
		if (cursor != null)
		{
			mDomain = domain;
			mDB = db;

			mValid = true;
			
			mKeyPath = cursor.getString(cursor.getColumnIndex("KeyPath"));
			mBuffer = cursor.getString(cursor.getColumnIndex("Text"));
			mInt64 = cursor.getLong(cursor.getColumnIndex("Int64"));
			mDouble = cursor.getDouble(cursor.getColumnIndex("Float"));

			return true;
		}
		return false;
	}
	
	public boolean saveNodeData()
	{
		if (!mDataChanged)
			return false;
		
		ContentValues values = new ContentValues();
		values.put("Text", mBuffer);
		values.put("Int64", mInt64);
		values.put("Float", mDouble);
		
		mDB.update(mDomain, values, "KeyPath=?", new String[]{mKeyPath});
		return true;
	}
	
	public String getDomain()
	{
		return mDomain;
	}
	
	public String getKeyPath()
	{
		return mKeyPath;
	}
	
	public long getInt64()
	{
		return mInt64;
	}
	
	public void setInt64(long value)
	{
		mDataChanged = true;
		mInt64 = value;
	}
	
	public String getString()
	{
		return mBuffer;
	}
	
	public void setString(String value)
	{
		mDataChanged = true;
		mBuffer = value;
	}
	
	public double getDouble()
	{
		return mDouble;
	}
	
	public void setDouble(double value)
	{
		mDataChanged = true;
		mDouble = value;
	}
	
	public boolean isValid()
	{
		return mValid;
	}
}
