package org.proof.recorder.receivers.holders;

import android.content.Context;
import android.net.Uri;

import org.proof.recorder.database.models.PhoneRecord;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.service.DataPersistanceManager;

public class PhoneRecordHolder {

	public static final Uri INSERT_VOICE_URI = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "records");

	public static final Uri INSERT_VNOTE_URI = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "notes");
	
	private PhoneRecord currentRecord = null;
	
	private DataPersistanceManager dpm = null;
	
	private static Context context = null;
	/**
	 * 
	 */
	public PhoneRecordHolder(Context context, DataPersistanceManager dpm) {
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
		PhoneRecordHolder.context = context;
	}

	/**
	 * @return the currentRecord
	 */
	public PhoneRecord getCurrentRecord() {
		return currentRecord;
	}

	/**
	 * @param currentRecord the currentRecord to set
	 */
	public void setCurrentRecord(PhoneRecord currentRecord) {
		this.currentRecord = currentRecord;
	}
	
	private void fillIn() {
		PhoneRecord record = new PhoneRecord(getContext());
		setCurrentRecord(record);
		
		long id;
		
		try {
			id = Long.parseLong(dpm.retrieveCachedRows("PhoneSavedId"));
		}
		catch (Exception e) {
			id = -1;
		}
		
		record.setSavedId(id);
		
		record.setPhoneAudioFile(
				dpm.retrieveCachedRows("PhoneAudioFile") == null ? "NULL" : dpm.retrieveCachedRows("PhoneAudioFile"));
		
		record.setPhoneNumber(
				dpm.retrieveCachedRows("phoneNumber") == null ? "NULL" : dpm.retrieveCachedRows("phoneNumber"));
		
		record.setDirectionCall(
				dpm.retrieveCachedRows("directionCall") == null ? "NULL" : dpm.retrieveCachedRows("directionCall"));
	}
	
	public void save() {		
		dpm.cacheRows("PhoneSavedId", getCurrentRecord().getSavedId() + "");
		dpm.cacheRows("PhoneAudioFile", getCurrentRecord().getPhoneAudioFile());
		dpm.cacheRows("phoneNumber", getCurrentRecord().getPhoneNumber());
		dpm.cacheRows("directionCall", getCurrentRecord().getDirectionCall());
	}
}
