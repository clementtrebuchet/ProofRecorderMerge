package org.proof.recorder.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.activity.ProofAsyncPreferenceActivity;
import org.proof.recorder.database.collections.RecordsList;
import org.proof.recorder.utils.Log.Console;

public class Preference extends ProofAsyncPreferenceActivity {

	private CheckBoxPreference INCALL;
	private CheckBoxPreference OUTCALL;
	
	private ListPreference autoCleanPref;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preference);
		
		getPreferences();	
		setCallBacks();		
		
		fillAutoClean();
	}
	
	@SuppressWarnings("deprecation")
	private void getPreferences() {
		INCALL = (CheckBoxPreference) getPreferenceScreen().findPreference("INCALL");
		OUTCALL = (CheckBoxPreference) getPreferenceScreen().findPreference("OUTCALL");
		autoCleanPref = (ListPreference) getPreferenceScreen().findPreference("auto_clean");
	}
	
	private void fillAutoClean() {
		autoCleanPref.setEntries(R.array.auto_clean_options);
		autoCleanPref.setEntryValues(new CharSequence[] {
			"NEVER",
			"2DAYS",
			"1WEEK",
			"1MONTH",
			"6MONTHS"
		});
	}
	
	private void setCallBacks() {
		INCALL.setOnPreferenceClickListener(micClick);
		OUTCALL.setOnPreferenceClickListener(micClick);	
		autoCleanPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(
					android.preference.Preference preference, Object newValue) {
				
				String selected = (String) newValue;
				
				if(!selected.equals("NEVER")) {
					AlertDialog.Builder alertDlg = new AlertDialog.Builder(getInternalContext());					
					alertDlg.setTitle(getInternalContext().getString(R.string.apply_changes_now_title));
					alertDlg.setMessage(getInternalContext().getString(R.string.apply_changes_now_msg));
					alertDlg.setPositiveButton(getInternalContext().getString(R.string.yes_btn), new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// TODO Apply immediately
							
						}
					});	
					
					alertDlg.setNegativeButton(getInternalContext().getString(R.string.no_btn), null);	

					alertDlg.show();
				}
					
				return true;
			}
		});
	}

	private final OnPreferenceClickListener micClick = new OnPreferenceClickListener() {

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

	@Override
	protected void _onPreExecute() {
		// TODO Auto-generated method stub	
	}

	@Override
	protected void _onProgressUpdate(Integer... progress) {
		// TODO Auto-generated method stub		
	}

	@Override
	protected void _onPostExecute(Long result) {
		// TODO Auto-generated method stub		
	}

	@Override
	protected void _doInBackground(Void... params) {
		
		String periodic = Settings.getPersistantData("auto_clean");
		
		RecordsList list = new RecordsList();		
		list.autoClean(periodic);

	}
		
}
