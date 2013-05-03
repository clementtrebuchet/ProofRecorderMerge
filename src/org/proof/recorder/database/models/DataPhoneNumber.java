package org.proof.recorder.database.models;

import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.ContentResolver;
import android.net.Uri;

public class DataPhoneNumber extends BasePhoneNumber implements DataLayerInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3370675016676353788L;

	// Attributes
	
	private static ContentResolver resolver;
	
	private static Uri uri = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "phone_numbers");
	
	// Constructors
	
	public DataPhoneNumber() {
		this.initialize();
		
	}
	
	public DataPhoneNumber(String phoneNumber) {
		this.initialize(phoneNumber);
	}
	
	// Initializing
	
	protected void initialize() {
		super.initialize();
	}
	
	protected void initialize(String phoneNumber) {
		super.initialize(phoneNumber);
	}
	
	// Methods
	@Override
	public void save() {
		this.fillValues();
		getResolver().insert(uri, DataLayerInterface._values);
	}

	@Override
	public void fillValues() {
		DataLayerInterface._values.put("", this.get_countryCode());
		DataLayerInterface._values.put("", this.get_nationalNumber());		
		DataLayerInterface._values.put("", this.get_fullName());
		DataLayerInterface._values.put("", this.get_shortName());		
	}

	/**
	 * @return the resolver
	 */
	public static ContentResolver getResolver() {
		return resolver;
	}

	/**
	 * @param resolver the resolver to set
	 */
	public static void setResolver(ContentResolver resolver) {
		DataPhoneNumber.resolver = resolver;
	}

	@Override
	public boolean fillFromDataBase() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
