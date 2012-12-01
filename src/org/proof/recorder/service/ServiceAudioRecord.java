package org.proof.recorder.service;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.Settings.mFormat;
import org.proof.recorder.utils.AudioHandler;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServiceAudioRecord extends Service {
	
	@SuppressWarnings("unused")
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("org.proof.recorder.service.ServiceAudioRecord".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	private static final String TAG = "ServiceAudioRecord";
	private Bundle b;

	private static String mSense;
	private static String mNumber;

	private static AudioHandler handleAudioRecording;
	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		ServiceAudioRecord getService() {
			return ServiceAudioRecord.this;
		}
	}

	@Override
	public void onCreate() {
		String mFormat = Settings.getAudioFormat(getApplicationContext());
		mFormat forma;
		if (mFormat.equals("3GP")) {

			forma = Settings.mFormat.THREE_GP;

		} else if (mFormat.equals("WAV")) {
			forma = Settings.mFormat.WAV;

		} else if (mFormat.equals("MP3")) {
			forma = Settings.mFormat.MP3;

		}else if (mFormat.equals("OGG")) {
			forma = Settings.mFormat.OGG;

		} else {
			forma = Settings.mFormat.THREE_GP;
		}
		if(Settings.isDebug())Log.d(TAG,"Format is set to :"+forma);
		handleAudioRecording = new AudioHandler(getApplicationContext(),
				forma, Settings.mType.CALL);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/*
		 * Don't recreate the service
		 */
		if(startId <= 1){
			
			super.onStartCommand(intent, flags, startId);
			getBundleInfos(intent);
			
				handleAudioRecording.setmNumber(mNumber);
				handleAudioRecording.setmSense(mSense);
				handleAudioRecording.startRecording();
				
			if (Settings.isDebug()) {
				Log.i(TAG, "Received start id " + startId + ": " + intent);
				Log.v(TAG, "Start Recording");
			}

			if (Settings.isToastNotifications())
				Toast.makeText(this, R.string.debutRec, Toast.LENGTH_SHORT).show();

			return START_STICKY;
		}
		
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {

		handleAudioRecording.stopRecording();
		if (Settings.isDebug())
			Log.v(TAG, "Stop Recording");

		if (Settings.isToastNotifications())
			Toast.makeText(this, R.string.finRec, Toast.LENGTH_SHORT).show();

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void getBundleInfos(Intent intent) {
		b = intent.getExtras();
		mSense = (String) b.get("SENS");
		mNumber = (String) b.get("Number");
	}
}