package com.besideu.source.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class FileHelper {

	private Context context;

	/** SD卡是否存在 **/
	private boolean mHasSD = false;

	/** SD卡的路径 **/
	private String mSDPath;

	/** 当前程序包的路径 **/
	private String mFilePath;

	public FileHelper(Context context) {

		this.context = context;

		mHasSD = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		mSDPath = Environment.getExternalStorageDirectory().getPath();

		mFilePath = this.context.getFilesDir().getPath();
	}

	/**
	 * 在SD卡上创建文件
	 * @throws IOException
	 */

	public File createSDFile(String fileName) throws IOException {

		File file = new File(mSDPath + "//" + fileName);

		if (!file.exists()) {
			file.createNewFile();
		}

		return file;
	}

	/**
	 * 删除SD卡上的文件
	 * @param fileName
	 */

	public boolean deleteSDFile(String fileName) {

		File file = new File(mSDPath + "//" + fileName);

		if (file == null || !file.exists() || file.isDirectory())
			return false;

		return file.delete();
	}

	/**
	 * 读取SD卡中文本文件
	 * @param fileName
	 * @return
	 */

	public String readSDFile(String fileName) {

		StringBuffer sb = new StringBuffer();
		File file = new File(mSDPath + "//" + fileName);

		try {

			FileInputStream fis = new FileInputStream(file);

			int c;
			while ((c = fis.read()) != -1) {
				sb.append((char) c);
			}

			fis.close();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}

		return sb.toString();

	}

	public String getFilePath() {
		return mFilePath;
	}

	public String getSDPath() {
		return mSDPath;
	}

	public boolean isHaveSD() {
		return mHasSD;
	}
}
