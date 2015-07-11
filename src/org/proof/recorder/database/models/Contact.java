package org.proof.recorder.database.models;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import org.proof.recorder.Settings;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import java.io.Serializable;

@SuppressWarnings({"serial", "unused"})
public class Contact implements Serializable, DataLayerInterface, Cloneable {
	
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

	private static final Uri mUri = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI,
			"excluded_contacts");

	private static final Uri mUriByPhone = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, 
			"excluded_contact_phone/");

	private String id;
	private String contactName;
	private String phoneNumber;
	private String contractId;

	private SimplePhoneNumber sPhoneNumber;	
	
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

	private void toConsole() {
		print(toString());
	}

	public SimplePhoneNumber getsPhoneNumber() {
		return sPhoneNumber;
	}

	private static ContentResolver resolver;

	private static boolean hasDataHandler = false;

	/**
	 * @return a boolean of an excluded contact
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
			Uri rowId = getResolver().insert(mUri, DataLayerInterface._values);
			this.setId(rowId.toString());
		}
		else {
			print_exception("No ContentResolver has been set via Contact" +
					" Class method Could not save Contact Instance: " + this);
		}
	}

	@Override
	public String toString(){
		return "Id: " + this.getId() +
				" ContractId: " + this.getContractId() +
				" ContactName: " + this.getContactName() +
				" PhoneNumber: " + this.getContactName() +
				" PhoneNumber (Object): " + this.getsPhoneNumber();
	}

	/**
	 * 
	 */
	public Contact(){
		this.defaultInit();
	}

	/**
	 * @param phone the phone number for a contact
	 */
	public Contact(String phone){
		this.fullInit(null, null, null, phone);
	}

	/**
	 * @param id the contact id
	 * @param apiId the api id
	 * @param name the name for the contact
	 * @param phone the phone number for the contact
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
	 * @param id the contact id
	 * @param apiId the api id
	 * @param name the name for the contact
	 * @param phone the phone number for the contact
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
	 * @return the name of the contact
	 */
	public String getContactName() {
		return contactName;
	}
	/**
	 * @param contactName the name of the contact
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	/**
	 * @return the id for a contact
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id for a contact
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the phone number for a contact
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	/**
	 * @param phoneNumber the phone number for a contact
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
	 * @return the contract id
	 */
	public String getContractId() {
		return contractId;
	}

	/**
	 * @return the contract id
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
	 * @param contractId the contract id
	 */
	public void setContractId(String contractId) {
		this.contractId = contractId != null ? contractId : "null";
	}

	/**
	 * @param message the message to print
	 */
	private void print(String message) {
		if(Settings.isDebug())
			Log.d(this.getClass().getName(), message);
	}

	/**
	 * @param message the message to print
	 */
	private void print_exception(String message) {
		Log.e(this.getClass().getName(), message);
	}		

	/**
	 * @return the hasDataHandler
	 */
	private static boolean hasDataHandler() {
		return hasDataHandler;
	}

	/**
	 */
	private static void setHasDataHandler() {
		Contact.hasDataHandler = true;
	}

	/**
	 * @return the ContentResolver
	 */
	private static ContentResolver getResolver() {
		return resolver;
	}

	/**
	 * @param resolver the ContentResolver
	 */
	public static void setResolver(ContentResolver resolver) {
		Contact.resolver = resolver;
		setHasDataHandler();
	}

	@Override
	public boolean fillFromDataBase() {
		// TODO Auto-generated method stub
		return false;
	}
}
