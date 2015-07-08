package org.proof.recorder.database.models;

import android.content.ContentResolver;

import org.proof.recorder.utils.DateUtils;
import org.proof.recorder.utils.OsInfo;
import org.proof.recorder.utils.ServiceAudioHelper;

import java.io.Serializable;

public class Voice implements DataLayerInterface, Serializable, Cloneable {
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		Object o = null;
		try {
			// On récupère l'instance à renvoyer par l'appel de la 
			// méthode super.clone()
			o = super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implémentons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		// on renvoie le clone
		return o;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_filePath == null) ? 0 : _filePath.hashCode());
		result = prime * result
				+ ((_fileSize == null) ? 0 : _fileSize.hashCode());
		result = prime * result
				+ ((_humanTime == null) ? 0 : _humanTime.hashCode());
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + ((_note == null) ? 0 : _note.hashCode());
		result = prime * result
				+ ((_timestamp == null) ? 0 : _timestamp.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Voice))
			return false;
		Voice other = (Voice) obj;
		if (_filePath == null) {
			if (other._filePath != null)
				return false;
		} else if (!_filePath.equals(other._filePath))
			return false;
		if (_fileSize == null) {
			if (other._fileSize != null)
				return false;
		} else if (!_fileSize.equals(other._fileSize))
			return false;
		if (_humanTime == null) {
			if (other._humanTime != null)
				return false;
		} else if (!_humanTime.equals(other._humanTime))
			return false;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (_note == null) {
			if (other._note != null)
				return false;
		} else if (!_note.equals(other._note))
			return false;
		if (_timestamp == null) {
			if (other._timestamp != null)
				return false;
		} else if (!_timestamp.equals(other._timestamp))
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1694689376059702355L;
	
	private static ContentResolver _resolver;
	private static boolean _hasDataLayer;	
	
	public static ContentResolver getResolver() {
		return Voice._resolver;
	}
	
	public static void setResolver(ContentResolver _resolver) {
		Voice._resolver = _resolver;
		setHasResolver();
		Note.setResolver(_resolver);
	}
	
	public static boolean hasResolver() {
		return Voice._hasDataLayer;
	}

	private static void setHasResolver() {
		Voice._hasDataLayer = true;
	}
	
	private String _id;
	private String _humanTime;
	private String _fileSize;
	private String _timestamp;
	private String _filePath;
	
	private Note _note;
	
	private boolean isChecked = false;

	/**
	 * @return the isChecked
	 */
	public boolean isChecked() {
		return isChecked;
	}

	/**
	 * @param isChecked the isChecked to set
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	public void toggle() {
		setChecked(!this.isChecked);		
	}
	
	/**
	 * 
	 */
	public Voice() {
		super();
		setId(null);
		this._humanTime = null;
		this._fileSize = null;
		this._timestamp = null;
		this._filePath = null;
	}
	/**
	 * @param _id
	 * @param _humanTime
	 * @param _fileSize
	 * @param _timestamp
	 * @param _filePath
	 */
	public Voice(String _id, String _humanTime, String _fileSize,
			String _timestamp, String _filePath) {
		super();
		setId(_id);
		this._humanTime = _humanTime;
		this._fileSize = _fileSize;
		this._timestamp = _timestamp;
		this._filePath = _filePath;
	}
	/**
	 * @return the _id
	 */
	public String getId() {
		return this._id;
	}
	/**
	 * @param _id the _id to set
	 */
	private void setId(String _id) {
		this._id = _id;
		
		if(_id != null)
			this.setNote(new Note(_id));
	}
	/**
	 * @return the _humanTime
	 */
	public String getHumanTime() {
		return this._humanTime;
	}
	/**
	 * @param _humanTime the _humanTime to set
	 */
	private void setHumanTime(String _humanTime) {
		this._humanTime = _humanTime;
	}
	/**
	 * @return the _fileSize
	 */
	public String getFileSize() {
		return this._fileSize;
	}
	/**
	 * @return the _fileSize
	 */
	public String getHumanFileSize() {		
		return ServiceAudioHelper.transByteToKo(this._fileSize);
	}
	/**
	 * @param _fileSize the _fileSize to set
	 */
	public void setFileSize(String _fileSize) {
		this._fileSize = _fileSize;
	}
	/**
	 * @return the _timestamp
	 */
	public String getTimestamp() {
		return this._timestamp;
	}
	/**
	 * @param _timestamp the _timestamp to set
	 */
	private void setTimestamp(String _timestamp) {
		this._timestamp = _timestamp;
	}
	/**
	 * @return the _filePath
	 */
	public String getFilePath() {
		return this._filePath;
	}
	/**
	 * @param _filePath the _filePath to set
	 */
	public void setFilePath(String _filePath) {
		this._filePath = _filePath;
		this.setTimestamp(OsInfo.getBaseNameWithNoExt(_filePath));
		this.setHumanTime(DateUtils.formatTime(this.getTimestamp()));
	}
	/**
	 * @return the _note
	 */
	public Note getNote() {
		return this._note;
	}
	/**
	 * @param _note the _note to set
	 */
	private void setNote(Note _note) {
		this._note = _note;
	}
	
	@Override
	public boolean fillFromDataBase() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void fillValues() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
}
