/**
 * 
 */
package org.proof.recorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author namgyal.brisson
 * 
 */
public final class Settings {

	/**
	 * CONSTANTES COMMUNES A L'ENSEMBLE DE L'APPLICATION @PUBLIC
	 * 
	 * @author appconceptlab
	 * 
	 */
	
	public static boolean OVERRIDE_MODE;
	
	
	public final static String APP_KEY = "q66vtgg2zeodm45";
	public final static String APP_SECRET = "9t0wm8zrp7kwlu4";
	public final static String BREAK = "\n";

	public static String mBasePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	public static String[] DEFAULT_FILE_PATHS = new String[] {
			mBasePath + "/proofRecorder/", 
			mBasePath + "/proofRecorder/calls/",
			mBasePath + "/proofRecorder/voices/",
			mBasePath + "/proofRecorder/calls/wav/",
			mBasePath + "/proofRecorder/voices/wav/",
			mBasePath + "/proofRecorder/calls/3gp/",
			mBasePath + "/proofRecorder/voices/3gp/" };
	
	public static String mAppPath = mBasePath + "/proofRecorder/";

	public static enum mFormat {
		THREE_GP, WAV, MP3, OGG;
	}

	public static enum mType {
		CALL, VOICE, VOICE_TITLED, VOICE_UNTITLED
	}

	public static enum mSampleRate {

		SAMPLERATE;

		private int rate;

		mSampleRate() {

			this.rate = 44100;

		}

		public int getSampleRate() {

			return this.rate;

		}

		public void setSamplerate(int sampleOrder) {
			switch (sampleOrder) {
			case 44100:
				mSampleRate.SAMPLERATE.rate = 44100;
				break;
			case 22050:
				mSampleRate.SAMPLERATE.rate = 22050;
				break;
			case 11025:
				mSampleRate.SAMPLERATE.rate = 11025;
				break;
			case 8000:
				mSampleRate.SAMPLERATE.rate = 8000;
				break;
			default:
				mSampleRate.SAMPLERATE.rate = 44100;
				break;
			}

		}
	}

	public static enum mChannel {
		CHANNEL;

		private int channel;

		mChannel() {

			this.channel = 1;
		}

		public int getMChannel(Context c) {
			setMChannel(getChannel(c));
			return this.channel;
		}

		public void setMChannel(String Chan) {

			if (Chan.equals("MONO")) {

				this.channel = 1;

			} else if (Chan.equals("STEREO")) {
				this.channel = 2;
			} else {
				this.channel = 1;
			}
		}
	}

	public static final int RECORDER_BPP = 16;
	public static final int RECORDER_SAMPLERATE = 44100;
	public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	public static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";

	/**
	 * CONSTANTES COMMUNES A L'ENSEMBLE DE L'APPLICATION @PRIVATE
	 * 
	 * @author appconceptlab
	 * 
	 */

