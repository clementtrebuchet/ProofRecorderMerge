package org.proof.recorder.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.proof.recorder.R;
import org.proof.recorder.receivers.AudioRecorderReceiver;
import org.proof.recorder.receivers.PhoneRecorderReceiver;
import org.proof.recorder.utils.PlugMiddleware;
import org.proof.recorder.utils.Log.Console;
import org.proofs.recorder.codec.mp3.utils.IServiceIntentRecorderMP3;
import org.proofs.recorder.codec.mp3.utils.IServiceIntentRecorderMP3Cx;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MP3Middleware extends Service implements PlugMiddleware  {

	public IServiceIntentRecorderMP3 mService;
	private static IServiceIntentRecorderMP3Cx remotePlugCnx;
	private String mFile;
	private int mSampleRate;
	private int audioSource;
	private int outBitrate;
	private int postEncode;
	private String broadcastClass;
	private String notificationIntent;
	private String notificationPkg;
	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] {int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];
	private Notification lNotif;
	private int NOTIFICATION_ID = 6661144;
	public static int startID;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		try {
			mStartForeground = getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground",
					mStopForegroundSignature);
			return;
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
		}
		try {
			mSetForeground = getClass().getMethod("setForeground",
					mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(
					"OS doesn't have Service.startForeground OR Service.setForeground!");
		}
		
		
	}

	/**
	 * 
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		lNotif = mNotification();
		mInitNotification(lNotif);
		startForegroundCompat(NOTIFICATION_ID , lNotif);
		
		MP3Middleware.remotePlugCnx = new IServiceIntentRecorderMP3Cx(this);
		MP3Middleware.remotePlugCnx.safelyConnectTheService();

		while(MP3Middleware.remotePlugCnx == null){
			Console.print_debug("this.remotePlugCnx is :"+MP3Middleware.remotePlugCnx);
		}
		Console.print_debug("proof onStartCommand: "+ MP3Middleware.remotePlugCnx+"mService is "+mService);
		mFile = intent.getStringExtra("FileName");
		mSampleRate = intent.getIntExtra("mSampleRate", 44100);
		audioSource = intent.getIntExtra("audioSource", 1);
		outBitrate = intent.getIntExtra("outBitrate", 192);
		postEncode = intent.getIntExtra("postEncode", 0);
		broadcastClass = intent.getStringExtra("broadcastClass");
		if (broadcastClass == null){
			broadcastClass = "org.proofs.recorder.codec.mp3.MainActivity";
		}
		if (intent.getExtras().containsKey("notificationIntent")) {

			notificationIntent = intent
					.getStringExtra("notificationIntent");
		} else {

			notificationIntent = "org.proofs.recorder.codec.mp3.MainActivity";

		}

		if (intent.getExtras().containsKey("notificationPkg")) {

			notificationPkg = intent
					.getStringExtra("notificationPkg");

		} else {

			notificationPkg = "org.proofs.recorder.codec.mp3";
		}
		Console.print_debug(mFile+"/"+mSampleRate+"/"+audioSource+"/"+outBitrate+"/"+broadcastClass);			
		return(START_STICKY);	
	}	

	@Override
	public void parametersRecAsynchronously(int message) {
		Console.print_debug("proof parametersRecAsynchronously RC =: "+message);

	}

	@Override
	public void startRecAsynchronously(int message){
		Console.print_debug("proof startRecAsynchronously RC =: "+message);
	}

	@Override
	public void stopRecAsynchronously(int message){
		Console.print_debug("proof stopRecAsynchronously RC =: "+message);
	}
	
	@Override
	public void EncodeRawFileAsynchronously(int message){
		Console.print_debug("proof EncodeRawFileAsynchronously RC =: "+message);
	}
	
	@Override
	public void callWhenReady() {

		MP3Middleware.remotePlugCnx.safelyPassParameters(mFile, mSampleRate,
				audioSource, outBitrate, postEncode, notificationIntent,
				notificationPkg, broadcastClass);
		MP3Middleware.remotePlugCnx.safelyStartRec();

	}	
	
	@Override
	public void onDestroy() {
		if(postEncode == 0){
		MP3Middleware.remotePlugCnx.safelyStopRec();
		}
		MP3Middleware.remotePlugCnx.safelyDisconnectTheService();
		stopForegroundCompat(NOTIFICATION_ID);
		super.onDestroy();


	}	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	/***
	 * pass static reference to connection manager
	 * class PhoneRecordReceiver
	 * from 0 :Phone 1:Audio
	 */
	public static void getCNX(int from){
		switch(from){
		case 0:
			PhoneRecorderReceiver.MP3Cnx(remotePlugCnx);
		case 1:
			AudioRecorderReceiver.MP3Cnx(remotePlugCnx);
		}
		
		
	}
	
	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(MP3Middleware.this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Log.w("MP3Middleware", "Unable to invoke method", e);
		} catch (IllegalAccessException e) {
			// Should not happen.
			Log.w("MP3Middleware", "Unable to invoke method", e);
		}
	}

	/**
	 * MP3Middleware.this is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			return;
		}

		// Fall back on the old API.
		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		mNM.notify(id, notification);
	}

	/**
	 * MP3Middleware.this is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mStopForeground, mStopForegroundArgs);
			return;
		}

		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		mSetForegroundArgs[0] = Boolean.FALSE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
	}
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Notification mNotification(){
		Notification note=new Notification(R.drawable.navigationrefresh,
                getString(R.string.notification_mp3_title),
                System.currentTimeMillis());
		return note;
	}
	
	@SuppressWarnings("deprecation")
	public void mInitNotification(Notification N) {

		Intent intent = new Intent();

		intent.setClass(this, org.proof.recorder.ProofRecorderActivity.class);

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

		N.setLatestEventInfo(this, getString(R.string.notification_mp3_title),
				getString(R.string.notification_mp3_text), pi);

		N.flags |= Notification.FLAG_NO_CLEAR;
	}
	


}
