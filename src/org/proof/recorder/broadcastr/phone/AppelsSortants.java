package org.proof.recorder.broadcastr.phone;

import org.proof.recorder.Settings;
import org.proof.recorder.utils.Log.Console;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class AppelsSortants extends BroadcastReceiver {

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

		Console.setTagName(this.getClass().getSimpleName());

		phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);		
		Bundle bundle = intent.getExtras();

		if (null == bundle)
			return;

		getPreferences(context);

		if (OUTCALL == false) {

			Console.print_debug(
					"PhoneOutGoingBroadCastReceiver OUTCALL INCALL OFF");
			
			customPhoneListener.resetDpm();			
			return;
		}

		customPhoneListener.setContext(context);
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		customPhoneListener.getManager(telephony);

		AudioManager audioManage = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		
		if (SPEAKERON) {
			audioManage.setSpeakerphoneOn(true);
		}
		else {
			audioManage.setSpeakerphoneOn(false);
		}

		customPhoneListener.startRecording(context, phoneNumber, "S");

		telephony.listen(customPhoneListener,
				PhoneStateListener.LISTEN_CALL_STATE);			

		Console.print_debug(phoneNumber);
		Console.print_debug(bundle.toString());

		String info = "Appel sortant : " + phoneNumber;

		if(Settings.isToastNotifications())
			Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}

}