package org.proof.recorder.database.models;

public class Record {
	
	private String mId;
	private String mFilePath;
	private String mPhone;
	private String mHtime;	
	private String mSense;
	private String mAndroidId;
	
	public Record () {
		setmId("-1");
		setmFilePath("null");
	}
	
	public Record (String id, String fileName) {
		setmId(id);
		setmFilePath(fileName);
	}
	
	public String getmHtime() {
		return mHtime;
	}

	public void setmHtime(String mHtime) {
		this.mHtime = mHtime;
	}

	public String getmSense() {
		return mSense;
	}

	public void setmSense(String mSense) {
		this.mSense = mSense;
	}

	/**
	 * @return the mId
	 */
	public String getmId() {
		return mId;
	}

	/**
	 * @param mId the mId to set
	 */
	public void setmId(String mId) {
		this.mId = mId;
	}

	/**
	 * @return the mFilePath
	 */
	public String getmFilePath() {
		return mFilePath;
	}

	/**
	 * @param mFilePath the mFilePath to set
	 */
	public void setmFilePath(String mFilePath) {
		this.mFilePath = mFilePath;
	}

	/**
	 * @return the mPhone
	 */
	public String getmPhone() {
		return mPhone;
	}

	/**
	 * @param mPhone the mPhone to set
	 */
	public void setmPhone(String mPhone) {
		this.mPhone = mPhone;
	}

	/**
	 * @return the mAndroidId
	 */
	public String getmAndroidId() {
		return mAndroidId;
	}

	/**
	 * @param mAndroidId the mAndroidId to set
	 */
	public void setmAndroidId(String mAndroidId) {
		this.mAndroidId = mAndroidId;
	}
}
