package org.proof.recorder.preferences;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.utils.QuickActionDlg;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Preference extends SherlockPreferenceActivity {
	//private static final String TAG = "Preference";

	
		private static final String TAG = null;
		private CheckBoxPreference INCALL;
		private CheckBoxPreference OUTCALL;
		
		@SuppressWarnings("deprecation")
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			QuickActionDlg.setmContext(this);
			
			addPreferencesFromResource(R.xml.preference);	
			INCALL = (CheckBoxPreference) getPreferenceScreen().findPreference("INCALL");
			OUTCALL = (CheckBoxPreference) getPreferenceScreen().findPreference("OUTCALL");
			INCALL.setOnPreferenceClickListener(micClick);
			OUTCALL.setOnPreferenceClickListener(micClick);
			
		}
		private OnPreferenceClickListener micClick = new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(
					android.preference.Preference paramPreference) {
				listenManipulate();
				return paramPreference.isEnabled();
			}
			
		};
		
		private void listenManipulate(){
			if(OUTCALL.isChecked() || INCALL.isChecked()){
				if(!INCALL.isChecked()){
					INCALL.setChecked(true);
					if(Settings.isDebug())Log.e(TAG, "INCALL set to : "+INCALL.isChecked());
				}
			}
		}
		@Override
	    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {		
			return QuickActionDlg.mainUiMenuHandler(menu);
	    }
		
		@Override
		public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		    return QuickActionDlg.mainActionsMenuHandler(item);
		}
		
}
