package org.proof.recorder.wigdet;

import java.util.Observable;
import java.util.Observer;

import org.proof.recorder.ProofRecorderActivity;
import org.proof.recorder.R;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class ProofRecorderWidget extends AppWidgetProvider implements Observer {

	private final static String TAG = ProofRecorderWidget.class.getName();
	private final String UPDATE = "org.proof.recorder.wigdet.ProofRecorderWidget.UPDATE";
	public final static String ACTION_ENABLE_SERVICE = "org.proof.recorder.wigdet.ProofRecorderWidget.ACTION_ENABLE_SERVICE";
	public final String ACTION_DISABLE_SERVICE = "org.proof.recorder.wigdet.ProofRecorderWidget.ACTION_DISABLE_SERVICE";
	public final static String SET_FORMAT = "org.proof.recorder.wigdet.ProofRecorderWidget.SET_FORMAT";
	private static final String START_ACTION = "android.intent.action.START_AUDIO_RECORDER";
	private static final String STOP_ACTION = "android.intent.action.STOP_AUDIO_RECORDER";
	public final static String REC = "org.proof.recorder.wigdet.ProofRecorderWidget.REC";
	public final static String SP = "org.proof.recorder.wigdet.ProofRecorderWidget.SPEAKER";
	private SharedPreferences mSharedPreferences = null;
	private Editor mEditor = null;
	public final static String ACTION_UPDATE_SERVICE = "org.proof.recorder.wigdet.ProofRecorderWidget.ACTION_UPDATE_SERVICE";
	public boolean isEnable;
	public boolean recOn;
	public boolean isrecording;
	private SharedPreferences preferences;
	private Editor mRecEditor;
	private boolean speakerOn;
	private static Long defaultTimerInMinutes = (long) (5);
	public RecorderDetector mRecorderDetector = null;
	private static int mAppWId = 0;

	/**
	 * @return the mAppWId
	 */
	public static int getmAppWId() {
		Log.d(TAG, "getmAppWId() : " + ProofRecorderWidget.mAppWId);
		return ProofRecorderWidget.mAppWId;
	}

	/**
	 * @param mAppWId
	 *            the mAppWId to set
	 */
	public static void setmAppWId(int mAppWId) {
		ProofRecorderWidget.mAppWId = mAppWId;
		Log.d(TAG, "setmAppWId(int mAppWId) : " + ProofRecorderWidget.mAppWId);
	}

	private static void resetAppWId() {

		ProofRecorderWidget.mAppWId = 0;
		Log.d(TAG, "resetAppWId() : " + ProofRecorderWidget.mAppWId);
	}

	private static long mRefreshInterval() {

		return defaultTimerInMinutes * 60 * 1000;
	}

	public ProofRecorderWidget() {

	}

	/**
	 * 
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		if (mRecorderDetector == null) {
			mRecorderDetector = new RecorderDetector(context);
			mRecorderDetector.getInstance().addObserver(
					ProofRecorderWidget.this);
			Log.d(TAG, "mRecorderDetector.addObserver(this) "
					+ mRecorderDetector.countObservers());
		}
		for (int i = 0; i < appWidgetIds.length; i++) {
			if (appWidgetIds[i] != AppWidgetManager.INVALID_APPWIDGET_ID) {
				Log.d(TAG,
						"appWidgetIds[i] != AppWidgetManager.INVALID_APPWIDGET_ID:"
								+ appWidgetIds[i] + "--"
								+ AppWidgetManager.INVALID_APPWIDGET_ID);
				// buildUpdate(context, appWidgetManager, appWidgetIds[i]);
				serviceUpdateView(context, appWidgetIds[i]);
				setAlarm(context, false, appWidgetIds[i],
						AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			} else {
				Log.d(TAG,
						"appWidgetIds[i] == AppWidgetManager.INVALID_APPWIDGET_ID:"
								+ appWidgetIds[i] + "--"
								+ AppWidgetManager.INVALID_APPWIDGET_ID);
			}

		}
		Log.d(TAG, "Finnih method onUpdate OK");

	}

	private void serviceUpdateView(Context mContext, int mAppWidgetId) {
		Intent iService = new Intent(mContext, MBuildUpdate.class);
		Bundle bService = new Bundle();
		bService.putInt("appWidgetIds", mAppWidgetId);
		iService.putExtras(bService);
		mContext.startService(iService);
	}

	public static class MBuildUpdate extends Service {
		int appWidgetIds;
		private final String TAG = MBuildUpdate.class.getName();

		public int onStartCommand(Intent intent, int flags, int startId) {
			super.onStartCommand(intent, flags, startId);
			appWidgetIds = intent.getIntExtra("appWidgetIds", 0);
			// extra assertion
			assert (appWidgetIds != 0);
			// Update the widget
			RemoteViews remoteView = buildRemoteView(this);
			// Push update to homescreen
			pushUpdate(remoteView);
			// No more updates so stop the service and free resources
			stopSelf();
			Log.d(TAG, "stopSelf()");
			return startId;
		}

		public RemoteViews buildRemoteView(Context context) {
			RemoteViews updateView = null;

			updateView = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);

			PendingIntent mActionEnableService = getControlIntent(context,
					appWidgetIds, ProofRecorderWidget.ACTION_ENABLE_SERVICE,
					updateView);
			updateView.setOnClickPendingIntent(R.id.imageButtonon,
					mActionEnableService);

			PendingIntent mActionUpdate = getControlIntent(context,
					appWidgetIds, ProofRecorderWidget.ACTION_UPDATE_SERVICE,
					updateView);
			updateView.setOnClickPendingIntent(R.id.imageButtonrefresh,
					mActionUpdate);

			PendingIntent mActionSetFormat = getControlIntent(context,
					appWidgetIds, ProofRecorderWidget.SET_FORMAT, updateView);
			updateView.setOnClickPendingIntent(R.id.imageButtonbox,
					mActionSetFormat);

			PendingIntent mRec = getControlIntent(context, appWidgetIds,
					ProofRecorderWidget.REC, updateView);
			updateView.setOnClickPendingIntent(R.id.imageButtonstoprec, mRec);

			PendingIntent mSp = getControlIntent(context, appWidgetIds,
					ProofRecorderWidget.SP, updateView);
			updateView.setOnClickPendingIntent(R.id.imageSpeaker, mSp);
			Log.d(TAG, "Finnih method buildRemoteView OK");
			return updateView;
		}

		@Override
		public void onConfigurationChanged(Configuration newConfig) {
			int oldOrientation = this.getResources().getConfiguration().orientation;

			if (newConfig.orientation != oldOrientation) {
				// Update the widget
				RemoteViews remoteView = buildRemoteView(this);
				Log.d(TAG, "newConfig.orientation != oldOrientation");

				// Push update to homescreen
				pushUpdate(remoteView);
			}
		}

		private void pushUpdate(RemoteViews remoteView) {
			ComponentName myWidget = new ComponentName(this,
					ProofRecorderWidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(myWidget, remoteView);
			Log.d(TAG, "pushUpdate(RemoteViews remoteView)");

		}

		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * 
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetIds
	 */
	public void buildUpdate(Context context, AppWidgetManager appWidgetManager,
			int appWidgetIds) {

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);

		PendingIntent mActionEnableService = getControlIntent(context,
				appWidgetIds, ACTION_ENABLE_SERVICE, remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonon,
				mActionEnableService);

		PendingIntent mActionUpdate = getControlIntent(context, appWidgetIds,
				ProofRecorderWidget.ACTION_UPDATE_SERVICE, remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonrefresh,
				mActionUpdate);

		PendingIntent mActionSetFormat = getControlIntent(context,
				appWidgetIds, SET_FORMAT, remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonbox,
				mActionSetFormat);

		PendingIntent mRec = getControlIntent(context, appWidgetIds, REC,
				remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonstoprec, mRec);

		PendingIntent mSp = getControlIntent(context, appWidgetIds, SP,
				remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.imageSpeaker, mSp);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

	}

	/**
	 * 
	 * @param mContext
	 * @param mAppWidgetId
	 * @param mCommand
	 * @param mRemoteViews
	 * @return
	 */
	public static PendingIntent getControlIntent(Context mContext,
			int mAppWidgetId, String mCommand, RemoteViews mRemoteViews) {
		Intent commandIntent = new Intent(mContext, ProofRecorderWidget.class);
		commandIntent.setAction(mCommand);
		commandIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				mAppWidgetId);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
				mAppWidgetId, commandIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	/**
	 * 
	 * @param aContext
	 * @param aCommand
	 * @return
	 */
	public static PendingIntent getControlIntent(Context aContext,
			String aCommand, int appWidgetId) {
		Intent commandIntent = new Intent(aContext, ProofRecorderWidget.class);
		commandIntent.setAction(aCommand);
		commandIntent
				.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(aContext, 0,
				commandIntent, 0);
		return pendingIntent;
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
	 */
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		if (mRecorderDetector == null) {
			mRecorderDetector = new RecorderDetector(context);
			mRecorderDetector.getInstance().addObserver(
					ProofRecorderWidget.this);
			Log.d(TAG, "mRecorderDetector.addObserver(this) "
					+ mRecorderDetector.countObservers());
		}

	}

	/**
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		super.onReceive(context, intent);
		try {
			int appWidgetId = intent.getExtras().getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);

			if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
				if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
					Log.d(TAG,
							"appWidgetIds[i] != AppWidgetManager.INVALID_APPWIDGET_ID:"
									+ appWidgetId + "--"
									+ AppWidgetManager.INVALID_APPWIDGET_ID
									+ "--" + action.toString());
					onDeleted(context, new int[] { appWidgetId });
				} else {
					Log.d(TAG,
							"appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID:"
									+ appWidgetId + "--"
									+ AppWidgetManager.INVALID_APPWIDGET_ID
									+ "--" + action.toString());
				}
			} else {

				if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {

					initmEditor(context);
					mAssertmShareNotNull();

					/*
					 * check action @ToDo
					 */
					if (action.equals(ACTION_ENABLE_SERVICE)) {
						if (isEnable) {
							mEditor.putBoolean("INCALL", false);
							mEditor.putBoolean("OUTCALL", false);
							isEnable = false;
							Log.d(TAG, "ACTION_ENABLE_SERVICE :" + isEnable);
						} else {
							mEditor.putBoolean("INCALL", true);
							mEditor.putBoolean("OUTCALL", true);
							isEnable = true;
							Log.d(TAG, "ACTION_ENABLE_SERVICE :" + isEnable);
						}

						mCommit();

					} else if (action.equals(ACTION_DISABLE_SERVICE)) {

						Log.d(TAG, "ACTION_DISABLE_SERVICE OK");

					} else if (action.equals(SET_FORMAT)) {

						Intent mActivity = new Intent(context,
								WidgetPreferenceFormat.class);
						mActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(mActivity);
						Log.d(TAG, "mActivity");

					} else if (action.equals(REC)) {
						Log.d(TAG, "REC");
						if (recOn) {
							mRecEditor.putBoolean("isrecording", false)
									.commit();
							recOn = false;
							Intent I = new Intent();
							I.setAction(STOP_ACTION);
							context.sendBroadcast(I);
						} else {
							mRecEditor.putBoolean("isrecording", true).commit();
							recOn = true;
							Intent I = new Intent();
							I.setAction(START_ACTION);
							context.sendBroadcast(I);
						}

					} else if (action
							.equals(ProofRecorderWidget.ACTION_UPDATE_SERVICE)) {
						Intent I = new Intent(context,
								ProofRecorderActivity.class);
						I.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(I);

					} else if (action.equals(SP)) {
						if (speakerOn) {
							mEditor.putBoolean("SPEAK", false);
							speakerOn = false;

						} else {
							mEditor.putBoolean("SPEAK", true);
							speakerOn = true;
						}
						mCommit();
					} else if (action.equals(UPDATE)
							|| action
									.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {

						Log.e(TAG, "Refreshing the "
								+ ProofRecorderWidget.class.getSimpleName()
								+ " Widget...");

						serviceUpdateView(context, appWidgetId);

					}

					RemoteViews remoteViews1 = new RemoteViews(
							context.getPackageName(), R.layout.widget_layout);
					if (isEnable) {

						remoteViews1.setImageViewResource(R.id.imageButtonon,
								R.drawable.on);
					} else {

						remoteViews1.setImageViewResource(R.id.imageButtonon,
								R.drawable.off);
					}

					if (recOn) {
						remoteViews1.setImageViewResource(
								R.id.imageButtonstoprec, R.drawable.avstop);
					} else {
						remoteViews1.setImageViewResource(
								R.id.imageButtonstoprec, R.drawable.avplay);
					}

					if (speakerOn) {

						remoteViews1.setImageViewResource(R.id.imageSpeaker,
								R.drawable.avspeekear);
					} else {

						remoteViews1.setImageViewResource(R.id.imageSpeaker,
								R.drawable.avspeekearoff);
					}

					ComponentName cn = new ComponentName(context,
							ProofRecorderWidget.class);
					AppWidgetManager.getInstance(context).updateAppWidget(cn,
							remoteViews1);

				} else {
					Log.d(TAG,
							"appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID:"
									+ appWidgetId + "--"
									+ AppWidgetManager.INVALID_APPWIDGET_ID
									+ "--" + action.toString());
				}
			}
		} catch (java.lang.NullPointerException e) {
			Log.d(TAG, "appWidgetId is **null**, doing nothing...");
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		try {
			for (int e : appWidgetIds) {
				setAlarm(context, true, e,
						AppWidgetManager.ACTION_APPWIDGET_UPDATE);
				Log.d(TAG, "onDeleted still remain in appWidgetIds: " + e);

			}
			mSharedPreferences = null;
			mEditor = null;
			mRecorderDetector.getInstance().deleteObserver(
					ProofRecorderWidget.this);
			Log.d(TAG, "onDeleted appWidgetIds  OK");
		} catch (Exception e) {

			Log.e(TAG, "onDeleted error - " + e.getMessage());
		}
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onDisabled(android.content.Context)
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		if (mRecorderDetector != null) {
			mRecorderDetector.getInstance().deleteObserver(
					ProofRecorderWidget.this);
			Log.d(TAG, "mRecorderDetector != null");
		}

	}

	/**
	 * 
	 */
	private void mAssertmShareNotNull() {

		assert (mSharedPreferences != null);

	}

	/**
	 * 
	 * @param context
	 */
	private void initMshPref(Context context) {
		try {
			if (mSharedPreferences == null) {
				mSharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				boolean incall, outcall, sp;
				incall = mSharedPreferences.getBoolean("INCALL", true);
				outcall = mSharedPreferences.getBoolean("OUTCALL", true);
				if (preferences == null) {
					preferences = context.getSharedPreferences("RECoN", 0);
				}
				isrecording = preferences.getBoolean("isrecording", false);
				if (isrecording == true) {
					recOn = true;
				}
				if (isrecording == false) {
					recOn = false;
				}

				sp = mSharedPreferences.getBoolean("SPEAK", true);
				if (incall || outcall) {

					isEnable = true;
				} else {
					isEnable = false;
				}
				if (sp) {
					speakerOn = true;
				} else {
					speakerOn = false;
				}
				Log.d(TAG, "isrecording : " + isrecording + " recOn : " + recOn
						+ " InitMshPref OK, isEnable = " + isEnable);

			}

		} catch (Exception e) {

			Log.e(TAG, "initMshPref failed:" + e.getMessage());
		}

	}

	private void initmEditor(Context context) {
		if (mSharedPreferences != null) {
			mEditor = mSharedPreferences.edit();
			mRecEditor = preferences.edit();
		} else {
			initMshPref(context);
			mEditor = mSharedPreferences.edit();
			mRecEditor = preferences.edit();
		}

	}

	@SuppressLint("NewApi")
	private void mCommit() {
		try {
			mEditor.apply();

		} catch (Exception e) {

			try {
				mEditor.commit();

			} catch (Exception e1) {

				Log.e(TAG, "mEditor.commit() failed:" + e.getMessage());

			}
			Log.e(TAG, "mEditor.apply() failed:" + e.getMessage());
		}

	}

	/**
	 * 
	 * @param aContext
	 * @param mCancel
	 * @param appWidgetId
	 * @param mAppWidgetId
	 * @param mCommand
	 * @param mRemoteViews
	 */
	private static void setAlarm(Context aContext, boolean mCancel,
			int appWidgetId, String mCommand) {
		PendingIntent refreshTestIntent = getControlIntent(aContext, mCommand,
				appWidgetId);
		ProofRecorderWidget.resetAppWId();
		ProofRecorderWidget.setmAppWId(appWidgetId);
		AlarmManager alarms = (AlarmManager) aContext
				.getSystemService(Context.ALARM_SERVICE);
		if (mCancel) {
			Log.d(TAG, "Disable Alarm");
			alarms.cancel(refreshTestIntent);
		} else {
			Log.d(TAG, "Setting Alarm for " + mRefreshInterval() / 1000
					+ " seconds");
			alarms.cancel(refreshTestIntent);
			alarms.setRepeating(AlarmManager.ELAPSED_REALTIME,
					SystemClock.elapsedRealtime(), mRefreshInterval(),
					refreshTestIntent);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object data) {
		RecorderDetector mRecorderDetector = (RecorderDetector) data;
		boolean recOn = mRecorderDetector.isRecOn();
		if (recOn) {
			recOn = false;
		} else {
			recOn = true;
		}
		Log.d(TAG, "Record position change to :" + recOn);

	}

}
