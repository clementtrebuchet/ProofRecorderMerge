package org.proof.recorder.database.models;

import java.io.Serializable;
import java.util.Locale;

import org.proof.recorder.Settings;

import android.telephony.PhoneNumberUtils;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

public abstract class BasePhoneNumber implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4775779211144924710L;

	@Override
	public String toString() {
		return "PhoneNumber [_originalNumber=" + _originalNumber + ", _shortName="
				+ _shortName + ", _fullName=" + _fullName + ", _countryCode="
				+ _countryCode + ", _nationalNumber=" + _nationalNumber
				+ ", isFormatted=" + isFormatted + ", get_originalNumber()="
				+ get_originalNumber() + "]";
	}
	
	// Enums
	
	protected enum internalType {
		BASIS,
		SIMPLE,
		WITHDATA
	}	
	
	// Constants Attributes

	protected final static String NULL = "null";
	protected final static Locale _platformLocale = Locale.getDefault();
	protected final static internalType _type = internalType.BASIS;
	
	// Private Attributes


	protected PhoneNumberUtil _libHelper;
	protected PhoneNumberOfflineGeocoder _geocoder;
	
	protected com.google.i18n.phonenumbers.Phonenumber.PhoneNumber _nativeModel;

	protected String _originalNumber;

	protected String _shortName;
	protected String _fullName;
	protected String _countryCode;
	protected String _nationalNumber;
	
	protected boolean isFormatted;
	protected boolean isFilled;
	
	// Initialization
	
	public boolean isFilled() {
		return isFilled;
	}

	public void setFilled(boolean isFilled) {
		this.isFilled = isFilled;
	}

	protected void initialize() {
		this._initialize();		
	}
	
	protected void initialize(String phoneNumber) {
		this._initialize();
		this.fillAttributesFromNumberStr(phoneNumber);
		
	}
	
	protected void _initialize() {
		
		this.set_countryCode(NULL);
		this.set_fullName(NULL);
		this._nationalNumber = NULL;
		this.set_shortName(NULL);
		
		this.setFilled(false);		
		this.setFormatted(false);
		
		this._libHelper = PhoneNumberUtil.getInstance();
		this._geocoder = PhoneNumberOfflineGeocoder.getInstance();
	}
	
	// Logging wrapping
	
	protected void print(String message) {
		if(Settings.isDebug())
			Log.d(this.getClass().getName(), message);
	}
	
	protected void print_exception(String message) {
		Log.e(this.getClass().getName(), message);
	}	
	
	// Methods
	
	/**
	 * @param phoneNumber
	 */
	private void fillAttributesFromNumberStr(String phoneNumber) {
		try {
			//print(_platformLocale.getCountry());
			
			this._nativeModel = this._libHelper.parse(phoneNumber, 
					_platformLocale.getCountry());
			
			this._originalNumber = "" + this._nativeModel.getNationalNumber();
			
			this.set_countryCode("" + this._nativeModel.getCountryCode());
			this.set_fullName("" + this._geocoder.getDescriptionForNumber(
					this._nativeModel, _platformLocale));			
			
			this._nationalNumber = this.get_originalNumber();
			
			//this.set_shortName(NULL);
			
			this.setFilled(true);
			
		} catch (NumberParseException e) {
			print_exception(e.getMessage());			
		}
	}
	
	public void toConsole() {
		print(toString());
	}
	
	// Getters and Setters
	
	public static internalType getType() {
		return _type;
	}

	public String get_originalNumber() {
		return PhoneNumberUtils.stripSeparators(_originalNumber);
	}

	public void set_originalNumber(String _originalNumber) {		
		this._originalNumber = _originalNumber;
		if(!this.isFilled())
			this.fillAttributesFromNumberStr(_originalNumber);
	}
	
	public com.google.i18n.phonenumbers.Phonenumber.PhoneNumber get_nativeModel() {
		return _nativeModel;
	}

	public void set_nativeModel(
			com.google.i18n.phonenumbers.Phonenumber.PhoneNumber _nativeModel) {
		this._nativeModel = _nativeModel;
	}

	public PhoneNumberUtil get_libHelper() {
		return _libHelper;
	}


	public PhoneNumberOfflineGeocoder get_geocoder() {
		return _geocoder;
	}


	public String get_shortName() {
		return _shortName;
	}


	public String get_fullName() {
		return _fullName;
	}


	public String get_countryCode() {
		return _countryCode;
	}


	public String get_nationalNumber() {
		return _nationalNumber;
	}


	public boolean isFormatted() {
		return isFormatted;
	}


	public void set_libHelper(PhoneNumberUtil _libHelper) {
		this._libHelper = _libHelper;
	}


	public void set_geocoder(PhoneNumberOfflineGeocoder _geocoder) {
		this._geocoder = _geocoder;
	}


	public void set_shortName(String _shortName) {
		this._shortName = _shortName;
	}


	public void set_fullName(String _fullName) {
		this._fullName = _fullName;
	}


	public void set_countryCode(String _countryCode) {
		this._countryCode = _countryCode;
	}


	public void set_nationalNumber(String _nationalNumber) {
		this._nationalNumber = _nationalNumber;
		if(!this.isFilled())
			this.fillAttributesFromNumberStr(_nationalNumber);
	}


	public void setFormatted(boolean isFormatted) {
		this.isFormatted = isFormatted;
	}

}
