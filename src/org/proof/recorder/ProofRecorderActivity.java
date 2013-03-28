package org.proof.recorder;

import org.proof.recorder.billing.vending.ProofStore;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.features.SpyRecorder;
import org.proof.recorder.fragment.contacts.FragmentListPhoneContactsTabs;
import org.proof.recorder.fragment.dialog.ApplicationInformations;
import org.proof.recorder.fragment.dialog.PhoneInformations;
import org.proof.recorder.fragment.dialog.Search;
import org.proof.recorder.fragment.phone.FragmentListRecordFoldersTabs;
import org.proof.recorder.fragment.voice.FragmentListVoiceTabs;
import org.proof.recorder.fragment.voice.FragmentVoiceMediaRecorder;
import org.proof.recorder.place.de.marche.AnalyticsRecorderProof;
import org.proof.recorder.place.de.marche.Eula;
import org.proof.recorder.preferences.SettingsTabs;
import org.proof.recorder.service.TestDevice;
import org.proof.recorder.service.VerifyContactsApi;
import org.proof.recorder.syncron.fragment.GMCActivity;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.ConnectivityInfo;
import org.proof.recorder.utils.OsHandler;
import org.proof.recorder.utils.StaticIntents;
import org.proof.recorder.utils.StaticNotifications;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class ProofRecorderActivity extends SherlockActivity {
	private static final String TAG = "ProofRecorderActivity";
	private static final String BR  = "\n";

	// private static boolean initTestDevice = true;

	private static boolean checkLicsenceOk;

	static final int PICK_CONTACT_REQUEST = 0;
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzlJHQPCfccFjpEBQPG29jnh18AWwDqovEKOOZ4ms2aYWvoUUabaJT3NPCJ9XlgQYhB7wsC9ZYt9jHZBYX+WbMJ1XJs14EoNbWIJYK2urIbNTYGd2yjH4bE0irf7AYmLchuOfO3AJzdUi0LYbY7A+LS+CGSOSTCNGlRFzsWAEqF0phQudbYE4rzyPNxs8wj192sI188GYiPhTAv77yVcqI8ppnaaVCDpq7XY+g4IunoltTg9Xtz6h5m/Upr44kLmjB1FPM0rvAe75EW2uXXjImXQKCxLhZy6Q07uMqSwXpc6shnLr9OifzbQI3zpxfPStoTmmcc3MmfuMdLIoueShPwIDAQAB";

	// Generate your own 20 random bytes, and put them here.
	private static final byte[] SALT = new byte[] { -46, 65, 30, -128, -103,
			-57, 74, -64, 51, 88, -95, -12, 77, -107, -36, -113, -11, 32, -64,
			89 };
	LicenseCheckerCallback mLicenseCheckerCallback;
	LicenseChecker mChecker;
	// A handler on the UI thread.
	Handler mHandler;

	private static Context mContext;

	private static boolean bTitled, bUntitled, bKnown, bUnknown;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 1, 0, getString(R.string.search_hint))
				.setIcon(R.drawable.ic_action_search)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		SubMenu sub = menu.addSubMenu("+");
		sub.add(0, 2, 1, getString(R.string.info_phone));
		sub.add(0, 3, 2, getString(R.string.stats));
		sub.add(0, 4, 3, getString(R.string.about_us));
		sub.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {

		if (Settings.isDebug()) {
			Log.e(TAG, "onNewIntent()->IntentAction: " + intent.getAction());
		}			

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setIntent(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (Settings.isDebug()) {
			
			String msg = "=== ITEM INFO ===" + BR;
			msg += "STRING: " + item.toString() + BR;
			msg += "ID    : " + item.getItemId() + BR;
			msg += "GROUP : " + item.getGroupId() + BR;
			
			Log.d(TAG, msg);
		}			

		switch (item.getItemId()) {
		case 0:
			return false;

		case 1:
			if (!Settings.isNotLicensed()) {
				StaticIntents intent = StaticIntents.create(
						ProofRecorderActivity.this, Search.class);
				startActivity(intent);
			}
			return true;

		case 2:
			if (!Settings.isNotLicensed()) {
				Intent intent = new Intent(ProofRecorderActivity.this,
						PhoneInformations.class);
				startActivity(intent);
			}
			return true;

		case 3:
			if (!Settings.isNotLicensed()) {
				Intent intent = new Intent(ProofRecorderActivity.this,
						ApplicationInformations.class);
				startActivity(intent);
			}
			return true;
			
		case 4:
			if (!Settings.isNotLicensed()) {
				Intent intent = new Intent(ProofRecorderActivity.this,
						ProofStore.class);
				startActivity(intent);
			}
			return true;

		default:
			return false;
		}
	}

	// private static final int ACTIVITY_RECORD_SOUND = 1;

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		new SpyRecorder().startIntercepting();
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		ConnectivityInfo.informationConnectivity(this);	
		

		if (ConnectivityInfo.WIFI || ConnectivityInfo.TROISG) {
			AnalyticsRecorderProof trackerd = new AnalyticsRecorderProof(
					getApplicationContext(), TAG);

			trackerd.execute();
			TestDevice bt = new TestDevice();
			bt.execute();

			mHandler = new Handler();
			/*
			 * GCM implementation only if pass and user are set
			 */
			if (!Settings.getUsername(mContext).equals("username")
					&& !Settings.getPassword(mContext).equals("password")) {

				Handler monRunnable = new Handler();
				Runnable C2M = new Runnable() {

					@Override
					public void run() {

						new GMCActivity(ProofRecorderActivity.this);
						/*
						 * if(SecCom.unionRooted()){ Log.v(TAG,
						 * "Device is rooted"); } else { Log.v(TAG,
						 * "Device is not rooted"); }
						 */

					}
				};

				monRunnable.postDelayed(C2M, 3);
			}

			// Try to use more data here. ANDROID_ID is a single point of
			// attack.
			String deviceId = getEmailOrId(this);

			// Library calls this when it's done.
			mLicenseCheckerCallback = new MyLicenseCheckerCallback();
			// Construct the LicenseChecker with a policy.
			mChecker = new LicenseChecker(this, new ServerManagedPolicy(this,
					new AESObfuscator(SALT, getPackageName(), deviceId)),
					BASE64_PUBLIC_KEY);

			doCheck();
			/*
			 * Ckeck eula at the end
			 */

		}
		SharedPreferences pre = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (!pre.getBoolean("FIRSTINSTALL", false)) {
			Eula.showEULA(ProofRecorderActivity.this);
			pre.edit().putBoolean("FIRSTINSTALL", true).commit();
		} else {
			eulaChecker();
		}
		StaticNotifications.cancelNotification(this);
		OsHandler.checkDirectoriesStructureIntegrity(this);

		// Microphone DashBoard Icon
		ImageButton recordVoice = (ImageButton) findViewById(R.id.recordVoice);
		// recordVoice.setImageResource(R.drawable.microphone);

		// Green Book DashBoard Icon
		ImageButton voiceList = (ImageButton) findViewById(R.id.voiceList);

		// Blue Book DashBoard Icon
		ImageButton phoneCallList = (ImageButton) findViewById(R.id.phoneCallList);

		// preferences DashBoard Icon
		ImageButton settings = (ImageButton) findViewById(R.id.settings);

		// excluded contacts DashBoard Icon
		ImageButton excludedContacts = (ImageButton) findViewById(R.id.excludedFromRecordingList);

		// excluded contacts DashBoard Icon
		ImageButton dashBoardPerformances = (ImageButton) findViewById(R.id.dashBoardPerformances);

		dashBoardPerformances.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {

				if (!Settings.isNotLicensed()) {
					
				}
			}

		});

		voiceList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {

				int titled, untitled;

				titled = AndroidContactsHelper.getTitledVoiceCount();
				untitled = AndroidContactsHelper.getUnTitledVoiceCount();

				Log.v(TAG, "TITLED: " + titled + " " + "UNTITLED: " + untitled);

				if (titled > 0) {
					bTitled = true;
				} else
					bTitled = false;

				if (untitled > 0) {
					bUntitled = true;
				} else
					bUntitled = false;

				if (!bTitled && !bUntitled) {

					AlertDialogHelper.openNoneRecordsDialog(mContext);

				}

				else {

					if (!Settings.isNotLicensed()) {
						Intent intent = new Intent(ProofRecorderActivity.this,
								FragmentListVoiceTabs.class);
						startActivity(intent);
					}

				}
			}

		});
		recordVoice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {

				/*
				 * Intent intent = new
				 * Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
				 * startActivityForResult(intent, ACTIVITY_RECORD_SOUND);
				 */

				if (!Settings.isNotLicensed()) {
					Intent intentPrefs = new Intent(ProofRecorderActivity.this,
							FragmentVoiceMediaRecorder.class);
					startActivity(intentPrefs);
				}

			}

		});

		excludedContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				/*
				 * Uri uri = Uri.parse("content://contacts/people/");
				 * startActivityForResult( new Intent(Intent.ACTION_PICK, uri),
				 * PICK_CONTACT_REQUEST);
				 */

				if (!Settings.isNotLicensed()) {
					Intent intent = new Intent(ProofRecorderActivity.this,
							FragmentListPhoneContactsTabs.class);
					startActivity(intent);
				}

			}

		});

		settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {

				if (!Settings.isNotLicensed()) {
					Intent intentPrefs = new Intent(ProofRecorderActivity.this,
							SettingsTabs.class);
					startActivity(intentPrefs);
				}
			}

		});

		phoneCallList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {

				int known, unknown;

				known = AndroidContactsHelper.getKnownFolderContactsCount();
				unknown = AndroidContactsHelper.getUnKnownFolderContactsCount();

				Log.e(TAG, "TITLED: " + known + " " + "UNTITLED: " + unknown);

				if (known > 0) {
					bKnown = true;
				} else
					bKnown = false;

				if (unknown > 0) {
					bUnknown = true;
				} else
					bUnknown = false;

				if (!bKnown && !bUnknown) {

					AlertDialogHelper.openNoneRecordsDialog(mContext);

				}

				else {

					if (!Settings.isNotLicensed()) {
						Intent intent = new Intent(ProofRecorderActivity.this,
								FragmentListRecordFoldersTabs.class);
						startActivity(intent);
					}

				}
			}
		});
		
		/*  Update Deleted Contacts to the list of contacts
		 *  in Excluded and not Excluded Contacts list.
		 *  Put those deleted Known Contacts in the appropriated 
		 *  Tab.
		 **/
		
		Intent checkContacts = new Intent(this, VerifyContactsApi.class); 
		startService(checkContacts);
		

	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { super.onActivityResult(requestCode,
	 * resultCode, data);
	 * 
	 * switch (requestCode) { case ACTIVITY_RECORD_SOUND: Log.i(TAG,
	 * data.getDataString()); break; }
	 * 
	 * }
	 */

	public String getEmailOrId(Context context) {

		try {
			AccountManager accountManager = AccountManager.get(context);
			Account account = getAccount(accountManager);
			Log.v(TAG, " " + account.name + " EMAIL ");
			return account.name;
		} catch (Exception e) {
			Log.v(TAG, "Error : " + e.toString());
			Log.v(TAG, "pas de compte identifié");
			return Secure.getString(getContentResolver(), Secure.ANDROID_ID);

		}

	}

	private static Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;
		if (accounts.length > 0) {
			account = accounts[0];
		} else {
			account = null;
		}
		return account;
	}

	private void eulaChecker() {
		final String TAG = "eulaChecker()";

		if (checkLicsenceOk) {
			Log.v(TAG, "Passe la vérification des conditions utilisateurs.");
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			Log.v(TAG,
					""
							+ preferences.getBoolean(
									"PREFERENCE_EULA_ACCEPTED", false));
			if (!preferences.getBoolean("PREFERENCE_EULA_ACCEPTED", false)) {
				Log.v(TAG, "Les conditions utilisateurs ne sont pas valides.");
				Eula.showEULA(ProofRecorderActivity.this);
			} else {
				Log.v(TAG, "Les conditions utilisateurs  sont  valides.");
			}
		}
	}

	/*
	 * Licence --> Market
	 */

	@Override
	protected Dialog onCreateDialog(int id) {
		final boolean bRetry = id == 1;
		return new AlertDialog.Builder(this)
				.setTitle(R.string.unlicensed_dialog_title)
				.setMessage(
						bRetry ? R.string.unlicensed_dialog_retry_body
								: R.string.unlicensed_dialog_body)
				.setPositiveButton(
						bRetry ? R.string.retry_button : R.string.buy_button,
						new DialogInterface.OnClickListener() {
							boolean mRetry = bRetry;

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (mRetry) {
									doCheck();
								} else {
									Intent marketIntent = new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("http://market.android.com/details?id="
													+ getPackageName()));
									startActivity(marketIntent);
								}
							}
						})
				.setNegativeButton(R.string.quit_button,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//finish();
							}
						}).create();
	}

	private void doCheck() {
		Log.v(TAG, "Liscensing go for checking result");
		setProgressBarIndeterminateVisibility(true);
		mChecker.checkAccess(mLicenseCheckerCallback);
	}

	private void displayResult(final String result) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Log.e(TAG, "Liscensing Process result : " + result);
				setProgressBarIndeterminateVisibility(false);
			}
		});
	}

	private void displayDialog(final boolean showRetry) {
		mHandler.post(new Runnable() {
			@Override
			@SuppressWarnings("deprecation")
			public void run() {
				setProgressBarIndeterminateVisibility(false);
				showDialog(showRetry ? 1 : 0);

			}
		});
	}

	private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
		@Override
		public void allow(int policyReason) {
			if (isFinishing()) {
				Log.v(TAG, "LISENCE AUTH ok");
				checkLicsenceOk = true;
				// Don't update UI if Activity is finishing.
				return;
			}
			if (policyReason == Policy.LICENSED) {
				// Should allow user access.
				displayResult(getString(R.string.allow));
				checkLicsenceOk = true;
				Log.v(TAG, "LISENCE AUTH ok");
			}
		}

		@Override
		public void dontAllow(int policyReason) {
			if (isFinishing()) {
				Log.v(TAG, "LISENCE AUTH NOok");
				checkLicsenceOk = true;
				// Don't update UI if Activity is finishing.
				return;
			}

			displayResult(getString(R.string.dont_allow));
			// Should not allow access. In most cases, the app should assume
			// the user has access unless it encounters this. If it does,
			// the app should inform the user of their unlicensed ways
			// and then either shut down the app or limit the user to a
			// restricted set of features.
			// In this example, we show a dialog that takes the user to Market.
			// If the reason for the lack of license is that the service is
			// unavailable or there is another problem, we display a
			// retry button on the dialog and a different message.
			displayDialog(policyReason == Policy.NOT_LICENSED);
			checkLicsenceOk = true;
			Log.v(TAG, "LISENCE AUTH NOok");
		}

		@Override
		public void applicationError(int errorCode) {
			if (isFinishing()) {
				Log.v(TAG, String.format(getString(R.string.application_error),
						errorCode));
				checkLicsenceOk = true;
				// Don't update UI if Activity is finishing.
				return;
			}
			// This is a polite way of saying the developer made a mistake
			// while setting up or calling the license checker library.
			// Please examine the error code and fix the error.
			String result = String.format(
					getString(R.string.application_error), errorCode);
			displayResult(result);
			checkLicsenceOk = true;
		}
	}

	/*
     * 
     */
	/**
	 * stop the recreation of the activity on Orientation Change the
	 * MediaRecorder, is therefore not recreated and keep recording on
	 * Orientation Changes
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		if (!Settings.getUsername(mContext).equals("username")
				&& !Settings.getPassword(mContext).equals("password")) {
			try {
				unregisterReceiver(GMCActivity.mHandleMessageReceiver);
			} catch (java.lang.IllegalArgumentException e) {
				if (Settings.isDebug())
					Log.e(TAG, "" + e);

			}
		}
		super.onDestroy();
		mChecker.onDestroy();
		// Stop the tracker when it is no longer needed.
	}
}