	private static enum FORMULA {
		BASIC, MEDIUM, FULL
	}

	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String device_id = telephonyManager.getDeviceId();
		return device_id;

	}

	public static String hostname = "sd-21117.dedibox.fr";
	public static String user = "clement";
	public static String pass = "sonyaltec";
	public static String methodCALL = "call";
	public static String methodVOICE = "voice";
	public static int defaultQuality = 7;

	private static boolean mNOTIFICATIONS = false;
	private static boolean mUAC_ASSISTED = false;
	private static SharedPreferences mSharedPreferences;
	private static final boolean TOAST_NOTIFICATIONS = true;
	private static final boolean DEBUG = false;

	private static boolean NOT_LICENSED = false;

	private static final FORMULA mFORMULA = FORMULA.BASIC;
	private static final String TAG = "SettingsProofRecorder";
	
	private static Context SettingsContext = null;

	private static void initSharedPreferences(Context mContext) {
		mSharedPreferences = null;
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
	}
	
	@SuppressWarnings("unused")
	private static void setSharedPrefs(String key, String value) {
		Editor editor = mSharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	private static void setSharedPrefs(String key, boolean value) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	@SuppressWarnings("unused")
	private static void setSharedPrefs(String key, float value) {
		Editor editor = mSharedPreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	
	@SuppressWarnings("unused")
	private static void setSharedPrefs(String key, long value) {
		Editor editor = mSharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	private static void setSharedPrefs(String key, int value) {
		Editor editor = mSharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * @return the mUAC_ASSISTED
	 */
	public static boolean isUAC_ASSISTED(Context mContext) {
		initSharedPreferences(mContext);
		mUAC_ASSISTED = mSharedPreferences.getBoolean("UAC_ASSISTED", true);
		return mUAC_ASSISTED;
	}

	/**
	 * @param mUAC_ASSISTED
	 *            the mUAC_ASSISTED to set
	 */
	public static void setUAC_ASSISTED(boolean mUAC_ASSISTED) {
		Settings.mUAC_ASSISTED = mUAC_ASSISTED;
	}

	/**
	 * Return a convertion to Mp3 quality compression
	 */
	public static int getMp3Compression(Context mContext) {
		initSharedPreferences(mContext);
		int mp3Q = Integer.parseInt(mSharedPreferences.getString("MP3QUALITY",
				"64"));
		int compression;
		switch (mp3Q) {
		case 64:
			compression = 64;
			break;
		case 80:
			compression = 80;
			break;
		case 96:
			compression = 96;
			break;
		case 112:
			compression = 112;
			break;
		case 128:
			compression = 128;
			break;
		case 160:
			compression = 160;
			break;
		case 192:
			compression = 192;
			break;
		case 224:
			compression = 224;
			break;
		case 256:
			compression = 256;
			break;
		case 320:
			compression = 320;
			break;
		default:
			compression = 128;
			break;
		}
		return compression;
	}

	/**
	 * Return a frequence in hz
	 */
	public static int getMP3Hertz(Context mContext) {
		initSharedPreferences(mContext);
		int quality = Integer.parseInt(mSharedPreferences.getString("MP3QHH",
				"8000"));
		return quality;
	}

	public static float getOGGQual(Context mContext) {
		initSharedPreferences(mContext);
		String quality = mSharedPreferences.getString("OGGQUAL", "Bonne");
		float qual = 0.4f;
		if (quality.equals("Bonne") || quality.equals("Good")) {
			qual = 0.4f;
		}
		if (quality.equals("Très Bonne") || quality.equals("Very Good")) {
			qual = 0.6f;
		}
		if (quality.equals("Excellente") || quality.equals("Amazing")) {
			qual = 0.8f;
		}
		return qual;
	}

	/**
	 * Query for plug
	 */

	public static boolean assertPlugExist(int plugId, Context mContext) {

		PackageManager mPackageManager = mContext.getPackageManager();
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
				Log.e(TAG, "Pluguin exist :" + plugIntent);
				return true;
			}
			Log.e(TAG, "Pluguin dont't exist :" + plugIntent + " Intent:" + mIntent);
			return false;
		} catch (Exception e) {
			Log.e(TAG, "Pluguin dont't exist :" + plugIntent);
			return false;
		}

	}
	public static boolean assertOthersPlugExist(String plugIntent, Context mContext) {
		PackageManager mPackageManager = mContext.getPackageManager();
		try {
			Intent mIntent = mPackageManager.getLaunchIntentForPackage(plugIntent);
			if (mIntent != null) {
				Log.e(TAG, "Pluguin exist :" + plugIntent);
				return true;
			}
			Log.e(TAG, "Pluguin dont't exist :" + plugIntent + " Intent:" + mIntent);
			return false;
		} catch (Exception e) {
			Log.e(TAG, "Pluguin dont't exist :" + plugIntent);
			return false;
		}

	}

	/**
	 * 
	 * @return the mNOTIFICATIONS
	 */
	public static boolean isNOTIFICATIONS(Context mContext) {
		initSharedPreferences(mContext);
		mNOTIFICATIONS = mSharedPreferences.getBoolean("NOTIFICATION", true);
		return mNOTIFICATIONS;
	}

	public static String getUsername(Context mContext) {
		initSharedPreferences(mContext);
		return mSharedPreferences.getString("username", "username");

	}

	public static String getAudioFormat(Context mContext) {
		initSharedPreferences(mContext);
		String result = "3GP"; 
		String f = mSharedPreferences.getString("audio_format", "3GP");
		if(f.equals("0")){
			result = "3GP";
		} else if (f.equals("1")){
			result = "WAV";
		} else if (f.equals("2")){
			result = "MP3";
		} else if (f.equals("3")){
			result = "OGG";
		}
		Log.e(TAG, "result: " + result);
		Log.e(TAG, "getAudioFormat id : " + f);
		return result;
	}

	public static String getChannel(Context mContext) {
		initSharedPreferences(mContext);
		return mSharedPreferences.getString("audio_channel", "MONO");
	}

	public static String getPassword(Context mContext) {
		initSharedPreferences(mContext);
		return mSharedPreferences.getString("password", "password");

	}

	public static boolean isSync(Context mContext) {
		initSharedPreferences(mContext);
		return mSharedPreferences.getBoolean("synchroAllCalls", false);

	}

	/**
	 * @return the toastNotifications
	 */
	public static boolean isToastNotifications() {
		return TOAST_NOTIFICATIONS;
	}

	/**
	 * @return the debug
	 */
	public static boolean isDebug() {
		return DEBUG;
	}

	/**
	 * @return the mformula
	 */
	public static FORMULA getMformula() {
		return mFORMULA;
	}

	/**
	 * @return the notLicensed
	 */
	public static boolean isNotLicensed() {
		return NOT_LICENSED;
	}

	public static void setNOT_LICENSED(boolean nOT_LICENSED) {
		NOT_LICENSED = nOT_LICENSED;
	}
	
	public static boolean isOverrideMode() {		
		initSharedPreferences(getSettingscontext());
		return mSharedPreferences.getBoolean("OVERRIDE_MODE", true);
	}

	public static void setOverrideMode(boolean b) {
		setSharedPrefs("OVERRIDE_MODE", b);
	}

	public static boolean isAlarm() {
		
		if(isOverrideMode()) {			
			setAlarm(false);
			setOverrideMode(false);
		}
		
		initSharedPreferences(getSettingscontext());
		return mSharedPreferences.getBoolean("checksServiceAlarm", false);
	}

	public static void setAlarm(boolean b) {
		setSharedPrefs("checksServiceAlarm", b);
	}
	
	public static int getRecordsCount() {
		initSharedPreferences(getSettingscontext());
		return mSharedPreferences.getInt("recordsCount", 0);
	}
	
	public static void setRecordsCount(int count) {
		setSharedPrefs("recordsCount", count);
	}	

	/**
	 * @return the settingscontext
	 */
	public static Context getSettingscontext() {
		return SettingsContext;
	}
	
	/**
	 * @return the settingscontext
	 */
	public static void setSettingscontext(Context _Context) {
		Settings.SettingsContext = _Context;
	}

}
