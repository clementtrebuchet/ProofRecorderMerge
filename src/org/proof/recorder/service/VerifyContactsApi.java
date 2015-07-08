package org.proof.recorder.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.service.ProofService;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.Log.Console;
import org.proof.recorder.utils.OsHandler;

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
public class VerifyContactsApi extends ProofService {

	private static boolean toProcess = false;
	private static boolean mExternalStorageAvailable = false;
	private static boolean mExternalStorageWriteable = false;
	
	private static final int DELTA_COUNT = 3;
	private static int START_ID = 0;

	private static Cursor mCursor;

	private static final Uri mUri = Uri.withAppendedPath(
			PersonnalProofContentProvider.CONTENT_URI, "records");

	// Proof Contacts database 'projection'
	private static final String[] proofProjection = new String[]{
		ProofDataBase.COLUMNRECODINGAPP_ID,
		ProofDataBase.COLUMN_CONTRACT_ID,
		ProofDataBase.COLUMN_TELEPHONE};

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
	private static boolean isExternalStorageAvailable() {
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
	private static boolean isToProcess() {
		return toProcess;
	}

	/**
	 * @param toProcess the toProcess to set
	 */
	private static void setToProcess(boolean toProcess) {
		VerifyContactsApi.toProcess = toProcess;
	}
	
	/**
	 * @param count
	 */
	private void setNewCount(int count) {	      
	      Settings.setRecordsCount(count);
	}	

	/**
	 * @return
	 */
	private int getPreviousCount() {
		return Settings.getRecordsCount();
	}

	/**
	 * 
	 */
	private void initializeDbConnection() {

		mCursor = getInternalContext().getContentResolver().query(
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
		
		Console.print_debug("Count: " + count + " Previous: " + previousCount);

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

					Console.print_debug("Unknown contact (id): " + apiId
							+ " - type(" + apiId.getClass().getName() + ")");

					String mPhone = mCursor.getString(
							mCursor.getColumnIndex(
									ProofDataBase.COLUMN_TELEPHONE
									));
					
					Console.print_debug("Unknown contact (phone): " + mPhone);

					Contact mContact = AndroidContactsHelper.getContactInfosByNumber(getInternalContext(), mPhone);

					Console.print_debug("Unknown contact (all): " + mContact);

					if(!mContact.getContractId().equals("null")) {

						ContentValues values = new ContentValues();
						values.put(
								ProofDataBase.COLUMN_CONTRACT_ID, 
								mContact.getContractId());
						
						Console.print_debug("Unknown contact (all): " + mContact + "\n" + "Uri: " + mUri);

						getInternalContext().getContentResolver().update(
								mUri,
								values, 
								" " + ProofDataBase.COLUMN_TELEPHONE + "=?",
								new String[] { mPhone }
								);
					}
				}
				else { // Known contact
					
					Console.print_debug("Known contact (id): " + apiId + " - type(" + apiId.getClass().getName() + ")");

					Cursor pCur = getInternalContext().getContentResolver().query(
							CommonDataKinds.Phone.CONTENT_URI, null,
							CommonDataKinds.Phone.CONTACT_ID + " = ?",
							new String[] { apiId }, null);

					if(pCur.getCount() == 0) {
						Console.print_debug("Deleted contact API phone Id: " + apiId);
						ContentValues values = new ContentValues();
						values.put(ProofDataBase.COLUMN_CONTRACT_ID, "null");
						getInternalContext().getContentResolver().update(
								mUri,
								values, 
								" " + ProofDataBase.COLUMN_CONTRACT_ID + "=?", 
								new String[] { apiId }
								);
						
							Console.print_debug("Updated contact API phone Id: " + apiId);	
							Console.print_debug("Checking for into excluded contacts table for match ...");			

						Uri mUriById = Uri.withAppendedPath(
								PersonnalProofContentProvider.CONTENT_URI, "excluded_contract_id/" + apiId);

						Cursor _cursor = getInternalContext().getContentResolver().query(mUriById, null, null, null, null);

						if(_cursor.getCount() > 0) {

							String name = _cursor.getColumnName(_cursor.getColumnIndex(ProofDataBase.COLUMN_DISPLAY_NAME));
							String phone = _cursor.getColumnName(_cursor.getColumnIndex(ProofDataBase.COLUMN_PHONE_NUMBER));

								Console.print_debug("Found Contact in excluded table! (" + name + " - " + phone + ")");
							
								getInternalContext().getContentResolver().delete(mUriById, null, null);
							
							Console.print_debug("Deleted!");
						}
					}

					if (!pCur.isClosed()) {
						pCur.close();
					}						
				}				
			}				
		}
		catch(Exception e) {
			Console.print_exception(e);
		}
		finally {
			mCursor.close();
		}

	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		super.onStartCommand(intent, flags, startId);
		
		Console.print_debug("Checks on database contacts and directories state starting ...");
		
		START_ID = startId;
		
		if(intent == null) stopSelf(startId);

		if(!isRunning()) {
			
			try {
				do {
					evaluateMedia();
				}			
				while(!isExternalStorageAvailable());
			}catch(Exception e) {
				Console.print_exception(e);
			}			

			initializeDbConnection();			
			evaluateContext();

			if(isToProcess()) {

				Console.print_debug("Starting database consistency checks ...");

				// Check if all stored record into database really point onto existing 
				// disk resource, if not delete the db record, on counter part, if a file
				// has no corresponding db record, it's deleted.

				OsHandler.checkDirectoriesStructureIntegrity(this);

				// check potential deleted contacts from phone API.

				checksDeletedContacts();

				Console.print_debug("Potential deleted Contacts from Phone API mapped!");
				Console.print_debug(getInternalContext().getString(R.string.analysis_over));

				setToProcess(false);
				
				Console.print_debug("Database consistency checks processed!");
			}
			
			else {
				Console.print_debug("No need to process!");
			}
		}
		else {
			Console.print_debug("Service already running no need to run!");
		}
		
		return START_STICKY;
	}
	

	@Override
	public void onDestroy() {
		super.onDestroy();

			Console.print_debug("Destroyed!");
		
		stopSelf(START_ID);
	}

	@Override
	public IBinder onBind(Intent paramIntent) {
			Console.print_debug("bind!");
		return null;
	}

}
