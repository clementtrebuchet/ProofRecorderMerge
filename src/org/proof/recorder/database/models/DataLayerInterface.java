package org.proof.recorder.database.models;

import android.content.ContentValues;

public interface DataLayerInterface {
	
	ContentValues _values = new ContentValues();
	
	void fillValues();
	
	void save();

}
