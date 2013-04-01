package org.proof.recorder.fragment.contacts.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.SimplePhoneNumber;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.telephony.PhoneNumberUtils;

import android.util.Log;

public final class ContactsDataHelper {

	private static enum type {
		EXCLUDED, 
		PHONE, 
		CALLS_FOLDER_OF_KNOWN, 
		CALLS_FOLDER_OF_UNKNOWN, 
		INCOMMING_CALLS, 
		OUTGOINGS_CALLS
	}

	private final static String TAG = "ContactsDataHelper";

	private static Context mContext;
	private static ArrayList<Contact>   mExcludedContacts, 
										mPhoneContacts, 
										mCallsFoldersKnownContacts, 
										mCallsFoldersUnKnownContacts;

	private static ArrayList<Record> mIncommingCalls, mOutGoingCalls;

	private static String[] phoneProjection = new String[] {
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.HAS_PHONE_NUMBER,
			ContactsContract.Contacts._ID };

	private static String[] excludedProjection = new String[] {
			ProofDataBase.COLUMN_CONTACT_ID,
			ProofDataBase.COLUMN_CONTRACT_CONTACTS_ID,
			ProofDataBase.COLUMN_DISPLAY_NAME,
			ProofDataBase.COLUMN_PHONE_NUMBER };
	
	private static String[] callsFolderProjection = new String[] {
		ProofDataBase.COLUMNRECODINGAPP_ID,
		ProofDataBase.COLUMN_TELEPHONE};
	
	private static String[] InAndOutProjection = new String[] { 
	ProofDataBase.COLUMNRECODINGAPP_ID,
	ProofDataBase.COLUMN_TELEPHONE, ProofDataBase.COLUMN_SENS,
	ProofDataBase.COLUMN_HTIME, ProofDataBase.COLUMN_FILE, 
	ProofDataBase.COLUMN_CONTRACT_ID };

	// private Constructor -> force to call methods only
	private ContactsDataHelper() {}

	private static void _getExcludedList() {
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "excluded_contacts");
		
		getContacts(uri, excludedProjection, null, null,
				ProofDataBase.COLUMN_DISPLAY_NAME, type.EXCLUDED);
	}
	
	private static void _getExcludedContact(String phone) {
		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, 
				"excluded_contact_phone/" + phone);
		
		getContacts(uri, excludedProjection, null, null,
				ProofDataBase.COLUMN_DISPLAY_NAME, type.EXCLUDED);
	}

	private static void _getPhoneList() {
		getContacts(ContactsContract.Contacts.CONTENT_URI, phoneProjection,
				ContactsContract.Contacts.HAS_PHONE_NUMBER + "=?",
				new String[] { "1" }, ContactsContract.Contacts.DISPLAY_NAME,
				type.PHONE);
	}
	
	private static void _getCallsFoldersOfKnown() {		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "record_distinct_known_contacts");		
		getContacts(uri, callsFolderProjection,
				null, null, ProofDataBase.COLUMNRECODINGAPP_ID, type.CALLS_FOLDER_OF_KNOWN);
	}
	
	private static void _getCallsFoldersOfUnKnown() {		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "record_distinct_unknown_contacts");	
		getContacts(uri, callsFolderProjection,
				null, null, ProofDataBase.COLUMNRECODINGAPP_ID, type.CALLS_FOLDER_OF_UNKNOWN);
	}
	
	private static void _getIncommingCalls(String mPhoneOrId, String mWhere) {
		String appendPath;
		
		if(mWhere.equalsIgnoreCase("phone"))
			appendPath = "record_tel/";
		else if (mWhere.equalsIgnoreCase("android_id"))
			appendPath = "records_by_android_id/";
		else
			throw new IllegalStateException("Bad appendPath variable line: 118 in _getIncommingCalls()");
		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, appendPath + mPhoneOrId);
		
		getContacts(uri, InAndOutProjection,
				null, null, ProofDataBase.COLUMN_HTIME, type.INCOMMING_CALLS);
	}
	
	private static void _getOutGoingCalls(String mPhone, String mWhere) {
		String appendPath;
		
		if(mWhere.equalsIgnoreCase("phone"))
			appendPath = "record_tel/";
		else if (mWhere.equalsIgnoreCase("android_id"))
			appendPath = "records_by_android_id/";
		else
			throw new IllegalStateException("Bad appendPath variable line: 135 in _getOutGoingCalls()");
		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, appendPath + mPhone);
		getContacts(uri, InAndOutProjection,
				null, null, ProofDataBase.COLUMN_HTIME, type.OUTGOINGS_CALLS);
	}
	
	private static class DupeContactComparator implements Comparator<Contact>{

		@Override
		public int compare(Contact lhs, Contact rhs) {
			if(!lhs.getContactName().equals("? (Contact Inconnu)"))
				return lhs.getContactName().compareToIgnoreCase(rhs.getContactName());
			return -1;
		}           
    }
	
	/*private static class DupeRecordComparator implements Comparator<Record>{

		@Override
		public int compare(Record lhs, Record rhs) {
			return lhs.getmId().compareToIgnoreCase(rhs.getmId());
		}           
    }*/
	
	private static void removeContactsDuplicates(List<Contact> list)
	{
	    TreeSet<Contact> set = new TreeSet<Contact>(new DupeContactComparator());
	    // this should give you a tree set without duplicates
	    set.addAll(list);
	
	    list.clear();
	    list.addAll(set);
	}
	
