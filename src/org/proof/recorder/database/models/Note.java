package org.proof.recorder.database.models;

import java.io.Serializable;

import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.Log.Console;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class Note implements DataLayerInterface, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3266657064474251987L;
	
	private static ContentResolver _resolver;
	private static boolean _hasDataLayer;
	
	private static final String[] from = new String[] {
		ProofDataBase.COLUMNVOICE_NOTES_ID,
		ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID,
		ProofDataBase.COLUMNVOICE_TITLE, 
		ProofDataBase.COLUMNVOICE_NOTE,
		ProofDataBase.COLUMNVOICE_DATE_CREATION,
		ProofDataBase.COLUMN_ISYNC_NOV
	};
	
	public static ContentResolver getResolver() {
		return Note._resolver;
	}
	
	public static void setResolver(ContentResolver _resolver) {
		Note._resolver = _resolver;
		setHasResolver(true);
	}
	
	public static boolean hasResolver() {
		return Note._hasDataLayer;
	}

	private static void setHasResolver(boolean _hasDataLayer) {
		Note._hasDataLayer = _hasDataLayer;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_creationTime == null) ? 0 : _creationTime.hashCode());
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result
				+ ((_recordId == null) ? 0 : _recordId.hashCode());
		result = prime * result + ((_synced == null) ? 0 : _synced.hashCode());
		result = prime * result + ((_text == null) ? 0 : _text.hashCode());
		result = prime * result + ((_title == null) ? 0 : _title.hashCode());
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
		if (!(obj instanceof Note))
			return false;
		Note other = (Note) obj;
		if (_creationTime == null) {
			if (other._creationTime != null)
				return false;
		} else if (!_creationTime.equals(other._creationTime))
			return false;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (_recordId == null) {
			if (other._recordId != null)
				return false;
		} else if (!_recordId.equals(other._recordId))
			return false;
		if (_synced == null) {
			if (other._synced != null)
				return false;
		} else if (!_synced.equals(other._synced))
			return false;
		if (_text == null) {
			if (other._text != null)
				return false;
		} else if (!_text.equals(other._text))
			return false;
		if (_title == null) {
			if (other._title != null)
				return false;
		} else if (!_title.equals(other._title))
			return false;
		return true;
	}

	private String _id;
	private String _recordId;
	private String _title;
	private String _text;
	private String _creationTime;
	private String _synced;	
	
	/**
	 * 
	 */
	protected Note() {
		super();

		this._id = null;
		this._recordId = null;
		this._title = null;
		this._text = null;
		this._creationTime = null;
		this._synced = null;
		
		initialize();
	}
	/**
	 * @param _id
	 */
	protected Note(String _recordId) {
		super();
		
		this._id = null;
		this._recordId = _recordId;
		this._title = null;
		this._text = null;
		this._creationTime = null;
		this._synced = null;
		
		initialize();
	}
	/**
	 * @param _id
	 * @param _recordId
	 * @param _title
	 * @param _text
	 * @param _creationTime
	 * @param _synced
	 */
	protected Note(String _id, String _recordId, String _title, String _text,
			String _creationTime, String _synced) {
		super();

		this._id = _id;
		this._recordId = _recordId;
		this._title = _title;
		this._text = _text;
		this._creationTime = _creationTime;
		this._synced = _synced;
		
		initialize();
	}
	
	private void initialize() {
		Console.setTagName(this.getClass().getSimpleName());
		
		boolean filled = fillFromDataBase();
		
		if(!filled) {
			Console.print_debug("not filled!");
		}
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
	public void setId(String _id) {
		this._id = _id;
	}
	/**
	 * @return the _recordId
	 */
	public String getRecordId() {
		return this._recordId;
	}
	/**
	 * @param _recordId the _recordId to set
	 */
	public void setRecordId(String _recordId) {
		this._recordId = _recordId;
	}
	/**
	 * @return the _title
	 */
	public String getTitle() {
		return this._title;
	}
	/**
	 * @param _title the _title to set
	 */
	public void setTitle(String _title) {
		this._title = _title;
	}
	/**
	 * @return the _text
	 */
	public String getText() {
		return this._text;
	}
	/**
	 * @param _text the _text to set
	 */
	public void setText(String _text) {
		this._text = _text;
	}
	/**
	 * @return the _creationTime
	 */
	public String getCreationTime() {
		return this._creationTime;
	}
	/**
	 * @param _creationTime the _creationTime to set
	 */
	public void setCreationTime(String _creationTime) {
		this._creationTime = _creationTime;
	}
	/**
	 * @return the _synced
	 */
	public String isSynced() {
		return this._synced;
	}
	/**
	 * @param _synced the _synced to set
	 */
	public void setSynced(String _synced) {
		this._synced = _synced;
	}
	
	@Override
	public boolean fillFromDataBase() {
		
		if(hasResolver() && this._recordId != null) {
			Uri uri = Uri.withAppendedPath(
					PersonnalProofContentProvider.CONTENT_URI, "vnote_recordid/"
							+ this._recordId);
			
			Cursor dataCursor = null;
			
			try {
				
				dataCursor = getResolver().query(uri, from, null, null, null);
				
				while (dataCursor.moveToNext()){
					
					this._id = (dataCursor.getString(dataCursor
							.getColumnIndex(
									ProofDataBase.COLUMNVOICE_NOTES_ID)));
					this._title = (dataCursor.getString(dataCursor
							.getColumnIndex(
									ProofDataBase.COLUMNVOICE_TITLE)));
					this._text = (dataCursor.getString(dataCursor
							.getColumnIndex(
									ProofDataBase.COLUMNVOICE_NOTE)));
					this._creationTime = (dataCursor.getString(dataCursor
							.getColumnIndex(
									ProofDataBase.COLUMNVOICE_DATE_CREATION)));
					this._synced = (dataCursor.getString(dataCursor
							.getColumnIndex(
									ProofDataBase.COLUMN_ISYNC_NOV)));
					
				}
				return true;
			}
			catch(Exception e) {
				Console.print_exception(e);
				return false;
			}
			finally {
				if(dataCursor != null) {
					dataCursor.close();
				}
			}			
		}		
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
