package org.proof.recorder.database.collections;

import android.database.Cursor;

import org.proof.recorder.database.models.Voice;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.utils.Log.Console;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class VoicesList {
	
	private List<Object> _collection = null;	

	/**
	 *
	 */
	public VoicesList() {
		super();

	}

	public VoicesList(Cursor cursor) {
		super();
		fillCollection(cursor);
	}

	private void createVoice(String _id, String _humanTime, String _fileSize,
							 String _timestamp, String _filePath) {
		Voice voice = new Voice(_id, _humanTime, _fileSize, _timestamp, _filePath);
		
		if(this.getCollection() == null)
			this.setCollection(new ArrayList<Object>());

		this.getCollection().add(voice);

	}

	/**
	 * @return the _collection
	 */
	public List<Object> getCollection() {
		return _collection;
	}
	
	/**
	 * @return the _collection
	 */
	public List<Object> getCollectionAsObjects() {
		return _collection;
	}

	
	/**
	 * @param _collection the _collection to set
	 */
	private void setCollection(List<Object> _collection) {
		this._collection = _collection;
	}
	
	/**
	 * 
	 */
	private void fillCollection(Cursor dataCursor) {
		
		try {

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
		
		} catch (Exception e) {
			Console.print_exception(e);
		} finally {
			if(dataCursor != null)
				dataCursor.close();
		}
	}
}
