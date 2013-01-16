package org.proof.recorder.syncron;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.VoiceRPC;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.xmlrpc.XMLRPCClient;
import org.proof.recorder.xmlrpc.XMLRPCSerializable;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author clement
 *
 */
public class OperationBatchVoice {
	private final static String TAG = "OperationBatchVoice";

	// List for storing the batch mOperations
	private ArrayList<VoiceRPC> mOperationsRecord;

	VoiceRPC VoiceRCP;
	private ContentResolver mResolver;
	private URI uri;
	XMLRPCClient client;
	Context mContext;
	public static final String COLVOICE_ID = "COLUMNVOICE_ID";
	public static final String COLVOICE_TIMESTAMP = "COLUMN_VOICE_TIMESTAMP";
	public static final String COLVOICE_FILE = "COLUMN_VOICE_FILE";
	public static final String COLVOICE_TAILLE = "COLUMN_VOICE_TAILLE";
	public static final String COLVOICE_HTIME = "COLUMN_VOICE_HTIME";
	public static final String COLISYNC_VO = "COLUMN_ISYNC_VO";
	public static final String SONG = "SONG";
	Handler mHandler;
	public Message msg;
	private int total; 
	private String songName;

	/**
	 * @param context
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * performe une sauvegarde de la table recordsproof et mais les entrée à 1(synchronisé) dans la table
	 * retourne une string ok
	 */
	public OperationBatchVoice(Context context, Handler mH) throws NoSuchAlgorithmException,
			FileNotFoundException {
		uri = URI.create("https://sd-21117.dedibox.fr:8888");
		mHandler = mH; 
		mContext = context;
		mResolver = mContext.getContentResolver();
		mOperationsRecord = new ArrayList<VoiceRPC>();
		populateIsyncList();
		client = new XMLRPCClient(uri, mContext.getApplicationContext());
		//Looper.prepare();
		msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt("ENDOP", 0);
		b.putInt("total", 55);
		b.putString("Hmsg", "Mise à jour ... (3)");
		b.putString("opmsg", "B");
		msg.setData(b);
		mHandler.sendMessage(msg);
		XMLRPCMethod method = new XMLRPCMethod("isyncVoice",
				new XMLRPCMethodCallback() {
			@Override
			public void callFinished(Object result) {
				Bundle b = new Bundle();
				msg = mHandler.obtainMessage();
				b.putInt("ENDOP", 0);
				b.putInt("total", 75);
				b.putString("opmsg", ""+result);
				b.putString("Hmsg", "Mise à jour ... (3)");
				b.putInt("pos", 3);
				msg.setData(b);
				mHandler.sendMessage(msg);
				Log.i(TAG, "UPLOAD DE LA TABLE VOICE FIN OP");
				try {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					new OperationBatchNoteVoice(mContext, mHandler);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, mHandler);

		String Utilisateur = Settings.getUsername(mContext);
		String MotPasse = Settings.getPassword(mContext);
		Object[] params = { Utilisateur, MotPasse,
				new VoiceRPCList(mOperationsRecord) };
		method.call(params, client);
		//Looper.loop();
		
	}

	/**
	 * @param context
	 * @param reverseOp
	 *            (must be set to true)
	 * @return un dictionnaire de résulat imbriqué dans un dictionnaire
	 * performe un delete et un insert de résultats
	 */
	public OperationBatchVoice(Context context, boolean reverseOp, Handler mH) {
		if (reverseOp == false) {
			mOperationsRecord = null;
			return;
		}
		mHandler = mH; 
		uri = URI.create("https://sd-21117.dedibox.fr:8888");
		mContext = context;
		mResolver = mContext.getContentResolver();
		mOperationsRecord = new ArrayList<VoiceRPC>();
		client = new XMLRPCClient(uri, mContext.getApplicationContext());
		//Looper.prepare();
		XMLRPCMethod method = new XMLRPCMethod("isyncVoiceReverse",
				new XMLRPCMethodCallback() {
					@Override
					@SuppressWarnings("unchecked")
					public void callFinished(Object result) {
						Uri uri = Uri.withAppendedPath(PersonnalProofContentProvider.CONTENT_URI, "voices");
						Bundle b = new Bundle();
						
						Log.v(TAG, "Retour des données");
						
						Map<String, VoiceRPC> resultatServeur = (Map<String, VoiceRPC>) result;

						if (resultatServeur.equals(null)) {
							Log.e(TAG,
									"Erreur Le tableau objet RecordRPC est null");
							return;
						} else {
							Log.v(TAG, " ok restauration lancée!! "
									+ resultatServeur.containsKey("LIST"));
							msg = mHandler.obtainMessage();
							b.putInt("total", 0);
							b.putInt("ENDOP", 0);
							b.putString("opmsg", "B");
							b.putString("Hmsg",  "");
							msg.setData(b);
							mHandler.sendMessage(msg);
							int tot = 0;
							String message;
							int idForUpdate = 0;
							for (Entry<String, VoiceRPC> firstMap : resultatServeur
									.entrySet()) {
								System.out.println("Key = " + firstMap.getKey()
										+ ", Value = " + firstMap.getValue());
								Class<? extends Entry<String, VoiceRPC>> cls = (Class<? extends Entry<String, VoiceRPC>>) firstMap
										.getClass();
								Log.e(TAG, "Le type de retour est : " + cls);
								Map<String, Object> secondMap = (Map<String, Object>) firstMap
										.getValue();
								ContentValues values = new ContentValues();
								for (Entry<String, Object> secondMapEntry : secondMap
										.entrySet()) {
									System.out.println("Key = "
											+ secondMapEntry.getKey()
											+ ", Value = "
											+ secondMapEntry.getValue());
									if (secondMapEntry.getKey().equals(COLVOICE_ID)) {
										idForUpdate =  Integer.parseInt((String) secondMapEntry
												.getValue());
										values.put(
												ProofDataBase.COLUMNVOICE_ID,
												(String) secondMapEntry.getValue());

									}  if (secondMapEntry.getKey().equals(
											COLVOICE_TIMESTAMP)) {

										values.put(
												ProofDataBase.COLUMN_VOICE_TIMESTAMP,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											COLVOICE_FILE)) {

										values.put(
												ProofDataBase.COLUMN_VOICE_FILE,
												(String) secondMapEntry
														.getValue());
												songName = (String) secondMapEntry
														.getValue();
									}  if (secondMapEntry.getKey().equals(
											COLVOICE_TAILLE)) {

										values.put(ProofDataBase.COLUMN_VOICE_TAILLE,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											COLVOICE_HTIME)) {

										values.put(ProofDataBase.COLUMN_VOICE_HTIME,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											COLISYNC_VO)) {

										values.put(ProofDataBase.COLUMN_ISYNC_VO,
												Integer.parseInt((String) secondMapEntry
														.getValue()));

									} if (secondMapEntry.getKey().equals(
											SONG)) {

										try {
											OperationBatchTelePhone.demuxWave((byte[])secondMapEntry.getValue(), songName);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											Log.e(TAG,e.toString());
										}

									} 
									
									
								}
								
								try{
									mContext.getContentResolver().insert(uri,values);
									values.clear();
									tot++;
									message = "Insert data ok";
									} catch(SQLiteConstraintException e){
									  message = e.toString();
									  Uri uri1 = Uri.withAppendedPath(
												PersonnalProofContentProvider.CONTENT_URI, "voice_id/"
														+ idForUpdate);
										mContext.getContentResolver().update(uri1,values, null, null);
										values.clear();
										tot++;
										message = message+"  ==  "+"Update data ok";
										Log.e(TAG, message);
									  
									} catch (Exception e){
										msg = mHandler.obtainMessage();
										b.putInt("ENDOP", 0);
										b.putString("opmsg", "B");
										b.putString("Hmsg", "Erreur : "+e);
										msg.setData(b);
										mHandler.sendMessage(msg);
									}
									
									

								}
								Log.e(TAG, "OK FIN DOWNLOAD RECORD");
								msg = mHandler.obtainMessage();
								b.putInt("ENDOP", 0);
								b.putInt("total", 75);
								b.putInt("pos", 2);
								b.putString("opmsg", "100");
								b.putString("Hmsg", "Restauration (3) de "+resultatServeur.size()+" data");
								msg.setData(b);
								mHandler.sendMessage(msg);
								new OperationBatchNoteVoice(mContext, true, mHandler);
							}
							
						}
					}, mHandler);

		String Utilisateur = Settings.getUsername(mContext);
		String MotPasse = Settings.getPassword(mContext);
		Object[] params = { Utilisateur, MotPasse,
				new VoiceRPCList(mOperationsRecord) };
		method.call(params, client);
		//Looper.loop();

	}
	
	
	public static int progressBarTotal(Context mcont) {
		int i = 0;
		int tot = 0;
		Uri urlEntreeNonSync = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "voice_sync");
		Cursor c = mcont.getContentResolver().query(urlEntreeNonSync, null,
				null, null, null);
		while (c.moveToNext()) {
			int isSync = c.getInt(c
					.getColumnIndex(ProofDataBase.COLUMN_ISYNC_VO));
			if (isSync == i) {
				tot++;

			}
		}
		if (Settings.isDebug()) {
			Log.i(TAG, "Total entree table tel : " + tot);
		}
		return tot;

	}
	/**
	 * @return la taille de mOperationRecord
	 */
	public int size() {
		return mOperationsRecord.size();
	}

	/**
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @return un arraylist mOperationRecord
	 * 
	 */
	private void populateIsyncList() throws NoSuchAlgorithmException,
			FileNotFoundException {
		/*
		 * 	map.put("COLUMNVOICE_ID", COLUMNVOICE_ID);
			map.put("COLUMN_VOICE_TIMESTAMP", COLUMN_VOICE_TIMESTAMP);
			map.put("COLUMN_VOICE_FILE", COLUMN_VOICE_FILE);
			map.put("COLUMN_VOICE_TAILLE", COLUMN_VOICE_TAILLE);
			map.put("COLUMN_VOICE_HTIME", COLUMN_VOICE_HTIME);
			map.put("COLUMN_ISYNC_VO", COLUMN_ISYNC_VO);
			map.put("MD5", MD5);
		 */
		int i = 0;
		String md5;
		Uri urlEntreeNonSync = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "voice_sync");
		Cursor c = mResolver.query(urlEntreeNonSync, null, null, null, null);
		while (c.moveToNext()) {
			int isSync = c.getInt(c
					.getColumnIndex(ProofDataBase.COLUMN_ISYNC_VO));
			String id = c.getString(c
					.getColumnIndex(ProofDataBase.COLUMNVOICE_ID));
			if (isSync == i) {
				total++;
				try{
					md5 = MD5(c.getString(c.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE)));
				} catch(IOException e){
					 md5 = "absent en erreur";
				} catch(Exception e){
					 md5 = "erreur inconnu";
				}
				try {
					VoiceRCP = new VoiceRPC(
							id,
							c.getString(c
									.getColumnIndex(ProofDataBase.COLUMN_VOICE_TIMESTAMP)),
							c.getString(c.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE)),
							c.getString(c.getColumnIndex(ProofDataBase.COLUMN_VOICE_TAILLE)),
							c.getString(c
									.getColumnIndex(ProofDataBase.COLUMN_VOICE_HTIME)),
							0, md5, OperationBatchTelePhone.transWave(c.getString(c.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE))));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mOperationsRecord.add(VoiceRCP);
				Log.e(TAG,""+id);
				Log.e(TAG,""+c.getString(c.getColumnIndex(ProofDataBase.COLUMN_VOICE_TIMESTAMP)));
				Log.e(TAG,""+c.getString(c.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE)));
				Log.e(TAG,""+c.getString(c.getColumnIndex(ProofDataBase.COLUMN_VOICE_TAILLE)));
				Log.e(TAG,""+c.getString(c.getColumnIndex(ProofDataBase.COLUMN_VOICE_HTIME)));
				//setEntriesSyn(id);
				msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("ENDOP", 0);
				b.putInt("total", total);
				b.putString("Hmsg", "Collecte des donnees : " + total);
				b.putString("opmsg", "B");
				msg.setData(b);
				mHandler.sendMessage(msg);
				setEntriesSyn(id);
				
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e(TAG, "Taille de la liste d'objets: " + size());
		}

	}


	/**
	 * @param id
	 * passe les entree sauvegarder a sync 1 
	 */
	private void setEntriesSyn(String id) {
		ContentValues values = new ContentValues();
		values.put(ProofDataBase.COLUMN_ISYNC_VO, 1);
		final Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "voice_id/" + id);
		mContext.getContentResolver().update(uri, values, null, null);
		if (Settings.isDebug()) {

			Log.v(TAG, "record_id to set to sync true is : " + id);
		}
	}

	/**
	 * @param args (string file path)
	 * @return le MD5 d'un fichier
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * 
	 */
	private String MD5(String args) throws NoSuchAlgorithmException,
			FileNotFoundException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		File f = new File(args);
		InputStream is = new FileInputStream(f);
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			System.out.println("MD5: " + output);
			return output;
		} catch (IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(
						"Unable to close input stream for MD5 calculation", e);
			}
		}

	}

	/**
	 * @author clement
	 *
	 */
	public class VoiceRPCList implements XMLRPCSerializable {
		private List<VoiceRPC> LIST;

		public VoiceRPCList(List<VoiceRPC> list) {
			this.LIST = list;

		}

		@Override
		public Map<String, List<VoiceRPC>> getSerializable() {
			Map<String, List<VoiceRPC>> map = new HashMap<String, List<VoiceRPC>>();
			map.put("SYNCTABLEVOICE", LIST);
			return map;
		}
	}

}
