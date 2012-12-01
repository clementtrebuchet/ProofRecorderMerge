package org.proof.recorder.broadcastr.phone;

import org.proof.recorder.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class AppelsSortants extends BroadcastReceiver {

	private static final String TAG = "AppelsSortants";
	public boolean OUTCALL;
	public boolean SPEAKERON;
	ObservateurTelephone customPhoneListener = new ObservateurTelephone();
	
	private static String phoneNumber = "";
	
	private void getPreferences(Context context) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		OUTCALL = preferences.getBoolean("OUTCALL", true);
		
		try {
			if (customPhoneListener.isExcluded()) {
				SPEAKERON = false;
			} else
				SPEAKERON = preferences.getBoolean("SPEAK", false);
		} catch (Exception e) {
			SPEAKERON = preferences.getBoolean("SPEAK", false);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);		
		Bundle bundle = intent.getExtras();
		
		if (null == bundle)
			return;
		
		getPreferences(context);
		
		if (OUTCALL == false) {
			
			if(Settings.isDebug())
				Log.v(TAG,
					"===============PhoneOutGoingBroadCastReceiver OUTCALL INCALL OFF=================");
			return;
		}

		customPhoneListener.getContext(context);
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		customPhoneListener.getManager(telephony);

			if (SPEAKERON) {
				
				AudioManager audioManage = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
				audioManage.setSpeakerphoneOn(true);
			}
			customPhoneListener.feedNumbers(phoneNumber);
			
			telephony.listen(customPhoneListener,
					PhoneStateListener.LISTEN_CALL_STATE);			

			if(Settings.isDebug())
			{
				Log.i(TAG, "====================" + phoneNumber
					+ "====================");
				Log.i(TAG, "====================" + bundle.toString()
					+ "====================");
			}

			String info = "Appel sortant : " + phoneNumber;
			ObservateurTelephone.sENS_COM = "S";
			
			if(Settings.isToastNotifications())
				Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
		}

}