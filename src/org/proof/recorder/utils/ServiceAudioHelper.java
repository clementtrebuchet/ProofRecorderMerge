package org.proof.recorder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ServiceAudioHelper {
	private static final String TAG = "ServiceAudioHelper";
	private final Context mContext;

	public ServiceAudioHelper(Context m) {
		mContext = m;

	}

	public int maConfAudio() {
		return getMaConfAudio();
	}
	
	public static String transByteToKo(String recordSize) {
		long timeStomp = Long.parseLong(recordSize);
		String[] suffixes = new String[] { "octets", "Ko", "Mo", "Go", "To" };

		double tmpSize = timeStomp;
		int i = 0;

		while (tmpSize >= 1024) {
			tmpSize /= 1024.0;
			i++;
		}
		// arrondi à 10^-2
		tmpSize *= 100;
		tmpSize = (int) (tmpSize + 0.5);
		tmpSize /= 100;

		return tmpSize + " " + suffixes[i];

	}

	private int getMaConfAudio() {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean mic = preferences.getBoolean("MIC", false);
		boolean voice_up = preferences.getBoolean("VOICE_UP", false);
		boolean voice_down = preferences.getBoolean("VOICE_DOWN", false);
		boolean voice_call = preferences.getBoolean("VOICE_CALL", false);
		boolean cam = preferences.getBoolean("CAM", false);
		boolean voice_reco = preferences.getBoolean("VOICE_RECO", false);
		boolean voice_com = preferences.getBoolean("VOICE_COM", false);
		if (mic) {
			Log.e(TAG, "mic is true");
			return 1;
		} else if (voice_up) {
			Log.e(TAG, "voice_up is true");
			return 2;
		} else if (voice_down) {
			Log.e(TAG, "voice_down is true");
			return 3;
		} else if (voice_call) {
			Log.e(TAG, "voice_call is true");
			return 4;
		} else if (cam) {
			Log.e(TAG, "cam is true");
			return 5;
		} else if (voice_reco) {
			Log.e(TAG, "voice_reco is true");
			return 6;
		} else if (voice_com) {
			Log.e(TAG, "voice_com is true");
			return 7;
		} else {
			Log.e(TAG,
					"NO PREFERENCES ARE SET TO true, setting default source mic");
			return 1;

		}

	}
}
