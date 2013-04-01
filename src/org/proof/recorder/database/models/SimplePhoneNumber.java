package org.proof.recorder.database.models;

public class SimplePhoneNumber extends BasePhoneNumber {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4305137167087960732L;
	
	protected final static internalType _type = internalType.SIMPLE;

	// Constructors

	public SimplePhoneNumber() {
		this.initialize();
	}

	public SimplePhoneNumber(String phoneNumber) {
		this.initialize(phoneNumber);
	}
}
