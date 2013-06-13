package org.proof.recorder.fragment.contacts.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.models.SimplePhoneNumber;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.ApproxRecordTime;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

public final class ContactsDataHelper {

	private static class DupeContactComparator implements Comparator<Object>{

		@Override
		public int compare(Object lhs, Object rhs) {
			if(!((Contact) lhs).getContactName().equals("? (Contact Inconnu)"))
				return ((Contact) lhs).getContactName().compareToIgnoreCase(((Contact) rhs).getContactName());
			return -1;
		}           
    }

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

	private static ArrayList<Object>   mExcludedContacts, 
										mPhoneContacts;

	private static List<Object> mOutGoingCalls, 
								mIncommingCalls, 
								mCallsFoldersKnownContacts, 
								mCallsFoldersUnKnownContacts;

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

	private static void _getExcludedContact(String phone) {
		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, 
				"excluded_contact_phone/" + phone);
		
		getContacts(uri, excludedProjection, null, null,
				ProofDataBase.COLUMN_DISPLAY_NAME, type.EXCLUDED);
	}
	
	private static void _getExcludedList() {
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "excluded_contacts");
		
		getContacts(uri, excludedProjection, null, null,
				ProofDataBase.COLUMN_DISPLAY_NAME, type.EXCLUDED);
	}
	
	private static void _getIncommingCalls(String mPhoneOrId) {
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "record_tel/" + mPhoneOrId);		
		getContacts(uri, InAndOutProjection,
				null, null, ProofDataBase.COLUMN_HTIME, type.INCOMMING_CALLS);
	}
	
	private static void _getOutGoingCalls(String mPhone) {
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "record_tel/" + mPhone);
		getContacts(uri, InAndOutProjection,
				null, null, ProofDataBase.COLUMN_HTIME, type.OUTGOINGS_CALLS);
	}
	
	private static void _getPhoneList() {
		getContacts(ContactsContract.Contacts.CONTENT_URI, phoneProjection,
				ContactsContract.Contacts.HAS_PHONE_NUMBER + "=?",
				new String[] { "1" }, ContactsContract.Contacts.DISPLAY_NAME,
				type.PHONE);
	}
	
	private static void cursorToCallsFolderAdapter(Context mContext, Cursor cursor, type t) {
		
		switch (t) {
		case CALLS_FOLDER_OF_KNOWN:
			mCallsFoldersKnownContacts = new ArrayList<Object>();
			break;
			
		case CALLS_FOLDER_OF_UNKNOWN:
			mCallsFoldersUnKnownContacts = new ArrayList<Object>();
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

	private static void cursorToExcludedAdapter(Cursor cursor) {
		mExcludedContacts = new ArrayList<Object>();
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
			
			Console.print_debug("Excluded contact added to collection: " + contact);

			mExcludedContacts.add(contact);
		}
	}

	private static void cursorToInAndOutCallsAdapter(Cursor cursor) {
		
		Record.setResolver(mContext.getApplicationContext().getContentResolver());

		mIncommingCalls = new ArrayList<Object>();
		mOutGoingCalls = new ArrayList<Object>();
		
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
			try {
				
				File g = new File(mFile);
				ApproxRecordTime f = new ApproxRecordTime(g);
				String stime = f.run();
				mRecord.setmSongTime(stime+" mn/s");
				Console.print_debug("proof" + stime);

			} catch (Exception e) {
				
				Console.print_exception("proof" + e.getMessage());
			}
			
			if(mRecord.isIncomingCall())
				mIncommingCalls.add(mRecord);
			else
				mOutGoingCalls.add(mRecord);
		}
	}
	
	private static void cursorToPhoneAdapter(Cursor cursor) {
		mPhoneContacts = new ArrayList<Object>();
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
				Console.print_exception(e);
			}
			finally {
				pCur.close();
			}
			
			Contact contact = new Contact(phone);

			contact.setContractId(contactId);
			contact.setContactName(name);
			
			mPhoneContacts.add(contact);
			
			for (Object co : mExcludedContacts)
			{
				Contact c = (Contact) co;
				
				if(c.getContactName().contains(contact.getContactName()) && 
						c.getContractId().contains(contact.getContractId()) && 
						c.getsPhoneNumber().get_nationalNumber().contains(
								contact.getsPhoneNumber().get_nationalNumber()))			
					mPhoneContacts.remove(contact);
			}
		}
	}
	
	public static List<Object> getCallsFoldersOfKnown(Context context) {
		mContext = context;
		_getCallsFoldersOfKnown();
		//removeContactsDuplicates(mCallsFoldersKnownContacts);
		return mCallsFoldersKnownContacts;
	}
	
	public static List<Object> getCallsFoldersOfUnKnown(Context context) {
		mContext = context;
		_getCallsFoldersOfUnKnown();
		//removeContactsDuplicates(mCallsFoldersUnKnownContacts);
		return mCallsFoldersUnKnownContacts;
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
	
	public static ArrayList<Object> getExcludedList(Context context) {
		mContext = context;
		_getExcludedList();
		removeContactsDuplicates(mExcludedContacts);
		return mExcludedContacts;
	}
	
	public static List<Object> getIncommingCalls(Context context, String mPhone) {
		mContext = context;	
		Console.print_debug("mPhone or Id" + mPhone);
		_getIncommingCalls(mPhone);	
		//removeRecordsDuplicates(mIncommingCalls);
		return mIncommingCalls;
	}
	
	public static List<Object> getOutGoingCalls(Context context, String mPhone) {
		mContext = context;
		Console.print_debug("mPhone or Id" + mPhone);
		_getOutGoingCalls(mPhone);
		//removeRecordsDuplicates(mOutGoingCalls);
		return mOutGoingCalls;
	}
	
	public static ArrayList<Object> getPhoneList(Context context) {
		mContext = context;
		_getExcludedList();
		_getPhoneList();
		removeContactsDuplicates(mPhoneContacts);
		return mPhoneContacts;
	}
	
	public static boolean isExcluded(Context context, String phoneNumber) {
		
		mContext = context;	
		
		SimplePhoneNumber _phoneNumber = new SimplePhoneNumber(phoneNumber);
		_phoneNumber.toConsole();		
		
		_getExcludedContact(
				PhoneNumberUtils.stripSeparators(phoneNumber));
		
		return mExcludedContacts.size() > 0;
	}

	private static void removeContactsDuplicates(List<Object> list)
	{
		TreeSet<Object> set = new TreeSet<Object>(new DupeContactComparator());
	    // this should give you a tree set without duplicates
	    set.addAll(list);
	
	    list.clear();
	    list.addAll(set);
	}

	// private Constructor -> force to call methods only
	private ContactsDataHelper() {}

}