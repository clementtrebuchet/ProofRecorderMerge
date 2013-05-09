package org.proof.recorder.preferences;

import org.proof.recorder.R;
import org.proof.recorder.utils.QuickActionDlg;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Preference extends SherlockPreferenceActivity {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			QuickActionDlg.setmContext(this);			
			addPreferencesFromResource(R.xml.preference);			
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
