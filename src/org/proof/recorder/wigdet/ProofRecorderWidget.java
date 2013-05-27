package org.proof.recorder.wigdet;

import org.proof.recorder.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class ProofRecorderWidget extends AppWidgetProvider {
	private static final String TAG = ProofRecorderWidget.class.getName();
	public static final String ACTION_ENABLE_SERVICE = "org.proof.recorder.wigdet.ACTION_ENABLE_SERVICE";
	public static final String ACTION_DISABLE_SERVICE = "org.proof.recorder.wigdet.ACTION_DISABLE_SERVICE";
	public static final String SET_MP3 = "org.proof.recorder.wigdet.SET_MP3";
	public static final String SET_OGG = "org.proof.recorder.wigdet.SET_OGG";
	public static final String SET_3GP = "org.proof.recorder.wigdet.SET_3GP";
	public static final String SET_WAV = "org.proof.recorder.wigdet.SET_WAV";
	private SharedPreferences mSharedPreferences = null;
	private Editor mEditor = null;
	public static final String ACTION_UPDATE = "org.proof.recorder.wigdet.ACTION_DISABLE_SERVICE";
	public static boolean isEnable;

	/**
	 * 
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int i = 0; i < appWidgetIds.length; i++) {
			buildUpdate(context, appWidgetManager, appWidgetIds[i]);

		}
		Log.d(TAG, "onUpdate OK");

	}

	/**
	 * 
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetIds
	 */
	public void buildUpdate(Context context, AppWidgetManager appWidgetManager,
			int appWidgetIds) {
		initMshPref(context);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);

		Intent active = new Intent(context, ProofRecorderWidget.class);
		active.setAction(ACTION_ENABLE_SERVICE);
		PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context,
				0, active, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageButton5,
				actionPendingIntent);

		active.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				active, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.imageButton1, pendingIntent);

		active = new Intent(context, ProofRecorderWidget.class);
		active.setAction(SET_MP3);
		actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageButton1,
				actionPendingIntent);

		active = new Intent(context, ProofRecorderWidget.class);
		active.setAction(SET_OGG);
		actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageButton2,
				actionPendingIntent);

		active = new Intent(context, ProofRecorderWidget.class);
		active.setAction(SET_3GP);
		actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageButton3,
				actionPendingIntent);

		active = new Intent(context, ProofRecorderWidget.class);
		active.setAction(SET_WAV);
		actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageButton4,
				actionPendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

	}

	/**
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		initMshPref(context);
		final String action = intent.getAction();
		mAssertmShareNotNull();
		initmEditor();
		/*
		 * check action @ToDo
		 */
		if (action.equals(ACTION_ENABLE_SERVICE)) {
			if (isEnable) {
				mEditor.putBoolean("INCALL", false);
				mEditor.putBoolean("OUTCALL", false);
				isEnable = false;
			} else {
				mEditor.putBoolean("INCALL", true);
				mEditor.putBoolean("OUTCALL", true);
				isEnable = true;
			}

			mCommit();

			Log.d(TAG, "ACTION_ENABLE_SERVICE OK");

		} else if (action.equals(ACTION_DISABLE_SERVICE)) {

			Log.d(TAG, "ACTION_DISABLE_SERVICE OK");

		} else if (action.equals(SET_MP3)) {

			mEditor.putString("audio_format", "MP3");
			mCommit();
			Log.d(TAG, "SET_MP3 OK");

		} else if (action.equals(SET_OGG)) {
			mEditor.putString("audio_format", "OGG");
			mCommit();
			Log.d(TAG, "SET_OGG OK");

		} else if (action.equals(SET_3GP)) {
			mEditor.putString("audio_format", "3GP");
			mCommit();
			Log.d(TAG, "SET_3GP OK");

		} else if (action.equals(SET_WAV)) {
			mEditor.putString("audio_format", "WAV");
			mCommit();
			Log.d(TAG, "SET_WAV OK");

		}

		RemoteViews remoteViews1 = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		if (isEnable) {
			
			remoteViews1.setImageViewResource(R.id.imageButton5,
					R.drawable.settings_w);
		} else {
			
			remoteViews1.setImageViewResource(R.id.imageButton5,
					R.drawable.settings_wi);
		}

		String format = mSharedPreferences.getString("audio_format", "3GP");
		if (format.equalsIgnoreCase("mp3")) {
			remoteViews1.setImageViewResource(R.id.imageButton1,
					R.drawable.plug_mp3_w);
			remoteViews1.setImageViewResource(R.id.imageButton2,
					R.drawable.plug_ogg_wi);
			remoteViews1.setImageViewResource(R.id.imageButton3,
					R.drawable.plug_3gp_wi);
			remoteViews1.setImageViewResource(R.id.imageButton4,
					R.drawable.plug_wav_wi);
		} else if (format.equalsIgnoreCase("ogg")) {
			remoteViews1.setImageViewResource(R.id.imageButton1,
					R.drawable.plug_mp3_wi);
			remoteViews1.setImageViewResource(R.id.imageButton2,
					R.drawable.plug_ogg_w);
			remoteViews1.setImageViewResource(R.id.imageButton3,
					R.drawable.plug_3gp_wi);
			remoteViews1.setImageViewResource(R.id.imageButton4,
					R.drawable.plug_wav_wi);

		} else if (format.equalsIgnoreCase("3gp")) {
			remoteViews1.setImageViewResource(R.id.imageButton1,
					R.drawable.plug_mp3_wi);
			remoteViews1.setImageViewResource(R.id.imageButton2,
					R.drawable.plug_ogg_wi);
			remoteViews1.setImageViewResource(R.id.imageButton3,
					R.drawable.plug_3gp_w);
			remoteViews1.setImageViewResource(R.id.imageButton4,
					R.drawable.plug_wav_wi);

		} else if (format.equalsIgnoreCase("wav")) {
			remoteViews1.setImageViewResource(R.id.imageButton1,
					R.drawable.plug_mp3_wi);
			remoteViews1.setImageViewResource(R.id.imageButton2,
					R.drawable.plug_ogg_wi);
			remoteViews1.setImageViewResource(R.id.imageButton3,
					R.drawable.plug_3gp_wi);
			remoteViews1.setImageViewResource(R.id.imageButton4,
					R.drawable.plug_wav_w);

		}
	
		ComponentName cn = new ComponentName(context, ProofRecorderWidget.class);
		AppWidgetManager.getInstance(context).updateAppWidget(cn, remoteViews1);

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
				boolean incall, outcall;
				incall = mSharedPreferences.getBoolean("INCALL", true);
				outcall = mSharedPreferences.getBoolean("OUTCALL", true);
				if (incall || outcall) {

					isEnable = true;
				} else {
					isEnable = false;
				}
				Log.d(TAG, "InitMshPref OK, isEnable = " + isEnable);

			}

		} catch (Exception e) {

			Log.e(TAG, "initMshPref failed:" + e.getMessage());
		}

	}

	private void initmEditor() {
		mEditor = mSharedPreferences.edit();

	}

	private void mCommit() {
		try {
			mEditor.commit();

		} catch (Exception e) {
			Log.e(TAG, "mEditor.commit() failed:" + e.getMessage());
		}

	}

}
