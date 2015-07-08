package org.proof.recorder.database.support;


import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import org.proof.recorder.utils.Log.Console;

import java.util.Locale;

//Database open/upgrade helper
public class ProofDataBaseHelper extends SQLiteOpenHelper {

    public ProofDataBaseHelper(Context context, String name, 
            CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }
    
    private static final String DATABASE_NAME = "proofdatabase.db";
	private static final int DATABASE_VERSION = 18;
	
	public ProofDataBaseHelper(Context context) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
		Console.setTagName(this.getClass().getSimpleName());
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ProofDataBase.onCreate(db);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onConfigure(SQLiteDatabase db) {
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
			try {
				db.enableWriteAheadLogging();
			} catch (Exception ignored) {
				
			}					
		}
		
		if(currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			try {
				db.setForeignKeyConstraintsEnabled(true);
			} catch (Exception ignored) {
				
			}
		}
		
		try {
			db.setLocale(Locale.getDefault());
		} catch (Exception ignored) {
			
		}	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {		
		Console.print_debug("UPGRADE DB");		
		ProofDataBase.onUpgrade(db, oldVersion, newVersion);		
	}
}
