package org.proof.recorder.wigdet;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class RecorderDetector {

	private static final String TAG = RecorderDetector.class.getName();
	private static RecorderDetector mInstance = null;
	private Context mContext;
	private SharedPreferences preferences;
	private boolean recOn;

	/**
	 * 
	 */
	private RecorderDetector() {

	}

	/**
	 * @@Singleton
	 * @param c
	 * @return
	 */
	public static RecorderDetector getInstance(Context c) {
		if (mInstance == null) {
			mInstance = new RecorderDetector(c);
			Log.d(TAG, "getInstance mInstance == null creating instance: "
					+ mInstance.toString());
		} else {
			Log.d(TAG, "getInstance mInstance != null instance is: "
					+ mInstance.toString());
		}
		return mInstance;
	}

	/**
	 * 
	 * @param c
	 */
	private RecorderDetector(Context c) {
		this.mContext = c;
		this._initPreferences();

	}

	/**
	 * @return the mContext
	 */
	private Context getmContext() {
		return mContext;
	}

	/**
	 * @return the recOn
	 */
	public boolean isRecOn() {
		return recOn;
	}

	/**
	 * 
	 * @param on
	 */
	public void ChangeRecPosition(boolean on) {
		recOn = false;
		Editor mEditor = preferences.edit();

		if (on) {
			recOn = true;
			mEditor.putBoolean("isrecording", true).apply();
			Log.d(TAG, "Recording");
		}
		if (!on) {
			recOn = false;
			mEditor.putBoolean("isrecording", false).apply();
			Log.d(TAG, "No recording");
		}
		
		/**
		 * make sure intent was deliver
		 * */
		Intent I = new Intent(getmContext(),
				ProofRecorderWidget.class);
		I.setAction("org.proof.recorder.wigdet.ProofRecorderWidget.UPDATE");
		I.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				ProofRecorderWidget.getmAppWId());
		getmContext().sendBroadcast(I);
		Log.d(TAG, "this.mContext.sendBroadcast(I) action : " + I.getAction());
		
		

	}

	/**
	 * 
	 * @return
	 */
	private void _initPreferences() {
		if (preferences == null) {
			preferences = this.mContext.getSharedPreferences("RECoN", 0);
		}

	}
}
