package com.nokia.memhog;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	private NativeHogLib mHogLib;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHogLib = new NativeHogLib();
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onBtnClicked(View v){
	    switch(v.getId()) {
	    case R.id.ButtonEat1:
	    	mHogLib.Swallow(1*1024*1024);
	    	break;
	    case R.id.ButtonEat10:
	    	mHogLib.Swallow(10*1024*1024);
	    	break;
	    case R.id.ButtonEat50:
	    	mHogLib.Swallow(50*1024*1024);
	    	break;
	    case R.id.ButtonEat100:
	    	mHogLib.Swallow(100*1024*1024);
	    	break;
        case R.id.ButtonShit:
            mHogLib.Shit();
            break;
	    }
        int r = mHogLib.HowFat();

        TextView currentRankText = (TextView)this.findViewById(R.id.TextViewHowFat);
        currentRankText.setText("The hog is" + (r/1024/1024) + "MB fat!");
	}
}
