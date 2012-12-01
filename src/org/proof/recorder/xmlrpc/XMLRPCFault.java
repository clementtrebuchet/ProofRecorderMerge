package org.proof.recorder.xmlrpc;

import org.proof.recorder.syncron.fragment.Messages;
import org.proof.recorder.syncron.fragment.SyncronUi;

public class XMLRPCFault extends XMLRPCException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5676562456612956519L;
	private String faultString;
	private int faultCode;
	final private int AUTHENTICATION_FAILED_CODE = 81;
	final private int PERMISSION_DENIED_CODE = 82;
	final private int SERVER_ERROR_CODE = 1;
	final private int SYNCHRO_ERROR_CODE = 800;
	final private int Synchro_Nothing_Exception = 900;

	public XMLRPCFault(String faultString, int faultCode) {
		super("XMLRPC Fault: " + faultString + " [code " + faultCode + "]");
		this.faultString = giveMeHumanComprehensive(faultCode);
		this.faultCode = faultCode;
	}
	
	public String giveMeHumanComprehensive(int faultCode){
		
		switch(faultCode){
		case AUTHENTICATION_FAILED_CODE:
			faultString = SyncronUi.getAppContext().getString(Messages.getStringResource(SyncronUi.getAppContext(), "ERROR81"));
			break;
		case PERMISSION_DENIED_CODE:
			faultString = SyncronUi.getAppContext().getString(Messages.getStringResource(SyncronUi.getAppContext(), "ERROR82"));
			break;
		case SERVER_ERROR_CODE:
			faultString = SyncronUi.getAppContext().getString(Messages.getStringResource(SyncronUi.getAppContext(), "ERROR1"));
			break;
		case SYNCHRO_ERROR_CODE:
			faultString = SyncronUi.getAppContext().getString(Messages.getStringResource(SyncronUi.getAppContext(), "ERROR800"));
			break;
		case Synchro_Nothing_Exception:
			faultString = SyncronUi.getAppContext().getString(Messages.getStringResource(SyncronUi.getAppContext(), "ERROR900"));
			break;
		}
		return faultString;
		
	}
	
	public String getFaultString() {
		return faultString;
	}
	
	public int getFaultCode() {
		return faultCode;
	}
}
