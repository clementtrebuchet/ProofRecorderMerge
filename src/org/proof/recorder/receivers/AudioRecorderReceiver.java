package org.proof.recorder.receivers;

import java.util.Locale;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.broadcast.ProofBroadcastReceiver;
import org.proof.recorder.database.models.VoiceRecord;
import org.proof.recorder.fragment.voice.FragmentListVoiceTabs;
import org.proof.recorder.receivers.holders.VoiceRecordHolder;
import org.proof.recorder.service.DataPersistanceManager;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.OsInfo;
import org.proof.recorder.utils.ServiceAudioHelper;
import org.proof.recorder.utils.StaticNotifications;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AudioRecorderReceiver extends ProofBroadcastReceiver {
	
	private static final String START_ACTION = "android.intent.action.START_AUDIO_RECORDER";
	private static final String STOP_ACTION = "android.intent.action.STOP_AUDIO_RECORDER";
	private static final String SAVE_ACTION = "android.intent.action.SAVE_AUDIO_RECORDER";
	
	private static final String SAVE_DELAYED_EXTERNAL_ACTION = "android.intent.action.SAVE_DELAYED_EXTERNAL_AUDIO_RECORDER";
	
	private static Intent service = null;
	
	private static int mAudioSource;	
	
	private boolean isEdited;
	
	private DataPersistanceManager dpm = null;
	
	private VoiceRecordHolder holder = null;
	private VoiceRecord record = null;
	
	/**
	 * @return the isEdited
	 */
	public boolean isEdited() {
		return isEdited;
	}

	/**
	 * @param isEdited the isEdited to set
	 */
	public void setEdited(String titleNote) {
		if(titleNote.equalsIgnoreCase(
				getInternalContext().getString(R.string.default_note_title)) |
				titleNote == null) {
			this.isEdited = false;
		}
		else {
			this.isEdited = true;
		}
	}
	
	private void notifyUser() {
		String title, info, text;
		Bundle extraNotification = new Bundle();
		Class<?> destination;
			
		title = getInternalContext().getString(R.string.app_name);
		
		destination = FragmentListVoiceTabs.class;
		
		extraNotification.putBoolean("isNotify", true);
		extraNotification.putLong("voiceId", record.getSavedId());
		
		info = "";
		
		setEdited(record.getAudioTitle());
		
		if(isEdited()) {
			text = record.getAudioTitle() + " - " + getInternalContext().getString(R.string.notifyEndOfVoice);
			extraNotification.putBoolean("hasTitle", true);
		}
		else {
			text = getInternalContext().getString(R.string.notifyEndOfVoice);
			extraNotification.putBoolean("hasTitle", false);
		}		
		
		StaticNotifications.show(getInternalContext(), destination, extraNotification,
				title, info, text, StaticNotifications.ICONS.DEFAULT, true,
				true, 0);
	}
	
	private Bundle prepareExtras() {
		Bundle extras = new Bundle();
	
		dpm = new DataPersistanceManager();
		
		String audioFormat = dpm.getAudioFormat();
		String audioFile = record.getAudioFile();
		
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
	
		dpm = new DataPersistanceManager();
		
		String audioFormat = dpm.getAudioFormat();		
		
		if(audioFormat.equalsIgnoreCase("wav")) {
			service.setClass(getInternalContext(), org.proof.recorder.services.ServiceIntentRecorderWav.class);	
			service.putExtra("broadcastClass", 
					SAVE_DELAYED_EXTERNAL_ACTION);
		}
		else if(audioFormat.equalsIgnoreCase("mp3")) {
			service.setClass(getInternalContext(), 
					org.proof.recorder.services.MP3Middleware.class);
			
			if(Settings.getPostEncoding() == 1) {
				service.putExtra("broadcastClass", 
						SAVE_DELAYED_EXTERNAL_ACTION);
				service.putExtra("postEncode", 1);
			}
		}
		else if(audioFormat.equalsIgnoreCase("ogg")) {
			service.setAction("org.proofs.recorder.codec.ogg.utils.ServiceIntentRecorderOgg");			
		}
		else {
			service.setClass(getInternalContext(), org.proof.recorder.services.ServiceIntentRecorder3gp.class);
		}		
	}
	
	private void onSave() {
		
		if(record.toBeInserted()) {
			record.save();
			notifyUser();
			holder.setCurrentRecord(new VoiceRecord());
		}
		
		holder.save();
	}
	
	private void saveCurrentData(String AudioFormat) {
		
		record = holder.getCurrentRecord();
		record.setAudioFile(OsInfo.newFileName(AudioFormat));
		
		dpm.setProcessing("1");	
		dpm.setAudioFormat(AudioFormat);		
		dpm.save();
		
		holder.save();
	}
	
	private void resetCurrentData() {		
		dpm.setProcessing("0");
		dpm.save();		
	}
	
	private void startService() {
		
		prepareService();				
		
		service.putExtras(prepareExtras());
		
		service.putExtra("notificationIntent",
			     "org.proof.recorder.fragment.voice.FragmentVoiceMediaRecorder");
		
		service.putExtra("notificationPkg",
			     "org.proof.recorder");
		
		getInternalContext().startService(service);	
	}
	
	private void handleStop(String AudioFormat) {
		if(AudioFormat.equalsIgnoreCase("wav") |
				  (AudioFormat.equalsIgnoreCase("mp3") &&
				  Settings.getPostEncoding() == 1)) {
					
					AlertDialogHelper.openProgressDialog(R.string.encoding_data);
				}
				else {
					AlertDialogHelper.openVoiceEditDialog();
				}
	}
	
	private void handleDelayedSave() {
		if(!isValid()) {
			AlertDialogHelper.closeProgressDialog();			
			AlertDialogHelper.openVoiceEditDialog();
		}
		else {
			// If any service recording was enabled by AudioRecorder and still running when call arise.
			// The service will respond to this action. 
			// We need to route the saving action to Appropriated listener (PhoneBroadcastReceiver).
			// then we reset globals "INVALID_STATE".
			
			Intent redirection = new Intent("android.intent.action.SAVE_PHONE_RECORDER");
			getInternalContext().sendBroadcast(redirection);
			
			dpm.cacheRows("INVALID_STATE", "false");
		}
	}
	
	private void stopService() {
		
		prepareService();			
		
		getInternalContext().stopService(service);
	}
	
	private boolean isValid() {
		return Boolean.parseBoolean(
				dpm.retrieveCachedRows("INVALID_STATE"));
	}

	@Override
	public void onReceive(Context context, Intent intent) {	
		
		super.onReceive(context, intent);		
		
		boolean mp3BadVersion, oggBadVersion;
		
		dpm = new DataPersistanceManager();
		
		String AudioFormat = Settings.getAudioFormat().toLowerCase(
				Locale.getDefault());
		
		mp3BadVersion = Boolean.parseBoolean(
				dpm.retrieveCachedRows("MP3_BAD_VERSION"));
		
		oggBadVersion = Boolean.parseBoolean(
				dpm.retrieveCachedRows("OGG_BAD_VERSION"));
		
		if(AudioFormat.equals("mp3") && mp3BadVersion) {
			Toast.makeText(context, context.getString(
					R.string.bad_mp3_version), Toast.LENGTH_LONG).show();	
			
			return;
		}
		
		if(AudioFormat.equals("ogg") && oggBadVersion) {
			Toast.makeText(context, context.getString(
					R.string.bad_ogg_version), Toast.LENGTH_LONG).show();
			
			return;
		}
		
		holder = new VoiceRecordHolder(context, dpm);				
		
		if (intent.getAction().equals(START_ACTION))
		{			
			saveCurrentData(AudioFormat);
			
			startService();
		}
		
		else if (intent.getAction().equals(STOP_ACTION))
		{
			stopService();
			
			resetCurrentData();
			
			handleStop(AudioFormat);
		}
		
		else if (intent.getAction().equals(SAVE_ACTION))
		{	
			record = holder.getCurrentRecord();			
			record.setAudioTitle(
					intent.getExtras().getString("audioTitle"));
			
			onSave();
		}
		
		else if (intent.getAction().equals(SAVE_DELAYED_EXTERNAL_ACTION))
		{			
			handleDelayedSave();
		}
		
		else {
			Console.print_debug(
					"*********** IGNORED (UNKNOWN ACTION) ***********");
		}
		
		Console.print_debug(intent.getAction());		
		Console.print_debug(record);
	}
}
