package org.proof.recorder.service;

import org.proof.recorder.Settings;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServiceSchd extends Service {

	public static final int ALARM_START = 0x012;
	private static final String TAG = null;
	private Intent mIntent;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		super.onStartCommand(intent, flags, startId);
		mIntent = intent;
		if(Settings.isDebug())
		{
			Log.i(TAG, "Received start id " + startId + ": " + mIntent.getFlags());
			Log.v(TAG, "Start SeviceSched");
			
		}
	
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		stopSelf();
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent paramIntent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
