package org.proof.recorder.wigdet;

import java.util.Observable;
import java.util.Observer;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class RecorderDetector extends Observable {

	private static final String TAG = RecorderDetector.class.getName();
	private Context mContext;
	private Editor mEditor;
	private SharedPreferences preferences;
	private boolean recOn;

	public RecorderDetector() {

	}

	public RecorderDetector(Context c) {
		this.mContext = c;
		this._initPreferences();

	}

	/**
	 * @return the mContext
	 */
	public Context getmContext() {
		return mContext;
	}

	/**
	 * @return the recOn
	 */
	public boolean isRecOn() {
		return recOn;
	}

	public RecorderDetector getInstance() {
		return this;
	}

	public void ChangeRecPosition(boolean on) {
		recOn = false;
		mEditor = preferences.edit();

		if (on == true) {
			recOn = true;
			mEditor.putBoolean("isrecording", true).commit();
			Log.d(TAG, "Recording");
		}
		if (on == false) {
			recOn = false;
			mEditor.putBoolean("isrecording", false).commit();
			Log.d(TAG, "No recording");
		}
		this.setChanged();
		this.notifyObservers(this);
		Intent I = new Intent(this.mContext, ProofRecorderWidget.class);
		I.setAction("org.proof.recorder.wigdet.ProofRecorderWidget.UPDATE");
		I.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ProofRecorderWidget.getmAppWId());
		this.mContext.sendBroadcast(I);
		Log.d(TAG, "this.mContext.sendBroadcast(I) action : " + I.getAction());

	}

	private SharedPreferences _initPreferences() {
		if (preferences == null) {
			preferences = this.mContext.getSharedPreferences("RECoN", 0);
		}
		return preferences;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observable#notifyObservers(java.lang.Object)
	 * 
	 * @Override public void notifyObservers(Object data) { // TODO
	 * Auto-generated method stub super.notifyObservers(data); Intent I = new
	 * Intent(this.mContext, ProofRecorderWidget.class);
	 * I.setAction("org.proof.recorder.wigdet.ProofRecorderWidget.REC");
	 * I.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 * this.mContext.sendBroadcast(I); Log.d(TAG,
	 * "this.mContext.sendBroadcast(I) action : "+I.getAction()); }
	 */

}
