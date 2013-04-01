package org.proof.recorder.database.models;

import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.ContentResolver;
import android.net.Uri;

public class DataPhoneNumber extends BasePhoneNumber implements DataLayerInterface {
	
	// Attributes
	
	private ContentResolver _resolver;
	
	private static Uri uri = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "phone_numbers");
	
	// Constructors
	
	public DataPhoneNumber(ContentResolver _resolver) {
		this.initialize(_resolver);
		
	}
	
	public DataPhoneNumber(ContentResolver _resolver, String phoneNumber) {
		this.initialize(_resolver, phoneNumber);
	}
	
	// Initializing
	
	protected void initialize(ContentResolver _resolver) {
		super.initialize();
		this.set_resolver(_resolver);
	}
	
	protected void initialize(ContentResolver _resolver, String phoneNumber) {
		super.initialize(phoneNumber);
		this.set_resolver(_resolver);
	}
	
	// Methods
	
	public void save() {
		this.fillValues();
		this._resolver.insert(uri, DataLayerInterface._values);
	}

	private void set_resolver(ContentResolver _resolver) {
		this._resolver = _resolver;
	}

	@Override
	public void fillValues() {
		DataLayerInterface._values.put("", this.get_countryCode());
		DataLayerInterface._values.put("", this.get_nationalNumber());		
		DataLayerInterface._values.put("", this.get_fullName());
		DataLayerInterface._values.put("", this.get_shortName());		
	}
	
}
