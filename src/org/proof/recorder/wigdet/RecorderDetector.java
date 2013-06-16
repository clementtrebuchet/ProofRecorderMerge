package org.proof.recorder.wigdet;

import java.util.Observable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class RecorderDetector extends Observable{
	
	private Context mContext;
	/**
	 * @return the mContext
	 */
	public Context getmContext() {
		return mContext;
	}

	private SharedPreferences preferences;
	private boolean recOn;
	/**
	 * @return the recOn
	 */
	public boolean isRecOn() {
		return recOn;
	}

	private Editor mEditor;
	
	public RecorderDetector(Context c){
		this.mContext = c;
		this._initPreferences();
		
		
	}
	
	public void ChangeRecPosition(boolean on){
		recOn = false;
		mEditor = preferences.edit();
		
		if (on == true) {
			recOn = true;
			mEditor.putBoolean("isrecording", true).commit();
			
		}
		if (on == false) {
			recOn = false;
			mEditor.putBoolean("isrecording", false).commit();
		}
		setChanged();
		notifyObservers(this);
		
		
	}
	
	private SharedPreferences _initPreferences(){
		if (preferences == null) {
			preferences = this.mContext.getSharedPreferences("RECoN", 0);
		}
		return preferences;
		
	}

	/* (non-Javadoc)
	 * @see java.util.Observable#hasChanged()
	 */
	@Override
	public boolean hasChanged() {
		// TODO Auto-generated method stub
		return super.hasChanged();
	}

	/* (non-Javadoc)
	 * @see java.util.Observable#notifyObservers(java.lang.Object)
	 */
	@Override
	public void notifyObservers(Object data) {
		// TODO Auto-generated method stub
		super.notifyObservers(data);
	}

	/* (non-Javadoc)
	 * @see java.util.Observable#setChanged()
	 */
	@Override
	protected void setChanged() {
		// TODO Auto-generated method stub
		super.setChanged();
	}

}
