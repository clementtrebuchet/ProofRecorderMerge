package org.proof.recorder.preferences;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofPreferenceActivity;
import org.proof.recorder.utils.Log.Console;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference.OnPreferenceClickListener;

public class Preference extends ProofPreferenceActivity {

	private CheckBoxPreference INCALL;
	private CheckBoxPreference OUTCALL;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preference);
		
		getPreferences();	
		setCallBacks();			
	}
	
	@SuppressWarnings("deprecation")
	private void getPreferences() {
		INCALL = (CheckBoxPreference) getPreferenceScreen().findPreference("INCALL");
		OUTCALL = (CheckBoxPreference) getPreferenceScreen().findPreference("OUTCALL");
	}
	
	private void setCallBacks() {
		INCALL.setOnPreferenceClickListener(micClick);
		OUTCALL.setOnPreferenceClickListener(micClick);	
	}
	
	private OnPreferenceClickListener micClick = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(
				android.preference.Preference paramPreference) {
			ensureSelection();
			return paramPreference.isEnabled();
		}
		
	};
	
	private void ensureSelection(){
		if(OUTCALL.isChecked() || INCALL.isChecked()){
			if(!INCALL.isChecked()){
				INCALL.setChecked(true);
				Console.print_debug("INCALL set to : "+INCALL.isChecked());
			}
		}
	}
		
}
