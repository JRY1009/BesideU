package com.besideu.source.sqlite;

import java.io.File;

import com.besideu.source.util.FileHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(new DBContext(context, getDefaultDir(context)), name, factory, version);
	}
	
	private static String getDefaultDir(Context context)
	{
		FileHelper fileHp = new FileHelper(context);
		String strPath = fileHp.getSDPath() + File.separator + DBCommonDef.CACHE_PATH;
		return strPath;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
