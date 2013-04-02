package org.proof.recorder.fragment.dialog;

import org.proof.recorder.R;
import org.proof.recorder.Settings;

import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PluginsInformations extends SherlockFragmentActivity {
		
		/**
		 * @param message
		 */
		private static void print(String message) {
			if(!Settings.isDebug())
				Log.d(AboutApps.class.getName(), message);
		}
		
		/**
		 * @param str
		 * @return
		 */
		private static String cleanString(String str) {
			return str.replace(" ", "").trim().toLowerCase();
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.about_apps_dialog);
			
		}

}
