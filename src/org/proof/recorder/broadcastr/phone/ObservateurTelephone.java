package org.proof.recorder.broadcastr.phone;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.receivers.PhoneRecorderReceiver;
import org.proof.recorder.service.DataPersistanceManager;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class ObservateurTelephone extends PhoneStateListener {

	public static String DIRECTION_CALL;

	private static String OUT_NUMBER = null;

	private static String AUDIO_FORMAT = "3gp";

	// Stopping Service from excluded contacts list
	// getting the SPEAKERON parameter to false by default for this case

	private static boolean IS_EXCLUDED;

	private Context _context = null;

	protected TelephonyManager _monManagerTel;

	private DataPersistanceManager dpm;

	/**
	 * @return the IS_EXCLUDED
	 */
	public boolean isExcluded() {
		return IS_EXCLUDED;
	}

	/**
	 * @param IS_EXCLUDED
	 *            the IS_EXCLUDED to set
	 */
	private void setExcluded(boolean isExcluded) {
		ObservateurTelephone.IS_EXCLUDED = isExcluded;
	}

	public void setContext(Context context) {
		_context = context;
	}

	/**
	 * @return the AUDIO_FORMAT
	 */
	public static String getAudioFormat() {
		return AUDIO_FORMAT;
	}

	/**
	 * @param AUDIO_FORMAT the AUDIO_FORMAT to set
	 */
	public static void setAudioFormat(String _audioFormat) {
		ObservateurTelephone.AUDIO_FORMAT = _audioFormat;
	}

	synchronized public void getManager(TelephonyManager telephony) {
		_monManagerTel = telephony;
	}

	public void resetDpm() {
		dpm.cacheRows("CAN_RECORD", "0");
	}

	synchronized @Override
	public void onCallStateChanged(int state, String incomingNumber) {

		Console.setTagName(this.getClass().getSimpleName());

		if (_context == null)
			return;

		Console.print_debug(incomingNumber);		
		Console.print_debug("L'ETAT A CHANGER");

		switch (state) {

		case TelephonyManager.CALL_STATE_RINGING:			

			String info = "Le téléphone sonne " + incomingNumber;

			Console.print_debug(info);

			if (Settings.isToastNotifications())
				Toast.makeText(_context, info, Toast.LENGTH_SHORT).show();

			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:

			Contact.setResolver(_context.getApplicationContext().getContentResolver());

			boolean excluded = false;
			Contact contact = new Contact();

			try {
				contact.setPhoneNumber(OUT_NUMBER);
			} catch (Exception e) {
				Console.print_exception("Exception 'ContactsDataHelper.isExcluded(<context>, ': " + OUT_NUMBER + "')" +
						"Contact info: " + contact + 
						"Details': " + e);
			}			

			excluded = contact.isExcluded();

			setExcluded(excluded);

			Console.print_debug("***********************************************");
			Console.print_debug("Contact Number: " + OUT_NUMBER + " is excluded: " + excluded);
			Console.print_debug("***********************************************");			

			if (excluded) {

				resetDpm();

				try {
					this.finalize();
				} catch (Throwable e) {
					Console.print_exception(e);
				}
				return;
			}

			Console.print_debug("L'APPEL A ETE PRIS");
			dpm.cacheRows("CALL_OFFHOOK", "1");
			break;

		case TelephonyManager.CALL_STATE_IDLE:

			Console.print_debug("L'APPEL Etat IDLE");			
			
			if(dpm.retrieveCachedRows("CALL_OFFHOOK").equals("1") &&
					dpm.retrieveCachedRows("CAN_RECORD").equals("1")) {				
				stopRecording(_context);				
				dpm.cacheRows("CALL_OFFHOOK", "0");
			}			

			_monManagerTel.listen(ObservateurTelephone.this,
					PhoneStateListener.LISTEN_NONE);
			break;
		}
	}

	public void startRecording(Context context, String phoneNumber, String directionCall) {

		Intent audioService = new Intent(context, PhoneRecorderReceiver.class);
		audioService.setAction("android.intent.action.START_PHONE_RECORDER");
		audioService.putExtra("phoneNumber", phoneNumber);
		audioService.putExtra("directionCall", directionCall);
		context.sendBroadcast(audioService);	    	
	}


	public void stopRecording(Context context) {

		Intent audioService = new Intent(context, PhoneRecorderReceiver.class);
		audioService.setAction("android.intent.action.STOP_PHONE_RECORDER");		    	
		context.sendBroadcast(audioService);    
	}	    

	/**
	 * 
	 */
	protected ObservateurTelephone() {
		super();
		Console.print_debug("CONSTRUCTOR ObservateurTelephone()");
		dpm = new DataPersistanceManager();
		dpm.cacheRows("CAN_RECORD", "1");
		dpm.cacheRows("CALL_OFFHOOK", "0");
	}

	public void feedNumbers(String phonenumber) {
		OUT_NUMBER = phonenumber;

	}

	/*
	 * true = ENTRANT false = SORTANT
	 */

}