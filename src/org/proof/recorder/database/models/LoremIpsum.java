package org.proof.recorder.database.models;

import java.util.HashMap;
import java.util.Map;

import org.proof.recorder.xmlrpc.XMLRPCSerializable;

/**
 * @author clement
 *
 */
public class LoremIpsum implements XMLRPCSerializable {
	
	private String imei; 
	private String userName;
	private String password; 
	private String email; 
	
	
	/**
	 * @param imei
	 * @param userName
	 * @param password
	 * @param email
	 */
	public LoremIpsum(String imei, String userName, String password,
			String email) {
		super();
		this.imei = imei;
		this.userName = userName;
		this.password = password;
		this.email = email;
	}
	
	public LoremIpsum() {
		 super();
	}

	

	@Override
	public Map<String, Object> getSerializable() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("imei", imei);
		map.put("userName", userName);
		map.put("password", password);
		map.put("email", email);
		return null;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
