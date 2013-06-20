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
import android.widget.Toast;

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
	private static Long defaultTimerInMinutes = (long) (3);
	public static RecorderDetector mRecorderDetector = null;
	private static int mAppWId = 0;
	public static boolean mForbbidenChFormat;
	public static ProofRecorderWidget mProofRecorderWidget = null;

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
	private static void setmAppWId(int mAppWId) {
		ProofRecorderWidget.mAppWId = mAppWId;
		Log.d(TAG, "setmAppWId(int mAppWId) : " + ProofRecorderWidget.mAppWId);
	}

	/**
	 * 
	 */
	private static void resetAppWId() {

		ProofRecorderWidget.mAppWId = 0;
		Log.d(TAG, "resetAppWId() : " + ProofRecorderWidget.mAppWId);
	}

	/**
	 * 
	 * @return
	 */
	private static long mRefreshInterval() {

		return defaultTimerInMinutes * 60 * 1000;
	}

	/**
	 * 
	 */
	public ProofRecorderWidget() {

	}
	/**
	 * 
	 * @return
	 */
	public static boolean testIfObserversAdded() {
		boolean result = false;
		try {
			if (mRecorderDetector != null) {
				if (mRecorderDetector.countObservers() != 0) {
					result = true;
				} else {
					mRecorderDetector.addObserver(mProofRecorderWidget);
					Log.d(TAG, "Added a mProofRecorderWidget  as Observers");
					result = true;
				}

			}

		} catch (Exception e) {
			Log.e(TAG, "" + e.getMessage());
			result = false;

		} finally {
			Log.d(TAG, "From ProofRecorderWidget  count Observers return : "
					+ mRecorderDetector.countObservers());

		}
		return result;

	}

	/**
	 * 
	 * @param mContext
	 */
	private void testIfObservers(Context mContext) {
		try {
			assert (mContext != null);
			if (mRecorderDetector != null) {
				Log.d(TAG, "mRecorderDetector.countObservers() = "
						+ mRecorderDetector.countObservers());
				if (mRecorderDetector.countObservers() == 0) {
					mRecorderDetector.addObserver(ProofRecorderWidget.this);
				}
			}
			if (mRecorderDetector == null) {
				mRecorderDetector = RecorderDetector.getInstance(mContext);
				mRecorderDetector.addObserver(ProofRecorderWidget.this);
				Log.d(TAG, "mRecorderDetector.addObserver(this) "
						+ mRecorderDetector.countObservers());
				Log.d(TAG, "mRecorderDetector.countObservers() = "
						+ mRecorderDetector.countObservers());

			}
		} catch (Exception e) {
			Log.d(TAG,
					"testIfObservers(Context mContext) error:" + e.getMessage());
		}
	}

	/**
	 * onDeleteObservers()
	 */
	private void onDeleteObservers() {
		if (mRecorderDetector != null) {
			mRecorderDetector.deleteObserver(ProofRecorderWidget.this);
			Log.d(TAG, "mRecorderDetector != null");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onUpdate(android.content.Context,
	 * android.appwidget.AppWidgetManager, int[])
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		testIfObservers(context);// reconnect or connect the observers
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

	/**
	 * 
	 * @param mContext
	 * @param mAppWidgetId
	 */
	private void serviceUpdateView(Context mContext, int mAppWidgetId) {
		Intent iService = new Intent(mContext, MBuildUpdate.class);
		Bundle bService = new Bundle();
		bService.putInt("appWidgetIds", mAppWidgetId);
		iService.putExtras(bService);
		mContext.startService(iService);
	}

	/**
	 * 
	 * @author clement service class@MBuildUpdate
	 */
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.app.Service#onConfigurationChanged(android.content.res.
		 * Configuration)
		 */
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

		/**
		 * 
		 * @param remoteView
		 */
		private void pushUpdate(RemoteViews remoteView) {
			ComponentName myWidget = new ComponentName(this,
					ProofRecorderWidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(myWidget, remoteView);
			Log.d(TAG, "pushUpdate(RemoteViews remoteView)");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.app.Service#onBind(android.content.Intent)
		 */
		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
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
		testIfObservers(context);// reconnect or connect the observers

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
						Log.d(TAG,
								"action.equals(SET_FORMAT) mForbbidenChFormat ? :"
										+ mForbbidenChFormat);
						if (mForbbidenChFormat == false) {
							Intent mActivity = new Intent(context,
									WidgetPreferenceFormat.class);
							mActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(mActivity);
							Log.d(TAG, "Start format dialog mActivity");
						} else {
							Toast.makeText(context,
									context.getString(R.string.FORRBIDEN),
									Toast.LENGTH_LONG).show();
						}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onDeleted(android.content.Context,
	 * int[])
	 */
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
			onDeleteObservers();
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
		onDeleteObservers();
		mProofRecorderWidget = null;

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

	/**
	 * 
	 * @param context
	 */
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

	/*
	 * 
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object data) {
		if (mProofRecorderWidget == null) {
			mProofRecorderWidget = this;
			Log.d(TAG, "mProofRecorderWidget was null");
		} else {
			Log.d(TAG, "mProofRecorderWidget is not null");
		}
		RecorderDetector mRecorderDetector = (RecorderDetector) data;
		mForbbidenChFormat = mRecorderDetector.isRecOn();
		Log.d(TAG, "****Observer Widget Event isRecOn ? " + mForbbidenChFormat
				+ " ****");
		Intent I = new Intent(mRecorderDetector.getmContext(),
				ProofRecorderWidget.class);
		I.setAction("org.proof.recorder.wigdet.ProofRecorderWidget.UPDATE");
		I.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				ProofRecorderWidget.getmAppWId());
		mRecorderDetector.getmContext().sendBroadcast(I);
		Log.d(TAG, "this.mContext.sendBroadcast(I) action : " + I.getAction());

	}

}
