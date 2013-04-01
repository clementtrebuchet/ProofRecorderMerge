package org.proof.recorder.personnal.provider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.models.SimplePhoneNumber;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.database.support.ProofDataBaseHelper;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SearchRecentSuggestionsProvider;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author clement
 * 
 */
/**
 * @author devel.machine
 *
 */
public class PersonnalProofContentProvider extends
		SearchRecentSuggestionsProvider {

	private static ProofDataBaseHelper database;

	// Used for the UriMacher

	/*
	 * Search Module
	 */

	private static final int SEARCHES_VOICES = 180;
	private static final int SEARCHES_VOICES_BY_DATE = 230;

	private static final int SEARCHES_CALLS = 210;
	private static final int SEARCHES_CALLS_BY_DATE = 220;

	/*
	 * Phone's Records
	 */
	private static final int RECORDS = 10;
	private static final int RECORD_ID = 20;
	private static final int RECORD_TEL = 30;
	private static final int RECORDS_BY_ANDROID_ID = 200;
	private static final int RECORD_DISTINCT_KNOWN_CONTACTS = 110;
	private static final int RECORD_DISTINCT_UNKNOWN_CONTACTS = 160;
	private static final int RECORD_UNIC_TEL = 780;
	private static final int RECORD_NON_SYNC = 410;

	/*
	 * {@link Phone's Notes, google band de pd} ;)
	 */
	private static final int NOTES = 40;
	private static final int NOTE_ID = 50;
	private static final int NOTE_RECORDID = 60;
	private static final int NOTE_NON_SYNC = 510;

	/*
	 * {@link Voices Records, on va vous ken groogle :p
	 */
	private static final int VOICES = 70;
	private static final int VOICE_ID = 80;
	private static final int VOICE_BY_TITLE = 150;
	private static final int VOICE_BY_UNTITLED = 170;
	private static final int VOICE_NON_SYNC = 610;

	/*
	 * {@link Voice's Notes
	 */
	private static final int VOICE_NOTES = 90;
	private static final int VOICE_NOTE_ID = 100;
	private static final int VOICE_NOTE_RECORDID = 120;
	private static final int VOICE_NOTE_NON_SYNC = 710;

	/*
	 * {@link Excluded Contacts, on va vous ken groogle :p
	 */
	private static final int EXCLUDED_CONTACTS = 130;
	private static final int EXCLUDED_CONTACT_ID = 140;
	private static final int EXCLUDED_CONTACT_BY_PHONE = 1000;

	// ///////////////////////////////////////////////////////////

	public static final String AUTHORITY = "org.proof.recorder.personnal.provider.PersonnalProofContentProvider";
	private static final String BASE_PATH = "personnal.provider";

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ getAuthority() + "/" + BASE_PATH);

	// ///////////////////////////////////////////////////////////

	interface searchModule {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + SearchManager.SUGGEST_URI_PATH_QUERY;

		public static final String CONTENT_INSERT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/suggestions";

		/**
		 * CUSTOM SEARCH MODULE
		 */

		public static final String CONTENT_VOICES_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/search_voices" + SearchManager.SUGGEST_URI_PATH_QUERY;

		public static final String CONTENT_CALLS_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/search_calls" + SearchManager.SUGGEST_URI_PATH_QUERY;

		public static final String CONTENT_VOICES_BY_DATE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/search_voices_by_date"
				+ SearchManager.SUGGEST_URI_PATH_QUERY;

		public static final String CONTENT_CALLS_BY_DATE__TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/search_calls_by_date"
				+ SearchManager.SUGGEST_URI_PATH_QUERY;

		// public static final String CONTENT_DELETE_TYPE =
		// ContentResolver.CURSOR_DIR_BASE_TYPE
		// + "/suggestions";
	}

	interface enregistrements {

		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/records";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/record_id";
		public static final String CONTENT_RECORD_TEL_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/record_tel/";
		/*
		 * 
		 */
		public static String CONTENT_RECORD_CASE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/record_unic_tel/";
		/*
		 * 
		 */
		public static String CONTENT_RECORD_SYNC = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/record_sync/";
		public static final String CONTENT_RECORDS_BY_ANDROID_ID = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/records_by_android_id/";
		/*
		 * 
		 */
		public static String CONTENT_RECORD_DISTINCT_KNOWN_CONTACTS = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/record_distinct_known_contacts/";

		public static String CONTENT_RECORD_DISTINCT_UNKNOWN_CONTACTS = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/record_distinct_unknown_contacts/";
	}

	interface priseDeNotes {

		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/notes";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/note_id";
		public static final String CONTENT_REC_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/note_recordid/";
		/*
		 * 
		 */
		public static String CONTENT_NOTE_SYNC = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/note_sync/";
	}

	interface VoiceNotes {

		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnotes";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnote_id";
		public static final String CONTENT_REC_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnote_recordid/";
		public static String VOICE_NOTE_NON_SYNC = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnote_sync/";
	}

	interface enregistrementsVocal {

		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/voices";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/voice_id";
		public static final String CONTENT_BY_TITLE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/voices_by_title";

		public static final String CONTENT_BY_UNTITLED_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/voices_by_untitled";
		public static String VOICE_NON_SYNC = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/voice_sync/";
	}

	interface excludedContacts {

		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/excluded_contacts";
		
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/excluded_contact_id";
		
		public static final String CONTENT_BY_PHONE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/excluded_contact_phone";
	}

	/**
	 * Search Suggestions
	 */

	public final static int MODE = DATABASE_MODE_QUERIES;

	public PersonnalProofContentProvider() {
		super();
		setupSuggestions(getAuthority(), MODE);
	}

	@Override
	public String getType(Uri uri) {
		final int match = sURIMatcher.match(uri);
		switch (match) {

			/**
			 * CUSTOM SEARCH MODULE
			 */

		case SEARCHES_VOICES:
			return searchModule.CONTENT_VOICES_TYPE;
		case SEARCHES_VOICES_BY_DATE:
			return searchModule.CONTENT_VOICES_BY_DATE_TYPE;
		case SEARCHES_CALLS:
			return searchModule.CONTENT_CALLS_TYPE;
		case SEARCHES_CALLS_BY_DATE:
			return searchModule.CONTENT_CALLS_BY_DATE__TYPE;

			// Phone Records

		case RECORDS:
			return enregistrements.CONTENT_TYPE;
		case RECORD_ID:
			return enregistrements.CONTENT_ITEM_TYPE;
		case RECORD_TEL:
			return enregistrements.CONTENT_ITEM_TYPE;
		case RECORD_UNIC_TEL:
			return enregistrements.CONTENT_RECORD_CASE;
		case RECORD_NON_SYNC:
			return enregistrements.CONTENT_RECORD_SYNC;
		case RECORDS_BY_ANDROID_ID:
			return enregistrements.CONTENT_RECORDS_BY_ANDROID_ID;
		case RECORD_DISTINCT_KNOWN_CONTACTS:
			return enregistrements.CONTENT_RECORD_DISTINCT_KNOWN_CONTACTS;
		case RECORD_DISTINCT_UNKNOWN_CONTACTS:
			return enregistrements.CONTENT_RECORD_DISTINCT_UNKNOWN_CONTACTS;

			// Phone's Note

		case NOTES:
			return priseDeNotes.CONTENT_TYPE;
		case NOTE_ID:
			return priseDeNotes.CONTENT_ITEM_TYPE;
		case NOTE_RECORDID:
			return priseDeNotes.CONTENT_ITEM_TYPE;
		case NOTE_NON_SYNC:
			return priseDeNotes.CONTENT_NOTE_SYNC;

			// Voice Records

		case VOICES:
			return enregistrementsVocal.CONTENT_TYPE;
		case VOICE_ID:
			return enregistrementsVocal.CONTENT_ITEM_TYPE;
		case VOICE_BY_TITLE:
			return enregistrementsVocal.CONTENT_BY_TITLE_TYPE;
		case VOICE_BY_UNTITLED:
			return enregistrementsVocal.CONTENT_BY_UNTITLED_TYPE;
		case VOICE_NON_SYNC:
			return enregistrementsVocal.VOICE_NON_SYNC;

			// Voice's Records

		case VOICE_NOTES:
			return VoiceNotes.CONTENT_TYPE;
		case VOICE_NOTE_ID:
			return VoiceNotes.CONTENT_ITEM_TYPE;
		case VOICE_NOTE_RECORDID:
			return VoiceNotes.CONTENT_ITEM_TYPE;
		case VOICE_NOTE_NON_SYNC :
			return VoiceNotes.VOICE_NOTE_NON_SYNC;

			// Excluded Contacts

		case EXCLUDED_CONTACTS:
			return excludedContacts.CONTENT_TYPE;

		case EXCLUDED_CONTACT_ID:
			return excludedContacts.CONTENT_ITEM_TYPE;
			
		case EXCLUDED_CONTACT_BY_PHONE:
			return excludedContacts.CONTENT_BY_PHONE;

		default:
			throw new UnsupportedOperationException("getType -> Unknown uri: "
					+ uri);
		}
	}

	/**
	 * END Search Suggestions
	 */

	/*
	 * ICI pour lister toutes les enregistrements params Uri Object CursorLoader
	 * cursorLoader Uri uri = Uri.withAppendedPath(
	 * PersonnalProofContentProvider.CONTENT_URI, "r/"+id); CursorLoader
	 * cursorLoader = new CursorLoader(getActivity(), uri, from, null, null,
	 * null); return cursorLoader;: PersonnalProofContentProvider.CONTENT_URI,
	 * "r" par id PersonnalProofContentProvider.CONTENT_URI, "r/"+id par tel
	 * PersonnalProofContentProvider.CONTENT_URI, "r/by_phone/"+tel
	 */
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	private static final String TAG = "PersonnalProofContentProvider";

	static {
		/**
		 * CUSTOM SEARCH MODULE
		 */

		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/search_calls/*",
				SEARCHES_CALLS);
		sURIMatcher.addURI(getAuthority(), BASE_PATH
				+ "/search_calls_by_date/*", SEARCHES_CALLS_BY_DATE);

		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/search_voices/*",
				SEARCHES_VOICES);
		sURIMatcher.addURI(getAuthority(), BASE_PATH
				+ "/search_voices_by_date/*", SEARCHES_VOICES_BY_DATE);

		/**
		 * END CUSTOM SEARCH MODULE
		 */

		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/records", RECORDS);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/record_id/#",
				RECORD_ID);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/record_tel/*",
				RECORD_TEL);
		sURIMatcher.addURI(getAuthority(), BASE_PATH
				+ "/records_by_android_id/#", RECORDS_BY_ANDROID_ID);

		sURIMatcher.addURI(getAuthority(), BASE_PATH
				+ "/record_distinct_known_contacts",
				RECORD_DISTINCT_KNOWN_CONTACTS);
		sURIMatcher.addURI(getAuthority(), BASE_PATH
				+ "/record_distinct_unknown_contacts",
				RECORD_DISTINCT_UNKNOWN_CONTACTS);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/record_unic_tel",
				RECORD_UNIC_TEL);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/record_sync",
				RECORD_NON_SYNC);

		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/notes", NOTES);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/note_id/#", NOTE_ID);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/note_recordid/#",
				NOTE_RECORDID);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/note_sync",
				NOTE_NON_SYNC);

		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/voices", VOICES);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/voice_id/#", VOICE_ID);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/voices_by_title/*",
				VOICE_BY_TITLE);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/voices_by_untitled",
				VOICE_BY_UNTITLED);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/voice_sync",
				VOICE_NON_SYNC);

		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/vnotes", VOICE_NOTES);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/vnote_id/#",
				VOICE_NOTE_ID);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/vnote_recordid/#",
				VOICE_NOTE_RECORDID);
		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/vnote_sync",
				VOICE_NOTE_NON_SYNC);

		sURIMatcher.addURI(getAuthority(), BASE_PATH + "/excluded_contacts",
				EXCLUDED_CONTACTS);
		
		sURIMatcher.addURI(getAuthority(),
				BASE_PATH + "/excluded_contact_id/#", EXCLUDED_CONTACT_ID);
		
		sURIMatcher.addURI(getAuthority(),
				BASE_PATH + "/excluded_contact_phone/*", EXCLUDED_CONTACT_BY_PHONE);
	}

	/**
	 * @param Nondelabase
	 * @return
	 */
	public static int lastInsertId(String Nondelabase) {
		int lastId = 0;
		SQLiteDatabase sqlDB;
		
		String query = "SELECT _id from " + Nondelabase
				+ " order by _id DESC limit 1";
		
		try {
			sqlDB = database.getWritableDatabase();
		}
		catch(Exception e) {
			sqlDB = database.getReadableDatabase();
		}
		
		Cursor c = sqlDB.rawQuery(query, null);
		
		try {
			if (c != null && c.moveToFirst()) {
				lastId = c.getInt(0); // The 0 is the column index, we only have 1
										// column, so the index is 0
				return lastId;
			}
		}
		catch(Exception e) {
			Log.e(TAG, "" + e);
		}
		finally {
			c.close();
		}	
		
		return lastId;

	}

	/**
	 * @param absolutePath: song file's path.
	 * @param type String
	 * @return the count of database song(s) matching this path
	 */
	public static int isRecordInDb(
			String absolutePath, Settings.mType type) {
		
		String query = "SELECT _id from ";
		SQLiteDatabase sqlDB;
		int _count = -2;

		switch (type) {
		case CALL:
			query += "recordsproof";
			break;
		case VOICE_TITLED:
		case VOICE_UNTITLED:
			query += "voicesproof";
			break;
		default:
			break;
		}

		query += " WHERE emplacement=?";
		
		try {
			sqlDB = database.getWritableDatabase();
		}
		catch(Exception e) {
			sqlDB = database.getReadableDatabase();
		}
		
		try {
			Cursor c = sqlDB.rawQuery(query, new String[] { absolutePath });
			
			_count = c.getCount();
			
			c.close();
		}
		catch(Exception e) {
			Log.e(TAG, "" + e);
		}		
		
		return _count;
	}


	/**
	 * @param mPhone
	 * @return
	 */
	public static int deleteContactsFolder(String mPhone) {
		int mDeletedRows = -1;
		String query, phone = mPhone.trim();
		Cursor c = null;
		String[] mIds = null;

		SQLiteDatabase sqlDB = database.getWritableDatabase();

		if (Settings.isDebug()) {
			Log.e(TAG, "<!-- BEFORE DELETING RECORDS FROM FOLDER !--> ");
			query = "SELECT _id from recordsproof WHERE telephone='" + phone
					+ "';";
			c = sqlDB.rawQuery(query, null);

			mIds = new String[c.getCount()];
			int i = 0;
			while (c != null && c.moveToNext()) {
				mIds[i] = c.getString(0);
				i++;
				Log.e(TAG, "ID CALL DELETED FROM FOLDER: " + c.getString(0));
			}
			
			c.close();

			for (String id : mIds) {
				query = "SELECT titre from notesproof WHERE RecId='" + id
						+ "';";
				c = sqlDB.rawQuery(query, null);
				while (c != null && c.moveToNext()) {
					Log.e(TAG,
							"TITRE CALL'S NOTE DELETED FROM FOLDER: "
									+ c.getString(0));
				}
				
				c.close();
			}

		}

		sqlDB.execSQL(" PRAGMA foreign_keys = ON ");
		
		mDeletedRows = sqlDB.delete("recordsproof", "telephone=?",
				new String[] { mPhone.trim() });

		if (Settings.isDebug()) {
			Log.e(TAG, "<!-- AFTER DELETING RECORDS FROM FOLDER !--> ");
			try {
				for (String id : mIds) {
					query = "SELECT titre from notesproof WHERE RecId='" + id
							+ "';";
					c = sqlDB.rawQuery(query, null);
					Log.e(TAG,
							"TITRE CALL'S NOTE DELETED FROM FOLDER: (cursor count:"
									+ c.getCount() + ")");
					while (c != null && c.moveToNext()) {
						Log.e(TAG, "TITRE CALL'S NOTE DELETED FROM FOLDER: "
								+ c.getString(0));
					}
					
					c.close();
				}
			} catch (Exception e) {
				Log.e(TAG, "E :: TITRE CALL'S NOTE DELETED FROM FOLDER: " + e);
			}			
		}

		sqlDB.close();

		return mDeletedRows;
	}
	
	/**
	 * @param type
	 * @return
	 */
	public static List<Record> getRecordsFilesList(Settings.mType type) {
		List<Record> list = new ArrayList<Record>();
		String query = "";

		switch (type) {
		case CALL:
			query = "SELECT _id, emplacement from recordsproof;";
			break;
		case VOICE_TITLED:
			query = "SELECT _id, emplacement from voicesproof;";
			break;
		case VOICE_UNTITLED:
			query = "SELECT _id, emplacement from voicesproof;";
			break;
		default:
			break;
		}
		
		SQLiteDatabase sqlDB;

		try {
			sqlDB = database.getWritableDatabase();
		}
		catch(SQLException e) {
			sqlDB = database.getReadableDatabase();
		}
		
		Cursor c = sqlDB.rawQuery(query, null);		
		
		try {			

			while (c != null && c.moveToNext()) {
				Record mRecord = new Record();

				switch (type) {
				case CALL:
					mRecord.setmId(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMNRECODINGAPP_ID)));
					mRecord.setmFilePath(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMN_FILE)));
					break;

				case VOICE_TITLED:
					mRecord.setmId(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMNVOICE_ID)));
					mRecord.setmFilePath(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE)));
					break;

				case VOICE_UNTITLED:
					mRecord.setmId(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMNVOICE_ID)));
					mRecord.setmFilePath(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE)));
					break;
				default:
					break;
				}
				list.add(mRecord);
			}
		}
		catch(Exception e) {
			Log.e(TAG, "" + e);
		}
		finally {
			c.close();
			sqlDB.close();
		}	
		
		return list;
	}


	/**
	 * @param uriType
	 * @param mId
	 * @return
	 */
	public static Uri deleteItem(String uriType, String mId) {
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, uriType + mId);
		if (Settings.isDebug())
			Log.v(TAG, "deleteItem-> " + uri);
		return uri;
	}

	@Override
	public boolean onCreate() {
		boolean create = super.onCreate();
		database = new ProofDataBaseHelper(getContext());
		return create;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		SQLiteDatabase sqlDB;
		Cursor cursor;
		// Check if the caller has requested a column which does not exists
		checkColumns(projection);
		
		try {
			sqlDB = database.getWritableDatabase();
		}
		catch(Exception e) {
			sqlDB = database.getReadableDatabase();
		}

		// Set the table

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {

			/**
			 * CUSTOM SEARCH MODULE
			 */
			
		case SEARCHES_CALLS:
			return this.searchRecordTel(
					queryBuilder, 
					uri, 
					projection, 
					selection, 
					selectionArgs,
					sortOrder
			);
			
		case SEARCHES_CALLS_BY_DATE:
			return this.searchRecordTelByDate(
					queryBuilder, 
					uri, 
					projection, 
					selection, 
					selectionArgs,
					sortOrder
			);
			
		case SEARCHES_VOICES:
			return this.searchRecordVoices(
					queryBuilder, 
					uri, 
					projection, 
					selection, 
					selectionArgs, 
					sortOrder
			);
			
		case SEARCHES_VOICES_BY_DATE:
			return this.searchRecordVoicesByDate(
					queryBuilder, 
					uri, 
					projection, 
					selection, 
					selectionArgs, 
					sortOrder
			);

			/**
			 * END CUSTOM SEARCH MODULE
			 */
			// Phone Records

		case RECORDS:
			queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);
			break;
		case RECORD_ID:
			queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);
			// Adding the ID to the original query
			queryBuilder.appendWhere(ProofDataBase.COLUMNRECODINGAPP_ID + "="
					+ uri.getLastPathSegment());
			break;
		case RECORDS_BY_ANDROID_ID:
			queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);

			String mAndroidId = uri.getLastPathSegment().trim();

			if (Settings.isDebug())
				Log.d(TAG,
						"demande les entrées avec le ID Android dans la table enregistrement: "
								+ mAndroidId);

			queryBuilder.appendWhere(ProofDataBase.COLUMN_CONTRACT_ID + "="
					+ mAndroidId);
			break;
		case RECORD_TEL:
			queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);
			
			SimplePhoneNumber mPhone = new SimplePhoneNumber(uri.getLastPathSegment());

			if (Settings.isDebug())
				Log.d(TAG,
						"demande les entrées avec lenumero de telephone dans la table enregistrement"
								+ uri.getLastPathSegment());
			queryBuilder.appendWhere(ProofDataBase.COLUMN_TELEPHONE + " LIKE "
					+ "\"%" + mPhone.get_nationalNumber() + "%\"");
			break;
		case RECORD_DISTINCT_KNOWN_CONTACTS:

			if (Settings.isDebug())
				Log.d(TAG,
						"RECORD_DISTINCT_KNOWN_CONTACTS: demande les entrées de façon distinct dans la table enregistrement:"
								+ uri.getLastPathSegment());

			cursor = sqlDB
					.rawQuery("SELECT * FROM "
							+ ProofDataBase.TABLE_RECODINGAPP + " WHERE "
							+ ProofDataBase.COLUMN_CONTRACT_ID
							+ "!=? GROUP BY "
							+ ProofDataBase.COLUMN_CONTRACT_ID,
							new String[] { "null" });
			
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;

		case RECORD_DISTINCT_UNKNOWN_CONTACTS:

			if (Settings.isDebug())
				Log.d(TAG,
						"RECORD_DISTINCT_UNKNOWN_CONTACTS: demande les entrées de façon distinct dans la table enregistrement:"
								+ uri.getLastPathSegment());
			
			cursor = sqlDB.rawQuery("SELECT * FROM "
					+ ProofDataBase.TABLE_RECODINGAPP + " WHERE "
					+ ProofDataBase.COLUMN_CONTRACT_ID + "=? GROUP BY "
					+ ProofDataBase.COLUMN_TELEPHONE, new String[] { "null" });
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			
			return cursor;
			
		case RECORD_UNIC_TEL:

			if (Settings.isDebug())
				Log.d(TAG,
						"demande les entrées de façon distinct dans la table enregistrement:"
								+ uri.getLastPathSegment());
			
			
			Cursor cursor1 = sqlDB.rawQuery("SELECT * FROM "
					+ ProofDataBase.TABLE_RECODINGAPP + " GROUP BY telephone",
					null);
			cursor1.setNotificationUri(getContext().getContentResolver(), uri);
			
			return cursor1;
		case RECORD_NON_SYNC:
			if (Settings.isDebug())
				Log.d(TAG,
						"demande les entrées de façon distinct dans la table enregistrement:"
								+ uri.getLastPathSegment());
			
			Cursor cursor11 = sqlDB.rawQuery("SELECT * FROM "
					+ ProofDataBase.TABLE_RECODINGAPP + " WHERE Isync = 0",
					null);
			cursor11.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor11;

			// Phone's Note

		case NOTES:
			queryBuilder.setTables(ProofDataBase.TABLE_NOTES);
			break;
		case NOTE_ID:
			queryBuilder.setTables(ProofDataBase.TABLE_NOTES);
			// Adding the ID to the original query

			if (Settings.isDebug())
				Log.d(TAG,
						"demande une entrée par le numéro de l'enregistrement"
								+ uri.getLastPathSegment());
			queryBuilder.appendWhere(ProofDataBase.COLUMNNOTES_ID + "="
					+ uri.getLastPathSegment());
			break;
		case NOTE_RECORDID:
			queryBuilder.setTables(ProofDataBase.TABLE_NOTES);
			// Adding the ID to the original query

			if (Settings.isDebug())
				Log.d(TAG,
						"demande une entrée par le numéro de record de l'enregistrement"
								+ uri.getLastPathSegment());
			queryBuilder
					.appendWhere(ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID
							+ "=" + uri.getLastPathSegment());
			break;

		case NOTE_NON_SYNC:
			if (Settings.isDebug())
				Log.d(TAG,
						"demande les NOTE_NON_SYNC:" + uri.getLastPathSegment());
			
			Cursor cursor111 = sqlDB.rawQuery("SELECT * FROM "
					+ ProofDataBase.TABLE_NOTES + " WHERE Isync = 0", null);
			
			cursor111.setNotificationUri(getContext().getContentResolver(), uri);
			
			return cursor111;

			// Voice's Records

		case VOICES:
			
			cursor = sqlDB
					.rawQuery(
							"SELECT voicesproof._id, voicesproof.htime, voicesproof.taille, voicesproof.timestamp, voicesproof.emplacement FROM "
									+ ProofDataBase.TABLE_VOICES
									+ " INNER JOIN "
									+ ProofDataBase.TABLE_VOICE_NOTES
									+ " ON "
									+ ProofDataBase.TABLE_VOICES
									+ "._id = "
									+ ProofDataBase.TABLE_VOICE_NOTES
									+ ".RecId WHERE "
									+ ProofDataBase.TABLE_VOICE_NOTES
									+ ".titre !=\"Insérer une note\"", null);

			cursor.setNotificationUri(getContext().getContentResolver(), uri);

			return cursor;
		case VOICE_ID:
			queryBuilder.setTables(ProofDataBase.TABLE_VOICES);
			// Adding the ID to the original query
			queryBuilder.appendWhere(ProofDataBase.COLUMNVOICE_ID + "="
					+ uri.getLastPathSegment());
			break;
		case VOICE_BY_TITLE:

			if (Settings.isDebug())
				Log.d(TAG,
						"demande des enregistrements vocaux par le titre de leurs note: "
								+ uri.getLastPathSegment());

			cursor = sqlDB
					.rawQuery(
							"SELECT voicesproof._id, voicesproof.htime, voicesproof.taille, voicesproof.timestamp, voicesproof.emplacement FROM "
									+ ProofDataBase.TABLE_VOICES
									+ " INNER JOIN "
									+ ProofDataBase.TABLE_VOICE_NOTES
									+ " ON "
									+ ProofDataBase.TABLE_VOICES
									+ "._id = "
									+ ProofDataBase.TABLE_VOICE_NOTES
									+ ".RecId WHERE "
									+ ProofDataBase.TABLE_VOICE_NOTES
									+ ".titre LIKE \"%"
									+ uri.getLastPathSegment().trim() + "%\"",
							null);

			cursor.setNotificationUri(getContext().getContentResolver(), uri);

			return cursor;

		case VOICE_BY_UNTITLED:

			if (Settings.isDebug())
				Log.d(TAG,
						"demande des enregistrements vocaux par le titre de leurs note: "
								+ uri.getLastPathSegment());

			cursor = sqlDB
					.rawQuery(
							"SELECT voicesproof._id, voicesproof.htime, voicesproof.taille, voicesproof.timestamp, voicesproof.emplacement FROM "
									+ ProofDataBase.TABLE_VOICES
									+ " INNER JOIN "
									+ ProofDataBase.TABLE_VOICE_NOTES
									+ " ON "
									+ ProofDataBase.TABLE_VOICES
									+ "._id = "
									+ ProofDataBase.TABLE_VOICE_NOTES
									+ ".RecId WHERE "
									+ ProofDataBase.TABLE_VOICE_NOTES
									+ ".titre LIKE \"%Insérer une note%\"",
							null);

			cursor.setNotificationUri(getContext().getContentResolver(), uri);

			return cursor;
		case VOICE_NON_SYNC:
			if (Settings.isDebug())
				Log.d(TAG,
						"demande les entrées de façon sync dans la table voice:"
								+ uri.getLastPathSegment());

			Cursor cursor12 = sqlDB.rawQuery("SELECT * FROM "
					+ ProofDataBase.TABLE_VOICES + " WHERE Isync = 0",
					null);
			cursor12.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor12;

		// Voice Notes

		case VOICE_NOTES:
			queryBuilder.setTables(ProofDataBase.TABLE_VOICE_NOTES);
			break;
		case VOICE_NOTE_ID:
			queryBuilder.setTables(ProofDataBase.TABLE_VOICE_NOTES);
			// Adding the ID to the original query

			if (Settings.isDebug())
				Log.d(TAG,
						"demande une entrée par le numéro de l'enregistrement"
								+ uri.getLastPathSegment());
			queryBuilder.appendWhere(ProofDataBase.COLUMNNOTES_ID + "="
					+ uri.getLastPathSegment());
			break;

		case VOICE_NOTE_RECORDID:

			queryBuilder.setTables(ProofDataBase.TABLE_VOICE_NOTES);
			// Adding the ID to the original query

			if (Settings.isDebug())
				Log.d(TAG,
						"demande une entrée par le numéro de record de l'enregistrement voice"
								+ uri.getLastPathSegment());
			queryBuilder
					.appendWhere(ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID
							+ "=" + uri.getLastPathSegment());
			break;
		
		case VOICE_NOTE_NON_SYNC:
			if (Settings.isDebug())
				Log.d(TAG,
						"demande les entrées de façon sync dans la table voice:"
								+ uri.getLastPathSegment());

			Cursor cursor13 = sqlDB.rawQuery("SELECT * FROM "
					+ ProofDataBase.TABLE_VOICE_NOTES + " WHERE Isync = 0",
					null);
			cursor13.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor13;

		// Excluded Contacts

		case EXCLUDED_CONTACTS:
			queryBuilder.setTables(ProofDataBase.TABLE_EXCLUDED_CONTACTS);
			break;
			
		// Handle the request for a given phone number
		// and return the corresponding excluded contact object or default object.
			
		case EXCLUDED_CONTACT_BY_PHONE:
			queryBuilder.setTables(ProofDataBase.TABLE_EXCLUDED_CONTACTS);
			// Adding the ID to the original query

			if (Settings.isDebug())
				Log.d(TAG,
						"(by phone) request for excluded contact: "
								+ uri.getLastPathSegment());
			queryBuilder.appendWhere(ProofDataBase.COLUMN_PHONE_NUMBER + "=\""
					+ uri.getLastPathSegment() + "\"");
			break;

		case EXCLUDED_CONTACT_ID:
			queryBuilder.setTables(ProofDataBase.TABLE_EXCLUDED_CONTACTS);
			// Adding the ID to the original query

			if (Settings.isDebug())
				Log.e(TAG,
						"(by id) request for excluded contact: "
								+ uri.getLastPathSegment());
			queryBuilder.appendWhere(ProofDataBase.COLUMN_CONTACT_ID + "="
					+ uri.getLastPathSegment());
			break;

		default:
			throw new IllegalArgumentException("query -> Unknown URI: " + uri);
		}

		SQLiteDatabase db;
		
		try {
			db = database.getWritableDatabase();
		}
		catch(Exception e) {
			db = database.getReadableDatabase();
		}
		
		Cursor c = queryBuilder.query(db, projection, selection, selectionArgs,
				null, null, sortOrder);
		
		// Make sure that potential listeners are getting notified
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}
	
	/**
	 * @param queryBuilder
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	private Cursor searchRecordVoicesByDate(SQLiteQueryBuilder queryBuilder, Uri uri,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		
		Cursor cursor;
		long mStart, mEnd, mPreciseStart, mPreciseEnd;
		String mFinalQuery, query = uri.getLastPathSegment();
		String[] tmpQueriesParts = null;
		tmpQueriesParts = query.split(";");
		
		SQLiteDatabase sqlDB;
		
		try {
			sqlDB = database.getWritableDatabase();
		}
		catch(Exception e) {
			sqlDB = database.getReadableDatabase();
		}
		
		mFinalQuery = tmpQueriesParts[0].trim();	
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date=null;
		
		
		if(tmpQueriesParts.length == 3) {
			
			if (Settings.isDebug())
				Log.e(TAG,
						"Demandes des enregistrements Voices BY_DATE (PERIOD): "
								+ query);
			
			try
			{
			  date = df.parse(tmpQueriesParts[1].trim() + " 00:00:01");
			} catch (ParseException e){

			}
			
			mStart = date.getTime();
			
			try
			{
			  date = df.parse(tmpQueriesParts[2].trim() + " 23:59:59");
			} catch (ParseException e){

			}
			
			mEnd = date.getTime();
			
			String mSql = "SELECT voicesproof._id, voicesproof.htime, voicesproof.taille, voicesproof.timestamp, voicesproof.emplacement FROM "
					+ ProofDataBase.TABLE_VOICES
					+ " INNER JOIN "
					+ ProofDataBase.TABLE_VOICE_NOTES
					+ " ON "
					+ ProofDataBase.TABLE_VOICES
					+ "._id = "
					+ ProofDataBase.TABLE_VOICE_NOTES
					+ ".RecId WHERE "
					+ ProofDataBase.TABLE_VOICE_NOTES
					+ ".titre LIKE \"%"
					+ mFinalQuery + "%\" AND (CAST("
					+ ProofDataBase.TABLE_VOICES
					+ ".timestamp AS INTEGER) BETWEEN CAST(" + mStart + " AS INTEGER) AND "
					+ "CAST(" + mEnd + " AS INTEGER))";
			
			Log.i(TAG, mSql);
			
			cursor = sqlDB
					.rawQuery(
							mSql,
							null);
		}
		else {
			
			if (Settings.isDebug())
				Log.e(TAG,
						"Demandes des enregistrements Voices BY_DATE (PRECISE): "
								+ query);
			
			try
			{
			  date = df.parse(tmpQueriesParts[1].trim() + " 00:00:01");
			} catch (ParseException e){

			}
			
			mPreciseStart = date.getTime();
			
			try
			{
			  date = df.parse(tmpQueriesParts[1].trim() + " 23:59:59");
			} catch (ParseException e){

			}
			
			mPreciseEnd = date.getTime();
			
			String mSql = "SELECT voicesproof._id, voicesproof.htime, voicesproof.taille, voicesproof.timestamp, voicesproof.emplacement FROM "
					+ ProofDataBase.TABLE_VOICES
					+ " INNER JOIN "
					+ ProofDataBase.TABLE_VOICE_NOTES
					+ " ON "
					+ ProofDataBase.TABLE_VOICES
					+ "._id = "
					+ ProofDataBase.TABLE_VOICE_NOTES
					+ ".RecId WHERE "
					+ ProofDataBase.TABLE_VOICE_NOTES
					+ ".titre LIKE \"%"
					+ mFinalQuery + "%\" AND (CAST("
					+ ProofDataBase.TABLE_VOICES
					+ ".timestamp AS INTEGER) BETWEEN CAST(" + mPreciseStart + " AS INTEGER) AND "
					+ "CAST(" + mPreciseEnd + " AS INTEGER))";	

			Log.i(TAG, mSql);
			
			cursor = sqlDB
					.rawQuery(
							mSql,
							null);
		}		

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}
	
	
	/**
	 * @param queryBuilder
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	private Cursor searchRecordVoices(SQLiteQueryBuilder queryBuilder, Uri uri,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		
		String query = uri.getLastPathSegment();
		
		if (Settings.isDebug())
			Log.e(TAG,
					"Demandes des enregistrements Voices: "
							+ query);
		
		SQLiteDatabase sqlDB;

		try {
			sqlDB = database.getWritableDatabase();
		}
		catch(Exception e) {
			sqlDB = database.getReadableDatabase();
		}

		Cursor cursor = sqlDB
				.rawQuery(
						"SELECT voicesproof._id, voicesproof.htime, voicesproof.taille, voicesproof.timestamp, voicesproof.emplacement FROM "
								+ ProofDataBase.TABLE_VOICES
								+ " INNER JOIN "
								+ ProofDataBase.TABLE_VOICE_NOTES
								+ " ON "
								+ ProofDataBase.TABLE_VOICES
								+ "._id = "
								+ ProofDataBase.TABLE_VOICE_NOTES
								+ ".RecId WHERE "
								+ ProofDataBase.TABLE_VOICE_NOTES
								+ ".titre LIKE \"%"
								+ uri.getLastPathSegment().trim() + "%\"",
						null);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	/**
	 * @param queryBuilder
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	private Cursor searchRecordTel(
			SQLiteQueryBuilder queryBuilder, Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);

		String mPhone = uri.getLastPathSegment().trim();

		if (Settings.isDebug())
			Log.e(TAG,
					"demande les entrées avec le numéro de téléphone dans la table enregistrement: "
							+ mPhone);

		// int mSizePhone = mPhone.length();

		Cursor[] cursors;
		
		SQLiteDatabase db;

		try {
			db = database.getWritableDatabase();
		}
		catch(Exception e) {
			db = database.getReadableDatabase();
		}

		if (mPhone.startsWith("+")) {
			cursors = new Cursor[2];
			queryBuilder.appendWhere(ProofDataBase.COLUMN_TELEPHONE
					+ " LIKE " + "\"%" + mPhone + "%\"");

			cursors[0] = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);

			mPhone = "0" + mPhone.substring(3).trim();

			queryBuilder = new SQLiteQueryBuilder();

			queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);

			queryBuilder.appendWhere(ProofDataBase.COLUMN_TELEPHONE
					+ " LIKE " + "\"%" + mPhone + "%\"");

			cursors[1] = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);

			if (Settings.isDebug())
				Log.e(TAG,
						"Le numéro commence par un '+' après substring(): "
								+ mPhone);

			for (Cursor c : cursors)
				c.setNotificationUri(getContext().getContentResolver(), uri);

			return new MergeCursor(cursors);

		} else if (mPhone.startsWith("0")) {
			cursors = new Cursor[2];
			queryBuilder.appendWhere(ProofDataBase.COLUMN_TELEPHONE
					+ " LIKE " + "\"%" + mPhone + "%\"");

			cursors[0] = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);

			mPhone = "+33" + mPhone.substring(1).trim();

			queryBuilder = new SQLiteQueryBuilder();

			queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);

			queryBuilder.appendWhere(ProofDataBase.COLUMN_TELEPHONE
					+ " LIKE " + "\"%" + mPhone + "%\"");

			cursors[1] = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);

			if (Settings.isDebug())
				Log.e(TAG,
						"Le numéro commence par un '0' après substring(): "
								+ mPhone);

			for (Cursor c : cursors)
				c.setNotificationUri(getContext().getContentResolver(), uri);

			return new MergeCursor(cursors);
		}

		else {
			queryBuilder.appendWhere(ProofDataBase.COLUMN_TELEPHONE
					+ " LIKE " + "\"%" + mPhone + "%\"");
			
			Cursor cursor = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);
			return cursor;
		}
		
		
	}
	
	
	/**
	 * @param queryBuilder
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return The Matching query Cursor object.
	 */
	private Cursor searchRecordTelByDate(
			SQLiteQueryBuilder queryBuilder, Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);

		Cursor[] cursors;
		long mStart, mEnd, mPreciseStart, mPreciseEnd;
		String mFinalQuery, query = uri.getLastPathSegment();
		String[] tmpQueriesParts = null;
		tmpQueriesParts = query.split(";");
		
		SQLiteDatabase db;

		try {
			db = database.getWritableDatabase();
		}
		catch(Exception e) {
			db = database.getReadableDatabase();
		}
		
		mFinalQuery = tmpQueriesParts[0].trim();	
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date=null;
		
		
		if(tmpQueriesParts.length == 3) {
			
			try
			{
			  date = df.parse(tmpQueriesParts[1].trim() + " 00:00:01");
			} catch (ParseException e){

			}
			
			mStart = date.getTime();
			
			try
			{
			  date = df.parse(tmpQueriesParts[2].trim() + " 23:59:59");
			} catch (ParseException e){

			}
			
			mEnd = date.getTime();
			
			if (mFinalQuery.startsWith("+")) {
				cursors = new Cursor[2];
				
				queryBuilder.appendWhere("CAST(" + ProofDataBase.COLUMN_TIMESTAMP
						+ " AS INTEGER) BETWEEN CAST(" + mStart + " AS INTEGER) AND CAST(" + mEnd + " AS INTEGER) AND telephone"
						+ " LIKE " + "\"%" + mFinalQuery + "%\"");

				cursors[0] = queryBuilder.query(db, projection, selection,
						selectionArgs, null, null, sortOrder);

				mFinalQuery = "0" + mFinalQuery.substring(3).trim();

				queryBuilder = new SQLiteQueryBuilder();

				queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);

				queryBuilder.appendWhere("CAST(" + ProofDataBase.COLUMN_TIMESTAMP
						+ " AS INTEGER) BETWEEN CAST(" + mStart + " AS INTEGER) AND CAST(" + mEnd + " AS INTEGER) AND telephone"
						+ " LIKE " + "\"%" + mFinalQuery + "%\"");

				cursors[1] = queryBuilder.query(db, projection, selection,
						selectionArgs, null, null, sortOrder);

				for (Cursor c : cursors)
					c.setNotificationUri(getContext().getContentResolver(), uri);

				return new MergeCursor(cursors);

			} else if (mFinalQuery.startsWith("0")) {
				cursors = new Cursor[2];
				queryBuilder.appendWhere("CAST(" + ProofDataBase.COLUMN_TIMESTAMP
						+ " AS INTEGER) BETWEEN CAST(" + mStart + " AS INTEGER) AND CAST(" + mEnd + " AS INTEGER) AND telephone"
						+ " LIKE " + "\"%" + mFinalQuery + "%\"");

				cursors[0] = queryBuilder.query(db, projection, selection,
						selectionArgs, null, null, sortOrder);

				mFinalQuery = "+33" + mFinalQuery.substring(1).trim();

				queryBuilder = new SQLiteQueryBuilder();

				queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);

				queryBuilder.appendWhere("CAST(" + ProofDataBase.COLUMN_TIMESTAMP
						+ " AS INTEGER) BETWEEN CAST(" + mStart + " AS INTEGER) AND CAST(" + mEnd + " AS INTEGER) AND telephone"
						+ " LIKE " + "\"%" + mFinalQuery + "%\"");

				cursors[1] = queryBuilder.query(db, projection, selection,
						selectionArgs, null, null, sortOrder);

				for (Cursor c : cursors)
					c.setNotificationUri(getContext().getContentResolver(), uri);

				return new MergeCursor(cursors);
			}

			else {
				
				queryBuilder.appendWhere("CAST(" + ProofDataBase.COLUMN_TIMESTAMP
						+ " AS INTEGER) BETWEEN CAST(" + mStart + " AS INTEGER) AND CAST(" + mEnd + " AS INTEGER) AND telephone"
						+ " LIKE " + "\"%" + mFinalQuery + "%\"");
				
				Cursor cursor = queryBuilder.query(db, projection, selection,
						selectionArgs, null, null, sortOrder);
				return cursor;
			}	
			
		}
		
		else {
			
			try
			{
			  date = df.parse(tmpQueriesParts[1].trim() + " 00:00:01");
			} catch (ParseException e){

			}
			
			mPreciseStart = date.getTime();
			
			try
			{
			  date = df.parse(tmpQueriesParts[1].trim() + " 23:59:59");
			} catch (ParseException e){

			}
			
			mPreciseEnd = date.getTime();
			
			if (mFinalQuery.startsWith("+")) {
				cursors = new Cursor[2];
				
				queryBuilder.appendWhere("CAST(" + ProofDataBase.COLUMN_TIMESTAMP
						+ " AS INTEGER) BETWEEN CAST(" + mPreciseStart + " AS INTEGER) AND CAST(" + mPreciseEnd + " AS INTEGER) AND telephone"
						+ " LIKE " + "\"%" + mFinalQuery + "%\"");

				cursors[0] = queryBuilder.query(db, projection, selection,
						selectionArgs, null, null, sortOrder);

				mFinalQuery = "0" + mFinalQuery.substring(3).trim();

				queryBuilder = new SQLiteQueryBuilder();

				queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);

				queryBuilder.appendWhere("CAST(" + ProofDataBase.COLUMN_TIMESTAMP
						+ " AS INTEGER) BETWEEN CAST(" + mPreciseStart + " AS INTEGER) AND CAST(" + mPreciseEnd + " AS INTEGER) AND telephone"
						+ " LIKE " + "\"%" + mFinalQuery + "%\"");


				cursors[1] = queryBuilder.query(db, projection, selection,
						selectionArgs, null, null, sortOrder);

				for (Cursor c : cursors)
					c.setNotificationUri(getContext().getContentResolver(), uri);

				return new MergeCursor(cursors);

			} else if (mFinalQuery.startsWith("0")) {
				cursors = new Cursor[2];
				
				queryBuilder.appendWhere("CAST(" + ProofDataBase.COLUMN_TIMESTAMP
						+ " AS INTEGER) BETWEEN CAST(" + mPreciseStart + " AS INTEGER) AND CAST(" + mPreciseEnd + " AS INTEGER) AND telephone"
						+ " LIKE " + "\"%" + mFinalQuery + "%\"");


				cursors[0] = queryBuilder.query(db, projection, selection,
						selectionArgs, null, null, sortOrder);

				mFinalQuery = "+33" + mFinalQuery.substring(1).trim();

				queryBuilder = new SQLiteQueryBuilder();

				queryBuilder.setTables(ProofDataBase.TABLE_RECODINGAPP);

				queryBuilder.appendWhere("CAST(" + ProofDataBase.COLUMN_TIMESTAMP
						+ " AS INTEGER) BETWEEN CAST(" + mPreciseStart + " AS INTEGER) AND CAST(" + mPreciseEnd + " AS INTEGER) AND telephone"
						+ " LIKE " + "\"%" + mFinalQuery + "%\"");


				cursors[1] = queryBuilder.query(db, projection, selection,
						selectionArgs, null, null, sortOrder);

				for (Cursor c : cursors)
					c.setNotificationUri(getContext().getContentResolver(), uri);

				return new MergeCursor(cursors);
			}

			else {
				queryBuilder.appendWhere("CAST(" + ProofDataBase.COLUMN_TIMESTAMP
						+ " AS INTEGER) BETWEEN CAST(" + mPreciseStart + " AS INTEGER) AND CAST(" + mPreciseEnd + " AS INTEGER) AND telephone"
						+ " LIKE " + "\"%" + mFinalQuery + "%\"");

				
				Cursor cursor = queryBuilder.query(db, projection, selection,
						selectionArgs, null, null, sortOrder);
				return cursor;
			}			
		}				
	}
	
	/* (non-Javadoc)
	 * @see android.content.SearchRecentSuggestionsProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		int uriType = sURIMatcher.match(uri);

		long id = 0;
		
		SQLiteDatabase sqlDB;

		try {
			sqlDB = database.getWritableDatabase();
		}
		catch(Exception e) {
			sqlDB = database.getReadableDatabase();
		}

		sqlDB.execSQL(" PRAGMA foreign_keys = ON ");

		switch (uriType) {

			// Phone Records

		case RECORDS:
			id = sqlDB.insertOrThrow(ProofDataBase.TABLE_RECODINGAPP, null,
					values);
			break;

		// Phone's Note

		case NOTES:
			id = sqlDB.insertOrThrow(ProofDataBase.TABLE_NOTES, null, values);
			break;

		// Voice Records

		case VOICES:
			id = sqlDB.insertOrThrow(ProofDataBase.TABLE_VOICES, null, values);
			break;

		// Voice's Note

		case VOICE_NOTES:
			id = sqlDB.insertOrThrow(ProofDataBase.TABLE_VOICE_NOTES, null,
					values);
			break;

		// Excluded Contacts

		case EXCLUDED_CONTACTS:
			id = sqlDB.insertOrThrow(ProofDataBase.TABLE_EXCLUDED_CONTACTS,
					null, values);
			break;

		default:
			throw new IllegalArgumentException("insert -> Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	/* (non-Javadoc)
	 * @see android.content.SearchRecentSuggestionsProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		int rowsDeleted = 0;
		
		SQLiteDatabase sqlDB;

		try {
			sqlDB = database.getWritableDatabase();
		}
		catch(Exception e) {
			sqlDB = database.getReadableDatabase();
		}

		sqlDB.execSQL(" PRAGMA foreign_keys = ON ");

		switch (uriType) {

			// Phone Records

		case RECORDS:
			rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_RECODINGAPP,
					selection, selectionArgs);
			break;
		case RECORD_ID:

			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_RECODINGAPP,
						ProofDataBase.COLUMNRECODINGAPP_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_RECODINGAPP,
						ProofDataBase.COLUMNRECODINGAPP_ID + "=" + id + " and "
								+ selection, selectionArgs);
			}
			break;

		// Phone's Note

		case NOTES:
			rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_NOTES, selection,
					selectionArgs);
			break;
		case NOTE_ID:
			String id1 = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_NOTES,
						ProofDataBase.COLUMNNOTES_ID + "=" + id1, null);
			} else {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_NOTES,
						ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID + "="
								+ id1 + " and " + selection, selectionArgs);
			}
			break;
		case NOTE_RECORDID:
			String recId = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_NOTES,
						ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID + "="
								+ recId, null);
			} else {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_NOTES,
						ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID + "="
								+ recId + " and " + selection, selectionArgs);
			}
			break;

		// Voice Records

		case VOICES:
			rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_VOICES, selection,
					selectionArgs);
			break;
		case VOICE_ID:
			String idv = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_VOICES,
						ProofDataBase.COLUMNVOICE_ID + "=" + idv, null);
			} else {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_VOICES,
						ProofDataBase.COLUMNVOICE_ID + "=" + idv + " and "
								+ selection, selectionArgs);
			}
			break;

		// Voice's Note

		case VOICE_NOTES:
			rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_VOICE_NOTES,
					selection, selectionArgs);
			break;
		case VOICE_NOTE_ID:
			String idVoiceNote = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_VOICE_NOTES,
						ProofDataBase.COLUMNVOICE_NOTES_ID + "=" + idVoiceNote,
						null);
			} else {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_NOTES,
						ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID + "="
								+ idVoiceNote + " and " + selection,
						selectionArgs);
			}
			break;
		case VOICE_NOTE_RECORDID:
			String voiceNoteId = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_VOICE_NOTES,
						ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID + "="
								+ voiceNoteId, null);
			} else {
				rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_VOICE_NOTES,
						ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID + "="
								+ voiceNoteId + " and " + selection,
						selectionArgs);
			}
			break;

		// Excluded Contacts

		case EXCLUDED_CONTACTS:
			rowsDeleted = sqlDB.delete(ProofDataBase.TABLE_EXCLUDED_CONTACTS,
					selection, selectionArgs);
			break;

		case EXCLUDED_CONTACT_ID:
			String idExcluded = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(
						ProofDataBase.TABLE_EXCLUDED_CONTACTS,
						ProofDataBase.COLUMN_CONTACT_ID + "=" + idExcluded,
						null);
			} else {
				rowsDeleted = sqlDB.delete(
						ProofDataBase.TABLE_EXCLUDED_CONTACTS,
						ProofDataBase.COLUMN_CONTACT_ID + "=" + idExcluded
								+ " and " + selection, selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("delete -> Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		int rowsUpdated = 0;
		
		SQLiteDatabase sqlDB;

		try {
			sqlDB = database.getWritableDatabase();
		}
		catch(Exception e) {
			sqlDB = database.getReadableDatabase();
		}

		sqlDB.execSQL(" PRAGMA foreign_keys = ON ");

		switch (uriType) {

			// Phone Records

		case RECORDS:
			rowsUpdated = sqlDB.update(ProofDataBase.TABLE_RECODINGAPP, values,
					selection, selectionArgs);
			break;
		case RECORD_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_RECODINGAPP,
						values, ProofDataBase.COLUMNRECODINGAPP_ID + "=" + id,
						null);
			} else {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_RECODINGAPP,
						values, ProofDataBase.COLUMNRECODINGAPP_ID + "=" + id
								+ " and " + selection, selectionArgs);
			}
			break;

		// Phone's Note

		case NOTES:
			rowsUpdated = sqlDB.update(ProofDataBase.TABLE_NOTES, values,
					selection, selectionArgs);
			break;
		case NOTE_ID:
			String id1 = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_NOTES, values,
						ProofDataBase.COLUMNNOTES_ID + "=" + id1, null);
			} else {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_NOTES, values,
						ProofDataBase.COLUMNNOTES_ID + "=" + id1 + " and "
								+ selection, selectionArgs);
			}
			break;
		case NOTE_RECORDID:
			String recId = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_NOTES, values,
						ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID + "="
								+ recId, null);
			} else {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_NOTES, values,
						ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID + "="
								+ recId + " and " + selection, selectionArgs);
			}
			break;

		// Voice Records

		case VOICES:
			rowsUpdated = sqlDB.update(ProofDataBase.TABLE_VOICES, values,
					selection, selectionArgs);
			break;
		case VOICE_ID:
			String idv = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_VOICES, values,
						ProofDataBase.COLUMNVOICE_ID + "=" + idv, null);
			} else {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_VOICES, values,
						ProofDataBase.COLUMNVOICE_ID + "=" + idv + " and "
								+ selection, selectionArgs);
			}
			break;

		// Voice's Note

		case VOICE_NOTES:
			rowsUpdated = sqlDB.update(ProofDataBase.TABLE_VOICE_NOTES, values,
					selection, selectionArgs);
			break;
		case VOICE_NOTE_ID:
			String idVoiceNote = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_VOICE_NOTES,
						values, ProofDataBase.COLUMNVOICE_NOTES_ID + "="
								+ idVoiceNote, null);
			} else {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_VOICE_NOTES,
						values, ProofDataBase.COLUMNVOICE_NOTES_ID + "="
								+ idVoiceNote + " and " + selection,
						selectionArgs);
			}
			break;
		case VOICE_NOTE_RECORDID:
			String noteVoiceId = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_VOICE_NOTES,
						values, ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID
								+ "=" + noteVoiceId, null);
			} else {
				rowsUpdated = sqlDB.update(ProofDataBase.TABLE_VOICE_NOTES,
						values, ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID
								+ "=" + noteVoiceId + " and " + selection,
						selectionArgs);
			}
			break;

		// Phone Records

		case EXCLUDED_CONTACTS:
			rowsUpdated = sqlDB.update(ProofDataBase.TABLE_EXCLUDED_CONTACTS,
					values, selection, selectionArgs);
			break;
		case EXCLUDED_CONTACT_ID:
			String idExcluded = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(
						ProofDataBase.TABLE_EXCLUDED_CONTACTS, values,
						ProofDataBase.COLUMN_CONTACT_ID + "=" + idExcluded,
						null);
			} else {
				rowsUpdated = sqlDB.update(
						ProofDataBase.TABLE_EXCLUDED_CONTACTS, values,
						ProofDataBase.COLUMN_CONTACT_ID + "=" + idExcluded
								+ " and " + selection, selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("update -> Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = {

				// Phone Records
				ProofDataBase.COLUMN_TELEPHONE,
				ProofDataBase.COLUMN_CONTRACT_ID,
				ProofDataBase.COLUMN_TIMESTAMP,
				ProofDataBase.COLUMN_FILE,
				ProofDataBase.COLUMNRECODINGAPP_ID,
				ProofDataBase.COLUMN_SENS,
				ProofDataBase.COLUMN_TAILLE,
				ProofDataBase.COLUMN_HTIME,
				ProofDataBase.COLUMN_TITLE,
				ProofDataBase.COLUMN_ISYNC_PH,

				// Phone's Note
				ProofDataBase.COLUMN_NOTE,
				ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID,
				ProofDataBase.COLUMNNOTES_ID,
				ProofDataBase.COLUMN_DATE_LAST_MODIF,
				ProofDataBase.COLUMN_ISYNC_NOP,

				// Voice Records
				ProofDataBase.COLUMNVOICE_ID,
				ProofDataBase.COLUMN_VOICE_TIMESTAMP,
				ProofDataBase.COLUMN_VOICE_FILE,
				ProofDataBase.COLUMN_VOICE_TAILLE,
				ProofDataBase.COLUMN_VOICE_HTIME,
				ProofDataBase.COLUMN_ISYNC_VO,
				
				// Voice's Note
				ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID,
				ProofDataBase.COLUMNVOICE_NOTE,
				ProofDataBase.COLUMNVOICE_NOTES_ID,
				ProofDataBase.COLUMNVOICE_TITLE,
				ProofDataBase.COLUMNVOICE_DATE_CREATION,
				ProofDataBase.COLUMN_ISYNC_NOV,
				
				// Excluded Contacts
				ProofDataBase.COLUMN_CONTACT_ID,
				ProofDataBase.COLUMN_CONTRACT_CONTACTS_ID,
				ProofDataBase.COLUMN_DISPLAY_NAME,
				ProofDataBase.COLUMN_PHONE_NUMBER

		};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

	/**
	 * @return the authority
	 */
	public static String getAuthority() {
		return AUTHORITY;
	}

	/**
	 * CUSTOM REQUESTS DB
	 */


	public static int getItemsCount(String mQuery) {
		
		SQLiteDatabase mSqlDb;
		int count;
		
		try {
			mSqlDb = database.getWritableDatabase();
		}
		catch(Exception e) {
			mSqlDb = database.getReadableDatabase();
		}

		Cursor c = mSqlDb.rawQuery(mQuery, null);

		count =  c.getCount();
		
		c.close();
		
		return count;
	}

	public static String getVoiceNoteById(String mId) {
		
		String query = "SELECT titre from voicenotesproof WHERE RecId=?";
		String mTitle = "";
		SQLiteDatabase mSqlDb;

		try {
			mSqlDb = database.getWritableDatabase();
		}
		catch(Exception e) {
			mSqlDb = database.getReadableDatabase();
		}
		
		Cursor c = mSqlDb.rawQuery(query, new String[] { mId });
		
		try {
			while (c != null && c.moveToNext()) {
				mTitle = (c.getString(c
						.getColumnIndex(ProofDataBase.COLUMNVOICE_TITLE)));

				if (Settings.isDebug())
					Log.d(TAG, "R.id.idrecord: " + mTitle);
			}
		}
		catch(Exception e) {
			Log.e(TAG, "" + e);
		}
		finally {
			c.close();
		}		

		return mTitle;
	}

	public static String[] getVoicesAndCallsCount() {

		String[] AllCounts = new String[2];

		SQLiteDatabase mSqlDb;

		try {
			mSqlDb = database.getWritableDatabase();
		}
		catch(Exception e) {
			mSqlDb = database.getReadableDatabase();
		}
		
		String query = "SELECT count(*) from recordsproof";
		Cursor cCalls = mSqlDb.rawQuery(query, null);
		
		try {
			while (cCalls != null && cCalls.moveToNext())
				AllCounts[0] = cCalls.getString(0);
		}
		catch(Exception e) {
			Log.e(TAG, "" + e);
		}
		finally {
			cCalls.close();
		}
		
		query = "SELECT count(*) from voicesproof";
		Cursor cVoice = mSqlDb.rawQuery(query, null);		
		
		try {
			while (cVoice != null && cVoice.moveToNext())
				AllCounts[1] = cVoice.getString(0);
		}
		catch(Exception e) {
			Log.e(TAG, "" + e);
		}
		finally {
			cVoice.close();
		}

		return AllCounts;
	}

	public static String[] getExContactsAndNotCount(ContentResolver cr) {

		String[] AllCounts = new String[2];
		SQLiteDatabase mSqlDb;
		String query = "SELECT count(*) from excludedcontactsproof";		
		
		try {
			mSqlDb = database.getWritableDatabase();
		}
		catch(Exception e) {
			mSqlDb = database.getReadableDatabase();
		}
		
		Cursor cEx = mSqlDb.rawQuery(query, null);
		
		try {
			while (cEx != null && cEx.moveToNext())
				AllCounts[0] = cEx.getString(0);

				Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
						ContactsContract.Contacts.HAS_PHONE_NUMBER + "=?",
						new String[] { "1" }, null);
				
				AllCounts[1] = (cur.getCount() - Integer.parseInt(AllCounts[0])) + "";
				
				cur.close();
		}
		catch(Exception e) {
			Log.e(TAG, "" + e);
		}
		finally {
			cEx.close();
		}

		return AllCounts;
	}



	/**
	 * @param type
	 * @return
	 */
	public static List<Record> getRecordsFilesList1(Settings.mType type) {
		
		List<Record> list = new ArrayList<Record>();
		SQLiteDatabase mSqlDb;
		String query = "";

		switch (type) {
		case CALL:
			query = "SELECT _id, emplacement from recordsproof;";
			break;
		case VOICE_TITLED:
			query = "SELECT _id, emplacement from voicesproof;";
			break;
		case VOICE_UNTITLED:
			query = "SELECT _id, emplacement from voicesproof;";
			break;
		default:
			break;
		}	
		

		try {
			mSqlDb = database.getWritableDatabase();
		}
		catch(Exception e) {
			mSqlDb = database.getReadableDatabase();
		}
		
		Cursor c = mSqlDb.rawQuery(query, null);
		
		try {
			while (c != null && c.moveToNext()) {
				Record mRecord = new Record();

				switch (type) {
				case CALL:
					mRecord.setmId(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMNRECODINGAPP_ID)));
					mRecord.setmFilePath(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMN_FILE)));
					break;

				case VOICE_TITLED:
					mRecord.setmId(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMNVOICE_ID)));
					mRecord.setmFilePath(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE)));
					break;

				case VOICE_UNTITLED:
					mRecord.setmId(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMNVOICE_ID)));
					mRecord.setmFilePath(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE)));
					break;
				default:
					break;
				}
				list.add(mRecord);
			}
		}
		catch(Exception e) {
			Log.e(TAG, "" + e);
		}
		finally {
			c.close();
		}
		
		return list;
	}



}