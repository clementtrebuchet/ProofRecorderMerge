package org.proof.recorder.async;
import org.proof.recorder.R;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;
import android.widget.Toast;

public class VerifyContactsApi extends AsyncTask<Void, Integer, String> {
	
	private static final String TAG = "VerifyContactsApi";
	private Uri mUri;
	private Context mContext;
	private Cursor mCursor;
	
	public VerifyContactsApi(Context _context) {
		mContext = _context;
	}

	@Override
	protected void onPreExecute() {
		mUri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "records");		
	}
	
	@Override
	protected String doInBackground(Void... _context) {
		
		mCursor = mContext.getContentResolver().query(mUri, null, null, null, null);	
		int pg = 1;
		
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
						publishProgress(pg);
						pg++;
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
		
		return mContext.getString(R.string.analysis_over);
	}	
	
	@Override
	protected void onPostExecute(String result) {
		Toast.makeText(mContext, result, Toast.LENGTH_LONG)
		.show();
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		Toast.makeText(
				mContext, 
				mContext.getString(R.string.deleted_contact) + (int)progress[0], 
				Toast.LENGTH_SHORT).show();
	}
}
