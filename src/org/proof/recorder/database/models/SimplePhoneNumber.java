package org.proof.recorder.database.models;

public class SimplePhoneNumber extends BasePhoneNumber {

	protected final static internalType _type = internalType.SIMPLE;

	// Constructors

	public SimplePhoneNumber() {
		this.initialize();
	}

	public SimplePhoneNumber(String phoneNumber) {
		this.initialize(phoneNumber);
	}
}
