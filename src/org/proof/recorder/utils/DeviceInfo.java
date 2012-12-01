package org.proof.recorder.utils;

import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.ContentResolver;
import android.content.Context;

public class DeviceInfo {
	
	private ConnectivityInfo mNetwork;
	private OsInfo mOsInfo;
	private static Context mContext;
	
	public DeviceInfo(
			ConnectivityInfo _network,
			OsInfo _osInfo, 
			Context _context
	) {
		this.mNetwork = _network;
		this.mOsInfo = _osInfo;
		mContext = _context;
	}
	
	/**
	 * Get free space on device -> mOsInfo	
	 * @param externalIncluded
	 * @return String
	 */
	public String getFreeSpaceOnDevice() {
		return this.mOsInfo.getFreeSpaceOnExternalDevice();		
	}
	
	/**
	 * Get space consumed by ProofRecorder -> mOsInfo
	 * @return String
	 */
	public String getSpaceConsumedByApp() {
		return this.mOsInfo.getSpaceConsumedByApp();		
	}
	
	/**
	 * Get the Network Info -> mNetwork
	 * @return String
	 */
	public char getNetworkState() {
		return this.mNetwork.getNetworkState(mContext);		
	}
	
	/**
	 * Get the number of voice recording & calls recording -> PersonnalProofContentProvider
	 * @return String[] 0: Calls 1: Voices 
	 */
	public String[] getVoicesAndCallsCount() {
		return PersonnalProofContentProvider.getVoicesAndCallsCount();
	}
	
	/**
	 * Get the number of excluded contacts and opposite -> PersonnalProofContentProvider
	 * @return String[] 0: Excluded 1: Not Excluded
	 */
	public String[] getExContactsAndNotCount(ContentResolver cr) {
		return PersonnalProofContentProvider.getExContactsAndNotCount(cr);		
	}
	
	/**
	 * Get the commercial status of the App -> ??
	 * @return String
	 */
	public String getCommercialStatusOfApp() {
		// @TODO : think about to make it!
		return null;		
	}
	
	
}
