package org.proof.recorder.database.collections;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.utils.Log.Console;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordsList {

	private List<Record> _collection = null;
	private Cursor _cursor = null;
	private final ContentResolver _resolver = null;
	private String periodic = null;
	private final Uri innerUri = null;

	private final String[] projection = null;
	private String[] selectionArgs;
	private String selection;
	private final String sortOrder = null;
	
	private boolean onlyIds = false;

	public void autoClean(String periodic) {
		
		Console.print_debug(
				String.format(
						"Received from User's Preferences for 'auto_clean' key: %s", 
						periodic));
		
		if(this.periodic.equals("NEVER")) {
			return;
		}
		
		this._collection.clear(); // clearing all objects left.
		
		this.periodic = periodic;
		this.onlyIds = true;
		
		this.queryCursor();
		this.fillCollection();
		this.fullClean();

	}
	
	private void queryCursor() {
		this.prepareSelection();
		this._cursor = this._resolver.query(
				innerUri, 
				projection,  // projection
				selection,  // selection
				selectionArgs,  // selectionArgs
				sortOrder); // sortOrder		
	}

	@SuppressWarnings("StatementWithEmptyBody")
	private void prepareSelection() {
		
		Date beforeThat = null;
		boolean rollback = false;

		Calendar calendar = Calendar.getInstance();
		
		if(this.periodic.equals("2DAYS")) {
			calendar.roll(Calendar.DAY_OF_MONTH, false);
			calendar.roll(Calendar.DAY_OF_MONTH, 2);
			beforeThat = calendar.getTime();
		}
		else if(this.periodic.equals("1WEEK")) {
			calendar.roll(Calendar.DAY_OF_MONTH, false);
			calendar.roll(Calendar.DAY_OF_MONTH, 7);
			beforeThat = calendar.getTime();
		}
		else if(this.periodic.equals("1MONTH")) {
			calendar.roll(Calendar.DAY_OF_MONTH, false);
			calendar.roll(Calendar.DAY_OF_MONTH, 30);
			beforeThat = calendar.getTime();
		}
		else if(this.periodic.equals("6MONTHS")) {
			calendar.roll(Calendar.DAY_OF_MONTH, false);
			calendar.roll(Calendar.DAY_OF_MONTH, 180);
			beforeThat = calendar.getTime();
		}
		else {} // Should never happened!
		
		String select = "WHERE %s<=?";
		this.selection = String.format(select, ProofDataBase.COLUMN_TIMESTAMP);
		assert beforeThat != null;
		this.selectionArgs = new String[] {
				beforeThat.toString()
		};
	}

	private void createRecord(
			String id,
			String fileName,
			String phone,
			String sense,
			String htime,
			String mAndroidId) {
		
		Record record = new Record(id, fileName, phone, sense, htime, mAndroidId);
		
		if(this._collection == null)
			this._collection = new ArrayList<Record>();

		this._collection.add(record);

	}

	private void fullClean() {
		for(Record rec : this._collection) {
			rec.delete();
		}
	}
	
	private void fillCollection() {
		
		Cursor dataCursor = this._cursor;
		while (dataCursor != null && dataCursor.moveToNext()) {
			
			String id = dataCursor.getString(dataCursor
					.getColumnIndex(ProofDataBase.COLUMNRECODINGAPP_ID));
			
			if(!onlyIds) {
				String phone = dataCursor.getString(dataCursor
						.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE));
				
				String fileName = dataCursor.getString(dataCursor
						.getColumnIndex(ProofDataBase.COLUMN_FILE));
				
				String sense = dataCursor.getString(dataCursor
						.getColumnIndex(ProofDataBase.COLUMN_SENS));
				
				String humanTime = dataCursor.getString(dataCursor
						.getColumnIndex(ProofDataBase.COLUMN_HTIME));
				
				String mAndroidId = dataCursor.getString(dataCursor
						.getColumnIndex(ProofDataBase.COLUMN_CONTRACT_ID));
				
				this.createRecord(id, fileName, phone, sense, humanTime, mAndroidId);
			} else {			
				this.createRecord(id);
			}
		}
	}

	private void createRecord(String id) {
		Record record = new Record();
		record.setmId(id);
		
		if(this._collection == null)
			this._collection = new ArrayList<Record>();

		this._collection.add(record);
	}
	
}
