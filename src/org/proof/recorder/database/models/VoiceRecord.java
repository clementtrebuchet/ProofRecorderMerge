package org.proof.recorder.database.models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import org.proof.recorder.R;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.receivers.holders.VoiceRecordHolder;
import org.proof.recorder.utils.DateUtils;
import org.proof.recorder.utils.OsInfo;

@SuppressWarnings("unused")
public class VoiceRecord {
	
	private static final String NULL = "NULL";
	
	private static Context context = null;
	
	private String audioFile;
	private String audioTitle;
	
	private String timeStamp;
	
	private long savedId = -1;
	
	public VoiceRecord() {
		super();
		setAudioFile(NULL);
		setAudioTitle(NULL);
		setTimeStamp(NULL);
		setSavedId(-1);
	}

	/**
	 * 
	 */
	public VoiceRecord(Context context) {
		super();
		setContext(context);
		setAudioFile(NULL);
		setAudioTitle(NULL);
		setTimeStamp(NULL);
		setSavedId(-1);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return  audioFile + ","
				+ audioTitle + "," + timeStamp + "," + savedId;
	}
	
	private ContentValues toValues(String parentId) {
		
		ContentValues values = new ContentValues();		
		String creationTime = DateUtils.formatTime(timeStamp);
		
		if(parentId != null) {			
			values.put(ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID, parentId);
			values.put(ProofDataBase.COLUMNVOICE_TITLE, audioTitle);
			values.put(ProofDataBase.COLUMNVOICE_NOTE, getContext().getString(R.string.default_note_text));
			values.put(ProofDataBase.COLUMNVOICE_DATE_CREATION, creationTime);
		}
		else {
			values.put(ProofDataBase.COLUMN_VOICE_FILE, audioFile);
			values.put(ProofDataBase.COLUMN_VOICE_TIMESTAMP, timeStamp);			
			values.put(ProofDataBase.COLUMN_VOICE_HTIME, creationTime);
			values.put(ProofDataBase.COLUMN_VOICE_TAILLE, OsInfo.getFileSize(audioFile));
		}	
		
		return values;
	}

	public void save() {
		Uri rowId = getResolver().insert(VoiceRecordHolder.INSERT_VOICE_URI, toValues(null));
		setSavedId(Long.parseLong(rowId.toString()));
		if(getSavedId() != -1) {
			getResolver().insert(VoiceRecordHolder.INSERT_VNOTE_URI, toValues(rowId.toString()));

		}
	}
	
	public boolean toBeInserted() {
		
		return hasAudioFile() && 
			   hasAudioTitle() && 
			   hasTimeStamp();				
	}
	
	/**
	 * @return the savedId
	 */
	public long getSavedId() {
		return savedId;
	}

	/**
	 * @param savedId the savedId to set
	 */
	public void setSavedId(long savedId) {
		this.savedId = savedId;
	}

	/**
	 * @return the resolver
	 */
	private static ContentResolver getResolver() {
		return context.getContentResolver();
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
		VoiceRecord.context = context;
	}

	/**
	 * @return the audioFile
	 */
	public String getAudioFile() {
		return audioFile;
	}
	
	/**
	 * @return if the audioFile is different from const NULL, true else false.
	 */
	private boolean hasAudioFile() {
		return !audioFile.equals(NULL);
	}

	/**
	 * @param audioFile the audioFile to set
	 */
	public void setAudioFile(String audioFile) {
		
		this.audioFile = audioFile;		
		this.setTimeStamp(OsInfo.getBaseNameWithNoExt(audioFile));
	}

	/**
	 * @return the audioTitle
	 */
	public String getAudioTitle() {
		return audioTitle;
	}
	
	/**
	 * @return if the audioTitle is different from const NULL, true else false.
	 */
	private boolean hasAudioTitle() {
		return !audioTitle.equals(NULL);
	}

	/**
	 * @param audioTitle the audioTitle to set
	 */
	public void setAudioTitle(String audioTitle) {
		this.audioTitle = audioTitle;
	}

	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @return if the timeStamp is different from const NULL, true else false.
	 */
	private boolean hasTimeStamp() {
		return !timeStamp.equals(NULL);
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	private void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}	
}
