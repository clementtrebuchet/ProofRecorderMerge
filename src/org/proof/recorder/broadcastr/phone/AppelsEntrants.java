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

public class AppelsEntrants extends BroadcastReceiver {

	public boolean INCALL;
	public boolean SPEAKERON;
	ObservateurTelephone customPhoneListener = new ObservateurTelephone();

	private static String phoneNumber = "";

	private void getPreferences(Context context) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		INCALL = preferences.getBoolean("INCALL", true);

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
		
		Bundle bundle = intent.getExtras();
		if (null == bundle)
			return;

		phoneNumber = bundle.getString("incoming_number");
		getPreferences(context);

		if (INCALL == false) {
			Console.print_debug(
					"BROADCASTRECEVEIVER ACTIF INCALL OFF");
			
			customPhoneListener.resetDpm();
			return;
		}

		Console.print_debug(
				"BROADCASTRECEVEIVER ACTIF");

		customPhoneListener.setContext(context);
		
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		customPhoneListener.getManager(telephony);

		telephony.listen(customPhoneListener,
				PhoneStateListener.LISTEN_CALL_STATE);
		
		AudioManager audioManage = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		
		if (SPEAKERON) {
			audioManage.setSpeakerphoneOn(true);
		}
		else {
			audioManage.setSpeakerphoneOn(false);
		}

		Console.print_debug(
				"NUMERO DE TELEPHONE: " + phoneNumber);
		
		if (phoneNumber != null) {
			String info = "Appel entrant  " + phoneNumber;
			if (Settings.isToastNotifications())
				Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
			
			customPhoneListener.startRecording(context, phoneNumber, "E");
		}

	}	
}