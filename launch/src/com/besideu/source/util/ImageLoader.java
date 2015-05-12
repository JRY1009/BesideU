package com.besideu.source.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// �̳߳�
	ExecutorService executorService;

	private static int g_nScreenWidth = 600;
	private static int g_nScreenHeight = 800;
	
	public static class ImageSize
	{
		public int width;
		public int height;
	}

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);

		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			g_nScreenWidth = dm.widthPixels;
			g_nScreenHeight = dm.heightPixels;
		}
	}

	// ����Ҫ�ķ���
	public void DisplayImage(String url, ImageView imageView, int default_id) {
		imageViews.put(imageView, url);
		// �ȴ��ڴ滺���в���

		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			// ��û�еĻ��������̼߳���ͼƬ
			queuePhoto(url, imageView, default_id);
			imageView.setImageResource(default_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView, int default_id) {
		PhotoToLoad p = new PhotoToLoad(url, imageView, default_id);
		executorService.submit(new PhotosLoader(p));
	}
	
	private Bitmap getBitmap(String url) {

		File file = new File(url);
		if (file.exists()) {
			Bitmap b = getImageThumbnail(url, g_nScreenWidth / 3, g_nScreenHeight / 4);
			if (b != null)
				return b;
		}
		
		File f = fileCache.getFile(url);
		Bitmap b = decodeFile(f);

		if (b != null)
			return b;

		// ����ָ����url������ͼƬ
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			//HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP,new InetSocketAddress("111.206.65.150", 80));
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection(proxy);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// decode���ͼƬ���Ұ����������Լ����ڴ����ģ��������ÿ��ͼƬ�Ļ����СҲ�������Ƶ�
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = Math.max(g_nScreenWidth, g_nScreenHeight);
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	static public Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		
		options.inJustDecodeBounds = false; // ��Ϊ false
		// �������ű�
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		if (bitmap == null)
			return null;
		// ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
		if (h < w) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, h * width
					/ w, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		} else {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, w * height / h,
					height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}
	
	static public ImageSize getThumbSize(int in_width, int in_height) {
		int width = g_nScreenWidth / 3;
		int height = g_nScreenHeight / 4;

		int beWidth = in_width / width;
		int beHeight = in_height / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		
		ImageSize size = new ImageSize();
		if (in_height < in_width) {
			size.width = width;
			size.height = in_height * width / in_width;
		} else {
			size.width = in_width * height / in_height;
			size.height = height;
		}
		
		return size;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public int default_id;
		public PhotoToLoad(String u, ImageView i, int id) {
			url = u;
			imageView = i;
			default_id = id;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// ���µĲ�������UI�߳���
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * ��ֹͼƬ��λ
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// ������UI�߳��и��½���
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(photoToLoad.default_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}
}