package com.example.modifysystemtime;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ModifySystemTimeActivity extends Activity  implements View.OnClickListener{

    private static final long MILLIS_A_DAY = 86400000; // 24*60*60*1000a
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_system_time);
        
        // add button listener
        ((Button) findViewById(R.id.button1)).setOnClickListener(this);
        ((Button) findViewById(R.id.button2)).setOnClickListener(this);
        ((Button) findViewById(R.id.button3)).setOnClickListener(this);
        ((Button) findViewById(R.id.button4)).setOnClickListener(this);
        ((Button) findViewById(R.id.button5)).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.modify_system_time, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        long currentRTC = System.currentTimeMillis();
        if (v.getId() == R.id.button1) {
            long setRTC = currentRTC + MILLIS_A_DAY;
            SystemClock.setCurrentTimeMillis(setRTC);
        } else if (v.getId() == R.id.button2) {
            long setRTC = currentRTC + MILLIS_A_DAY*2;
            SystemClock.setCurrentTimeMillis(setRTC);
        } else if (v.getId() == R.id.button3) {
            long setRTC = currentRTC + MILLIS_A_DAY*14;
            SystemClock.setCurrentTimeMillis(setRTC);
        } else if (v.getId() == R.id.button4) {
            Toast.makeText(this, getCurrentTime(), Toast.LENGTH_LONG).show();
        } else if (v.getId() == R.id.button5) {
            finish();
        }
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
    
    public static String getTimeFromTimeMillis(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(millis);
        return formatter.format(curDate);
    }

}
