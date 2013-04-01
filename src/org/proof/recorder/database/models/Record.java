package org.proof.recorder.database.models;

import java.util.ArrayList;
import java.util.List;

import org.proof.recorder.Settings;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

public class Record implements DataLayerInterface {

	// Uris 

	private static Uri mUriRecords = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "records");

	private static Uri mUriRecordById = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "record_by_id/");

	// Private Attributes

	private String mId;
	private String mFilePath;
	private String mPhone;	
	private String mHtime;
	private String mTimeStamp;
	private String mSense;
	private String mAndroidId;
	private String mSize;	

	private DataPhoneNumber mDataNumber;
	private static ContentResolver mResolver;
	private Contact mContact;

	private static boolean hasDataLayer = false;

	// Logging wrapping

	protected void print(String message) {
		if(Settings.isDebug())
			Log.d(this.getClass().getName(), message);
	}

	protected void print_exception(String message) {
		Log.e(this.getClass().getName(), message);
	}

	public void toConsole() {
		this.print(this.toString());
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

	public static List<Record> getAllObjects() {

		List<Record> mRecordCollection =  new ArrayList<Record>();
		Record mRecord;

		Cursor mCursor = Record.getResolver().query(
				mUriRecords, null, null, null, null);

		if(mCursor.moveToFirst()) {
			do {
				mRecord = new Record();
				mRecordCollection.add(mRecord);
			}while(mCursor.moveToNext());
		}

		return mRecordCollection;		
	}

	public static Record getRecordById(String id) {

		Record mRecord = null;

		Uri mUri = Uri.withAppendedPath(mUriRecordById, id);

		Cursor mCursor = Record.getResolver().query(
				mUri, null, null, null, null);

		if(mCursor.moveToFirst()) {
			do {
				mRecord = new Record();			
			}while(mCursor.moveToNext());
		}

		return mRecord;		
	}

	public static List<Record> getRecordsByPhone(String phone) {

		return null;		
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
		setmSense(sense);
		setmHtime(htime);
		setmAndroidId(mAndroidId);
		
	}
	
	public boolean isIncomingCall() {
		return this.getmSense().equalsIgnoreCase("e");
	}

	private void initialize(String id, String fileName) {
		this.setmContact(new Contact());
		this.mDataNumber = new DataPhoneNumber();
		setmId(id);
		setmFilePath(fileName);
	}
	
	private void initialize(String id, String fileName, String phone) {
		
		this.setmContact(new Contact(phone));
		this.mDataNumber = new DataPhoneNumber(phone);
		setmPhone(phone);
		setmId(id);
		setmFilePath(fileName);
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
		return mAndroidId.trim();
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
		DataLayerInterface._values.put(ProofDataBase.COLUMN_TELEPHONE, this.getPhoneNumberFK());
		DataLayerInterface._values.put(ProofDataBase.COLUMN_SENS, this.getmSense());
		DataLayerInterface._values.put(ProofDataBase.COLUMN_TAILLE, this.getmSize());
		DataLayerInterface._values.put(ProofDataBase.COLUMN_ISYNC_PH, "0");		
	}

	@Override
	public void save() {
		
		if(hasDataLayer()) {
			this.mDataNumber.save();
			this.fillValues();
			getResolver().insert(mUriRecords, DataLayerInterface._values);	
		}
		else {
			print_exception("No data access (None ContentResolver has been passed)");
		}
		
	}	
}
