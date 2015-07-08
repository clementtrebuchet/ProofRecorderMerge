package org.proof.recorder.place.de.marche;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.Log;

import org.proof.recorder.R;

import java.io.InputStream;

public class Eula {


	private static final String TAG = "Eula";
	SharedPreferences preference;
	private static CheckBoxPreference eu;
	private static Eula mSingleTon;
	private static Eula getYnique(){
		if(null == mSingleTon)
		mSingleTon= new Eula();
		
			return mSingleTon;
		
	}
	public static void setEulaBox(CheckBoxPreference CB){getYnique();eu = CB;}
	public  static void showEULA(final Activity activity) {
		getYnique();
		Log.e(TAG, "" + activity.toString());
		final SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(activity.getApplicationContext());
			 AlertDialog.Builder builder = new AlertDialog.Builder(
					activity);
			builder.setTitle(R.string.eula_title);
			builder.setCancelable(true);
			builder.setPositiveButton(R.string.eula_accept,
					new DialogInterface.OnClickListener() {

						
						@Override
						public void onClick(DialogInterface dialog, int which) {
						preference.edit()
									.putBoolean("PREFERENCE_EULA_ACCEPTED", true)
									.commit();
									if (eu != null)eu.setChecked(true);
									des();

						}
					});
			builder.setNegativeButton(R.string.eula_refuse,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							preference.edit()
							.putBoolean("PREFERENCE_EULA_ACCEPTED", false)
							.commit();
							//android.os.Process.killProcess(android.os.Process.myPid());
							
							activity.finish();
							des();
						

						}
					});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					preference.edit()
					.putBoolean("PREFERENCE_EULA_ACCEPTED", false)
					.commit();
					//android.os.Process.killProcess(android.os.Process.myPid());
					activity.finish();
					des();
					
				

				}
			});
			builder.setMessage(readFile(activity, R.raw.eula));
			builder.create().show();
			Log.e(TAG, "" + activity.toString());
		

	}

	private  static CharSequence readFile(Activity activity, int eula) {

		try {
			InputStream in_s = activity.getResources().openRawResource(eula);
			byte[] b = new byte[in_s.available()];
			//noinspection ResultOfMethodCallIgnored
			in_s.read(b);
			return new String(b);

		} catch (Exception e) {
			Log.e(TAG, "" + e);
			return null;
		}

	}
	private static void des(){
		try {
			mSingleTon.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
