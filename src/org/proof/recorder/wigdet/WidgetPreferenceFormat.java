package org.proof.recorder.wigdet;

import java.util.ArrayList;

import org.proof.recorder.R;
import org.proof.recorder.Settings;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class WidgetPreferenceFormat extends SherlockPreferenceActivity {

	private ListPreference mFormat;
	protected final String TAG = WidgetPreferenceFormat.class.getName();

	private ArrayList<String> pluginMyForma() {
		String required = getApplicationContext().getResources().getString(
				R.string.REQUIRED);
		ArrayList<String> mlPlug = new ArrayList<String>();
		mlPlug.add("3GP");
		mlPlug.add("WAV");
		if (assertPlugExist(0)) {
			mlPlug.add("MP3");

		} else {
			Toast.makeText(getApplicationContext(), "MP3 " + required,
					Toast.LENGTH_SHORT).show();
		}

		if (assertPlugExist(1)) {
			mlPlug.add("OGG");
		} else {
			Toast.makeText(getApplicationContext(), "OGG " + required,
					Toast.LENGTH_SHORT).show();
		}

		return mlPlug;
	}
	public  boolean assertPlugExist(int plugId) {

		PackageManager mPackageManager = getBaseContext().getPackageManager();
		String plugIntent;
		switch (plugId) {
		case 0:
			plugIntent = "org.proofs.recorder.codec.mp3";
			break;
		case 1:
			plugIntent = "org.proofs.recorder.codec.ogg";
			break;
		case 2:
			plugIntent = "org.proof.recorderftp";
			break;
		default:
			return false;

		}
		try {
			Intent mIntent = mPackageManager.getLaunchIntentForPackage(plugIntent);
			if (mIntent != null) {
				Log.d(TAG,"Pluguin exist :" + plugIntent);
				return true;
			}
			Log.d(TAG,"Pluguin dont't exist :" + plugIntent + " Intent:" + mIntent);
			return false;
		} catch (Exception e) {
			Log.d(TAG,"Pluguin dont't exist :" + plugIntent);
			return false;
		}

	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.widget_preferences);
		mFormat = (ListPreference) getPreferenceScreen().findPreference(
				"audio_format");
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

	
}
