package org.proof.recorder.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.activity.ProofPreferenceActivity;
import org.proof.recorder.place.de.marche.Eula;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.StaticIntents;

public class FormulaPreferences extends ProofPreferenceActivity {

	private final static String TAG = "FormulaPreferences";

	private CheckBoxPreference mp3;
	private CheckBoxPreference ogg;
	//private CheckBoxPreference ftp;
	private CheckBoxPreference eula;
	private static Context mContext;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);

		mDialog.setNegativeButton("Annuler",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		addPreferencesFromResource(R.xml.formula_preference);

		mp3 = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"OFFER_MP3");
		ogg = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"OFFER_OGG");
		//ftp = (CheckBoxPreference) getPreferenceScreen().findPreference(
		//		"OFFER_FTP");
		eula = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"PREFERENCE_EULA_ACCEPTED");
		CheckBoxPreference hide = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"FIRSTINSTALL");
		hide.setEnabled(false);
		PreferenceCategory mCategory = (PreferenceCategory) findPreference("EULA");
		mCategory.removePreference(hide);
		mp3.setOnPreferenceClickListener(mFormulaClick);
		ogg.setOnPreferenceClickListener(mFormulaClick);
		//ftp.setOnPreferenceClickListener(mFormulaClick);
		eula.setOnPreferenceClickListener(mFormulaClick);
		assertOgg();
		//assertFtp();
		assertMP3();
		if (!eula.isChecked())
			eula();
		onSettings();
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		return QuickActionDlg.mainUiMenuHandler(menu);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		return QuickActionDlg.mainActionsMenuHandler(item);
	}

	@SuppressWarnings("EmptyMethod")
	private void onSettings() {

	}

	/*private void assertFtp() {
		if (Settings.assertPlugExist(2, getApplicationContext())) {
			ftp.setChecked(true);

		} else {
			ftp.setChecked(false);
			Intent intent = new Intent(this,ProofStore.class);
			ftp.setIntent(intent);
		}
	}*/

	private void assertMP3() {
		if (Settings.assertPlugExist(0)) {
			mp3.setChecked(true);

		} else {
			mp3.setChecked(false);			
			mp3.setIntent(StaticIntents.goPlugins(mContext));
		}
	}

	private void assertOgg() {
		if (Settings.assertPlugExist(1)) {
			ogg.setChecked(true);

		} else {
			ogg.setChecked(false);
			ogg.setIntent(StaticIntents.goPlugins(mContext));
			
			
		}
	}

	private final OnPreferenceClickListener mFormulaClick = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(
				android.preference.Preference preference) {
			if (Settings.isDebug())
				Log.e(TAG, "" + preference.getKey());

			/*if (preference.getKey().equals("OFFER_FTP")) {
				assertFtp();
				if(ftp.getIntent() != null)startActivity(ftp.getIntent());

			}*/

			if (preference.getKey().equals("OFFER_OGG")) {
				assertOgg();
				if(ogg.getIntent() != null)startActivity(ogg.getIntent());

			}

			if (preference.getKey().equals("OFFER_MP3")) {
				assertMP3();
				if(mp3.getIntent() != null)startActivity(mp3.getIntent());

			}

			if (preference.getKey().equals("PREFERENCE_EULA_ACCEPTED")) {

				eula();
				Log.e(TAG,
						""
								+ preference.getSharedPreferences().getBoolean(
										"PREFERENCE_EULA_ACCEPTED", false));

			}

			return preference.isEnabled();

		}
	};

	private void eula() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Log.e(TAG,
				"" + preferences.getBoolean("PREFERENCE_EULA_ACCEPTED", false));
		if (!preferences.getBoolean("PREFERENCE_EULA_ACCEPTED", false)) {

			Eula.setEulaBox(eula);
			Eula.showEULA(FormulaPreferences.this);
		}
	}

	@SuppressWarnings("EmptyMethod")
	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Stop the tracker when it is no longer needed.
	}
}
