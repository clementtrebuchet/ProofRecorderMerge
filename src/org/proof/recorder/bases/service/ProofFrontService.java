package org.proof.recorder.bases.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import org.proof.recorder.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
public abstract class ProofFrontService extends ProofService {

	// ForGround Service Mechanic

	private static final int NOTIFICATION_ID = 1234567890;

	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
		int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private final Object[] mSetForegroundArgs = new Object[1];
	private final Object[] mStartForegroundArgs = new Object[2];
	private final Object[] mStopForegroundArgs = new Object[1];

	@SuppressWarnings("deprecation")
	private Notification mNotification() {
		int notifText = R.string.sc_upload_file_msg;
		int notifIcon = R.drawable.icon_soundcloud;
		return new Notification(notifIcon,
				getString(notifText),
				System.currentTimeMillis());
	}

	@SuppressWarnings("deprecation")
	private void mInitNotification(Notification N) {


		Intent intent = new Intent();

		intent.setClass(this, org.proof.recorder.ProofRecorderActivity.class);

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
				Intent.FLAG_ACTIVITY_NEW_TASK|
				Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi=PendingIntent.getActivity(this, 0, intent, 0);

		int eventInfoSubText = R.string.sc_upload_file_msg;
		int eventInfoText = R.string.sc_upload_file_msg;
		N.setLatestEventInfo(this,
				getString(eventInfoText),
				getString(eventInfoSubText),
				pi);

		N.flags|=Notification.FLAG_NO_CLEAR;
	}

	private void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Log.w("ApiDemos", "Unable to invoke method", e);
		} catch (IllegalAccessException e) {
			// Should not happen.
			Log.w("ApiDemos", "Unable to invoke method", e);
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	private void startForegroundCompat(Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = ProofFrontService.NOTIFICATION_ID;
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			return;
		}

		// Fall back on the old API.
		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		mNM.notify(ProofFrontService.NOTIFICATION_ID, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	private void stopForegroundCompat() {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mStopForeground, mStopForegroundArgs);
			return;
		}

		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(ProofFrontService.NOTIFICATION_ID);
		mSetForegroundArgs[0] = Boolean.FALSE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
	}

	@Override
	public void onCreate() {
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		_onDestroy();
		/*
		 * stopping ForegroundCompat
		 */
		stopForegroundCompat();
	}

	// End ForeGround
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		_onStartCommand(intent, flags, startId);		
		/*
		 * start startForegroundCompat
		 */
		Notification lNotif = mNotification();
		mInitNotification(lNotif);
		startForegroundCompat(lNotif);

		return (START_STICKY);
	}

	private final String pendingIntent = null;
	private final String pendingIntentPackage = null;

	protected abstract void _onStartCommand(Intent intent, int flags, int startId);
	protected abstract void _onDestroy();
}
