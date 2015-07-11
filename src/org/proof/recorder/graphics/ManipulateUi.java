package org.proof.recorder.graphics;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.Log;

import org.proof.recorder.Settings;

/**
 * @author clement
 *
 */
@SuppressWarnings("unused")
public class ManipulateUi {
	
	private static final String TAG = "ManipulateUi";
	private CheckBoxPreference CB;
	public Context mContext;
	SharedPreferences preferences;

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
		int i;
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
		int i;
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
		int i;
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
		int i;
		for (i=0; i < chk.length; i++){
			CB = chk[i];
			CB.setChecked(true);
			if(Settings.isDebug())
			Log.e(TAG, "coches : "+CB);
		}

	}

	
	/**
	 * @param chk the checked item
	 * @param mCont the context
	 * @category valoriser un tableau (String optionsName true OR false)
	 */
	public void setArrayBoolean(String[] chk, Context mCont){
		Editor prefEdit = PreferenceManager.getDefaultSharedPreferences(mCont).edit();
		int i;
		for (i=0; i < chk.length; i++){
			
			prefEdit.putBoolean(chk[i], false);
			if(Settings.isDebug())
			Log.e(TAG, "préférence : "+chk[i]+" mise a : "+ false);
		
		}
		prefEdit.apply();
	}
	
}
