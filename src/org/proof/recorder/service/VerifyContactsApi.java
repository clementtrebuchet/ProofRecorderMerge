package org.proof.recorder.service;
import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;
import android.widget.Toast;

public class VerifyContactsApi extends Service {

	private static final String TAG = "VerifyContactsApi";
	private Uri mUri;
	private Context mContext;
	private Intent mIntent;
	private Cursor mCursor;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		super.onStartCommand(intent, flags, startId);
		mIntent = intent;		
		if(Settings.isDebug())
		{
			Log.i(TAG, "Received start id " + startId + ": " + mIntent.getFlags());
			Log.v(TAG, "Starting database consistency checks ...");
			
		}
		
		mUri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "records");
		
		mCursor = mContext.getContentResolver().query(mUri, null, null, null, null);	
		
		if (mCursor.moveToFirst()) {
			
			do {
				String apiId = mCursor.getString(
						mCursor.getColumnIndex(
								ProofDataBase.COLUMN_CONTRACT_ID
							));
				if(!apiId.equals("null")) {
					
					Cursor pCur = mContext.getApplicationContext().getContentResolver().query(
							CommonDataKinds.Phone.CONTENT_URI, null,
							CommonDataKinds.Phone.CONTACT_ID + " = ?",
							new String[] { apiId }, null);
					
					if(pCur.getCount() == 0) {
						Log.d(TAG, "CONTACT SUPPRIME: " + apiId);
						ContentValues values = new ContentValues();
						values.put(ProofDataBase.COLUMN_CONTRACT_ID, "null");
						mContext.getContentResolver().update(
								mUri,
								values, 
								" " + ProofDataBase.COLUMN_CONTRACT_ID + "=?", 
								new String[] { apiId }
						);
						Log.d(TAG, "CONTACT ACTUALISE: " + apiId);
					}
				}
				else {
					String mPhone = mCursor.getString(
							mCursor.getColumnIndex(
									ProofDataBase.COLUMN_PHONE_NUMBER
								));
					
					Contact mContact = AndroidContactsHelper.getContactInfosByNumber(mContext, mPhone);
					if(mContact.getContactName() != "? " + mContext.getString(R.string.unknownContact)) {
						ContentValues values = new ContentValues();
						values.put(ProofDataBase.COLUMN_CONTRACT_ID, "null");
						mContext.getContentResolver().update(
								mUri,
								values, 
								" " + ProofDataBase.COLUMN_CONTRACT_ID + "=?",
								new String[] { apiId }
						);
					}
				}
			}while(mCursor.moveToNext());
		}
		
		mCursor.close();
		
		Toast.makeText(
				mContext, 
				mContext.getString(R.string.analysis_over), 
				Toast.LENGTH_LONG)
		.show();
	
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		stopSelf();
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent paramIntent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
