package org.proof.recorder.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.proof.recorder.R;
import org.proof.recorder.Settings;

public class StaticNotifications {

	public enum ICONS {
		NONE, DEFAULT, CUSTOM
	}

	/**
	 * CONSTANTS
	 */
	private final static String TAG = "StaticNotifications";
	private final static String NS = Context.NOTIFICATION_SERVICE;
	private static final int _ID = 1;
	private static final int PI_REQ_CODE = 0x02;

	/**
	 * STATICS
	 */
	private static Context mContext;
	private static NotificationCompat.Builder mBuilder;
	private static NotificationManager mNM;
	private static Resources mRessources;
	private static PendingIntent mContentIntent;
	private static int mDefaultIcon;

	/**
	 * GETTERS AND SETTERS
	 */

	/**
	 * @return the mContentIntent
	 */
	private static PendingIntent getmContentIntent() {
		return mContentIntent;
	}

	/**
	 * @param mContentIntent
	 *            the mContentIntent to set
	 */
	private static void setmContentIntent(PendingIntent mContentIntent) {
		StaticNotifications.mContentIntent = mContentIntent;
	}

	/**
	 * @return the mDefaultIcon
	 */
	private static int getmDefaultIcon() {
		return mDefaultIcon;
	}

	/**
	 */
	private static void setmDefaultIcon() {
		StaticNotifications.mDefaultIcon = R.drawable.ic_home;
	}

	/**
	 * @return the mRessources
	 */
	@SuppressWarnings("unused")
	private static Resources getmRessources() {
		return mRessources;
	}

	/**
	 * @param mRessources
	 *            the mRessources to set
	 */
	private static void setmRessources(Resources mRessources) {
		StaticNotifications.mRessources = mRessources;
	}

	/**
	 * @return mContext
	 */
	private static Context getmContext() {
		return mContext;
	}

	private static void setmContext(Context mContext) {
		StaticNotifications.mContext = mContext;
	}

	/**
	 * @return the mNM
	 */
	private static NotificationManager getmNM() {
		return mNM;
	}

	/**
	 * @param mNM
	 *            the mNM to set
	 */
	private static void setmNM(NotificationManager mNM) {
		StaticNotifications.mNM = mNM;
	}

	/**
	 * @return the mBuilder
	 */
	private static NotificationCompat.Builder getmBuilder() {
		return mBuilder;
	}

	/**
	 * @param mBuilder
	 *            the mBuilder to set
	 */
	private static void setmBuilder(NotificationCompat.Builder mBuilder) {
		StaticNotifications.mBuilder = mBuilder;
	}

	/**
	 * END GETTERS AND SETTERS
	 */

	/**
	 * CONSTRUCTORS :: Make it private to force calling methods only
	 */
	private StaticNotifications() {
	}

	/**
	 * METHODS (private)
	 */
	private static void initialize(Class<?> destination, Bundle b) {

		if (Settings.isDebug())
			Log.d(TAG, "initialize()");

		setmBuilder(new NotificationCompat.Builder(getmContext()));
		setmNM((NotificationManager) getmContext().getSystemService(NS));
		setmRessources(getmContext().getResources());
		setmDefaultIcon();

		StaticIntents notificationIntent = StaticIntents.create(getmContext(),
				destination, b);
		setmContentIntent(PendingIntent.getActivity(getmContext(), PI_REQ_CODE,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT));
	}

	/**
	 * METHODS (public)
	 */

	public static void show(Context context, Class<?> destination, Bundle b,
							CharSequence title, CharSequence info, CharSequence text,
							ICONS opt, boolean autoCancel, boolean flashLight) {

		if (Settings.showNotifications()) {
			if (Settings.isDebug())
				Log.d(TAG, "show()");

			setmContext(context);
			initialize(destination, b);
			getmBuilder().setContentIntent(getmContentIntent())
					.setContentTitle(title).setTicker(title)
					.setContentInfo(info).setContentText(text)
					.setWhen(System.currentTimeMillis())
					.setAutoCancel(true);

			getmBuilder().setLights(0xff00ff00, 300, 1000);

			switch (ICONS.DEFAULT) {
			case NONE:
				break;

			case CUSTOM:
				getmBuilder().setSmallIcon(0);
				break;

			default:
				getmBuilder().setSmallIcon(getmDefaultIcon());
				break;
			}

			@SuppressWarnings("deprecation")
			Notification notification = getmBuilder().getNotification();
			getmNM().notify(_ID, notification);
		}
	}

	public static void cancelNotification(Context mContext) {
		if (Settings.showNotifications()) {
			try {
				getmNM().cancel(_ID);
			} catch (Exception e) {
				if (Settings.isDebug())
					Log.e(TAG, "" + e);
			}
		}
	}
}
