package org.proof.recorder.service;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.OsHandler;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;

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
/**
 * @author devel.machine
 *
 */
/**
 * @author devel.machine
 *
 */
public class VerifyContactsApi extends Service {

	private static boolean toProcess = false;
	private static boolean mExternalStorageAvailable = false;
	private static boolean mExternalStorageWriteable = false;
	
	private static final String PREFS_NAME = "ProofRecordsInfo";
	
	private static final int DELTA_COUNT = 3;
	private static int START_ID = 0;

	private static Context mContext;
	private static Cursor mCursor;
	private static Intent mIntent;
	private static Uri mUri = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "records");

	// Proof Contacts database 'projection'
	private static String[] proofProjection = new String[] {
		ProofDataBase.COLUMNRECODINGAPP_ID,
		ProofDataBase.COLUMN_CONTRACT_ID,
		ProofDataBase.COLUMN_TELEPHONE};
	
	/**
	 * @param message
	 */
	private void print(String message) {
		if(Settings.isDebug())
			Log.d(this.getClass().getName(), "" + message);
	}

	/**
	 * @param message
	 */
	private void print_exception(String message) {
		Log.e(this.getClass().getName(), "" + message);
	}

	/**
	 * @return
	 */
	private boolean isRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("org.proofs.recorder.service.VerifyContactsApi"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return the mExternalStorageAvailable
	 */
	public static boolean isExternalStorageAvailable() {
		return mExternalStorageAvailable;
	}

	/**
	 * @return the mExternalStorageWriteable
	 */
	public static boolean isExternalStorageWriteable() {
		return mExternalStorageWriteable;
	}
	
	/**
	 * @return the toProcess
	 */
	public static boolean isToProcess() {
		return toProcess;
	}

	/**
	 * @param toProcess the toProcess to set
	 */
	public static void setToProcess(boolean toProcess) {
		VerifyContactsApi.toProcess = toProcess;
	}
	
	/**
	 * @param count
	 */
	private void setNewCount(int count) {
	      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putInt("count", count);
	      editor.commit();		
	}	

	/**
	 * @return
	 */
	private int getPreviousCount() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		return settings.getInt("count", 0);
	}

	/**
	 * 
	 */
	private void initializeDbConnection() {

		mCursor = mContext.getContentResolver().query(
				mUri, proofProjection, null, null, null);
	}
	
	/**
	 * 
	 */
	private void evaluateMedia() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}

	/**
	 * 
	 */
	private void evaluateContext() {

		int previousCount = getPreviousCount() + DELTA_COUNT;
		int count = mCursor.getCount();
		
		print("Count: " + count + " Previous: " + previousCount);

		if(count > previousCount) {
			setToProcess(true);
			setNewCount(count);
		}
		else {
			mCursor.close();
		}		
	}

	/**
	 * Checks if some previous known contacts have been deleted
	 * in phone contacts API, if so, set the Contact database object
	 * to corresponding state.
	 */
	private void checksDeletedContacts() {

		try {

			while(mCursor.moveToNext()) {

				String apiId = mCursor.getString(
						mCursor.getColumnIndex(
								ProofDataBase.COLUMN_CONTRACT_ID
								));


				if(apiId.equals("null")) { // Unknown contact

					print("Unknown contact (id): " + apiId
							+ " - type(" + apiId.getClass().getName() + ")");

					String mPhone = mCursor.getString(
							mCursor.getColumnIndex(
									ProofDataBase.COLUMN_TELEPHONE
									));

					print("Unknown contact (phone): " + mPhone);

					Contact mContact = AndroidContactsHelper.getContactInfosByNumber(mContext, mPhone);

					print("Unknown contact (all): " + mContact);

					if(!mContact.getContractId().equals("null")) {

						ContentValues values = new ContentValues();
						values.put(
								ProofDataBase.COLUMN_CONTRACT_ID, 
								mContact.getContractId());

						print("Unknown contact (all): " + mContact + "\n" + "Uri: " + mUri);

						mContext.getContentResolver().update(
								mUri,
								values, 
								" " + ProofDataBase.COLUMN_TELEPHONE + "=?",
								new String[] { mPhone }
								);
					}
				}
				else { // Known contact

					print("Known contact (id): " + apiId + " - type(" + apiId.getClass().getName() + ")");

					Cursor pCur = mContext.getContentResolver().query(
							CommonDataKinds.Phone.CONTENT_URI, null,
							CommonDataKinds.Phone.CONTACT_ID + " = ?",
							new String[] { apiId }, null);

					if(pCur.getCount() == 0) {
						print("Deleted contact API phone Id: " + apiId);
						ContentValues values = new ContentValues();
						values.put(ProofDataBase.COLUMN_CONTRACT_ID, "null");
						mContext.getContentResolver().update(
								mUri,
								values, 
								" " + ProofDataBase.COLUMN_CONTRACT_ID + "=?", 
								new String[] { apiId }
								);

						print("Updated contact API phone Id: " + apiId);						
						print("Checking for into excluded contacts table for match ...");						

						Uri mUriById = Uri.withAppendedPath(
								PersonnalProofContentProvider.CONTENT_URI, "excluded_contract_id/" + apiId);

						Cursor _cursor = mContext.getContentResolver().query(mUriById, null, null, null, null);

						if(_cursor.getCount() > 0) {

							String name = _cursor.getColumnName(_cursor.getColumnIndex(ProofDataBase.COLUMN_DISPLAY_NAME));
							String phone = _cursor.getColumnName(_cursor.getColumnIndex(ProofDataBase.COLUMN_PHONE_NUMBER));
							print("Found Contact in excluded table! (" + name + " - " + phone + ")");
							mContext.getContentResolver().delete(mUriById, null, null);
							print("Deleted!");
						}
					}

					if( pCur != null && !pCur.isClosed() ) {
						pCur.close();
					}						
				}				
			}				
		}
		catch(Exception e) {
			print_exception("" + e);
		}
		finally {
			mCursor.close();
		}

	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		super.onStartCommand(intent, flags, startId);
		
		START_ID = startId;
		
		if(intent == null) stopSelf(startId);

		if(!isRunning()) {
			
			try {
				do {
					evaluateMedia();
				}			
				while(!isExternalStorageAvailable());
			}catch(Exception e) {
				print_exception(e.getMessage());
			}			

			mIntent = intent;

			initializeDbConnection();			
			evaluateContext();

			if(isToProcess()) {

				print("Received start id " + startId + ": " + mIntent.getFlags());
				print("Starting database consistency checks ...");

				// Check if all stored record into database really point onto existing 
				// disk resource, if not delete the db record, on counter part, if a file
				// has no corresponding db record, it's deleted.

				OsHandler.checkDirectoriesStructureIntegrity(this);

				// check potential deleted contacts from phone API.

				checksDeletedContacts();

				print("Potential deleted Contacts from Phone API mapped!");
				print(mContext.getString(R.string.analysis_over));

				setToProcess(false);
			}
		}
		
		return startId;
	}
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf(START_ID);
	}

	@Override
	public IBinder onBind(Intent paramIntent) {
		return null;
	}

}
