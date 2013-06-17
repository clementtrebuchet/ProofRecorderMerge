package org.proof.recorder.database.collections;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.utils.Log.Console;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class RecordsList {

	private List<Record> _collection = null;
	private Cursor _cursor = null;
	private ContentResolver _resolver = null;
	private Calendar calendar = null;
	private String periodic = null;
	private Uri innerUri = null;
	
	private String[] projection = null, selectionArgs;
	private String selection, sortOrder = null;
	
	private boolean onlyIds = false;
	
	public boolean autoClean(String periodic) {
		
		Console.print_debug(
				String.format(
						"Received from User's Preferences for 'auto_clean' key: %s", 
						periodic));
		
		if(this.periodic.equals("NEVER")) {
			return true;
		}
		
		this._collection.clear(); // clearing all objects left.
		
		this.periodic = periodic;
		this.onlyIds = true;
		
		this.queryCursor();
		this.fillCollection();
		this.fullClean();
		
		return true;
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
	
	private void prepareSelection() {
		
		Date beforeThat = null;
		boolean rollback = false;
		
		this.calendar = Calendar.getInstance();
		
		if(this.periodic.equals("2DAYS")) {
			this.calendar.roll(Calendar.DAY_OF_MONTH, rollback);
			this.calendar.roll(Calendar.DAY_OF_MONTH, 2);
			beforeThat = (Date) this.calendar.getTime();
		}
		else if(this.periodic.equals("1WEEK")) {
			this.calendar.roll(Calendar.DAY_OF_MONTH, rollback);
			this.calendar.roll(Calendar.DAY_OF_MONTH, 7);
			beforeThat = (Date) this.calendar.getTime();
		}
		else if(this.periodic.equals("1MONTH")) {
			this.calendar.roll(Calendar.DAY_OF_MONTH, rollback);
			this.calendar.roll(Calendar.DAY_OF_MONTH, 30);
			beforeThat = (Date) this.calendar.getTime();
		}
		else if(this.periodic.equals("6MONTHS")) {
			this.calendar.roll(Calendar.DAY_OF_MONTH, rollback);
			this.calendar.roll(Calendar.DAY_OF_MONTH, 180);
			beforeThat = (Date) this.calendar.getTime();
		}
		else {} // Should never happened!
		
		String select = "WHERE %s<=?";
		this.selection = String.format(select, ProofDataBase.COLUMN_TIMESTAMP);
		this.selectionArgs = new String[] {
				beforeThat.toString()
		};
	}

	private Record createRecord(
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
		
		return record;
	}
	
	private Report fullClean() {
		for(Record rec : this._collection) {
			rec.delete();
		}		
		return null;
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

	private Record createRecord(String id) {
		Record record = new Record();
		record.setmId(id);
		
		if(this._collection == null)
			this._collection = new ArrayList<Record>();

		this._collection.add(record);
		
		return record;		
	}
	
}
