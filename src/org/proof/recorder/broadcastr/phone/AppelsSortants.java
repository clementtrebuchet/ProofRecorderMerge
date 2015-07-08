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

public class AppelsSortants extends ProofBroadcastReceiver {

	private boolean OUTCALL;
	private boolean SPEAKERON;

	private ObservateurTelephone customPhoneListener = null;

	private void getPreferences(Context context) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		OUTCALL = preferences.getBoolean("OUTCALL", true);

		try {
			SPEAKERON = !customPhoneListener.isExcluded() && preferences.getBoolean("SPEAK", false);
		} catch (Exception e) {
			SPEAKERON = preferences.getBoolean("SPEAK", false);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);

		String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		Bundle bundle = intent.getExtras();

		if (null == bundle)
			return;		
		
		customPhoneListener = new ObservateurTelephone(context);
		
		getPreferences(context);

		if (!OUTCALL) {

			Console.print_debug(
					"PhoneOutGoingBroadCastReceiver OUTCALL INCALL OFF");
			
			customPhoneListener.resetDpm();			
			return;
		}
		
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

		if(Settings.isToastNotifications()) {
			Toast.makeText(
					context, 
					"Appel sortant : " + phoneNumber, 
					Toast.LENGTH_SHORT).show();
		}
		
		Console.print_debug(phoneNumber);
		Console.print_debug(bundle.toString());
		
		customPhoneListener.feedNumbers(phoneNumber);
		customPhoneListener.prepareRecording(phoneNumber, "S");
	}

}