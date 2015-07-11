package org.proof.recorder.database.models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import org.proof.recorder.R;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.receivers.holders.PhoneRecordHolder;
import org.proof.recorder.utils.DateUtils;
import org.proof.recorder.utils.Log.Console;
import org.proof.recorder.utils.OsInfo;

public class PhoneRecord {
	
	private static final String NULL = "NULL";
	
	private static Context context = null;
	
	private String phoneAudioFile;
	private String phoneNumber;
	private String directionCall;
	
	private long savedId = -1;
	
	public PhoneRecord() {
		super();
		setPhoneAudioFile(NULL);
		setPhoneNumber(NULL);
		setDirectionCall(NULL);
		setSavedId(-1);
	}
	// OsInfo.getBaseNameWithNoExt(phoneAudioFile)

	/**
	 * 
	 */
	public PhoneRecord(Context context) {
		super();
		setContext(context);
		setPhoneAudioFile(NULL);
		setPhoneNumber(NULL);
		setDirectionCall(NULL);
		setSavedId(-1);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return  phoneAudioFile + ","
				+ phoneNumber + "," + directionCall + "," + savedId;
	}
	
	private ContentValues toValues(String parentId) {
		
		ContentValues values = new ContentValues();	
		
		Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
				context, phoneNumber);
		
		String timeStamp = OsInfo.getBaseNameWithNoExt(phoneAudioFile);
		String creationTime = DateUtils.formatTime(timeStamp);
		
		if(parentId != null) {				
			values.put(ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID, parentId);
			values.put(ProofDataBase.COLUMNVOICE_TITLE, getContext().getString(R.string.default_note_title));
			values.put(ProofDataBase.COLUMNVOICE_NOTE, getContext().getString(R.string.default_note_text));
			values.put(ProofDataBase.COLUMN_DATE_LAST_MODIF, creationTime);
			
			Console.print_debug("Note last insert id :" + parentId);
			Console.print_debug("Note :" + "Insérer une note");
			Console.print_debug("Note :" + "Aucune note pour cet appel");
			Console.print_debug("Note :" + creationTime);
		}
		else {
			String mSize = OsInfo.getFileSize(phoneAudioFile);
			values.put(ProofDataBase.COLUMN_TELEPHONE, mContact.getPhoneNumber());
			values.put(ProofDataBase.COLUMN_CONTRACT_ID, mContact.getContractId());
			
			values.put(ProofDataBase.COLUMN_SENS, directionCall);			
			values.put(ProofDataBase.COLUMN_FILE, phoneAudioFile);			
			
			values.put(ProofDataBase.COLUMN_TIMESTAMP, timeStamp);
			values.put(ProofDataBase.COLUMN_HTIME, creationTime);
			
			values.put(ProofDataBase.COLUMN_TAILLE, mSize);
			
			Console.print_debug(mContact.getPhoneNumber());
			Console.print_debug(phoneAudioFile);
			Console.print_debug(timeStamp);
			Console.print_debug(directionCall);
			Console.print_debug(mSize);
			Console.print_debug(creationTime);
			Console.print_debug(ProofDataBase.COLUMN_CONTRACT_ID + ": "
								+ mContact.getContractId());
		}
		
		return values;
	}

	@SuppressWarnings("UnusedAssignment")
	public void save() {
		
		boolean saved = false;
		
		if(!toBeInserted())
			return;
		
		Uri rowId = getResolver().insert(PhoneRecordHolder.INSERT_VOICE_URI, toValues(null));
		setSavedId(Long.parseLong(rowId.toString()));
		if(getSavedId() != -1) {
			getResolver().insert(PhoneRecordHolder.INSERT_VNOTE_URI, toValues(rowId.toString()));
		}

	}
	
	public boolean toBeInserted() {
		
		return hasPhoneAudioFile() && 
			   hasPhoneNumber() && 
			   hasDirectionCall();				
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
		PhoneRecord.context = context;
	}

	/**
	 * @return the phoneAudioFile
	 */
	public String getPhoneAudioFile() {
		return phoneAudioFile;
	}
	
	/**
	 * @return if the phoneAudioFile is different from const NULL, true else false.
	 */
	private boolean hasPhoneAudioFile() {
		return !phoneAudioFile.equals(NULL);
	}

	/**
	 * @param phoneAudioFile the phoneAudioFile to set
	 */
	public void setPhoneAudioFile(String phoneAudioFile) {
		
		this.phoneAudioFile = phoneAudioFile;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	/**
	 * @return if the phoneNumber is different from const NULL, true else false.
	 */
	private boolean hasPhoneNumber() {
		return !phoneNumber.equals(NULL);
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the directionCall
	 */
	public String getDirectionCall() {
		return directionCall;
	}
	
	/**
	 * @return if the directionCall is different from const NULL, true else false.
	 */
	private boolean hasDirectionCall() {
		return !directionCall.equals(NULL);
	}

	/**
	 */
	public void setDirectionCall(String direction) {
		this.directionCall = direction;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((directionCall == null) ? 0 : directionCall.hashCode());
		result = prime * result
				+ ((phoneAudioFile == null) ? 0 : phoneAudioFile.hashCode());
		result = prime * result
				+ ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result + (int) (savedId ^ (savedId >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PhoneRecord))
			return false;
		PhoneRecord other = (PhoneRecord) obj;
		if (directionCall == null) {
			if (other.directionCall != null)
				return false;
		} else if (!directionCall.equals(other.directionCall))
			return false;
		if (phoneAudioFile == null) {
			if (other.phoneAudioFile != null)
				return false;
		} else if (!phoneAudioFile.equals(other.phoneAudioFile))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		return savedId == other.savedId;
	}
}
