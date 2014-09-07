package com.example.exercise;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private static final String TAG = "Keith's exercise";

	int mContainerWidth;
	int mContainerHeight;
	int mImageWidth;
	int mImageHeight;
	GridLayout gridContainer;
	int mTaskCount = R.raw.aj - R.raw.a1 + 1;
	DownloadImageFilesTask[] mTask = new DownloadImageFilesTask[mTaskCount];

	// Add two number for MainActivityTest using
	public int cellNum;
	public int savedCellNum;

	private SharedPreferences preferences;

	private String SAVED_NUM = "Saved num";

	// private MyHandler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i(TAG, "onCreate() start.");
		
		Debug.startMethodTracing("Exercise");

		// initialize
		gridContainer = (GridLayout) findViewById(R.id.gridLayout);
		// int mContainerWidth = gridContainer.getWidth();
		// int mContainerHeight = gridContainer.getHeight();
		mContainerWidth = 480; // just for test
		mContainerHeight = 200; // just for test

		Log.i(TAG, "Container width=" + mContainerWidth + ", Height="
				+ mContainerHeight);

		mImageWidth = 190;
		mImageHeight = 150;
		// mHandler = new MyHandler(MainActivity.this, gridContainer);

		for (int i = 0; i < mTaskCount; i++) {
			mTask[i] = new DownloadImageFilesTask();
			mTask[i].execute(Integer.valueOf(R.raw.a1 + i), null, null);
		}

		Log.i(TAG, "onCreate() end.");
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume() start.");

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		savedCellNum = preferences.getInt(SAVED_NUM, 0);

		Log.i(TAG, "onResume() end.");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause() start.");
		for (int i = 0; i < mTaskCount; i++) {
			if (mTask[i] != null) {
				mTask[i].cancel(false);
			}
		}

		// savedCellNum = cellNum;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(SAVED_NUM, savedCellNum).commit();

		Log.i(TAG, "onPause() end.");

		Debug.stopMethodTracing();
		
		super.onPause();
	}

	private class DownloadImageFilesTask extends AsyncTask<Integer, Void, Void> {
		Bitmap bitmap = null;

		@Override
		protected Void doInBackground(Integer... resource) {
			Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
					+ getPackageName() + "/" + resource[0].intValue());

			try {
				bitmap = safeDecodeStream(uri, mImageWidth, mImageHeight);
			} catch (FileNotFoundException e) {
				Log.e(TAG, "file not found");
				e.printStackTrace();
			}

			// do not need to use handler, you can do UI operation on
			// onPostExecute()
			// mHandler.setBitmap(bitmap);
			// mHandler.sendEmptyMessage(0);
			// mHandler.sendEmptyMessageDelayed(0, 1000);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			ImageView mImage = new ImageView(MainActivity.this);
			mImage.setLayoutParams(new LayoutParams(140, 100));
			Log.i(TAG, "onPostExecute(), setImageBitmap");
			mImage.setImageBitmap(bitmap);
			gridContainer.addView(mImage, gridContainer.getChildCount());
			cellNum++;
		}

	}

	static class MyHandler extends Handler {

		Context mContext;
		GridLayout mGridContainer;
		Bitmap mBitmap;

		MyHandler(Context context, GridLayout gridLayout) {
			mContext = context;
			mGridContainer = gridLayout;
		}

		public void setBitmap(Bitmap bitmap) {
			mBitmap = bitmap;
		}

		public void handleMessage(Message msg) {

			ImageView mImage = new ImageView(mContext);
			Log.i(TAG, "before setImageBitmap()");
			mImage.setImageBitmap(mBitmap);
			mGridContainer.addView(mImage, mGridContainer.getChildCount());
			super.handleMessage(msg);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		Log.i(TAG, "onConfigurationChanged(), newConfig=" + newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setTitle(getString(R.string.app_name) + "-landscape");
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setTitle(getString(R.string.app_name) + "-portrait");
		}
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * A safer decodeStream method rather than the one of {@link BitmapFactory}
	 * which will be easy to get OutOfMemory Exception while loading a big image
	 * file.
	 * 
	 * @param uri
	 * @param width
	 * @param height
	 * @return
	 * @throws FileNotFoundException
	 */
	protected Bitmap safeDecodeStream(Uri uri, int width, int height)
			throws FileNotFoundException {
		int scale = 1;
		BitmapFactory.Options options = new BitmapFactory.Options();
		android.content.ContentResolver resolver = this.getContentResolver();

		if (width > 0 || height > 0) {
			// Decode image size without loading all data into memory
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(
					new BufferedInputStream(resolver.openInputStream(uri),
							16 * 1024), null, options);

			int w = options.outWidth;
			int h = options.outHeight;
			Log.i(TAG, "Image width=" + w + ", heith=" + h);
			while (true) {
				if ((width > 0 && w / 2 < width)
						|| (height > 0 && h / 2 < height)) {
					break;
				}
				w /= 2;
				h /= 2;
				scale *= 2;
			}
		}

		// Decode with inSampleSize option
		options.inJustDecodeBounds = false;
		options.inSampleSize = scale;
		return BitmapFactory.decodeStream(
				new BufferedInputStream(resolver.openInputStream(uri),
						16 * 1024), null, options);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
