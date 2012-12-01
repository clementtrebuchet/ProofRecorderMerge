package org.proof.recorder.database.models;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Contact implements Serializable {
	
	private String id;
	private String contactName;
	private String phoneNumber;
	private String contractId;
	
	public Contact(){
		this.init();
	}
	
	private void init(){
		this.setId("null");
		this.setContractId("null");
		this.setContactName("null");
		this.setPhoneNumber("null");
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
		this.phoneNumber = phoneNumber;
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
		String contactStr = "Id: " + this.getId() + " ContractId: " +
							this.getContractId() + " ContactName: " +
							this.getContactName() + " PhoneNumber: " +
							this.getPhoneNumber();
		return contactStr;
	}
}
