package org.proof.recorder.graphics;

import org.proof.recorder.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author clement
 *
 */
public class ManipulateUi {
	
	private static final String TAG = "ManipulateUi";
	public CheckBoxPreference CB;
	public Context mContext;
	SharedPreferences preferences;
	Editor prefEdit;
	/**
	 * @category Valoriser les préférences, manipuler l'UI
	 */
	public ManipulateUi(){
		
		
	}
	
	/**
	 * @param chk {array}
	 * @category Passe un tableau de chk et les désactives tous
	 */
	public void disableArrayChk(CheckBoxPreference[] chk){
		int i = 0;
		for (i=0; i < chk.length; i++){
			CB = chk[i];
			CB.setEnabled(false);
			if(Settings.isDebug())
			Log.e(TAG, "désactives : "+CB);
		}
		
	}
	/**
	 * @param chk {array}
	 * @category Passe un tableau de chk et les actives tous
	 */
	public void enabledArrayChk(CheckBoxPreference[] chk){
		int i = 0;
		for (i=0; i < chk.length; i++){
			CB = chk[i];
			CB.setEnabled(true);
			if(Settings.isDebug())
			Log.e(TAG, "actives : "+CB);
		}
		
	}


	/**
	 * @param chk {array}
	 * @category Passe un tableau de chk et les décoches tous
	 */
	public void noCheckedArrayChk(CheckBoxPreference[] chk){
		int i = 0;
		for (i=0; i < chk.length; i++){
			CB = chk[i];
			CB.setChecked(false);
			if(Settings.isDebug())
			Log.e(TAG, "decoches : "+CB);
		}
		
	}
	
	/**
	 * @param chk {array}
	 * @category Passe un tableau de chk et les coches tous
	 */
	public void checkedArrayChk(CheckBoxPreference[] chk){
		int i = 0;
		for (i=0; i < chk.length; i++){
			CB = chk[i];
			CB.setChecked(true);
			if(Settings.isDebug())
			Log.e(TAG, "coches : "+CB);
		}
		
	}

	
	/**
	 * @param chk
	 * @param mCont
	 * @param position (true or false)
	 * @category valoriser un tableau (String optionsName true OR false)
	 */
	public void setArrayBoolean(String[] chk ,Context mCont, boolean position){
		prefEdit = PreferenceManager.getDefaultSharedPreferences(mCont).edit();
		int i = 0;
		for (i=0; i < chk.length; i++){
			
			prefEdit.putBoolean(chk[i], position);
			if(Settings.isDebug())
			Log.e(TAG, "préférence : "+chk[i]+" mise a : "+position);
		
		}
		prefEdit.commit();
	}
	
}
