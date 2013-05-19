package org.proof.recorder.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.proof.recorder.R;
import org.proof.recorder.utils.PlugMiddleware;
import org.proof.recorder.utils.Log.Console;
import org.proofs.recorder.codec.ogg.utils.IServiceRecorderOggCx;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OGGMiddleware extends Service implements PlugMiddleware {

	private IServiceRecorderOggCx remotePlugCnx;
	private String mFile;
	private int mSampleRate;
	private int audioSource;
	private int outBitrate;
	private String notificationIntent;
	private String notificationPkg;
	private float mQuality;
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
	private int NOTIFICATION_ID = 66611454;

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
		
		this.remotePlugCnx = new IServiceRecorderOggCx(this);
		this.remotePlugCnx.safelyConnectTheService();

		while (this.remotePlugCnx == null) {
			Console.print_debug("this.remotePlugCnx is :" + this.remotePlugCnx);
		}
		/**
		 * 
		 * @param Filename
		 * @param mSampleRate
		 * @param mQuality
		 * @param audioSource
		 * @param notificationIntent
		 * @param notificationPkg
		 */
		Console.print_debug("proof onStartCommand: " + this.remotePlugCnx);
		mFile = intent.getStringExtra("file");
		mSampleRate = intent.getIntExtra("sampleRate", 44100);
		mQuality = intent.getFloatExtra("quality", 0.4f);
		audioSource = intent.getIntExtra("audioSource", 1);

		if (intent.getExtras().containsKey("notificationIntent")) {

			notificationIntent = intent.getStringExtra("notificationIntent");
		} else {

			notificationIntent = "org.proofs.recorder.codec.mp3.MainActivity";

		}

		if (intent.getExtras().containsKey("notificationPkg")) {

			notificationPkg = intent.getStringExtra("notificationPkg");

		} else {

			notificationPkg = "org.proofs.recorder.codec.mp3";
		}
		Console.print_debug(mFile + "/" + mSampleRate + "/" + audioSource + "/"
				+ outBitrate + "/");
		return (START_STICKY);
	}

	@Override
	public void callWhenReady() {
		this.remotePlugCnx.safelyPassParameters(mFile, mSampleRate, mQuality,
				audioSource, notificationIntent, notificationPkg);
		this.remotePlugCnx.safelyStartRec();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parametersRecAsynchronously(int message) {
		 Console.print_debug("proof parametersRecAsynchronously RC =: "+message);

	}

	@Override
	public void startRecAsynchronously(int message) {
		Console.print_debug("proof startRecAsynchronously RC =: "+message);

	}

	@Override
	public void stopRecAsynchronously(int message) {
		 Console.print_debug("proof stopRecAsynchronously RC =: "+message);

	}
	
	@Override
	public void onDestroy() {
		this.remotePlugCnx.safelyStopRec();
		this.remotePlugCnx.safelyDisconnectTheService();
		stopForegroundCompat(NOTIFICATION_ID);
		super.onDestroy();
		

	}

	@Override
	public void EncodeRawFileAsynchronously(int message) {
		// TODO Auto-generated method stub
		
	}

	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(OGGMiddleware.this, args);
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
		Notification note=new Notification(R.drawable.app_sample_code,
                getString(R.string.notification_ogg_title),
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

		N.setLatestEventInfo(this, getString(R.string.notification_ogg_title),
				getString(R.string.notification_ogg_text), pi);

		N.flags |= Notification.FLAG_NO_CLEAR;
	}

}
