package org.proof.recorder.preferences;

import java.util.ArrayList;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.activity.ProofPreferenceActivity;
import org.proof.recorder.graphics.ManipulateUi;
import org.proof.recorder.service.BestAudioConfiguration;
import org.proof.recorder.service.TestDevice;
import org.proof.recorder.utils.QuickActionDlg;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DeviceCapabilities extends ProofPreferenceActivity {
	
	private static final String TAG = "DeviceCapabilities";
	public Bundle OPTIONS;
	public Bundle BESTOPTION;
	boolean isInit;
	private CheckBoxPreference mic;
	private CheckBoxPreference voice_up;
	private CheckBoxPreference voice_down;
	private CheckBoxPreference voice_call;
	private CheckBoxPreference cam;
	private CheckBoxPreference voice_reco;
	private CheckBoxPreference voice_com;
	private CheckBoxPreference B;
	private ListPreference mFormat;
	private ListPreference mCompression;
	private ListPreference mQual;
	private ListPreference postEncode;
	
	//private ListPreference mChan;
	private String bestOption;

	// SharedPreferences pref;
	private Editor prefEdit;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.device_capabilities);
		mic = (CheckBoxPreference) getPreferenceScreen().findPreference("MIC");

		voice_up = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"VOICE_UP");
		voice_down = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"VOICE_DOWN");
		voice_call = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"VOICE_CALL");
		cam = (CheckBoxPreference) getPreferenceScreen().findPreference("CAM");
		voice_reco = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"VOICE_RECO");
		voice_com = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"VOICE_COM");
		B = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"BESTCONF");
		mFormat = (ListPreference) getPreferenceScreen().findPreference(
				"audio_format");
		mCompression = (ListPreference) getPreferenceScreen().findPreference(
				"MP3QUALITY");
		mQual = (ListPreference) getPreferenceScreen()
				.findPreference("OGGQUAL");
		
		postEncode = (ListPreference) getPreferenceScreen()
				.findPreference("post_encode");
		
		/*mChan = (ListPreference) getPreferenceScreen().findPreference(
				"audio_channel");
		mChan.setEnabled(false);*/
		
		mic.setOnPreferenceClickListener(micClick);
		voice_up.setOnPreferenceClickListener(micClick);
		voice_down.setOnPreferenceClickListener(micClick);
		voice_call.setOnPreferenceClickListener(micClick);
		cam.setOnPreferenceClickListener(micClick);
		voice_reco.setOnPreferenceClickListener(micClick);
		voice_com.setOnPreferenceClickListener(micClick);
		B.setOnPreferenceClickListener(micClick);
		mFormat.setOnPreferenceClickListener(spinClick);
		
		
		ArrayList<String> formatList = pluginMyForma();
		CharSequence entries[] = new String[formatList.size()];
		CharSequence entryValues[] = new String[formatList.size()];
		int i = 0;
		for (String category : formatList) {
			entries[i] = category;
			entryValues[i] = category;
			i++;
		}
		mFormat.setEntries(entries);
		mFormat.setEntryValues(entryValues);
		
		postEncode.setEntries(new String[] {
				getString(R.string.encode_while),
				getString(R.string.encode_after)				
		});
		
		postEncode.setEntryValues(new String[] {
				"0",
				"1"
		});
		
		onSettings();
	}

	private ArrayList<String> pluginMyForma() {
		
		String required = getApplicationContext().getResources().getString(
				R.string.REQUIRED);
		
		ArrayList<String> mlPlug = new ArrayList<String>();
		mlPlug.add("3GP");
		mlPlug.add("WAV");
		
		if (Settings.assertPlugExist(0)) {
			mlPlug.add("MP3");

		} else {
			mCompression.setEnabled(false);
			Toast.makeText(getApplicationContext(), "MP3 " + required,
					Toast.LENGTH_SHORT).show();
		}

		if (Settings.assertPlugExist(1)) {
			mlPlug.add("OGG");
		} else {
			mQual.setEnabled(false);
			Toast.makeText(getApplicationContext(), "OGG " + required,
					Toast.LENGTH_SHORT).show();
		}

		return mlPlug;
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

	private void onSettings() {
		OPTIONS = TestDevice.BUNDLECONFIGURATIONAUDIO;
		showSourceOptions();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		isInit = preferences.getBoolean("BESTCONF", true);
		if (isInit) {
			BESTOPTION = BestAudioConfiguration.bestCapabilities(OPTIONS);
			bestOption = BESTOPTION.getString("key");
			onBestConf(bestOption);
			Log.e(TAG, "Meilleur valeur car BESTCONF est enabled :"
					+ bestOption);

		} else {
			CheckBoxPreference[] chk = { mic, voice_up, voice_down, voice_call,
					cam, voice_reco, voice_com };
			ManipulateUi uI = new ManipulateUi();
			uI.enabledArrayChk(chk);
			showSourceOptions();
			Log.e(TAG, "BESTCONF disable");

		}
	}

	private OnPreferenceClickListener spinClick = new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(
				android.preference.Preference preference) {
			if (Settings.isDebug())
				Log.e(TAG, "" + preference.getKey().toString());
			if (preference.getKey().contains("Plugin")) {
				preference.setEnabled(false);
			}
			return preference.isEnabled();

		}
	};
	private OnPreferenceClickListener micClick = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(
				android.preference.Preference preference) {
			ManipulateUi uI = new ManipulateUi();
			if (Settings.isDebug())
				Log.e(TAG, "" + preference.getKey().toString());

			if (preference.getKey().contains("MIC")) {

				if (Settings.isDebug())
					Log.e(TAG, "if MOC");
				String[] chk = { "VOICE_UP", "VOICE_DOWN", "VOICE_CALL", "CAM",
						"VOICE_RECO", "VOICE_COM" };
				CheckBoxPreference[] chkB = { voice_up, voice_down, voice_call,
						cam, voice_reco, voice_com };
				uI.setArrayBoolean(chk, getApplicationContext(), false);
				uI.noCheckedArrayChk(chkB);
			}

			if (preference.getKey().contains("VOICE_UP")) {

				String[] chk = { "MIC", "VOICE_DOWN", "VOICE_CALL", "CAM",
						"VOICE_RECO", "VOICE_COM" };
				CheckBoxPreference[] chkB = { mic, voice_down, voice_call, cam,
						voice_reco, voice_com };
				uI.setArrayBoolean(chk, getApplicationContext(), false);
				uI.noCheckedArrayChk(chkB);

			}

			if (preference.getKey().contains("VOICE_DOWN")) {

				String[] chk = { "MIC", "VOICE_UP", "VOICE_CALL", "CAM",
						"VOICE_RECO", "VOICE_COM" };
				CheckBoxPreference[] chkB = { mic, voice_up, voice_call, cam,
						voice_reco, voice_com };
				uI.setArrayBoolean(chk, getApplicationContext(), false);
				uI.noCheckedArrayChk(chkB);

			}

			if (preference.getKey().contains("VOICE_CALL")) {

				String[] chk = { "MIC", "VOICE_UP", "VOICE_DOWN", "CAM",
						"VOICE_RECO", "VOICE_COM" };
				CheckBoxPreference[] chkB = { mic, voice_up, voice_down, cam,
						voice_reco, voice_com };
				uI.setArrayBoolean(chk, getApplicationContext(), false);
				uI.noCheckedArrayChk(chkB);

			}

			if (preference.getKey().contains("CAM")) {

				String[] chk = { "MIC", "VOICE_UP", "VOICE_DOWN", "VOICE_CALL",
						"VOICE_RECO", "VOICE_COM" };
				CheckBoxPreference[] chkB = { mic, voice_up, voice_down,
						voice_call, voice_reco, voice_com };
				uI.setArrayBoolean(chk, getApplicationContext(), false);
				uI.noCheckedArrayChk(chkB);

			}

			if (preference.getKey().contains("VOICE_RECO")) {

				String[] chk = { "MIC", "VOICE_UP", "VOICE_DOWN", "VOICE_CALL",
						"CAM", "VOICE_COM" };
				CheckBoxPreference[] chkB = { mic, voice_up, voice_down,
						voice_call, cam, voice_com };
				uI.setArrayBoolean(chk, getApplicationContext(), false);
				uI.noCheckedArrayChk(chkB);

			}

			if (preference.getKey().contains("VOICE_COM")) {

				String[] chk = { "MIC", "VOICE_UP", "VOICE_DOWN", "VOICE_CALL",
						"CAM", "VOICE_RECO" };
				CheckBoxPreference[] chkB = { mic, voice_up, voice_down,
						voice_call, cam, voice_reco };
				uI.setArrayBoolean(chk, getApplicationContext(), false);
				uI.noCheckedArrayChk(chkB);

			}
			if (preference.getKey().equals("BESTCONF")) {
				if (Settings.isDebug())
					Log.e(TAG, "BestConf clicked");
				onSettings();

			}

			return preference.isEnabled();

		}
	};

	private final void showSourceOptions() {
		/*
		 * all = {mic, voice_up, voice_down, voice_call,cam,
		 * voice_reco,voice_com}
		 */

		if (OPTIONS.getBoolean("mic") == false) {
			mic.setEnabled(false);

			if (Settings.isDebug())
				Log.e(TAG, "Preference mic false");
		}

		if (OPTIONS.getBoolean("voice_up") == false) {
			voice_up.setEnabled(false);

		}

		if (OPTIONS.getBoolean("voice_down") == false) {
			voice_down.setEnabled(false);

		}

		if (OPTIONS.getBoolean("voice_call") == false) {
			voice_call.setEnabled(false);

		}

		if (OPTIONS.getBoolean("cam") == false) {
			cam.setEnabled(false);

		}

		if (OPTIONS.getBoolean("voice_reco") == false) {
			voice_reco.setEnabled(false);

		}

		if (OPTIONS.getBoolean("voice_com") == false) {
			voice_com.setEnabled(false);
		}

	}

	public void onBestConf(String c) {
		/*
		 * disable all
		 */
		CheckBoxPreference[] chk = { mic, voice_up, voice_down, voice_call,
				cam, voice_reco, voice_com };
		ManipulateUi uI = new ManipulateUi();
		uI.disableArrayChk(chk);
		uI.noCheckedArrayChk(chk);

		prefEdit = getSharedPreferences(c, 0).edit();

		if (c.contains("voice_call")) {
			prefEdit.putBoolean("VOICE_CALL", true);
			voice_call.setChecked(true);
			voice_call.setEnabled(true);
			voice_call.setSummary(getResources().getString(
					R.string.summary_checkbox_VOICE_CALL)
					+ "\n"
					+ getResources().getString(
							R.string.extra_checkbox_INFORMATION));
		} else if (c.contains("voice_reco")) {
			prefEdit.putBoolean("VOICE_RECO", true);
			voice_reco.setChecked(true);
			voice_reco.setEnabled(true);
			voice_reco.setSummary(getResources().getString(
					R.string.summary_checkbox_VOICE_RECO)
					+ "\n"
					+ getResources().getString(
							R.string.extra_checkbox_INFORMATION));

		} else if (c.contains("voice_com")) {
			prefEdit.putBoolean("VOICE_COM", true);
			voice_com.setChecked(true);
			voice_com.setEnabled(true);
			voice_com.setSummary(getResources().getString(
					R.string.summary_checkbox_VOICE_COM)
					+ "\n"
					+ getResources().getString(
							R.string.extra_checkbox_INFORMATION));

		} else {
			prefEdit.putBoolean("MIC", true);
			mic.setChecked(true);
			mic.setEnabled(true);
			mic.setSummary(getResources().getString(
					R.string.summary_checkbox_MIC)
					+ "\n"
					+ getResources().getString(
							R.string.extra_checkbox_INFORMATION));

		}
		prefEdit.commit();

	}
}
