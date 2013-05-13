package org.proofs.recorder.codec.mp3.utils;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class IServiceIntentRecorderMP3Cx implements ServiceConnection {

	IServiceIntentRecorderMP3 service = null;

	private String TAG = IServiceIntentRecorderMP3Cx.class.getName();

	public void onServiceConnected(ComponentName name, IBinder boundService) {
		service = IServiceIntentRecorderMP3.Stub
				.asInterface((IBinder) boundService);
		Log.d(TAG, "onServiceConnected() connected");

	}

	public IServiceIntentRecorderMP3 getService() {

		if (service != null) {

			return service;
		}
		return null;
	}

	public void onServiceDisconnected(ComponentName name) {
		service = null;
		Log.d(TAG, "onServiceDisconnected() disconnected");

	}

}
