package org.proof.recorder.database.collections;

import java.util.ArrayList;
import java.util.List;

import org.proof.recorder.database.models.Voice;
import org.proof.recorder.database.support.ProofDataBase;

import android.database.Cursor;

public class VoicesList {
	

	private List<Object> _collection = null;
	private Cursor _cursor;
	
	/**
	 * 
	 */
	public VoicesList(String uri) {
		super();
		
	}
	
	public VoicesList(Cursor cursor) {
		super();
		setCursor(cursor);
		fillCollection();
	}
	
	private Voice createVoice(String _id, String _humanTime, String _fileSize,
			String _timestamp, String _filePath) {
		Voice voice = new Voice(_id, _humanTime, _fileSize, _timestamp, _filePath);
		
		if(this.getCollection() == null)
			this.setCollection(new ArrayList<Object>());
		
		this.getCollection().add(voice);
		return voice;
	}

	/**
	 * @return the _collection
	 */
	public List<Object> getCollection() {
		return _collection;
	}

	/**
	 * @param _collection the _collection to set
	 */
	public void setCollection(List<Object> _collection) {
		this._collection = _collection;
	}

	/**
	 * @return the _cursor
	 */
	public Cursor getCursor() {
		return _cursor;
	}

	/**
	 * @param _cursor the _cursor to set
	 */
	public void setCursor(Cursor _cursor) {
		this._cursor = _cursor;
	}
	
	/**
	 * 
	 */
	private void fillCollection() {
		
		Cursor dataCursor = getCursor();
		while (dataCursor != null && dataCursor.moveToNext()) {
			
			String _id = dataCursor.getString(dataCursor
					.getColumnIndex(ProofDataBase.COLUMNVOICE_ID));
			
			String _timestamp = dataCursor.getString(dataCursor
					.getColumnIndex(ProofDataBase.COLUMN_VOICE_TIMESTAMP));
			
			String _filePath = dataCursor.getString(dataCursor
					.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE));
			
			String _fileSize = dataCursor.getString(dataCursor
					.getColumnIndex(ProofDataBase.COLUMN_VOICE_TAILLE));
			
			String _humanTime = dataCursor.getString(dataCursor
					.getColumnIndex(ProofDataBase.COLUMN_VOICE_HTIME));
			
			this.createVoice(_id, _humanTime, _fileSize, _timestamp, _filePath);
		}
	}
}
