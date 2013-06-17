package org.proof.recorder.database.models;

import java.io.Serializable;

import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.DateUtils;
import org.proof.recorder.utils.OsInfo;
import org.proof.recorder.utils.Log.Console;

import android.content.ContentResolver;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;

public class Record implements DataLayerInterface, Serializable, Cloneable {

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		Object o = null;
		try {
			// On récupère l'instance à renvoyer par l'appel de la 
			// méthode super.clone()
			o = super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implémentons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		// on renvoie le clone
		return o;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7792014800935229041L;

	// Uris 

	private static Uri mUriRecords = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "records");

/*	private static Uri mUriRecordById = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "record_by_id/");*/
	
	private static boolean hasDataLayer = false;
	private static ContentResolver mResolver;

	// Private Attributes

	private String mId;
	private String mFilePath;
	private String mPhone;	
	private String mHtime;
	private String mTimeStamp;
	private String mSense;
	private String mAndroidId;
	private String mSize;
	private String mSongTime;
	private String Format;
	
	private DataPhoneNumber mDataNumber;
	
	private Contact mContact;
	
	private boolean isChecked = false;

	/**
	 * @return the isChecked
	 */
	public boolean isChecked() {
		return isChecked;
	}

	/**
	 * @param isChecked the isChecked to set
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	public void toggle() {
		setChecked(!this.isChecked);		
	}
	
	// Logging wrapping
	public void toConsole() {
		Console.print_debug(this.toString());
	}

	@Override
	public String toString() {
		return "Record [mId=" + mId + ", mFilePath=" + mFilePath
				+ ", mPhone=" + mPhone + ", mHtime=" + mHtime
				+ ", mTimeStamp=" + mTimeStamp + ", mSense=" + mSense
				+ ", mAndroidId=" + mAndroidId + ", mSize=" + mSize
				+ ", mDataNumber=" + mDataNumber + ", mContact=" + mContact
				+ ", hasDataLayer=" + hasDataLayer + "]";
	}

	public String getmTimeStamp() {
		return mTimeStamp;
	}

	public void setmTimeStamp(String mTimeStamp) {
		this.mTimeStamp = mTimeStamp;
	}

	public String getmSize() {
		return mSize;
	}

	public void setmSize(String mSize) {
		this.mSize = mSize;
	}

	public static Uri getmUriRecords() {
		return mUriRecords;
	}

	public static boolean hasDataLayer() {
		return hasDataLayer;
	}

	public static void setmUriRecords(Uri mUriRecords) {
		Record.mUriRecords = mUriRecords;
	}

	public static void setHasDataLayer(boolean hasDataLayer) {
		Record.hasDataLayer = hasDataLayer;
	}

	public Record () {
		this.initialize("-1", "null");
	}
	
	public Record (String size, String fileName, String phone, String sense) {
		this.initialize("-1", fileName, phone);
		this.setmSense(sense);
		this.setmSize(size);
	}
	/**
	 * sorry for this bad thing
	 * 
	 * 
	 */
	public String getmSongTime() {
		return mSongTime;
	}

	public void setmSongTime(String mSongTime) {
		this.mSongTime = mSongTime;
		
	}

	public Record (String id, String fileName) {
		this.initialize(id, fileName);
	}
	
	public Record (String id, String fileName, String phone) {
		this.initialize(id, fileName, phone);
	}
	
	public Record (String id, 
				   String fileName, 
				   String phone, 
				   String sense, 
				   String htime, 
				   String mAndroidId) {
		
		this.initialize(id, fileName, phone);
		this.setmSense(sense);
		this.setmHtime(htime);
		this.setmAndroidId(mAndroidId);
		
	}
	
	public boolean isIncomingCall() {
		return this.getmSense().equalsIgnoreCase("e");
	}

	private void initialize(String id, String fileName) {
		this.setmContact(new Contact());
		this.mDataNumber = new DataPhoneNumber();
		this.setmId(id);
		this.setmFilePath(fileName);
	}
	
	private void initialize(String id, String fileName, String phone) {
		
		this.setmContact(new Contact(phone));
		this.mDataNumber = new DataPhoneNumber(phone);
		this.setmPhone(phone);
		this.setmId(id);
		this.setmFilePath(fileName);
	}

