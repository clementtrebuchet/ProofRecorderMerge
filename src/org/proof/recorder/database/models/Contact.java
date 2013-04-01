package org.proof.recorder.database.models;

import java.io.Serializable;

import org.proof.recorder.Settings;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

@SuppressWarnings("serial")
public class Contact implements Serializable, DataLayerInterface {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sPhoneNumber.get_nationalNumber() == null) ? 0 : sPhoneNumber.get_nationalNumber().hashCode());
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
		if (getClass() != obj.getClass())
			return false;
		
		Contact other = (Contact) obj;
		if (sPhoneNumber == null) {
			if (other.sPhoneNumber != null)
				return false;
		} else if (!sPhoneNumber.get_nationalNumber().equals(
				other.sPhoneNumber.get_nationalNumber()))
			return false;
		
		return true;
	}

	private static final String DEFAULT_VALUE = "null";
	
	private static Uri mUri = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI,
			"excluded_contacts");
	
	private static Uri mUriByPhone = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, 
			"excluded_contact_phone/");

	private String id;
	private String contactName;
	private String phoneNumber;
	private String contractId;
	
	private SimplePhoneNumber sPhoneNumber;	
		
		public void toConsole() {
			this.print(this.toString());
		}
	
	public SimplePhoneNumber getsPhoneNumber() {
		return sPhoneNumber;
	}

	private static ContentResolver resolver;
	
	private static boolean hasDataHandler = false;
	
	/**
	 * @return
	 */
	public boolean isExcluded() {
		int count = 0;
		
		if(hasDataHandler()) {			
			
			Uri uri = Uri.withAppendedPath(
					mUriByPhone, 
					this.getsPhoneNumber().get_nationalNumber());
			
			Cursor cursor = getResolver().query(uri,
					null, null, null, null);
			
			count = cursor.getCount();
			
			cursor.close();			
		}
		
		this.toConsole();
		
		return count > 0;
	}
	
	@Override
	public void fillValues() {
		DataLayerInterface._values.put(
				ProofDataBase.COLUMN_CONTRACT_CONTACTS_ID, this.getContractId());
		DataLayerInterface._values.put(
				ProofDataBase.COLUMN_DISPLAY_NAME, this.getContactName());
		DataLayerInterface._values.put(
				ProofDataBase.COLUMN_PHONE_NUMBER, this.sPhoneNumber.get_nationalNumber());	
	}

	@Override
	public void save() {
		if(hasDataHandler()) {
			this.fillValues();
			getResolver().insert(mUri, DataLayerInterface._values);
			int lastId = PersonnalProofContentProvider.lastInsertId(
					"excludedcontactsproof");
			this.setId("" + lastId);
		}
		else {
			print_exception("No ContentResolver has been set via Contact" +
					" Class method Could not save Contact Instance: " + this);
		}
	}

	@Override
	public String toString(){
		String contactStr = "Id: " + this.getId() + 
				" ContractId: " + this.getContractId() + 
				" ContactName: " + this.getContactName() + 
				" PhoneNumber: " + this.getContactName() + 
				" PhoneNumber (Object): " + this.getsPhoneNumber();
		return contactStr;
	}

	/**
	 * 
	 */
	public Contact(){
		this.defaultInit();
	}
	
	/**
	 * @param phone
	 */
	public Contact(String phone){
		this.fullInit(null, null, null, phone);
	}

	/**
	 * @param id
	 * @param apiId
	 * @param name
	 * @param phone
	 */
	public Contact(String id, 
			String apiId, 
			String name, 
			String phone){
		this.fullInit(id, apiId, name, phone);
	}

	/**
	 * 
	 */
	private void defaultInit(){
		this.fullInit(null, null, null, null);
	}

	/**
	 * @param id
	 * @param apiId
	 * @param name
	 * @param phone
	 */
	private void fullInit(
			String id, 
			String apiId, 
			String name, 
			String phone){

		this.setId(
				id != null ? id : DEFAULT_VALUE);
		this.setContractId(
				apiId != null ? apiId : DEFAULT_VALUE);
		this.setContactName(
				name != null ? name : DEFAULT_VALUE);
		this.phoneNumber =
				phone != null ? phone : DEFAULT_VALUE;
		
		if(phone != null)
			this.sPhoneNumber = new SimplePhoneNumber(phone);
		else
			this.sPhoneNumber = new SimplePhoneNumber();
	}

	/**
	 * @return
	 */
	public String getContactName() {
		return contactName;
	}
	/**
	 * @param contactName
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	/**
	 * @return
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	/**
	 * @param phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = PhoneNumberUtils.stripSeparators(phoneNumber);
		this.sPhoneNumber.set_originalNumber(this.phoneNumber);
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void load() {
		
		if(hasDataHandler()) {			
			
			Uri uri = Uri.withAppendedPath(
					mUriByPhone, 
					this.getsPhoneNumber().get_nationalNumber());
			
			Cursor cursor = getResolver().query(uri,
					null, null, null, null);
			
			try {
				if(cursor.moveToFirst()) {
					this.setContactName(
							cursor.getColumnName(
									cursor.getColumnIndex(
											ProofDataBase.COLUMN_DISPLAY_NAME)));
					
					this.setContractId(
							cursor.getColumnName(
									cursor.getColumnIndex(
											ProofDataBase.COLUMN_CONTRACT_CONTACTS_ID)));
					
					this.setId(
							cursor.getColumnName(
									cursor.getColumnIndex(
											ProofDataBase.COLUMN_CONTACT_ID)));
				}	
			}
			catch(Exception e) {
				this.print_exception(e.getMessage() + "");
			}
			finally {
				cursor.close();	
			}					
		}	
	}

	/**
	 * @return
	 */
	public String getContractId() {
		return contractId;
	}

	/**
	 * @return
	 */
	public long getLongContractId() {
		long id;
		try {
			id = Long.parseLong(contractId);
		} catch (NumberFormatException e) {
			id = -1;
		}		
		return id;
	}

	/**
	 * @param contractId
	 */
	public void setContractId(String contractId) {
		this.contractId = contractId != null ? contractId : "null";
	}
	
	/**
	 * @param message
	 */
	private void print(String message) {
		if(Settings.isDebug())
			Log.d(this.getClass().getName(), message);
	}
	
	/**
	 * @param message
	 */
	private void print_exception(String message) {
		Log.e(this.getClass().getName(), message);
	}		

	/**
	 * @return the hasDataHandler
	 */
	public static boolean hasDataHandler() {
		return hasDataHandler;
	}

	/**
	 * @param hasDataHandler the hasDataHandler to set
	 */
	public static void setHasDataHandler(boolean hasDataHandler) {
		Contact.hasDataHandler = hasDataHandler;
	}
	
	/**
	 * @return
	 */
	public static ContentResolver getResolver() {
		return resolver;
	}

	/**
	 * @param resolver
	 */
	public static void setResolver(ContentResolver resolver) {
		Contact.resolver = resolver;
		setHasDataHandler(true);
	}
}
