package org.proof.recorder.fragment.contacts.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;

public final class ContactsDataHelper {

	private static enum type {
		EXCLUDED, PHONE, CALLS_FOLDER_OF_KNOWN, CALLS_FOLDER_OF_UNKNOWN, INCOMMING_CALLS, OUTGOINGS_CALLS
	}

	private final static String TAG = "ContactsDataHelper";

	private static Context mContext;
	private static ArrayList<Contact> mExcludedContacts, mPhoneContacts, mCallsFoldersKnownContacts, mCallsFoldersUnKnownContacts;

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
	ProofDataBase.COLUMN_CONTACT_ID };

	// private Constructor -> force to call methods only
	private ContactsDataHelper() {
	}

	private static void _getExcludedList() {
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "excluded_contacts");
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
	
	private static void _getIncommingCalls(String mPhone, String mWhere) {
		String appendPath;
		
		if(mWhere.equalsIgnoreCase("phone"))
			appendPath = "record_tel/";
		else if (mWhere.equalsIgnoreCase("android_id"))
			appendPath = "records_by_android_id/";
		else
			throw new IllegalStateException("Bad appendPath variable line: 96 in _getIncommingCalls()");
		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, appendPath + mPhone);		
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
			throw new IllegalStateException("Bad appendPath variable line: 104 in _getOutGoingCalls()");
		
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
		_getIncommingCalls(mPhone, mWhere);	
		//removeRecordsDuplicates(mIncommingCalls);
		return mIncommingCalls;
	}

	public static ArrayList<Record> getOutGoingCalls(Context context, String mWhere, String mPhone) {
		mContext = context;
		_getOutGoingCalls(mPhone, mWhere);
		//removeRecordsDuplicates(mOutGoingCalls);
		return mOutGoingCalls;
	}	

	public static boolean isExcluded(Context context, String phoneNumber) {
		mContext = context;
		_getExcludedList();
		for (Contact c : mExcludedContacts) {
			
			if(Settings.isDebug())
				Log.e(TAG, "CONTACT " + c.getContactName() + " (" + c.getPhoneNumber() + ") NUMERO RECU: " + phoneNumber);
			
			if (phoneNumber.contains(c.getPhoneNumber()))
				return true;
		}
		return false;
	}
	
	private static void cursorToInAndOutCallsAdapter(Cursor cursor) {
		
		mIncommingCalls = new ArrayList<Record>();
		mOutGoingCalls = new ArrayList<Record>();
		
		while (cursor.moveToNext()) {
			Record mRecord = new Record();
			
			String mId = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMNRECODINGAPP_ID));
			
			String mPhone = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE));
			
			String mFile = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_FILE));
			
			String mHtime = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_HTIME));
			
			String mSense = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_SENS));
			
			mRecord.setmId(mId);
			mRecord.setmHtime(mHtime);
			mRecord.setmFilePath(mFile);
			mRecord.setmSense(mSense);
			mRecord.setmPhone(mPhone);
			
			if(mSense.equalsIgnoreCase("E"))
				mIncommingCalls.add(mRecord);
			else
				mOutGoingCalls.add(mRecord);
		}
		cursor.close();
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
				mCallsFoldersKnownContacts.add(mContact);
				break;
				
			case CALLS_FOLDER_OF_UNKNOWN:
				mCallsFoldersUnKnownContacts.add(mContact);
				break;

			default:
				throw new IllegalArgumentException(TAG
						+ " -> Ce type n'est pas pris en charge!");
			}
						
		}
		cursor.close();
	}

	private static void cursorToPhoneAdapter(Cursor cursor) {
		mPhoneContacts = new ArrayList<Contact>();
		while (cursor.moveToNext()) {
			Contact contact = new Contact();

			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));

			String name = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			
			String phone = "null";
			
			try
			{

			Cursor pCur = mContext.getApplicationContext().getContentResolver().query(
					CommonDataKinds.Phone.CONTENT_URI, null,
					CommonDataKinds.Phone.CONTACT_ID + " = ?",
					new String[] { contactId }, null);
			
			while (pCur.moveToNext()) {
				phone = pCur.getString(pCur
						.getColumnIndex(CommonDataKinds.Phone.NUMBER));		
			}
			
				pCur.close();
				//Log.e(TAG, phone);
			}catch(Exception e)
			{
				if(Settings.isDebug())
					Log.e(TAG, e.getMessage());
			}

			contact.setPhoneNumber(phone);
			contact.setContractId(contactId);
			contact.setContactName(name);
			
			mPhoneContacts.add(contact);
			
			for (Contact c : mExcludedContacts)
			{
				if(c.getContactName().contains(contact.getContactName()) && 
						c.getContractId().contains(contact.getContractId()) && 
						c.getPhoneNumber().contains(contact.getPhoneNumber()))			
					mPhoneContacts.remove(contact);
			}
		}
		cursor.close();
	}

	private static void cursorToExcludedAdapter(Cursor cursor) {
		mExcludedContacts = new ArrayList<Contact>();
		while (cursor.moveToNext()) {
			Contact contact = new Contact();

			String contactId = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_CONTACT_ID));
			String contractId = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_CONTRACT_CONTACTS_ID));
			String name = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_DISPLAY_NAME));
			String phone = cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_PHONE_NUMBER));

			contact.setId(contactId);
			contact.setContractId(contractId);
			contact.setContactName(name);
			contact.setPhoneNumber(phone);

			mExcludedContacts.add(contact);
		}
		cursor.close();
	}

	private static void getContacts(Uri uri, String[] projection,
			String Selection, String[] selectionArgs, String Sorted, type t) {
		try {

			Cursor cursor = mContext.getContentResolver().query(uri,
					projection, Selection, selectionArgs, Sorted);
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
	}

}
