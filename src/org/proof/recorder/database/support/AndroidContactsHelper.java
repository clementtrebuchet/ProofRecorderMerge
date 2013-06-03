package org.proof.recorder.database.support;

import org.proof.recorder.R;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.SimplePhoneNumber;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;


public final class AndroidContactsHelper {
	private static final String TAG = "AndroidContactsHelper";
	private AndroidContactsHelper() {}

	public static Contact getContactInfosByNumber(Context context, String number) {
			
			Contact mContact = new Contact(number);
			
		    Uri uri = Uri.withAppendedPath(
		    		ContactsContract.PhoneLookup.CONTENT_FILTER_URI, 
		    		Uri.encode(number));
		    
		    String name = "? " + context.getString(R.string.unknownContact);
		    String contactId = "null";
	
		    ContentResolver contentResolver = context.getContentResolver();
		    
		    Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
		            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
	
		    try {
		        if (contactLookup != null && contactLookup.getCount() > 0) {
		            contactLookup.moveToNext();
		            name = contactLookup.getString(
		            		contactLookup.getColumnIndex(
		            				ContactsContract.Data.DISPLAY_NAME));
		            contactId = contactLookup.getString(
		            		contactLookup.getColumnIndex(BaseColumns._ID));
		        }
		    }
		    catch(Exception e) {
		    	Log.e(TAG, " E: " + e.getMessage());
		    }
		    finally {
		        if (contactLookup != null) {
		            contactLookup.close();
		        }
		    }
	
		    mContact.setContractId(contactId);
		    mContact.setContactName(name);
		    
		    return mContact;
		}
	
	
	public static int getTitledVoiceCount() {
		
		int count;
		String mQuery = "SELECT _id from " + ProofDataBase.TABLE_VOICE_NOTES + " WHERE " + ProofDataBase.COLUMN_TITLE + " != \"Insérer un titre\"";
		count = PersonnalProofContentProvider.getItemsCount(mQuery);
		return count;
	}
	
	public static int getUnTitledVoiceCount() {
		int count;
		String mQuery = "SELECT _id from " + ProofDataBase.TABLE_VOICE_NOTES + " WHERE " + ProofDataBase.COLUMN_TITLE + " LIKE \"%Insérer un titre%\"";
		count = PersonnalProofContentProvider.getItemsCount(mQuery);
		return count;
	}
	
	public static int getKnownFolderContactsCount() {
		int count;
		String mQuery = "SELECT _id from " + ProofDataBase.TABLE_RECODINGAPP + " WHERE " + ProofDataBase.COLUMN_CONTRACT_ID + "!=\"null\"";
		count = PersonnalProofContentProvider.getItemsCount(mQuery);
		return count;
	}
	
	public static int getUnKnownFolderContactsCount() {
		int count;
		String mQuery = "SELECT _id from " + ProofDataBase.TABLE_RECODINGAPP + " WHERE " + ProofDataBase.COLUMN_CONTRACT_ID + "=\"null\"";
		count = PersonnalProofContentProvider.getItemsCount(mQuery);
		return count;
	}
	
	public static int getInRecordsCount(String mPhone) {
		int count;
		String appendWhere;
		
			SimplePhoneNumber phone = new SimplePhoneNumber(mPhone);
			appendWhere = ProofDataBase.COLUMN_TELEPHONE + " LIKE \"%" + phone.get_nationalNumber() + "%\"";			

		String mQuery = "SELECT _id from " + ProofDataBase.TABLE_RECODINGAPP + " WHERE " +  appendWhere + " AND " + ProofDataBase.COLUMN_SENS + " LIKE \"%E%\"";
		count = PersonnalProofContentProvider.getItemsCount(mQuery);
		
		return count;
	}
	
	public static int getOutRecordsCount(String mPhone) {
		int count;
		String appendWhere;

			SimplePhoneNumber phone = new SimplePhoneNumber(mPhone);
			appendWhere = ProofDataBase.COLUMN_TELEPHONE + " LIKE \"%" + phone.get_nationalNumber() + "%\"";		

		String mQuery = "SELECT _id from " + ProofDataBase.TABLE_RECODINGAPP + " WHERE " +  appendWhere + " AND " + ProofDataBase.COLUMN_SENS + " LIKE \"%S%\"";
		
		count = PersonnalProofContentProvider.getItemsCount(mQuery);
		
		return count;
	}
}
