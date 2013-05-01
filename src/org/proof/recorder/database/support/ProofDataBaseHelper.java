package org.proof.recorder.database.support;


import java.util.Locale;

import org.proof.recorder.Settings;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

//Database open/upgrade helper
public class ProofDataBaseHelper extends SQLiteOpenHelper {
	
	private final static String TAG = "PROOF_DB_BASE_HELPER";

    public ProofDataBaseHelper(Context context, String name, 
            CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }
    
    private static final String DATABASE_NAME = "proofdatabase.db";
	private static final int DATABASE_VERSION = 18;
	
	public ProofDataBaseHelper(Context context) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ProofDataBase.onCreate(db);
		
	}
	
	
	@SuppressLint("NewApi")
	@Override
	public void onConfigure(SQLiteDatabase db) {
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
			db.enableWriteAheadLogging();			
		}
		else if(currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			db.setForeignKeyConstraintsEnabled(true);
		}
		
		db.setLocale(Locale.getDefault());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if(Settings.isDebug())
			Log.e(TAG, "UPGRADE DB");
		
		ProofDataBase.onUpgrade(db, oldVersion, newVersion);
		
	}
}
