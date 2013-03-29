package org.proof.recorder.database.support;

import org.proof.recorder.Settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProofDataBase {
	/*
	 * TABLE DE LA BASE DE DONNEE
	 */

	// Phone Call Records

	public static String TABLE_RECODINGAPP = "recordsproof";
	public static final String COLUMNRECODINGAPP_ID = "_id";
	public static final String COLUMN_TELEPHONE = "telephone";
	public static final String COLUMN_CONTRACT_ID = "contract_id";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_FILE = "emplacement";
	public static final String COLUMN_SENS = "sens";
	public static final String COLUMN_TAILLE = "taille";
	public static final String COLUMN_HTIME = "htime";
	public static final String COLUMN_ISYNC_PH ="Isync";
	

	// Voice Records

	public static String TABLE_VOICES = "voicesproof";
	public static final String COLUMNVOICE_ID = "_id";
	public static final String COLUMN_VOICE_TIMESTAMP = "timestamp";
	public static final String COLUMN_VOICE_FILE = "emplacement";
	public static final String COLUMN_VOICE_TAILLE = "taille";
	public static final String COLUMN_VOICE_HTIME = "htime";
	public static final String COLUMN_ISYNC_VO = "Isync";

	// Phone's Notes

	public static String TABLE_NOTES = "notesproof";
	public static final String COLUMNNOTES_ID = "_id";
	public static final String COLUMN_ID_COLUMNRECODINGAPP_ID = "RecId";
	public static final String COLUMN_TITLE = "titre";
	public static final String COLUMN_NOTE = "note";
	public static final String COLUMN_DATE_LAST_MODIF = "DateLastModif";
	public static final String COLUMN_ISYNC_NOP = "Isync";

	// Voice's Notes

	public static String TABLE_VOICE_NOTES = "voicenotesproof";
	public static final String COLUMNVOICE_NOTES_ID = "_id";
	public static final String COLUMNVOICE_ID_COLUMNVOICE_ID = "RecId";
	public static final String COLUMNVOICE_TITLE = "titre";
	public static final String COLUMNVOICE_NOTE = "note";
	public static final String COLUMNVOICE_DATE_CREATION = "date_cr";
	public static final String COLUMN_ISYNC_NOV = "Isync";
	
	// Mapping excluded Contacts
	public static String TABLE_EXCLUDED_CONTACTS = "excludedcontactsproof";
	public static final String COLUMN_CONTACT_ID = "_id";
	public static final String COLUMN_CONTRACT_CONTACTS_ID = "contacts_id";
	public static final String COLUMN_DISPLAY_NAME = "display_name";
	public static final String COLUMN_PHONE_NUMBER = "phone_number";

	private Context context;
	private SQLiteDatabase database;
	private ProofDataBaseHelper dbHelper;

	public ProofDataBase(Context context) {
		this.context = context;
	}

	public ProofDataBase open() throws SQLException {
		dbHelper = new ProofDataBaseHelper(this.context);
		
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	// Mapped excluded Contacts

	public long onCreateEntrieExcludedContacts(String contractContactsId,
			String displayName, String phoneNumber) {

		ContentValues valeur = new ContentValues();

		valeur.put(COLUMN_CONTRACT_CONTACTS_ID, contractContactsId);
		valeur.put(COLUMN_DISPLAY_NAME, displayName);
		valeur.put(COLUMN_PHONE_NUMBER, phoneNumber);

		return database.insert(TABLE_EXCLUDED_CONTACTS, "VIDE", valeur);
	}

	public Cursor onRestoreEntrieExcludedContacts() {
		return database.query(TABLE_EXCLUDED_CONTACTS, new String[] {
				COLUMN_CONTRACT_CONTACTS_ID, COLUMN_DISPLAY_NAME,
				COLUMN_PHONE_NUMBER, }, null, null, null, null, null);
	}

	/*
	 * REQUETE SQL
	 */
	private static final String EXCLUDED_CONTACTS_TABLE = "create table "
			+ TABLE_EXCLUDED_CONTACTS + "(" + COLUMN_CONTACT_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_CONTRACT_CONTACTS_ID + " text not null, "
			+ COLUMN_DISPLAY_NAME + " text not null," + ""
			+ COLUMN_PHONE_NUMBER + " text not null" + ", UNIQUE ("
			+ COLUMN_CONTRACT_CONTACTS_ID + "," + COLUMN_PHONE_NUMBER
			+ ") ON CONFLICT REPLACE);";

	// Phone's Records

	public long onCreateEntrieRecording(String number, String mContractId, String Ts, String file,
			String sens, String taille, String htime, String voice) {

		ContentValues valeur = new ContentValues();
		valeur.put(COLUMN_TELEPHONE, number);
		valeur.put(COLUMN_CONTRACT_ID, mContractId);
		valeur.put(COLUMN_TIMESTAMP, Ts);
		valeur.put(COLUMN_FILE, file);
		valeur.put(COLUMN_SENS, sens);
		valeur.put(COLUMN_TAILLE, taille);
		valeur.put(COLUMN_HTIME, htime);
		return database.insert(TABLE_RECODINGAPP, "VIDE", valeur);
	}

	public Cursor onRestoreEntrieRecording() {
		return database.query(TABLE_RECODINGAPP, new String[] {
				COLUMNRECODINGAPP_ID, COLUMN_CONTRACT_ID, COLUMN_TELEPHONE, COLUMN_TIMESTAMP,
				COLUMN_FILE, COLUMN_SENS, COLUMN_TAILLE, COLUMN_HTIME }, null,
				null, null, null, null);
	}
	/*
	 * REQUETE SQL
	 */
	private static final String RECORDING_TABLE = "create table "
			+ TABLE_RECODINGAPP + "(" + COLUMNRECODINGAPP_ID
			+ " integer primary key autoincrement, " + COLUMN_TELEPHONE
			+ " text not null, " + 
			COLUMN_CONTRACT_ID + " text not null," +
			COLUMN_TIMESTAMP + " text not null," + ""
			+ COLUMN_FILE + " text not null," + "" + COLUMN_SENS
			+ " text not null," + COLUMN_TAILLE + " text not null,"
			+ COLUMN_HTIME + " text not null," 
			+ COLUMN_ISYNC_PH + " integer NOT NULL DEFAULT(0)" + ");";

	// Phone Notes

	public long onCreateEntrieNotes(String id, String title, String note,
			long date) {

		ContentValues valeur = new ContentValues();
		valeur.put(COLUMN_ID_COLUMNRECODINGAPP_ID, id);
		valeur.put(COLUMN_TITLE, title);
		valeur.put(COLUMN_NOTE, note);
		valeur.put(COLUMN_DATE_LAST_MODIF, date);
		return database.insert(TABLE_NOTES, "VIDE", valeur);

	}

	public Cursor onRestoreEntrieNotes() {
		return database.query(TABLE_NOTES, new String[] { COLUMNNOTES_ID,
				COLUMN_ID_COLUMNRECODINGAPP_ID, COLUMN_TITLE, COLUMN_NOTE,
				COLUMN_DATE_LAST_MODIF, }, null, null, null, null, null);
	}

	/*
	 * REQUETE SQL
	 */
	/*
	 * REQUETE SQL
	 */
	private static final String NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES
			+ "(" + COLUMNNOTES_ID + " integer primary key autoincrement, "
			+ COLUMN_ID_COLUMNRECODINGAPP_ID + " integer not null, "
			+ COLUMN_TITLE + " text DEFAULT 'Titre de la note'," + "" + COLUMN_NOTE
			+ " text DEFAULT 'Votre note ...'," +
			COLUMN_DATE_LAST_MODIF + " text DEFAULT CURRENT_DATE," 
			+ COLUMN_ISYNC_NOP + " integer NOT NULL DEFAULT(0)," + 
			"FOREIGN KEY(" + COLUMN_ID_COLUMNRECODINGAPP_ID + ") REFERENCES " + TABLE_RECODINGAPP + "(" + COLUMNRECODINGAPP_ID + ") "+ 
			"ON DELETE CASCADE"
			+ ");";
	
	/*
	 * Voice's Records
	 */

	public long onCreateEntrieVoiceRecording(String number, String Ts,
			String file, String sens, String taille, String htime, String voice) {

		ContentValues valeur = new ContentValues();
		valeur.put(COLUMN_VOICE_FILE, file);
		valeur.put(COLUMN_VOICE_TIMESTAMP, sens);
		valeur.put(COLUMN_VOICE_TAILLE, taille);
		valeur.put(COLUMN_VOICE_HTIME, htime);
		return database.insert(TABLE_VOICES, "VIDE", valeur);
	}

	public Cursor onRestoreEntrieVoiceRecording() {
		return database.query(TABLE_VOICES, new String[] { COLUMNVOICE_ID,
				COLUMN_VOICE_FILE, COLUMN_VOICE_TIMESTAMP, COLUMN_VOICE_TAILLE,
				COLUMN_VOICE_HTIME }, null, null, null, null, null);
	}

	/*
	 * REQUETE SQL
	 */
	private static final String VOICES_TABLE = "create table " + TABLE_VOICES
			+ "(" + COLUMNVOICE_ID + " integer primary key autoincrement, "
			+ COLUMN_VOICE_FILE + " text not null, " + COLUMN_VOICE_TIMESTAMP
			+ " text not null," + "" + COLUMN_VOICE_TAILLE + " text not null,"
			+ "" + COLUMN_VOICE_HTIME + " text not null," 
			+ COLUMN_ISYNC_VO + " integer NOT NULL DEFAULT(0)" + ");";


	// Voice's Notes

	public long onCreateEntrieVoiceNotes(String id, String title, String note,
			long date) {

		ContentValues valeur = new ContentValues();
		valeur.put(COLUMNVOICE_ID_COLUMNVOICE_ID, id);
		valeur.put(COLUMNVOICE_TITLE, title);
		valeur.put(COLUMNVOICE_NOTE, note);
		valeur.put(COLUMNVOICE_DATE_CREATION, date);
		return database.insert(TABLE_VOICE_NOTES, "VIDE", valeur);

	}

	public Cursor onRestoreEntrieVoiceNotes() {
		return database.query(TABLE_VOICE_NOTES,
				new String[] { COLUMNVOICE_NOTES_ID,
						COLUMNVOICE_ID_COLUMNVOICE_ID, COLUMNVOICE_TITLE,
						COLUMNVOICE_NOTE, COLUMNVOICE_DATE_CREATION, }, null,
				null, null, null, null);
	}

	/*
	 * REQUETE SQL
	 */
	
	private static final String VOICE_NOTES_TABLE = "CREATE TABLE " + TABLE_VOICE_NOTES
			+ "(" + COLUMNVOICE_NOTES_ID + " integer primary key autoincrement, "
			+ COLUMNVOICE_ID_COLUMNVOICE_ID + "  text not null, "
			+ COLUMNVOICE_TITLE + " text DEFAULT 'Titre de la note'," + "" + COLUMNVOICE_NOTE
			+ " text DEFAULT 'Votre note ...'," +
			COLUMNVOICE_DATE_CREATION + " text DEFAULT CURRENT_DATE," 
			+ COLUMN_ISYNC_NOV + " integer NOT NULL DEFAULT(0),"+
			"FOREIGN KEY(" + COLUMNVOICE_ID_COLUMNVOICE_ID + ") REFERENCES " + TABLE_VOICES + "(" + COLUMNVOICE_ID + ") "+ 
			"ON DELETE CASCADE"
			+ ");";

	/*
	 * CREATION DE LA BASE DE DONNEE
	 */
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(RECORDING_TABLE);
		database.execSQL(NOTES_TABLE);
		database.execSQL(VOICES_TABLE);
		database.execSQL(VOICE_NOTES_TABLE);
		database.execSQL(EXCLUDED_CONTACTS_TABLE);
	}

	/*
	 * MISE A JOUR DE LA BASE DE DONNEE
	 */
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		
		if(Settings.isDebug())
			Log.w(ProofDataBase.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_RECODINGAPP);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_VOICES);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_VOICE_NOTES);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_EXCLUDED_CONTACTS);
		onCreate(database);
	}
}