	public String getmHtime() {
		return mHtime;
	}

	public DataPhoneNumber getmDataNumber() {
		return mDataNumber;
	}

	public static ContentResolver getResolver() {
		return mResolver;
	}

	public void setmDataNumber(DataPhoneNumber mDataNumber) {
		this.mDataNumber = mDataNumber;
	}

	public static void setResolver(ContentResolver mResolver) {
		Record.mResolver = mResolver;
		Contact.setResolver(mResolver);
		DataPhoneNumber.setResolver(mResolver);
		setHasDataLayer(true);
	}

	public void setmHtime(String mHtime) {
		this.mHtime = mHtime;
	}

	public String getmSense() {
		return mSense.trim();
	}

	public void setmSense(String mSense) {
		this.mSense = mSense;
	}

	/**
	 * @return the mId
	 */
	public String getmId() {
		return mId.trim();
	}

	/**
	 * @param mId the mId to set
	 */
	public void setmId(String mId) {
		this.mId = mId;
	}

	/**
	 * @return the mFilePath
	 */
	public String getmFilePath() {
		return mFilePath.trim();
	}

	/**
	 * @param mFilePath the mFilePath to set
	 */
	public void setmFilePath(String mFilePath) {
		this.mFilePath = mFilePath;
		this.setmTimeStamp(OsInfo.getBaseNameWithNoExt(mFilePath));
		this.setmHtime(DateUtils.formatTime(this.getmTimeStamp()));
	}

	/**
	 * @return the mPhone
	 */
	public String getmPhone() {
		return mPhone;
	}

	/**
	 * @param mPhone the mPhone to set
	 */
	public void setmPhone(String mPhone) {			
		this.mPhone = PhoneNumberUtils.stripSeparators(mPhone);
		
		if(hasDataLayer()) {
			this.mContact.setPhoneNumber(this.mPhone);
			this.mDataNumber.set_originalNumber(this.mPhone);
		}		
	}

	/**
	 * @return the mAndroidId
	 */
	public String getmAndroidId() {
		if(mAndroidId != null)
			return mAndroidId.trim();
		return "null";
	}

	/**
	 * @param mAndroidId the mAndroidId to set
	 */
	public void setmAndroidId(String mAndroidId) {
		this.mAndroidId = mAndroidId;
	}

	public Contact getmContact() {
		return mContact;
	}

	public void setmContact(Contact mContact) {
		this.mContact = mContact;
	}
	
	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}

	@SuppressWarnings("unused")
	private String getPhoneNumberFK() {
		return this.mDataNumber.get_countryCode() + ";" + 
				this.mDataNumber.get_nationalNumber();
	}

	@Override
	public void fillValues() {
		DataLayerInterface._values.put(ProofDataBase.COLUMN_CONTRACT_ID, this.getmAndroidId());
		DataLayerInterface._values.put(ProofDataBase.COLUMN_FILE, this.getmFilePath());
		DataLayerInterface._values.put(ProofDataBase.COLUMN_TIMESTAMP, this.getmTimeStamp());
		DataLayerInterface._values.put(ProofDataBase.COLUMN_HTIME, this.getmHtime());
		DataLayerInterface._values.put(ProofDataBase.COLUMN_TELEPHONE, this.getmPhone());
		DataLayerInterface._values.put(ProofDataBase.COLUMN_SENS, this.getmSense());
		DataLayerInterface._values.put(ProofDataBase.COLUMN_TAILLE, this.getmSize());
		DataLayerInterface._values.put(ProofDataBase.COLUMN_ISYNC_PH, "0");		
	}

	@Override
	public void save() {
		
		if(hasDataLayer()) {
			//this.mDataNumber.save();
			this.fillValues();
			getResolver().insert(mUriRecords, DataLayerInterface._values);	
		}
		else {
			Console.print_exception("No data access (None ContentResolver has been passed)");
		}
		
	}

	@Override
	public boolean fillFromDataBase() {
		// TODO Auto-generated method stub
		return false;
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}	
}
