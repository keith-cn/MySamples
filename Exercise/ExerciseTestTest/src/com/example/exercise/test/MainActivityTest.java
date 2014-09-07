package com.example.exercise.test;

import com.example.exercise.MainActivity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.GridLayout;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	private static final String TAG = "MainActivityTest";

	private static final int CHILD_COUNT = 19;
	private MainActivity mActivity;
	private GridLayout mGridLayout;

	public MainActivityTest() {
		super(MainActivity.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setUp() throws Exception {

		setActivityInitialTouchMode(false);

		mActivity = getActivity();
		mGridLayout = (GridLayout) mActivity
				.findViewById(com.example.exercise.R.id.gridLayout);

		super.setUp();
	}

	public void testPreConditions() {
		assertNotNull(mGridLayout);
		assertTrue(mGridLayout.getChildCount() >= 0);
	}

	public void testUIOperation() {

		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				mGridLayout.requestFocus();
				mGridLayout.scrollBy(10, 0);
				mGridLayout.scrollBy(0, 10);
			}

		});

		Log.e(TAG, "testStateDestory() -- mActivity.savedCellNum:"
				+ mActivity.savedCellNum + "mActivity.cellNum:"
				+ mActivity.cellNum + ", mGridLayout.getChildCount():"
				+ mGridLayout.getChildCount());

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(mActivity.cellNum == CHILD_COUNT);
		assertTrue(mGridLayout.getChildCount() == CHILD_COUNT);
	}

	public void testStateDestory() {

		mActivity.savedCellNum = 1122;

		mActivity.finish();
		mActivity = this.getActivity();

		int currentCellNum = mActivity.savedCellNum;

		Log.e(TAG, "testStateDestory() -- mActivity.savedCellNum:"
				+ mActivity.savedCellNum + "mActivity.cellNum:"
				+ mActivity.cellNum + ", mGridLayout.getChildCount():"
				+ mGridLayout.getChildCount());

		assertEquals(currentCellNum, 1122);
	}

	@UiThreadTest
	public void testStatePause() {

		/*
		 * Get the instrumentation object for this application. This object does
		 * all the instrumentation work for the test runner
		 */

		Instrumentation instr = this.getInstrumentation();
		mActivity.savedCellNum = 2233;

		/*
		 * Use the instrumentation to onPause() on the currently running
		 * Activity. This analogous to calling finish() in the
		 * testStateDestroy() method. This way demonstrates using the test
		 * class' instrumentation.
		 */

		instr.callActivityOnPause(mActivity);

		mActivity.savedCellNum = 0;

		/*
		 * Call the activity's onResume() method. This forces the activity to
		 * restore its state.
		 */

		instr.callActivityOnResume(mActivity);

		Log.e(TAG, "testStateDestory() -- mActivity.savedCellNum:"
				+ mActivity.savedCellNum + "mActivity.cellNum:"
				+ mActivity.cellNum + ", mGridLayout.getChildCount():"
				+ mGridLayout.getChildCount());

		int currentCellNum = mActivity.savedCellNum;
		assertEquals(currentCellNum, 2233);
	}
}
