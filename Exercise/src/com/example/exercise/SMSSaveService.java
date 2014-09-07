package com.example.exercise;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class SMSSaveService extends Service {
	private static final String TAG = "Keith's exercise";

	private static final String FILE_NAME = "sms_saved_file";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand(), intent=" + intent + ", flags=" + flags
				+ ", startId=" + startId);
		Bundle extras = intent.getExtras();
		String content = extras.getString(SMSReceiver.SMS_CONTENT);
		Log.i(TAG, "onStartCommand(), sms content=" + content);

		// Save to a file
		saveToFile(content);
		saveToSDCard(content);

		// For test
		readFromFile();

		return super.onStartCommand(intent, flags, startId);
	}

	private void saveToSDCard(String content) {
		Log.i(TAG, "saveToSDCard(), sms content=" + content);
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dirPath = Environment.getExternalStorageDirectory();
			Log.i(TAG, "saveToSDCard(), dirPath="+dirPath);
			
			File file = new File(dirPath, FILE_NAME);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(content.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void saveToFile(String content) {
		Log.i(TAG, "saveToFile(), sms content=" + content);
		
		FileOutputStream fOut = null;
		OutputStreamWriter osw;
		try {
			fOut = openFileOutput(FILE_NAME, MODE_WORLD_WRITEABLE);
			osw = new OutputStreamWriter(fOut);

			// Write string to the file
			osw.write(content);
			osw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
	}

	private void readFromFile() {
		char[] inputBuffer = new char[] { '\0', '\0','\0','\0','\0','\0'}; // max:6
//		char[] inputBuffer = new char[6];
//		inputBuffer[0] = '\0';
//		inputBuffer[1] = '\0';
//		inputBuffer[2] = '\0';
//		inputBuffer[3] = '\0';
//		inputBuffer[4] = '\0';
//		inputBuffer[5] = '\0';
		try {
			FileInputStream fIn = openFileInput(FILE_NAME);
			InputStreamReader isr = new InputStreamReader(fIn);

			isr.read(inputBuffer);
			String readString = new String(inputBuffer);
			Log.i(TAG, "readFromFile(), content=" + readString);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
