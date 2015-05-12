package com.besideu.source.sqlite;

import java.io.File;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBContext extends ContextWrapper {

	private String mDefaultDir;
	
	public DBContext(Context base) {
		super(base);
		// TODO Auto-generated constructor stub
	}

	public DBContext(Context base, String defaultDir)
	{
		super(base);
		mDefaultDir = defaultDir;
	}

	@Override
	public File getDatabasePath(String name) 
	{
        File file = new File(mDefaultDir + File.separator + name);
        if (!file.getParentFile().exists())
        	file.getParentFile().mkdirs();

        return file;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler)
	{
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), factory);
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory) 
	{
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
	}
	
	
}
