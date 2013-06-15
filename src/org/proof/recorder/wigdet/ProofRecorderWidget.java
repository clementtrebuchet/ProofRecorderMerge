package org.proof.recorder.wigdet;

import org.proof.recorder.ProofRecorderActivity;
import org.proof.recorder.R;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class ProofRecorderWidget extends AppWidgetProvider {
	private final static String TAG = ProofRecorderWidget.class.getName();
	public final String ACTION_ENABLE_SERVICE = "org.proof.recorder.wigdet.ProofRecorderWidget.ACTION_ENABLE_SERVICE";
	public final String ACTION_DISABLE_SERVICE = "org.proof.recorder.wigdet.ProofRecorderWidget.ACTION_DISABLE_SERVICE";
	public final String SET_FORMAT = "org.proof.recorder.wigdet.ProofRecorderWidget.SET_FORMAT";
	private static final String START_ACTION = "android.intent.action.START_AUDIO_RECORDER";
	private static final String STOP_ACTION = "android.intent.action.STOP_AUDIO_RECORDER";
	public final String REC = "org.proof.recorder.wigdet.ProofRecorderWidget.REC";
	public final String SP = "org.proof.recorder.wigdet.ProofRecorderWidget.SPEAKER";
	private SharedPreferences mSharedPreferences = null;
	private Editor mEditor = null;
	public final static String ACTION_UPDATE = "org.proof.recorder.wigdet.ProofRecorderWidget.ACTION_UPDATE_SERVICE";
	public boolean isEnable;
	public boolean recOn;
	public boolean isrecording;
	private SharedPreferences preferences;
	private Editor mRecEditor;
	private boolean speakerOn;
	private static Long defaultTimer = (long) (10); 
	private static long mRefreshInterval(){
		
		return defaultTimer*60*1000;
	}

	/**
	 * 
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		for (int i = 0; i < appWidgetIds.length; i++) {

			buildUpdate(context, appWidgetManager, appWidgetIds[i]);

		}
		Log.d(TAG, "onUpdate OK");
		
		// super.onUpdate(context, appWidgetManager, appWidgetIds);

	}

	/**
	 * 
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetIds
	 */
	public void buildUpdate(Context context, AppWidgetManager appWidgetManager,
			int appWidgetIds) {

		// initMshPref(context);
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		
		
		/*Intent active = new Intent(context, ProofRecorderWidget.class);
		active.setAction(ACTION_ENABLE_SERVICE);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context,
				appWidgetIds, active, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonon,
				actionPendingIntent);*/
		PendingIntent mActionEnableService = getControlIntent(context,
				appWidgetIds, ACTION_ENABLE_SERVICE, remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonon,
				mActionEnableService);
		
		
		/*active = new Intent(context, ProofRecorderWidget.class);
		active.setAction(ACTION_UPDATE);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				appWidgetIds, active, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonrefresh,
				pendingIntent);*/
		PendingIntent mActionUpdate = getControlIntent(context,
				appWidgetIds, ACTION_UPDATE, remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonrefresh,
				mActionUpdate);
		
		/*active = new Intent(context, ProofRecorderWidget.class);
		active.setAction(SET_FORMAT);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		mActionEnableService = PendingIntent.getBroadcast(context, appWidgetIds,
				active, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonbox,
				mActionEnableService);*/
		PendingIntent mActionSetFormat = getControlIntent(context,
				appWidgetIds, SET_FORMAT, remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonbox,
				mActionSetFormat);

		/*active = new Intent(context, ProofRecorderWidget.class);
		active.setAction(REC);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		mActionEnableService = PendingIntent.getBroadcast(context, appWidgetIds,
				active, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonstoprec,
				mActionEnableService);*/
		PendingIntent mRec = getControlIntent(context,
				appWidgetIds, REC, remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.imageButtonstoprec,
				mRec);
		
		/*active = new Intent(context, ProofRecorderWidget.class);
		active.setAction(SP);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		mActionEnableService = PendingIntent.getBroadcast(context, appWidgetIds,
				active, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageSpeaker,
				mActionEnableService);*/
		PendingIntent mSp = getControlIntent(context,
				appWidgetIds, SP, remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.imageSpeaker,
				mSp);
		//active.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		//active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
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
    public static PendingIntent getControlIntent( Context mContext, int mAppWidgetId, String mCommand, RemoteViews mRemoteViews) 
	{
        Intent commandIntent = new Intent( mContext, ProofRecorderWidget.class );
        commandIntent.setAction( mCommand );
        commandIntent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId );
        PendingIntent pendingIntent  = PendingIntent.getBroadcast(mContext,mAppWidgetId, commandIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    /**
     * 
     * @param aContext
     * @param aCommand
     * @return
     */
    public static PendingIntent getControlIntent( Context aContext, String aCommand ) 
	{
        Intent commandIntent = new Intent( aContext,  ProofRecorderWidget.class );
        commandIntent.setAction( aCommand );
        PendingIntent pendingIntent = PendingIntent.getService( aContext, 0, commandIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    /*
     * 
     * (non-Javadoc)
     * @see android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
     */
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		setAlarm(context, false, ACTION_UPDATE);

	}

	/**
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {

			int appWidgetId = intent.getExtras().getInt(

					AppWidgetManager.EXTRA_APPWIDGET_ID,
							AppWidgetManager.INVALID_APPWIDGET_ID);

			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {

				this.onDeleted(context, new int[] { appWidgetId });
			}
		} else {
			

			// If they gave us an intent without a valid widget Id, just bail.
		
				super.onReceive(context, intent);
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
						mRecEditor.putBoolean("isrecording", false).commit();
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

				} else if (action.equals(ACTION_UPDATE)){
					
					Intent I = new Intent(context, ProofRecorderActivity.class);
					I.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(I);
				
				} else if (action.equals(SP)){
					if(speakerOn){
						mEditor.putBoolean("SPEAK", false);
						speakerOn = false;
						
					} else {
						mEditor.putBoolean("SPEAK", true);
						speakerOn = true;
					}
					mCommit();
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
					remoteViews1.setImageViewResource(R.id.imageButtonstoprec,
							R.drawable.avstop);
				} else {
					remoteViews1.setImageViewResource(R.id.imageButtonstoprec,
							R.drawable.avplay);
				}
				
				if(speakerOn){
					
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

			
		}

	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		try {
			mSharedPreferences = null;
			mEditor = null;
			Log.d(TAG, "onDeleted appWidgetIds is null OK");
		} catch (Exception e) {

			Log.e(TAG, "onDeleted error - " + e.getMessage());
		}
	}
	/*
	 * 
	 * (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onDisabled(android.content.Context)
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		setAlarm(context, true, ACTION_UPDATE);
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
				if(isrecording == true) {recOn = true;}
				if(isrecording == false) {recOn = false;}
				Log.d(TAG, "isrecording :" + isrecording + "recOn :"+recOn);
				
				sp = mSharedPreferences.getBoolean("SPEAK", true);
				if (incall || outcall) {

					isEnable = true;
				} else {
					isEnable = false;
				}
				if (sp){
					speakerOn = true;
				} else {
					speakerOn = false;
				}
				Log.d(TAG, "InitMshPref OK, isEnable = " + isEnable);

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
	 * @param mAppWidgetId
	 * @param mCommand
	 * @param mRemoteViews
	 */
	private static void setAlarm(Context aContext, boolean mCancel,String mCommand) {
		PendingIntent refreshTestIntent = getControlIntent(aContext,mCommand);
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
}
