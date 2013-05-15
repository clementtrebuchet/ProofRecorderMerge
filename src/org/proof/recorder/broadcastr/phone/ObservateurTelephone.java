package org.proof.recorder.broadcastr.phone;

import java.util.logging.Logger;

import org.proof.recorder.Settings;
import org.proof.recorder.bases.utils.SetStaticContext;
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

	private static Context _context = null;

	protected TelephonyManager _monManagerTel;

	private DataPersistanceManager dpm = null;

	private static Intent audioService;

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

	private void setContext(Context context) {
		_context = context;
		
		if(!Settings.hasContext()) {
			Settings.setSettingscontext(context);
		}	
	}
	
	private Context getContext() {
		return _context;
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

		if (getContext() == null) {
			Console.print_exception("The Context is Null!");
			return;	
		}
		
		SetStaticContext.setStaticsContext(getContext(), 0);
			
		dpm = new DataPersistanceManager();

		switch (state) {

		case TelephonyManager.CALL_STATE_RINGING:			

			String info = "Le téléphone sonne " + incomingNumber;

			Console.print_debug(info);

			if (Settings.isToastNotifications())
				Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();

			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:

			Contact.setResolver(
					getContext().getApplicationContext().getContentResolver());

			boolean excluded = false;
			Contact contact = new Contact();

			try {
				contact.setPhoneNumber(OUT_NUMBER);
			} catch (Exception e) {
				Console.print_exception(e);
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

			if(dpm.retrieveCachedRows("CAN_RECORD").equals("1")) {					
				startRecording();
			}
			
			break;

		case TelephonyManager.CALL_STATE_IDLE:

			Console.print_debug("L'APPEL Etat IDLE");	
			
			if(dpm.retrieveCachedRows("CAN_RECORD").equals("1")) {					
				stopRecording();				
			}									

			_monManagerTel.listen(ObservateurTelephone.this,
					PhoneStateListener.LISTEN_NONE);
			break;
		}
		
		Console.print_debug(incomingNumber);		
		Console.print_debug("L'ETAT A CHANGER");
	}

	public void prepareRecording(String phoneNumber, String directionCall) {
		
		
		// should fix when audio recorder is on 
		// and an incoming / outgoing call is done at the same time.
		// Avoiding strange behavior or/and bugs.
		
		// We let the service on, but we keep track of all data:
		// Voice & Call, then we ask the user to choose what we're to do
		// with this hybrid audio record.
		
		// ie. Save it as:
		//     1 A voice record
		//     2 A call record
		//     3 both.
		
		// Note: we indicate the user that's not a recommended state :)
		// Note: If option 1 or 3 is chosen, then we display Voice dialog for title edit purpose.
		
		audioService = new Intent(getContext(), PhoneRecorderReceiver.class);
    	
    	if(!dpm.isProcessing()) {
    		// Normal behavior    		
    		audioService.setAction("android.intent.action.START_PHONE_RECORDER");
    	}    	
    	else {
    		audioService.setAction("android.intent.action.KEEP_PHONE_DATA_TRACK");
    	}
    	
		audioService.putExtra("phoneNumber", phoneNumber);
		audioService.putExtra("directionCall", directionCall);
		
		dpm.cacheRows("PhoneServicePrepared", "true");
	}
	
	private void startRecording() {
		
		boolean isPrepared;
		
		try {
			isPrepared = Boolean.parseBoolean(
					dpm.retrieveCachedRows("PhoneServicePrepared"));
		}
		catch (Exception e) {
			isPrepared = false;
		}
		
		if(isPrepared) {
			getContext().sendBroadcast(audioService);			
			
			dpm.cacheRows("PhoneServicePrepared", "false");
			dpm.cacheRows("PhoneServiceRunning", "true");
		}	
	}


	public void stopRecording() {
		
		boolean hasStarted;
		
		try {
			hasStarted = Boolean.parseBoolean(
					dpm.retrieveCachedRows("PhoneServiceRunning"));
		}
		catch (Exception e) {
			hasStarted = false;
		}
		
		if(hasStarted) {
			Intent audioService = new Intent(getContext(), PhoneRecorderReceiver.class);
			
			if(!dpm.isProcessing()) {
	    		// Normal behavior    		
	    		audioService.setAction("android.intent.action.STOP_PHONE_RECORDER");
	    	}    	
	    	else {
	    		audioService.setAction("android.intent.action.SAVE_PHONE_KEPT_DATA");
	    	}
			
			getContext().sendBroadcast(audioService);  
			
			dpm.cacheRows("PhoneServiceRunning", "false");
		}		
	}	    

	public ObservateurTelephone(Context context) {
		super();
		
		Console.setTagName(this.getClass().getSimpleName());
		
		setContext(context);
		
		SetStaticContext.setStaticsContext(context, 0);
		
		dpm = new DataPersistanceManager();
		
		dpm.cacheRows("CAN_RECORD", "1");
	}

	public void feedNumbers(String phonenumber) {
		OUT_NUMBER = phonenumber;
	}

	/*
	 * true = ENTRANT false = SORTANT
	 */

}