/*	private static void removeRecordsDuplicates(List<Record> list)
	{
	    TreeSet<Record> set = new TreeSet<Record>(new DupeRecordComparator());
	    // this should give you a tree set without duplicates
	    set.addAll(list);
	
	    list.clear();
	    list.addAll(set);
	}*/

	public static ArrayList<Contact> getPhoneList(Context context) {
		mContext = context;
		_getExcludedList();
		_getPhoneList();
		removeContactsDuplicates(mPhoneContacts);
		return mPhoneContacts;
	}

	public static ArrayList<Contact> getExcludedList(Context context) {
		mContext = context;
		_getExcludedList();
		removeContactsDuplicates(mExcludedContacts);
		return mExcludedContacts;
	}
	
	public static ArrayList<Contact> getCallsFoldersOfKnown(Context context) {
		mContext = context;
		_getCallsFoldersOfKnown();
		//removeContactsDuplicates(mCallsFoldersKnownContacts);
		return mCallsFoldersKnownContacts;
	}
	
	public static ArrayList<Contact> getCallsFoldersOfUnKnown(Context context) {
		mContext = context;
		_getCallsFoldersOfUnKnown();
		//removeContactsDuplicates(mCallsFoldersUnKnownContacts);
		return mCallsFoldersUnKnownContacts;
	}
	
	public static ArrayList<Record> getIncommingCalls(Context context, String mWhere, String mPhone) {
		mContext = context;	
		print("mPhone or Id" + mPhone + " mWhere: " + mWhere);
		_getIncommingCalls(mPhone, mWhere);	
		//removeRecordsDuplicates(mIncommingCalls);
		return mIncommingCalls;
	}

	public static ArrayList<Record> getOutGoingCalls(Context context, String mWhere, String mPhone) {
		mContext = context;
		print("mPhone or Id" + mPhone + " mWhere: " + mWhere);
		_getOutGoingCalls(mPhone, mWhere);
		//removeRecordsDuplicates(mOutGoingCalls);
		return mOutGoingCalls;
	}
	
	private static void _print(String message, char level) {
		
		switch(level) {
		
		case 'v':
			Log.v(TAG, message);
			break;
			
		case 'i':
			Log.i(TAG, message);
			break;
		
		case 'e':
			Log.d(TAG, message);
			break;
			
		default:
			Log.d(TAG, message);
			break;
		}
		
	}
	
	/**
	 * @param message
	 */
	private static void print(String message) {	
		_print(message, 'd');	
	}
	
	/**
	 * @param message
	 */
	@SuppressWarnings("unused")
	private static void print_exception(String message) {
		_print(message, 'e');	
	}
	
	public static boolean isExcluded(Context context, String phoneNumber) {
		
		mContext = context;	
		
		SimplePhoneNumber _phoneNumber = new SimplePhoneNumber(phoneNumber);
		_phoneNumber.toConsole();		
		
		_getExcludedContact(
				PhoneNumberUtils.stripSeparators(phoneNumber));
		
		return mExcludedContacts.size() > 0;
	}
	
	private static void cursorToInAndOutCallsAdapter(Cursor cursor) {
		
		Record.setResolver(mContext.getApplicationContext().getContentResolver());
		
		mIncommingCalls = new ArrayList<Record>();
		mOutGoingCalls = new ArrayList<Record>();
		
		while (cursor.moveToNext()) {
			
			
			String mId = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMNRECODINGAPP_ID));
			
			String mAndroidId = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_CONTRACT_ID));
			
			String mPhone = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE));
			
			String mFile = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_FILE));
			
			String mHtime = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_HTIME));
			
			String mSense = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_SENS));
			
			Record mRecord = new Record(
					mId, mFile, mPhone, mSense, mHtime, mAndroidId);
			
			if(mRecord.isIncomingCall())
				mIncommingCalls.add(mRecord);
			else
				mOutGoingCalls.add(mRecord);
		}
	}
	
	private static void cursorToCallsFolderAdapter(Context mContext, Cursor cursor, type t) {
		
		switch (t) {
		case CALLS_FOLDER_OF_KNOWN:
			mCallsFoldersKnownContacts = new ArrayList<Contact>();
			break;
			
		case CALLS_FOLDER_OF_UNKNOWN:
			mCallsFoldersUnKnownContacts = new ArrayList<Contact>();
			break;

		default:
			throw new IllegalArgumentException(TAG
					+ " -> Ce type n'est pas pris en charge!");
		}
		
		
		while (cursor.moveToNext()) {
			String phone = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE));
			
			Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
					mContext, phone);
			
			switch (t) {
			case CALLS_FOLDER_OF_KNOWN:
				if(!mCallsFoldersKnownContacts.contains(mContact)) {
					mCallsFoldersKnownContacts.add(mContact);
				}				
				break;
				
			case CALLS_FOLDER_OF_UNKNOWN:
				if(!mCallsFoldersUnKnownContacts.contains(mContact)) {
					mCallsFoldersUnKnownContacts.add(mContact);
				}
				break;

			default:
				throw new IllegalArgumentException(TAG
						+ " -> Ce type n'est pas pris en charge!");
			}
						
		}
	}

	private static void cursorToPhoneAdapter(Cursor cursor) {
		mPhoneContacts = new ArrayList<Contact>();
		while (cursor.moveToNext()) {
			

			String contactId = cursor.getString(cursor
					.getColumnIndex(BaseColumns._ID));

			String name = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			
			String phone = "null";
			
			Cursor pCur = mContext.getApplicationContext().getContentResolver().query(
					CommonDataKinds.Phone.CONTENT_URI, null,
					CommonDataKinds.Phone.CONTACT_ID + " = ?",
					new String[] { contactId }, null);
			
			try
			{
						
				while (pCur.moveToNext()) {
					phone = pCur.getString(pCur
							.getColumnIndex(CommonDataKinds.Phone.NUMBER));		
				}				

			}catch(Exception e)
			{
				if(Settings.isDebug())
					Log.e(TAG, e.getMessage());
			}
			finally {
				pCur.close();
			}
			
			Contact contact = new Contact(phone);

			contact.setContractId(contactId);
			contact.setContactName(name);
			
			mPhoneContacts.add(contact);
			
			for (Contact c : mExcludedContacts)
			{
				if(c.getContactName().contains(contact.getContactName()) && 
						c.getContractId().contains(contact.getContractId()) && 
						c.getsPhoneNumber().get_nationalNumber().contains(
								contact.getsPhoneNumber().get_nationalNumber()))			
					mPhoneContacts.remove(contact);
			}
		}
	}

	private static void cursorToExcludedAdapter(Cursor cursor) {
		mExcludedContacts = new ArrayList<Contact>();
		while (cursor.moveToNext()) {
			

			String contactId = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_CONTACT_ID));
			String contractId = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_CONTRACT_CONTACTS_ID));
			String name = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_DISPLAY_NAME));
			String phone = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_PHONE_NUMBER));
			
			Contact contact = new Contact(phone);

			contact.setId(contactId);
			contact.setContractId(contractId);
			contact.setContactName(name);
			
			if(Settings.isDebug()) {
				print("Excluded contact added to collection: " + contact);
			}

			mExcludedContacts.add(contact);
		}
	}

	private static void getContacts(Uri uri, String[] projection,
			String Selection, String[] selectionArgs, String Sorted, type t) {
		
		Cursor cursor = mContext.getContentResolver().query(uri,
				projection, Selection, selectionArgs, Sorted);
		
		try {
			
			switch (t) {

			case EXCLUDED:
				cursorToExcludedAdapter(cursor);
				break;

			case PHONE:
				cursorToPhoneAdapter(cursor);
				break;
				
			case CALLS_FOLDER_OF_KNOWN:
				cursorToCallsFolderAdapter(mContext, cursor, t);
				break;
				
			case CALLS_FOLDER_OF_UNKNOWN:
				cursorToCallsFolderAdapter(mContext, cursor, t);
				break;
				
			case INCOMMING_CALLS:
				cursorToInAndOutCallsAdapter(cursor);
				break;
				
			case OUTGOINGS_CALLS:
				cursorToInAndOutCallsAdapter(cursor);
				break;

			default:
				throw new IllegalArgumentException(TAG
						+ " -> Ce type n'est pas pris en charge!");
			}

		} catch (Exception e) {
			
			if(Settings.isDebug())
				Log.e(TAG, e.getMessage());
		}
		finally {
			cursor.close();
		}
	}

}