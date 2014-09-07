package com.training.MediaPlayer;

import android.app.Activity;
import android.support.v4.app.LoaderManager;
//import android.app.LoaderManager;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
//import android.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
//import android.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
//import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InfoFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "Keith's InfoFragment";

	private View rootView;
	private Button mButton;
	private String mUri;
	private TextView mpTitle;
	private TextView mpArtist;
	private TextView mpAlbum;
	private TextView mpDuration;
	private TextView mpSize;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.i(TAG, getClass().getSimpleName() + ":onCreate()");

		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i(TAG, getClass().getSimpleName() + ":onCreateView()");

		rootView = inflater.inflate(R.layout.info_fragment, container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		Log.i(TAG, getClass().getSimpleName() + ":onActivityCreated()");

		super.onActivityCreated(savedInstanceState);

		mpTitle = (TextView) rootView.findViewById(R.id.info_title);
		mpArtist = (TextView) rootView.findViewById(R.id.info_artist);
		mpAlbum = (TextView) rootView.findViewById(R.id.info_album);
		mpDuration = (TextView) rootView.findViewById(R.id.info_duration);
		mpSize = (TextView) rootView.findViewById(R.id.info_size);

		mButton = (Button) rootView.findViewById(R.id.share);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				// intent.setType("text/plain");
				intent.setType("image/*");
				intent.putExtra(Intent.EXTRA_SUBJECT, "Share something");
				intent.putExtra(Intent.EXTRA_TEXT, "test");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "title test"));
			}
		});

	}

	public void setMusicUriInfo(String uri) {
		Log.i(TAG, "setMusicUriInfo(): " + uri);
		mUri = uri;

		updateMusicInfo(mUri);
	}

	private void updateMusicInfo(String uri) {

		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
//		getLoaderManager().initLoader(0, null, this);
		getLoaderManager().restartLoader(0, null, this);

	}

	@Override
	public void onAttach(Activity activity) {

		Log.i(TAG, getClass().getSimpleName() + ":onAttach()");

		super.onAttach(activity);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		Log.i(TAG, getClass().getSimpleName() + ":onConfigurationChanged()");

		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {

		Log.i(TAG, getClass().getSimpleName() + ":onDestroy()");

		super.onDestroy();
	}

	@Override
	public void onDestroyView() {

		Log.i(TAG, getClass().getSimpleName() + ":onDestroyView()");

		super.onDestroyView();
	}

	@Override
	public void onDetach() {

		Log.i(TAG, getClass().getSimpleName() + ":onDetach()");

		super.onDetach();
	}

	@Override
	public void onPause() {

		Log.i(TAG, getClass().getSimpleName() + ":onPause()");

		super.onPause();
	}

	@Override
	public void onResume() {

		Log.i(TAG, getClass().getSimpleName() + ":onResume()");

		super.onResume();
	}

	@Override
	public void onStart() {

		Log.i(TAG, getClass().getSimpleName() + ":onStart()");

		super.onStart();
	}

	@Override
	public void onStop() {

		Log.i(TAG, getClass().getSimpleName() + ":onStop()");

		super.onStop();
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int id,
			Bundle args) {
		Log.i(TAG, "onCreateLoader(): id=" + id);
		CursorLoader cl = null;
		

		try {
			// prepare parameters
			Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			String[] projection = {
					// MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.ARTIST,
					MediaStore.Audio.Media.ALBUM,
					MediaStore.Audio.Media.DURATION,
					MediaStore.Audio.Media.SIZE
			// MediaStore.Audio.Media.DATA, // --> Location
			// MediaStore.Audio.Media.DISPLAY_NAME,
			};
			String selection = MediaStore.Audio.Media.DATA + " = ?";
			String[] selectionArgs = { mUri };

			cl = new CursorLoader(getActivity(), uri, projection, selection,
					selectionArgs, null);
			Log.i(TAG, "onCreateLoader(): cl=" + cl);
			cl.setUpdateThrottle(2000); // update at most every 2 seconds.
		} catch (Exception e) {
			Log.e(TAG, "onCreateLoader(): ERROR");
		} 

		return cl;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.i(TAG, "onLoadFinished(): cursor=" + cursor);
		
//		String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
		Log.i(TAG, "onLoadFinished(): cursor.getCount()=" + cursor.getCount());
//		Log.i(TAG, "onLoadFinished(): cursor.getInt(0)=" + cursor.getInt(0));
		cursor.moveToFirst();
		mpTitle.setText(cursor.getString(0).toString());
		mpArtist.setText(cursor.getString(1).toString());
		mpAlbum.setText(cursor.getString(2).toString());
		mpDuration.setText(cursor.getString(3).toString());
		mpSize.setText(cursor.getString(4).toString());
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}
}
