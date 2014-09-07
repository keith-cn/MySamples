package com.training.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

public class SearchAudioFileService extends Service {
	private static final String TAG = "Keith's SearchAudioFileService";

	private IBinder binder = new SearchAudioFileImpl();
	private List<String> mList = new ArrayList<String>();
	private int mCurrent = 0;	

	@Override
	public IBinder onBind(Intent arg0) {

		// take the folder name
		String sdcardPath = Environment
				.getExternalStorageDirectory().getAbsolutePath();
		Log.i(TAG, "sdcardPath=" + sdcardPath);
		String path = sdcardPath + "/Music";

		// this not work, why?
//		final String selection = MediaStore.Audio.Media.DATA + " like '?" + "%'";
		final String selection = MediaStore.Audio.Media.DATA + " like '" + path + "%'";
		String[] selectionArgs = { path };
		Cursor cursor = getApplication()
				.getContentResolver()
				.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						null,
						selection,
						null,
						MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		
		if (cursor != null) {
			Log.i(TAG, "cursor.getCount(): " + cursor.getCount());

			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				mList.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				Log.i(TAG, "Data: " + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				cursor.moveToNext();
			} 
		}
		cursor.close();
		
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	private class SearchAudioFileImpl extends ISearchAudioFile.Stub {

		@Override
		public String getNext() throws RemoteException {
			return doGetNext();
		}

		@Override
		public String getPrevious() throws RemoteException {
			return doGetPrevious();
		}

		@Override
		public String getCurrent() throws RemoteException {
			return doGetCurrent();
		}

		@Override
		public String getCurrentMusicTitle() throws RemoteException {
			return doGetCurrentMusicTitle();
		}

	}

	public String doGetNext() {
		int next = mCurrent;
		if (mCurrent == mList.size() - 1) {
			next = 0;
		} else {
			next = mCurrent + 1;
		}
		mCurrent = next;
		return mList.get(next);
	}

	public String doGetCurrentMusicTitle() {
		String sdcardPath = Environment
				.getExternalStorageDirectory().getAbsolutePath();
		Log.i(TAG, "sdcardPath=" + sdcardPath);
		String path = sdcardPath + "/Music";
		return doGetCurrent().substring(path.length()+1);
	}

	public String doGetCurrent() {
		return mList.get(mCurrent);
	}

	public String doGetPrevious() {
		int previous = mCurrent;
		if (mCurrent == 0) {
			previous = mList.size() - 1;
		} else {
			previous = mCurrent - 1;
		}
		mCurrent = previous;
		return mList.get(previous);
	}
}
