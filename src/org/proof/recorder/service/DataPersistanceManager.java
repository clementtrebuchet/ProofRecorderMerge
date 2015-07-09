package org.proof.recorder.service;

import org.proof.recorder.Settings;

import java.util.Locale;

/**
 * @author Namgyal Brisson
 *
 */
public class DataPersistanceManager {

	private final static String currentFormat = "currentAudioFormat";
	private final static String isProcessing = "isProcessing";

	private String mAudioFormat = null;
	private String mProcessing = "0";
	
	/**
	 * @return the mAudioFormat
	 */
	public String getAudioFormat() {
		return mAudioFormat;
	}
	/**
	 * @param mAudioFormat the mAudioFormat to set
	 */
	public void setAudioFormat(String mAudioFormat) {
		this.mAudioFormat = mAudioFormat.toLowerCase(Locale.getDefault());
	}
	
	/**
	 * @return the mProcessing
	 */
	public boolean isProcessing() {
		try {
			if(Integer.parseInt(mProcessing) == 1)
				return true;
		} catch (NumberFormatException ignored) {
			
		}		
		return false;
	}
	/**
	 * @param mProcessing the mProcessing to set
	 */
	public void setProcessing(String mProcessing) {
		this.mProcessing = mProcessing;
	}
	private void fill() {
		mAudioFormat = Settings.getPersistantData(currentFormat);
		mProcessing = Settings.getPersistantData(isProcessing);
	}
	
	public DataPersistanceManager() {				
		this.fill();
	}
	
	public void save() {
		Settings.setPersistantData(currentFormat, mAudioFormat);
		Settings.setPersistantData(isProcessing, mProcessing);		
	}
	
	public void cacheRows(String key, String serialized) {
		Settings.setPersistantData(key, serialized);
	}
	
	public String retrieveCachedRows(String key) {
		return Settings.getPersistantData(key);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DataPersistanceManager [mAudioFormat=" + mAudioFormat + ", mProcessing="
				+ mProcessing + ", getAudioFormat()=" + getAudioFormat()
				+ ", isProcessing()=" + isProcessing() + "]";
	}	
}
