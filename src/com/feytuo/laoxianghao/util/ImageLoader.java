package com.feytuo.laoxianghao.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;
/**
 * 图片加载辅助类
 * 通过一级本地map缓存和二级SoftReference缓存获取图片加快图片的加载
 * 在通过本地SD卡文件读取
 * 若本地没有缓存则根据url从网络获取
 * @author feytuo
 *
 */
public class ImageLoader {
	private static final int MAX_CAPACITY = 10;// 一级缓存的最大空间
	private static final long DELAY_BEFORE_PURGE = 1200 * 1000;// 定时清理缓存
	
	private ImageFileCache  fileCache=new ImageFileCache();
	private Context context;
	//用于防止图片异步加载错位问题
	private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());

	// 0.75是加载因子为经验值，true则表示按照最近访问量的高低排序，false则表示按照插入顺序排序
	private HashMap<String, Bitmap> mFirstLevelCache = new LinkedHashMap<String, Bitmap>(
			MAX_CAPACITY / 2, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
			if (size() > MAX_CAPACITY) {// 当超过一级缓存阈值的时候，将老的值从一级缓存搬到二级缓存
				mSecondLevelCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			}
			return false;
		};
	};
	// 二级缓存，采用的是软应用，只有在内存吃紧的时候软应用才会被回收，有效的避免了oom
	private ConcurrentHashMap<String, SoftReference<Bitmap>> mSecondLevelCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			MAX_CAPACITY / 2);

	// 定时清理缓存
	private Runnable mClearCache = new Runnable() {
		@Override
		public void run() {
			clear();
		}
	};
	private Handler mPurgeHandler = new Handler();

	public ImageLoader(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	// 重置缓存清理的timer
	private void resetPurgeTimer() {
		mPurgeHandler.removeCallbacks(mClearCache);
		mPurgeHandler.postDelayed(mClearCache, DELAY_BEFORE_PURGE);
	}

	/**
	 * 清理缓存
	 */
	private void clear() {
		mFirstLevelCache.clear();
		mSecondLevelCache.clear();
	}

	/**
	 * 返回缓存，如果没有则返回null
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		bitmap = getFromFirstLevelCache(url);// 从一级缓存中拿
		if (bitmap != null) {
			return bitmap;
		}
		bitmap = getFromSecondLevelCache(url);// 从二级缓存中拿
		return bitmap;
	}

	/**
	 * 从二级缓存中拿
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getFromSecondLevelCache(String url) {
		Bitmap bitmap = null;
		SoftReference<Bitmap> softReference = mSecondLevelCache.get(url);
		if (softReference != null) {
			bitmap = softReference.get();
			if (bitmap == null) {// 由于内存吃紧，软引用已经被gc回收了
				mSecondLevelCache.remove(url);
			}
		}
		return bitmap;
	}

	/**
	 * 从一级缓存中拿
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getFromFirstLevelCache(String url) {
		Bitmap bitmap = null;
		synchronized (mFirstLevelCache) {
			bitmap = mFirstLevelCache.get(url);
			if (bitmap != null) {// 将最近访问的元素放到链的头部，提高下一次访问该元素的检索速度（LRU算法）
				mFirstLevelCache.remove(url);
				mFirstLevelCache.put(url, bitmap);
			}
		}
		return bitmap;
	}

//	/**
//	 * 加载图片，如果缓存中有就直接从缓存中拿，缓存中没有就下载
//	 * 
//	 * @param url
//	 * @param adapter
//	 * @param holder
//	 */
//	public void loadImage(String url, BaseAdapter adapter, ViewHolder holder) {
//		resetPurgeTimer();
//		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
//		if (bitmap == null) {
//			holder.image.setImageResource(R.drawable.default_avatar);// 缓存没有设为默认图片
//			ImageLoadTask imageLoadTask = new ImageLoadTask();
//			imageLoadTask.execute(url, adapter, holder);
//		} else {
//			holder.image.setImageBitmap(bitmap);// 设为缓存图片
//		}
//
//	}
	/**
	 * 加载图片，如果缓存中有就直接从缓存中拿，缓存中没有就下载
	 * 
	 * @param url
	 * @param adapter
	 * @param holder
	 */
	public void loadImage(String url, BaseAdapter adapter, ImageView iv) {
		imageViews.put(iv, url);
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if(bitmap != null){
			Log.i("ImageLoader", "从cache中取出");
		}
		if(bitmap == null){
			bitmap = fileCache.getImage(url);//从文件中获取
			if(bitmap != null){
				Log.i("ImageLoader", "从文件中取出");
			}
		}
		if (bitmap == null) {
			ImageLoadTask imageLoadTask = new ImageLoadTask();
			imageLoadTask.execute(url, adapter,iv,0);
		}else{
			iv.setImageBitmap(bitmap);
		}
	}
	/**
	 * 加载图片，如果缓存中有就直接从缓存中拿，缓存中没有就下载
	 * 
	 * @param url
	 * @param adapter
	 * @param holder
	 */
	public void loadCornerImage(String url, BaseAdapter adapter, ImageView iv) {
		imageViews.put(iv, url);
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if(bitmap != null){
			Log.i("ImageLoader", "从cache中取出");
		}
		if(bitmap == null){
			bitmap = fileCache.getImage(url);//从文件中获取
			if(bitmap != null){
				Log.i("ImageLoader", "从文件中取出");
			}
		}
		if (bitmap == null) {
			ImageLoadTask imageLoadTask = new ImageLoadTask();
			imageLoadTask.execute(url, adapter,iv,1);
		} else {
			CommonUtils.corner(context, bitmap, iv);
		}
		
	}
	/**
	 * 加载图片，如果缓存中有就直接从缓存中拿，缓存中没有就下载
	 * 
	 * @param url
	 * @param adapter
	 * @param holder
	 */
	public void loadNoImage(String url, BaseAdapter adapter, ImageView iv) {
		imageViews.put(iv, url);
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if(bitmap != null){
			Log.i("ImageLoader", "从cache中取出");
		}
		if(bitmap == null){
			bitmap = fileCache.getImage(url);//从文件中获取
			if(bitmap != null){
				Log.i("ImageLoader", "从文件中取出");
			}
		}
		if (bitmap == null) {
			ImageLoadTask imageLoadTask = new ImageLoadTask();
			imageLoadTask.execute(url, adapter,iv,1);
		} else {
			CommonUtils.cornersmall(context, bitmap, iv);
		}
		
	}
	/**
	 * 放入缓存
	 * 
	 * @param url
	 * @param value
	 */
	public void addImage2Cache(String url, Bitmap value) {
		if (value == null || url == null) {
			return;
		}
		synchronized (mFirstLevelCache) {
			mFirstLevelCache.put(url, value);
		}
	}

	class ImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
		String url;
		BaseAdapter adapter = null;
		ImageView iv;
		int type;//0为方形，1为圆形

		@Override
		protected Bitmap doInBackground(Object... params) {
			url = (String) params[0];
			if(params[1] != null){
				adapter = (BaseAdapter) params[1];
			}
			if(params[2] != null){
				iv = (ImageView)params[2];
			}
			if(params[3] != null){
				type = (int)params[3];
			}
			Bitmap drawable = loadImageFromInternet(url);// 获取网络图片
			return drawable;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if(imageviewReused(iv,url)){
				return;
			}
			if (result == null) {
				return;
			}else{
				Log.i("ImageLoader", "从net中取出");
			}
			addImage2Cache(url, result);// 放入缓存
			fileCache.saveBitmap(result, url);//保存为文件
			if(adapter != null){
				adapter.notifyDataSetChanged();// 触发getView方法执行，这个时候getView实际上会拿到刚刚缓存好的图片
			}
			if(result != null){
				if(type == 0){
					iv.setImageBitmap(result);
				}else{
					CommonUtils.corner(context, result, iv);
				}
			}
		}
	}

	/**
	 * 防止图片异步加载过程中从网络获取后
	 * 因listview重用的view导致的错位
	 * @param iv
	 * @param url
	 * @return
	 */
	private boolean imageviewReused(ImageView iv,String url){
		String tag = imageViews.get(iv);
		if(tag == null || !tag.equals(url)){
			return true;
		}
		return false;
	}
	public Bitmap loadImageFromInternet(String url) {
		URL imageUrl;
		InputStream i = null;
		try {
			imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setInstanceFollowRedirects(true);
			i = (InputStream) imageUrl.getContent();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return BitmapFactory.decodeStream(i);
	}

}
