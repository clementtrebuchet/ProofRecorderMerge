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

public class AppelsEntrants extends BroadcastReceiver {

	private static final String TAG = "AppelsEntrants";
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
		Bundle bundle = intent.getExtras();
		if (null == bundle)
			return;

		phoneNumber = bundle.getString("incoming_number");
		getPreferences(context);

		if (INCALL == false) {
			if (Settings.isDebug())
				Log.v(TAG,
						"===============BROADCASTRECEVEIVER ACTIF INCALL OFF=================");
			return;
		}

		if (Settings.isDebug())
			Log.v(TAG,
					"===============BROADCASTRECEVEIVER ACTIF=================");

		customPhoneListener.getContext(context);
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		customPhoneListener.getManager(telephony);

		telephony.listen(customPhoneListener,
				PhoneStateListener.LISTEN_CALL_STATE);
		if (SPEAKERON) {
			AudioManager audioManage = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			audioManage.setSpeakerphoneOn(true);
		}

		if (Settings.isDebug())
			Log.v(TAG, "===============NUMERO DE TELEPHONE: " + phoneNumber
					+ " =================");
		if (phoneNumber != null) {
			String info = "Appel entrant  " + phoneNumber;
			customPhoneListener.feedNumbers(phoneNumber);
			ObservateurTelephone.sENS_COM = "E";

			if (Settings.isToastNotifications())
				Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
		}

	}
}