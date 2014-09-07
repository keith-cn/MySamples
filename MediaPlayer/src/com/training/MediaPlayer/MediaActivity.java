package com.training.MediaPlayer;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.io.File;

public class MediaActivity extends FragmentActivity {
	private static final String TAG = "Keith's MediaActivity";

	// Tag that we will use for logging
	// private static final String TAG = MediaActivity.class.getSimpleName();
	// flag to turn on/off debug messages from this class
	private static final boolean DEBUG = true;
	// Links to Java instances of our UI widgets in our Activity
	// Buttons
	private ImageButton prevButton;
	private ImageButton playButton;
	private ImageButton stopButton;
	private ImageButton nextButton;
	// Text view to display folder name
	private TextView folderName;
	// Song name founded in folder
	private TextView songName;
	// progress bar to show current position
	private ProgressBar songProgress;
	// flag to check is player in pause state
	private boolean isPaused;
	// interface to our service
	private IMediaPlayer player;
	// flag to specify is service bound or not
	private boolean isBound = false;
	// our broadcast receiver event handler
	private PlayerEventReceiver eventReceiver = new PlayerEventReceiver();
	// message IDs that we will send to Handler that is responsible for UI
	// update
	private static final int MSG_DURATION = 0;
	private static final int MSG_POSITION = 1;

	private ImageView mAnimationImageView;
	private Animation mAnimation;
	private ViewPager mTabPager;
	private TabPagerAdapter mTabPagerAdapter;
	private final TabPagerListener mTabPagerListener = new TabPagerListener();

	private InfoFragment mInfoFragment;
	private LyricsFragment mLyricsFragment;

	private boolean isPlaying;
	private String mCurrentMusicFullPath;
	// interface to our service
	private ISearchAudioFile searchAudioFile;
	// flag to specify is service bound or not
	private boolean isSearchAudioFileBound = false;
	public int mDuration;
	private TextView mInfoTextView;
	private TextView mLyricTextVeiw;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set main.xml as layout for our activity
		setContentView(R.layout.main);
		// get links to widgets. Use findViewById method to find exact widget by
		// its id
		prevButton = (ImageButton) findViewById(R.id.prev_button);
		playButton = (ImageButton) findViewById(R.id.play_button);
		stopButton = (ImageButton) findViewById(R.id.stop_button);
		nextButton = (ImageButton) findViewById(R.id.next_button);
		folderName = (TextView) findViewById(R.id.folder_name);
		songName = (TextView) findViewById(R.id.song_name);
		songProgress = (ProgressBar) findViewById(R.id.song_progress);

		// Add animation
		mAnimationImageView = (ImageView) findViewById(R.id.image_animation);
		mAnimation = AnimationUtils.loadAnimation(this, R.anim.view_animation);

		mAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				if (!isPlaying) {
					mAnimationImageView.clearAnimation();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (isPlaying) {
					mAnimationImageView.startAnimation(mAnimation);
				}
			}
		});

		// Add Tab

		mTabPager = (ViewPager) findViewById(R.id.tab_pager);
		mTabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
		mTabPager.setAdapter(mTabPagerAdapter);
		mTabPager.setOnPageChangeListener(mTabPagerListener);

		mInfoFragment = new InfoFragment();
		mLyricsFragment = new LyricsFragment();

		// if (mTabPager != null) {
		// mTabPager.setVisibility(View.VISIBLE);
		// }

		mInfoTextView = (TextView) findViewById(R.id.info);
		mInfoTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mInfoTextView.setBackgroundColor(0xffff8000);
				mLyricTextVeiw.setBackgroundColor(0xff7aa0c7);
				mTabPager.setCurrentItem(0);
			}
		});

		mLyricTextVeiw = (TextView) findViewById(R.id.lyrics);
		mLyricTextVeiw.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mInfoTextView.setBackgroundColor(0xff7aa0c7);
				mLyricTextVeiw.setBackgroundColor(0xffff8000);
				mTabPager.setCurrentItem(1);
			}
		});
		mInfoTextView.setBackgroundColor(0xffff8000);
		mLyricTextVeiw.setBackgroundColor(0xff7aa0c7);

		prevButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPlaying) {

					try {
						player.stop();
						mCurrentMusicFullPath = searchAudioFile.getPrevious();
						player.play(Uri.parse(mCurrentMusicFullPath));
						songName.setText("previous");

						// show music information
						mInfoFragment.setMusicUriInfo(mCurrentMusicFullPath);
						mLyricsFragment.setMusicUriInfo(mCurrentMusicFullPath);
					} catch (RemoteException e) {
						Log.e(TAG, "Remote player service died");
					}
				}
			}
		});

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPlaying) {

					try {
						player.stop();
						mCurrentMusicFullPath = searchAudioFile.getNext();
						player.play(Uri.parse(mCurrentMusicFullPath));
						songName.setText(searchAudioFile.getCurrentMusicTitle());

						// show music information
						mInfoFragment.setMusicUriInfo(mCurrentMusicFullPath);
						mLyricsFragment.setMusicUriInfo(mCurrentMusicFullPath);
					} catch (RemoteException e) {
						Log.e(TAG, "Remote player service died");
					}
				}
			}
		});

		// Add the click listener for play button
		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (DEBUG) {
					Log.d(TAG, "Play media, isPlaying=" + isPlaying);
				}

				if (!isPlaying) {
					// start animation
					isPlaying = true;
					mAnimationImageView.startAnimation(mAnimation);

					// the next action is pause
					playButton.setImageResource(R.drawable.pause_icon);
				} else {
					// stop animation
					isPlaying = false;
					mAnimationImageView.clearAnimation();

					// the next action is play
					playButton.setImageResource(R.drawable.play_icon);
				}

				// if service is bound
				if (isBound) {
					// if paused, just resume
					if (isPaused) {
						try {
							player.resume();
						} catch (RemoteException e) {
							Log.e(TAG, "Remote player service died");
						}
					} else {
						// otherwise, start playing and update song name in UI
						try {
							mCurrentMusicFullPath = searchAudioFile
									.getCurrent();
							player.play(Uri.parse(mCurrentMusicFullPath));
							songName.setText(searchAudioFile
									.getCurrentMusicTitle());

							// show music information
							mInfoFragment
									.setMusicUriInfo(mCurrentMusicFullPath);
							mLyricsFragment
									.setMusicUriInfo(mCurrentMusicFullPath);
						} catch (RemoteException e) {
							Log.e(TAG, "Remote player service died");
						}
					}
				}
			}
		});
		// Add onClickListener for pause button
		// pauseButton.setOnClickListener(new View.OnClickListener() {
		// // just pause player if service is bound
		// @Override
		// public void onClick(View v) {
		// if (DEBUG) {
		// Log.d(TAG, "Pause media");
		// }
		// if (isBound) {
		// try {
		// player.pause();
		// isPaused = true;
		// } catch (RemoteException e) {
		// Log.e(TAG, "Remote player service died");
		// }
		// }
		// }
		// });
		// add onClickListener to stop button
		stopButton.setOnClickListener(new View.OnClickListener() {
			// stop player if service is bound
			@Override
			public void onClick(View v) {
				if (DEBUG) {
					Log.d(TAG, "Stop playing media");
				}

				// stop animation
				isPlaying = false;
				mAnimationImageView.clearAnimation();

				// reset progress bar status and play icon
				songProgress.setProgress(0);
				playButton.setImageResource(R.drawable.play_icon);

				if (isBound) {
					try {
						player.stop();
						isPaused = false;
					} catch (RemoteException e) {
						Log.e(TAG, "Remote player service died");
					}
				}
			}
		});
	}

	// Override onStart method in Activity to registry our broadcast receiver
	@Override
	protected void onStart() {
		super.onStart();
		// Create IntentFilter with action from our service
		IntentFilter filter = new IntentFilter(MediaService.PLAY_ACTION);
		// Register our class responsible for broadcast receiver events
		// handling.
		registerReceiver(eventReceiver, filter);
		// Now bind to the service. To do so, create explicit Intent
		Intent serviceIntent = new Intent(this, MediaService.class);
		if (!isBound) {
			// if we are not already bound, bind to the service
			bindService(serviceIntent, playerConnection, BIND_AUTO_CREATE);
		}

		// Now bind to the service. To do so, create explicit Intent
		Intent searchAudioFileIntent = new Intent(this,
				SearchAudioFileService.class);
		if (!isSearchAudioFileBound) {
			// if we are not already bound, bind to the service
			bindService(searchAudioFileIntent, searchAudioFileConnection,
					BIND_AUTO_CREATE);
		}
	}

	// override onStop method of Activity to unregister our broadcast receiver
	// and unbind from service
	@Override
	protected void onStop() {
		super.onStop();
		// unregister our broadcast receiver
		unregisterReceiver(eventReceiver);
		if (isBound) {
			// unbind from our service
			unbindService(playerConnection);
		}

		if (isSearchAudioFileBound) {
			// unbind from our service
			unbindService(searchAudioFileConnection);
		}
	}

	// Service connection implementation to handle events from bind/unbind state
	// changes in service
	private ServiceConnection playerConnection = new ServiceConnection() {
		// service is bound
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// get the interface to our service
			player = IMediaPlayer.Stub.asInterface(service);
			isBound = true;
		}

		// service is unbound
		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound = false;
			player = null;
		}
	};

	// Service connection implementation to handle events from bind/unbind state
	// changes in service
	private ServiceConnection searchAudioFileConnection = new ServiceConnection() {
		// service is bound
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// get the interface to our service
			searchAudioFile = ISearchAudioFile.Stub.asInterface(service);
			isSearchAudioFileBound = true;
		}

		// service is unbound
		@Override
		public void onServiceDisconnected(ComponentName name) {
			isSearchAudioFileBound = false;
			searchAudioFile = null;
		}
	};

	// implementation of our broadcast receiver handler
	private class PlayerEventReceiver extends BroadcastReceiver {

		// broadcast event received
		@Override
		public void onReceive(Context context, Intent intent) {
			// take the media file duration value from event, second parameter
			// is default value
			int duration = intent.getIntExtra(MediaService.SET_DURATION, -1);
			if (duration > 0) {
				mDuration = duration;
			}
			Log.i(TAG, "duration=" + duration);
			if (duration > 0) {
				// create message to be handled by our Handler that is
				// responsible for UI update
				Message m = Message.obtain(handler, MSG_DURATION, duration, 0);
				// send message to the handler. Handler will handle it on UI
				// thread
				handler.sendMessage(m);
			}
			// take the current position of playback
			int position = intent.getIntExtra(MediaService.SET_POSITION, -1);
			Log.i(TAG, "position=" + position);
			if (position >= 0 || position <= duration) {
				// create message to be handled by our Handler that is
				// responsible for UI update
				Message m = Message.obtain(handler, MSG_POSITION, position, 0);
				// send message to the handler. Handler will handle it on UI
				// thread
				handler.sendMessage(m);
			}
		}
	}

	// Handler class implementation
	private Handler handler = new Handler() {
		// message handle callback. It's invoked on UI thread
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// handle the duration event and update progress bar accordingly
			case MSG_DURATION:
				if (DEBUG) {
					Log.i(TAG, "Duration: " + msg.arg1);
				}
				songProgress.setMax(msg.arg1);
				break;
			// handle the position event and update progress bar accordingly
			case MSG_POSITION:
				if (DEBUG) {
					// Log.i(TAG, "Position: " + msg.arg1);
				}
				songProgress.setProgress(msg.arg1);

				// After a song play, set playButton icon to Play status
				if (msg.arg1 + 1000 >= mDuration) {
					Log.i(TAG, "Music has played done.");
					playButton.setImageResource(R.drawable.play_icon);
				}
				break;
			}
		}
	};

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			mAnimationImageView.startAnimation(mAnimation);
		}
	}

	private void updatePager() {
		Log.i(TAG, "updatePager()");

		// FragmentManager fragmentManager = getFragmentManager();
		// FragmentTransaction ft = fragmentManager.beginTransaction();

		// ft.show(mInfoFragment);
		// ft.commit();
	}

	// Tab pager adapter
	private class TabPagerAdapter extends FragmentPagerAdapter {
		// private final FragmentManager mFragmentManager;
		private FragmentTransaction mCurTransaction = null;

		public TabPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
			// mFragmentManager = getFragmentManager();
		}

		@Override
		public int getCount() {
			// Log.i(TAG, "getCount()=" + 2);
			return 2;
		}

		// @Override
		// public boolean isViewFromObject(View arg0, Object arg1) {
		// // TODO Auto-generated method stub
		// return false;
		// }

		// @Override
		// public Object instantiateItem(ViewGroup container, int position) {
		// if (mCurTransaction == null) {
		// // mCurTransaction = mFragmentManager.beginTransaction();
		// }
		// Fragment f = getFragment(position);
		// mCurTransaction.show(f);
		//
		// return f;
		// }

		// private Fragment getFragment(int position) {
		// Log.i(TAG, "getFragment() position=" + position);
		// if (position == 0) {
		// return (Fragment) mInfoFragment;
		// } else if (position == 1) {
		// return (Fragment) mLyricsFragment;
		// }
		// return null;
		// }

		@Override
		public Fragment getItem(int arg0) {
			Log.i(TAG, "getItem() arg0=" + arg0);

			Fragment fragment = null;

			// getItem is called to instantiate the fragment for the given page.
			if (arg0 == 0) {
				fragment = mInfoFragment;
			} else {
				fragment = mLyricsFragment;
			}
			return (Fragment) fragment;
		}
		
		@Override
		public void setPrimaryItem(View container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			Log.i(TAG, "setPrimaryItem position=" + position); 
		}
	}

	// Tab pager listener
	private class TabPagerListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			Log.i(TAG, "onPageScrollStateChanged arg0=" + arg0);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			Log.i(TAG, "onPageScrolled arg0=" + arg0 + "arg1=" + arg1 + "arg2=" + arg2);

		}

		@Override
		public void onPageSelected(int arg0) {
			Log.i(TAG, "onPageSelected arg0=" + arg0);
			if (arg0 == 0) {
				mInfoTextView.setBackgroundColor(0xffff8000);
				mLyricTextVeiw.setBackgroundColor(0xff7aa0c7);
			} else {
				mInfoTextView.setBackgroundColor(0xff7aa0c7);
				mLyricTextVeiw.setBackgroundColor(0xffff8000);
			}

		}
		
	}
}
