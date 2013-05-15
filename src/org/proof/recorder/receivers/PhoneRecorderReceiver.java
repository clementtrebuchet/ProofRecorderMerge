package org.proof.recorder.receivers;

import java.util.Locale;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.broadcast.ProofBroadcastReceiver;
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
import org.proofs.recorder.codec.mp3.utils.IServiceIntentRecorderMP3;
import org.proofs.recorder.codec.mp3.utils.IServiceIntentRecorderMP3Cx;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class PhoneRecorderReceiver extends ProofBroadcastReceiver {
	
	private static final String START_ACTION = "android.intent.action.START_PHONE_RECORDER";
	private static final String STOP_ACTION = "android.intent.action.STOP_PHONE_RECORDER";
	private static final String SAVE_ACTION = "android.intent.action.SAVE_PHONE_RECORDER";
	
	private static final String KEEP_DATA_TRACK = "android.intent.action.KEEP_PHONE_DATA_TRACK";
	private static final String SAVE_KEPT_DATA = "android.intent.action.SAVE_PHONE_KEPT_DATA";

	private static Intent service = null;
	
	private static int mAudioSource;	
	
	private DataPersistanceManager dpm;
	IServiceIntentRecorderMP3Cx connection;
	IServiceIntentRecorderMP3 mService = null;
	private PhoneRecordHolder holder = null;
	private PhoneRecord record = null;
	
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

		title = getInternalContext().getString(R.string.app_name);
			
			Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
					getInternalContext(), phoneNumber);
			
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
			text = getInternalContext().getString(R.string.notifyEndOfCallOne) + " "
					+ mFrom + " "
					+ getInternalContext().getString(R.string.notifyEndOfCallTwo);
			
		StaticNotifications.show(getInternalContext(), destination, extraNotification,
				title, info, text, StaticNotifications.ICONS.DEFAULT, true,
				true, 0);
	}
	
	private Bundle prepareExtras() {
		Bundle extras = new Bundle();
		
		String audioFormat = dpm.getAudioFormat();
		String audioFile = record.getPhoneAudioFile();
		
		mAudioSource = new ServiceAudioHelper(getInternalContext()).maConfAudio();		
		
		extras.putInt("audioSource", mAudioSource);
		
		if(audioFormat.equals("mp3")) {
			
			extras.putString("FileName", audioFile);
			extras.putInt("mSampleRate", Settings.getMP3Hertz());
			extras.putInt("mp3Channel", 1);
			extras.putInt("outBitrate", Settings.getMp3Compression());			
		}
		else if(audioFormat.equals("ogg")) {
			
			extras.putString("file", audioFile);
			extras.putInt("sampleRate", Settings.getMP3Hertz());
			extras.putInt("channel", 1);
			extras.putFloat("quality", Settings.getOGGQual());			
		}
		else {
			extras.putString("FileName", audioFile);
		}			
		
		return extras;
	}
	
	private void prepareService() {
		
		service = new Intent();
		
		String audioFormat = dpm.getAudioFormat();		
		
		if(audioFormat.equalsIgnoreCase("wav")) {
			
			service.setClass(getInternalContext(), 
					org.proof.recorder.services.ServiceIntentRecorderWav.class);	
			
			service.putExtra("broadcastClass", 
					SAVE_ACTION);
		}
		else if(audioFormat.equalsIgnoreCase("mp3")) {
			
			service.setClass(getInternalContext(), 
					org.proof.recorder.services.MP3Middleware.class);
				
			
			if(Settings.getPostEncoding() == 1) {
				service.putExtra("broadcastClass", 
						SAVE_ACTION);
				service.putExtra("postEncode", 1);
			}
			else {
				service.putExtra("broadcastClass", "");
				service.putExtra("postEncode", 0);
			}
		}
		else if(audioFormat.equalsIgnoreCase("ogg")) {
			
			service.setClass(getInternalContext(), 
					org.proof.recorder.services.OGGMiddleware.class);		
		}
		else {
			service.setClass(getInternalContext(), 
					org.proof.recorder.services.ServiceIntentRecorder3gp.class);
		}		
	}
	
	private void onSave() {
		
		record = holder.getCurrentRecord();
		
		if(record.toBeInserted()) {
			record.save();
			notifyUser();
			holder.setCurrentRecord(new PhoneRecord());
		}
		
		holder.save();
	}
	
	private void startService() {
		
			prepareService();				
			
			service.putExtras(prepareExtras());
			
			getInternalContext().startService(service);	
		
		
		
	}
	
	
	private void stopService() {
		
			prepareService();			
			
			getInternalContext().stopService(service);
		
		
	}
	
	private void keepDataTrack(Intent intent, String AudioFormat) {		
		keepDataTrack(intent, AudioFormat, OsInfo.newFileName(AudioFormat));
	}
	
	private void keepDataTrack(Intent intent, String AudioFormat, String fileName) {		
		
		record = holder.getCurrentRecord();
		
		record.setPhoneAudioFile(fileName);
		record.setDirectionCall(intent.getStringExtra("directionCall"));
		record.setPhoneNumber(intent.getStringExtra("phoneNumber"));
		
		dpm.setAudioFormat(AudioFormat);
		dpm.save();
		
		holder.save();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		super.onReceive(context, intent);
		
		boolean mp3BadVersion, oggBadVersion;
		
		dpm = new DataPersistanceManager();	
		
		holder = new PhoneRecordHolder(context, dpm);
		
		String AudioFormat = Settings.getAudioFormat().toLowerCase(
				Locale.getDefault());		
		
		mp3BadVersion = Boolean.parseBoolean(
				dpm.retrieveCachedRows("MP3_BAD_VERSION"));
		
		oggBadVersion = Boolean.parseBoolean(
				dpm.retrieveCachedRows("OGG_BAD_VERSION"));
		
		if(AudioFormat.equals("mp3") && mp3BadVersion) {
			Toast.makeText(context, context.getString(R.string.bad_mp3_version), Toast.LENGTH_LONG).show();	
			return;
		}
		
		if(AudioFormat.equals("ogg") && oggBadVersion) {
			Toast.makeText(context, context.getString(R.string.bad_ogg_version), Toast.LENGTH_LONG).show();
			return;
		}		
		
		if (intent.getAction().equals(START_ACTION))
		{				
			keepDataTrack(intent, AudioFormat);
			startService();
		}
		
		else if (intent.getAction().equals(KEEP_DATA_TRACK)) {
			
			// getting current AudioFile recorded from MediaRecorder not call! 
			String currentAudioFile = dpm.retrieveCachedRows("AudioFile"); 			
			
			keepDataTrack(intent, AudioFormat, currentAudioFile);
			
			dpm.cacheRows("INVALID_STATE", "true");
			
			saveVoiceDpmOnStop();
		}
		
		else if (intent.getAction().equals(SAVE_KEPT_DATA)) {			
			stopService();						
			handleStop();
		}
		
		else if (intent.getAction().equals(STOP_ACTION))
		{			
			stopService();			
			handleStop();
		}
		
		else if (intent.getAction().equals(SAVE_ACTION))
		{						
			onSave();
		}
		
		else {
			Console.print_debug(
					"*********** IGNORED (UNKNOWN ACTION) ***********");
		}
		
		Console.print_debug(intent.getAction());
		Console.print_debug(record);
	}
	
	private void saveVoiceDpmOnStop() {
		dpm.setProcessing("0");
		dpm.save();
	}

	private void handleStop() {
		
		if(dpm.getAudioFormat().equalsIgnoreCase("wav") |
		  (dpm.getAudioFormat().equalsIgnoreCase("mp3") &&
		  Settings.getPostEncoding() == 1)) {
			
		}
		else {
			Intent service = new Intent(SAVE_ACTION);
			getInternalContext().sendBroadcast(service);
		}
	}
	
}
