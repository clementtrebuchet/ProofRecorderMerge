package org.proof.recorder.receivers.holders;

import android.content.Context;
import android.net.Uri;

import org.proof.recorder.database.models.VoiceRecord;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.service.DataPersistanceManager;

public class VoiceRecordHolder {

	public static final Uri INSERT_VOICE_URI = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "voices");

	public static final Uri INSERT_VNOTE_URI = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "vnotes");
	
	private VoiceRecord currentRecord = null;
	
	private DataPersistanceManager dpm = null;
	
	private static Context context = null;
	/**
	 * 
	 */
	public VoiceRecordHolder(Context context, DataPersistanceManager dpm) {
		super();
		
		this.dpm = dpm;
		setContext(context);
		fillIn();
	}
	
	/**
	 * @return the resolver
	 */
	private static Context getContext() {
		return context;
	}

	/**
	 */
	private static void setContext(Context context) {
		VoiceRecordHolder.context = context;
	}

	/**
	 * @return the currentRecord
	 */
	public VoiceRecord getCurrentRecord() {
		return currentRecord;
	}

	/**
	 * @param currentRecord the currentRecord to set
	 */
	public void setCurrentRecord(VoiceRecord currentRecord) {
		this.currentRecord = currentRecord;
	}
	
	private void fillIn() {
		VoiceRecord record = new VoiceRecord(getContext());
		
		setCurrentRecord(record);
		
		long id;
		
		try {
			id = Long.parseLong(dpm.retrieveCachedRows("SavedId"));
		}
		catch (Exception e) {
			id = -1;
		}
		
		record.setSavedId(id);
		
		record.setAudioFile(
				dpm.retrieveCachedRows("AudioFile") == null ? "NULL" : dpm.retrieveCachedRows("AudioFile"));
		
		record.setAudioTitle(
				dpm.retrieveCachedRows("AudioTitle") == null ? "NULL" : dpm.retrieveCachedRows("AudioTitle"));
	}
	
	public void save() {		
		dpm.cacheRows("SavedId", getCurrentRecord().getSavedId() + "");
		dpm.cacheRows("AudioFile", getCurrentRecord().getAudioFile());
		dpm.cacheRows("AudioTitle", getCurrentRecord().getAudioTitle());
	}
}
