package org.proof.recorder.receivers;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.VoiceRecord;
import org.proof.recorder.fragment.voice.FragmentListVoiceTabs;
import org.proof.recorder.receivers.holders.VoiceRecordHolder;
import org.proof.recorder.service.DataPersistanceManager;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.OsInfo;
import org.proof.recorder.utils.ServiceAudioHelper;
import org.proof.recorder.utils.StaticNotifications;
import org.proof.recorder.utils.Log.Console;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AudioRecorderReceiver extends BroadcastReceiver {
	
	private static final String START_ACTION = "android.intent.action.START_AUDIO_RECORDER";
	private static final String STOP_ACTION = "android.intent.action.STOP_AUDIO_RECORDER";
	private static final String SAVE_ACTION = "android.intent.action.SAVE_AUDIO_RECORDER";
	
	private static final String SAVE_DELAYED_EXTERNAL_ACTION = "android.intent.action.SAVE_DELAYED_EXTERNAL_AUDIO_RECORDER";
	
	private static Context mContext = null;
	private static Intent service = null;
	
	private static int mAudioSource;	
	
	private boolean isEdited;
	
	private DataPersistanceManager dpm = null;
	
	private VoiceRecordHolder holder = null;
	private VoiceRecord record = null;
	
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
		AudioRecorderReceiver.mContext = mContext;
	}
	
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
				getContext().getString(R.string.default_note_title)) |
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
			
		title = getContext().getString(R.string.app_name);
		
		destination = FragmentListVoiceTabs.class;
		
		extraNotification.putBoolean("isNotify", true);
		extraNotification.putLong("voiceId", record.getSavedId());
		
		info = "";
		
		setEdited(record.getAudioTitle());
		
		if(isEdited()) {
			text = record.getAudioTitle() + " - " + getContext().getString(R.string.notifyEndOfVoice);
			extraNotification.putBoolean("hasTitle", true);
		}
		else {
			text = getContext().getString(R.string.notifyEndOfVoice);
			extraNotification.putBoolean("hasTitle", false);
		}		
		
		StaticNotifications.show(getContext(), destination, extraNotification,
				title, info, text, StaticNotifications.ICONS.DEFAULT, true,
				true, 0);
	}
	
	private Bundle prepareExtras() {
		Bundle extras = new Bundle();
		
		if(dpm == null)		
			dpm = new DataPersistanceManager();
		
		String audioFormat = dpm.getAudioFormat();
		String audioFile = record.getAudioFile();
		
		mAudioSource = new ServiceAudioHelper(getContext()).maConfAudio();		
		
		extras.putInt("audioSource", mAudioSource);
		
		if(audioFormat.equals("mp3")) {
			
			extras.putString("FileName", audioFile);
			extras.putInt("mSampleRate", Settings.getMP3Hertz(getContext()));
			extras.putInt("mp3Channel", 1);
			extras.putInt("outBitrate", Settings.getMp3Compression(getContext()));			
		}
		else if(audioFormat.equals("ogg")) {
			
			extras.putString("file", audioFile);
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
		
		if(dpm == null)		
			dpm = new DataPersistanceManager();
		
		String audioFormat = dpm.getAudioFormat();		
		
		if(audioFormat.equalsIgnoreCase("wav")) {
			service.setClass(getContext(), org.proof.recorder.services.ServiceIntentRecorderWav.class);	
			service.putExtra("broadcastClass", 
					SAVE_DELAYED_EXTERNAL_ACTION);
		}
		else if(audioFormat.equalsIgnoreCase("mp3")) {
			service.setAction("org.proofs.recorder.codec.mp3.utils.ServiceIntentRecorderMP3");
			
			if(Settings.getPostEncoding(getContext()) == 1) {
				service.putExtra("broadcastClass", 
						SAVE_DELAYED_EXTERNAL_ACTION);
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
			holder.setCurrentRecord(new VoiceRecord());
		}
		
		holder.save();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		setContext(context);
		Console.setTagName(this.getClass().getSimpleName());
		
		if(dpm == null)		
			dpm = new DataPersistanceManager();	
		
		holder = new VoiceRecordHolder(getContext(), dpm);		
		
		Console.print_debug(intent.getAction());		
		
		if (intent.getAction().equals(START_ACTION))
		{	
			String AudioFormat = Settings.getAudioFormat(context).toLowerCase();
			record = holder.getCurrentRecord();
			record.setAudioFile(OsInfo.newFileName(AudioFormat));
			
			dpm.setProcessing("1");	
			dpm.setAudioFormat(AudioFormat);		
			dpm.save();
			
			holder.save();
			
			prepareService();				
			
			service.putExtra("notificationIntent",
				     "org.proof.recorder.fragment.voice.FragmentVoiceMediaRecorder");
			
			service.putExtra("notificationPkg",
				     "org.proof.recorder");
			
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
				AlertDialogHelper.openProgressDialog(R.string.encoding_data);
			}
			else {
				AlertDialogHelper.openVoiceEditDialog();
			}
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
			boolean invalid = Boolean.parseBoolean(
					dpm.retrieveCachedRows("INVALID_STATE"));
			
			if(!invalid) {
				AlertDialogHelper.closeProgressDialog();			
				AlertDialogHelper.openVoiceEditDialog();
			}
			else {
				// If any service recording was enabled by AudioRecorder and still running when call arise.
				// The service will respond to this action. 
				// We need to route the saving action to Appropriated listener (PhoneBroadcastReceiver).
				// then we reset globals "INVALID_STATE".
				
				Intent redirection = new Intent("android.intent.action.SAVE_PHONE_RECORDER");
				context.sendBroadcast(redirection);
				dpm.cacheRows("INVALID_STATE", "false");
			}		
		}
		
		else {
			Console.print_debug("IGNORED (UNKNOWN ACTION): " + intent.getAction());
		}
		
		Console.print_debug(record);
	}
}
