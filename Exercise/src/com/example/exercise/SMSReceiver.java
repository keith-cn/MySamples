package com.example.exercise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
	private static final String TAG = "Keith's exercise";

	public static final String SMS_CONTENT = "sms_content";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive(), intent=" + intent);

		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Object[] objs = (Object[]) bundle.get("pdus");
			if (objs == null) {
				Log.i(TAG, "onReceive: objs is null");
				return;
			}
			Log.i(TAG, "onReceive: bundle's lenght=" + objs.length);
			SmsMessage[] msg = new SmsMessage[objs.length];
			for (int i = 0; i < objs.length; i++) {
				msg[i] = SmsMessage.createFromPdu((byte[]) objs[i]);

				String msg_str = msg[0].getMessageBody();
				Toast.makeText(context, msg_str, Toast.LENGTH_LONG).show();

				// Start save service
				Intent intentToSave = new Intent(context, SMSSaveService.class);
				intentToSave.putExtra(SMS_CONTENT, msg_str);
				context.startService(intentToSave);
				return;
			}
		}
	}
}
