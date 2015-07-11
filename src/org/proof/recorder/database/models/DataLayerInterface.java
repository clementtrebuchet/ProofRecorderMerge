package org.proof.recorder.database.models;

import android.content.ContentValues;

@SuppressWarnings("unused")
interface DataLayerInterface {
	
	ContentValues _values = new ContentValues();
	
	boolean fillFromDataBase();
	
	void fillValues();
	
	void save();
}
