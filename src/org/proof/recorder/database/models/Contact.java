package org.proof.recorder.database.models;

import java.io.Serializable;

import android.telephony.PhoneNumberUtils;

@SuppressWarnings("serial")
public class Contact implements Serializable {

	private static final String DEFAULT_VALUE = "null";

	private String id;
	private String contactName;
	private String phoneNumber;
	private String contractId;

	public Contact(){
		this.defaultInit();
	}

	public Contact(String id, 
			String apiId, 
			String name, 
			String phone){
		this.fullInit(id, apiId, name, phone);
	}

	private void defaultInit(){
		this.fullInit(null, null, null, null);
	}

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
		this.setPhoneNumber(
				phone != null ? phone : DEFAULT_VALUE);
	}

	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = PhoneNumberUtils.stripSeparators(phoneNumber);
	}
	public String getContractId() {
		return contractId;
	}

	public long getLongContractId() {
		long id;
		try {
			id = Long.parseLong(contractId);
		} catch (NumberFormatException e) {
			id = -1;
		}		
		return id;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	@Override
	public String toString(){
		String contactStr = "Id: " + this.getId() + 
				" ContractId: " + this.getContractId() + 
				" ContactName: " + this.getContactName() + 
				" PhoneNumber: " + this.getPhoneNumber();
		return contactStr;
	}
}
