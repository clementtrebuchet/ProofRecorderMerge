package org.proof.recorder.wigdet;

import java.util.ArrayList;

import org.proof.recorder.R;
import org.proof.recorder.Settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class WidgetPreferenceFormat extends SherlockPreferenceActivity {

	private ListPreference mFormat;
	protected final String TAG = WidgetPreferenceFormat.class.getName();

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

	private ArrayList<String> pluginMyForma() {
		String required = getApplicationContext().getResources().getString(
				R.string.REQUIRED);
		ArrayList<String> mlPlug = new ArrayList<String>();
		mlPlug.add("3GP");
		mlPlug.add("WAV");
		if (Settings.assertPlugExist(0)) {
			mlPlug.add("MP3");

		} else {
			Toast.makeText(getApplicationContext(), "MP3 " + required,
					Toast.LENGTH_SHORT).show();
		}

		if (Settings.assertPlugExist(1)) {
			mlPlug.add("OGG");
		} else {
			Toast.makeText(getApplicationContext(), "OGG " + required,
					Toast.LENGTH_SHORT).show();
		}

		return mlPlug;
	}

}
