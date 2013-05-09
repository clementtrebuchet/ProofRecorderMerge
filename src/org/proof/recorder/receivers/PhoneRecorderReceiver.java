package org.proof.recorder.receivers;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.PhoneRecord;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.fragment.phone.FragmentListRecordTabs;
import org.proof.recorder.receivers.holders.PhoneRecordHolder;
import org.proof.recorder.service.DataPersistanceManager;
import org.proof.recorder.utils.OsInfo;
import org.proof.recorder.utils.ServiceAudioHelper;
import org.proof.recorder.utils.StaticNotifications;
import org.proof.recorder.utils.Log.Console;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PhoneRecorderReceiver extends BroadcastReceiver {
	
	private static final String START_ACTION = "android.intent.action.START_PHONE_RECORDER";
	private static final String STOP_ACTION = "android.intent.action.STOP_PHONE_RECORDER";
	private static final String SAVE_ACTION = "android.intent.action.SAVE_PHONE_RECORDER";
	
	private static Context mContext = null;
	private static Intent service = null;
	
	private static int mAudioSource;	
	
	private DataPersistanceManager dpm;
	
	private PhoneRecordHolder holder = null;
	private PhoneRecord record = null;
	
	/**
	 * @return the mContext
	 */
	public static Context getContext() {
		return mContext;
	}

	/**
	 * @param mContext the mContext to set
	 */
	public static void setContext(Context mContext) {
		PhoneRecorderReceiver.mContext = mContext;
	}
	
	private void notifyUser() {
		
		String phoneNumber, directionCall;
		
		phoneNumber = record.getPhoneNumber();
		directionCall = record.getDirectionCall();
		
		if(phoneNumber.equals("NULL") && 
		   directionCall.equals("NULL")) {
			return;
		}

		String title, info, text;
		Bundle extraNotification = new Bundle();
		Class<?> destination;

		title = getContext().getString(R.string.app_name);
			
			Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
					getContext(), phoneNumber);
			
			extraNotification.putBoolean("isNotify", true);
			extraNotification.putString("Sense", directionCall);
			
			extraNotification.putLong("RecordId", record.getSavedId());
			
			String mFrom;
			if (!mContact.getContractId().equalsIgnoreCase("null")) {
				mFrom = mContact.getContactName();
			} else {
				mFrom = phoneNumber;
			}

			destination = FragmentListRecordTabs.class;			
			info = "";
			text = getContext().getString(R.string.notifyEndOfCallOne) + " "
					+ mFrom + " "
					+ getContext().getString(R.string.notifyEndOfCallTwo);
			
		StaticNotifications.show(getContext(), destination, extraNotification,
				title, info, text, StaticNotifications.ICONS.DEFAULT, true,
				true, 0);
	}
	
	private Bundle prepareExtras() {
		Bundle extras = new Bundle();
		
		String audioFormat = dpm.getAudioFormat();
		String audioFile = record.getPhoneAudioFile();
		
		mAudioSource = new ServiceAudioHelper(getContext()).maConfAudio();
		
		extras.putString("FileName", audioFile);
		extras.putInt("audioSource", mAudioSource);
		
		if(audioFormat == "mp3") {
			extras.putInt("mSampleRate", Settings.getMP3Hertz(getContext()));
			extras.putInt("mp3Channel", 1);
			extras.putInt("outBitrate", Settings.getMp3Compression(getContext()));			
		}
		else if(audioFormat == "ogg") {
			extras.putInt("sampleRate", Settings.getMP3Hertz(getContext()));
			extras.putInt("channel", 1);
			extras.putFloat("quality", Settings.getOGGQual(getContext()));			
		}
		else {
			
		}			
		
		return extras;
	}
	
	private void prepareService() {
		
		service = new Intent();
		
		String audioFormat = dpm.getAudioFormat();		
		
		if(audioFormat.equalsIgnoreCase("wav")) {
			service.setClass(getContext(), org.proof.recorder.services.ServiceIntentRecorderWav.class);	
			service.putExtra("broadcastClass", 
					SAVE_ACTION);
		}
		else if(audioFormat.equalsIgnoreCase("mp3")) {
			service.setAction("org.proofs.recorder.codec.mp3.utils.ServiceIntentRecorderMP3");
			
			if(Settings.getPostEncoding(getContext()) == 1) {
				service.putExtra("broadcastClass", 
						SAVE_ACTION);
				service.putExtra("postEncode", 1);
			}
		}
		else if(audioFormat.equalsIgnoreCase("ogg")) {
			service.setAction("org.proofs.recorder.codec.ogg.utils.ServiceIntentRecorderOgg");			
		}
		else {
			service.setClass(getContext(), org.proof.recorder.services.ServiceIntentRecorder3gp.class);
		}		
	}
	
	private void onSave() {
		
		if(record.toBeInserted()) {
			record.save();
			notifyUser();
			holder.setCurrentRecord(new PhoneRecord());
		}
		
		holder.save();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		setContext(context);
		Console.setTagName(this.getClass().getSimpleName());
		
		dpm = new DataPersistanceManager();	
		
		holder = new PhoneRecordHolder(getContext(), dpm);		
		
		Console.print_debug(intent.getAction());		
		
		if (intent.getAction().equals(START_ACTION))
		{	
			String AudioFormat = Settings.getAudioFormat(context).toLowerCase();
			record = holder.getCurrentRecord();
			
			record.setPhoneAudioFile(OsInfo.newFileName(AudioFormat));
			record.setDirectionCall(intent.getStringExtra("directionCall"));
			record.setPhoneNumber(intent.getStringExtra("phoneNumber"));
			
			dpm.setProcessing("1");	
			dpm.setAudioFormat(AudioFormat);		
			dpm.save();
			
			holder.save();
			
			prepareService();				
			
			service.putExtra("notificationIntent",
				     "org.proof.recorder.ProofRecorderActivity");
			
			service.putExtras(prepareExtras());
			
			context.startService(service);
		}
		
		else if (intent.getAction().equals(STOP_ACTION))
		{
			prepareService();			
			context.stopService(service);
			
			dpm.setProcessing("0");
			dpm.save();
			
			if(dpm.getAudioFormat().equalsIgnoreCase("wav") |
			  (dpm.getAudioFormat().equalsIgnoreCase("mp3") &&
			  Settings.getPostEncoding(getContext()) == 1)) {
				
			}
			else {
				Intent service = new Intent(SAVE_ACTION);
				context.sendBroadcast(service);
			}
		}
		
		else if (intent.getAction().equals(SAVE_ACTION))
		{			
			record = holder.getCurrentRecord();	
			onSave();
		}
		
		else {
			Console.print_debug("IGNORED (UNKNOWN ACTION): " + intent.getAction());
		}
		
		Console.print_debug(record);
	}
}
