package org.proof.recorder.broadcastr.phone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import org.proof.recorder.Settings;
import org.proof.recorder.bases.broadcast.ProofBroadcastReceiver;
import org.proof.recorder.utils.Log.Console;

public class AppelsEntrants extends ProofBroadcastReceiver {

	private boolean INCALL;
	private boolean SPEAKERON;

	private ObservateurTelephone customPhoneListener = null;

	private void getPreferences(Context context) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		INCALL = preferences.getBoolean("INCALL", true);

		try {
			SPEAKERON = !customPhoneListener.isExcluded() && preferences.getBoolean("SPEAK", false);
		} catch (Exception e) {
			SPEAKERON = preferences.getBoolean("SPEAK", false);
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {			
		
		super.onReceive(context, intent);
		
		Bundle bundle = intent.getExtras();
		
		if (null == bundle)
			return;

		String phoneNumber = bundle.getString("incoming_number");
		
		customPhoneListener = new ObservateurTelephone(context);
		
		getPreferences(context);

		if (!INCALL) {
			
			Console.print_debug(
					"BROADCASTRECEVEIVER ACTIF INCALL OFF");
			
			customPhoneListener.resetDpm();
			return;
		}

		Console.print_debug(
				"BROADCASTRECEVEIVER ACTIF");		
		
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
			if (Settings.isToastNotifications()) {
				Toast.makeText(
						context, 
						"Appel entrant  " + phoneNumber, 
						Toast.LENGTH_SHORT).show();
			}			
			
			customPhoneListener.feedNumbers(phoneNumber);
			customPhoneListener.prepareRecording(phoneNumber, "E");
		}

	}	
}