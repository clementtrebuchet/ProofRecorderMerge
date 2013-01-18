package org.proof.recorder.preferences;

import org.proof.recorder.R;
import org.proof.recorder.broadcastr.phone.AlarmSchd;
import org.proof.recorder.utils.QuickActionDlg;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class ServerAccount extends SherlockPreferenceActivity{
	private SharedPreferences sh ;
	protected static final String TAG = "ServerAccount";
	private TimePickerPreference DP; 
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.accounting);
		DP	= (TimePickerPreference) getPreferenceScreen().findPreference("timePreference");
		
		DP.setOnPreferenceClickListener(clickpref);
		sh =  PreferenceManager.getDefaultSharedPreferences(this);
		Log.e(TAG, "VALUE DAY : "+ sh.getString("DayRep", "pas de pref"));
		Log.e(TAG, "VALUE TIME : "+ sh.getString("timePreference", "pas de pref"));
	}
	
	private OnPreferenceClickListener clickpref = new OnPreferenceClickListener(){
		
		@Override
		public boolean onPreferenceClick(Preference paramPreference) {
			Log.e(TAG, "VALUE "+ sh.getString(paramPreference.getKey().toString(), "pas de pref"));
			
			return false;
		}

		
	};
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		return QuickActionDlg.mainUiMenuHandler(menu);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		return QuickActionDlg.mainActionsMenuHandler(item);
	}

	@Override
	public void onDestroy(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AlarmSchd.HandleAlarmStateChange(ServerAccount.this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		super.onDestroy();
	}


	 
}