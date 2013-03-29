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
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;
import android.widget.Toast;

/**
 * @author devel.machine
 * 
 * This service ensure n database consistency checks.
 * 
 * As follow:
 * 
 * 	1. When Incoming call arise, 
 *     if caller is unknown from contacts API, the record 
 *     is stored into db as unknown Contact.
 *     But, if user add the contact to the phone list, our
 *     db state remains still.
 *     
 *  2. If a known contact for our db and this one is been deleted
 *     by the user, our db must map info.
 *     
 */
public class VerifyContactsApi extends Service {

	private static final String TAG = "VerifyContactsApi";

	private Context mContext;
	private Intent mIntent;

	// Proof Contacts database 'projection'
	private static String[] proofProjection = new String[] {
		ProofDataBase.COLUMNRECODINGAPP_ID,
		ProofDataBase.COLUMN_CONTRACT_ID,
		ProofDataBase.COLUMN_TELEPHONE};	

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
			Log.d(TAG, "Received start id " + startId + ": " + mIntent.getFlags());
			Log.d(TAG, "Starting database consistency checks ...");

		}

		// check potential deleted contacts from phone API.
		this.checksDeletedContacts();
		Log.d(TAG, "Potential deleted Contacts from Phone API mapped!");


		Toast.makeText(
				mContext, 
				mContext.getString(R.string.analysis_over), 
				Toast.LENGTH_LONG)
				.show();

		return START_STICKY;
	}



	/**
	 * Checks if some previous known contacts have been deleted
	 * in phone contacts API, if so, set the Contact database object
	 * to corresponding state.
	 */
	private void checksDeletedContacts() {

		Uri mUri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "records");

		Cursor mCursor = mContext.getContentResolver().query(mUri, proofProjection, null, null, null);

		try {
			while(mCursor.moveToNext()) {

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
									ProofDataBase.COLUMN_TELEPHONE
									));

					Contact mContact = AndroidContactsHelper.getContactInfosByNumber(mContext, mPhone);
					if(mContact.getContactName() != "? " + mContext.getString(R.string.unknownContact)) {
						ContentValues values = new ContentValues();
						values.put(ProofDataBase.COLUMN_CONTRACT_ID, mContact.getContractId());
						mContext.getContentResolver().update(
								mUri,
								values, 
								" " + ProofDataBase.COLUMN_CONTRACT_ID + "=?",
								new String[] { apiId }
								);
					}
				}				
			}		
		}
		catch(Exception e) {
			Log.e(TAG, "" + e);
		}
		finally {
			mCursor.close();
		}

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
