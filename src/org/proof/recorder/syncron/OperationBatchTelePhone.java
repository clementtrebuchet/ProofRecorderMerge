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
import org.proof.recorder.database.models.RecordRPC;
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
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * @author clement
 * 
 */
public class OperationBatchTelePhone {

	private final static String TAG = "OperationBatchTelePhone";

	// List for storing the batch mOperations
	private ArrayList<RecordRPC> mOperationsRecord;

	RecordRPC RecordRCP;
	private ContentResolver mResolver;
	private URI uri;
	XMLRPCClient client;
	Context mContext;
	private final String _ID = "applicationID";
	private final String CTEL = "COLUMN_TELEPHONE";
	private final String CCONTID = "COLUMN_CONTRACT_ID";
	private final String CSTAMP = "COLUMN_TIMESTAMP";
	private final String CFILE = "COLUMN_FILE";
	private final String CSENS = "COLUMN_SENS";
	private final String CTAILLE = "COLUMN_TAILLE";
	private final String CHTIME = "COLUMN_HTIME";
	private final String CISYNC_PH = "COLUMN_ISYNC_PH";
	public Handler mHandler;
	Message msg;
	int total;

	/**
	 * @param context
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 *             performe une sauvegarde de la table recordsproof et met les
	 *             entrée à 1(synchronisé) dans la table retourne une string ok
	 */
	public OperationBatchTelePhone(Context context, Handler mH, boolean looper)
			throws NoSuchAlgorithmException, FileNotFoundException {
		mHandler = mH;
		uri = URI.create("https://sd-21117.dedibox.fr:8888");
		mContext = context;
		mResolver = mContext.getContentResolver();
		mOperationsRecord = new ArrayList<RecordRPC>();
		populateIsyncList();
		client = new XMLRPCClient(uri, mContext.getApplicationContext());
		if (looper){Looper.prepare();}
		msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt("ENDOP", 0);
		b.putInt("total", 05);
		b.putString("Hmsg", "Mise à jour ... (1)");
		b.putString("opmsg", "B");
		msg.setData(b);
		mHandler.sendMessage(msg);
		XMLRPCMethod method = new XMLRPCMethod("isyncRecord",
				new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						Bundle b = new Bundle();
						msg = mHandler.obtainMessage();
						b.putInt("ENDOP", 0);
						b.putString("opmsg", ""+result);
						b.putString("Hmsg", "Mise à jour ... (1)");
						b.putInt("total", 25);
						b.putInt("pos", 1);
						msg.setData(b);
						mHandler.sendMessage(msg);
						Log.i(TAG, "UPLOAD DE LA TABLE ENREGISTREMENT FIN OP");
						try {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							new OperationBatchNoteTel(mContext, mHandler);
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
				new RecordRPCList(mOperationsRecord) };
		method.call(params, client);
		if (looper){Looper.loop();}
		
	}

	/**
	 * @param context
	 * @param reverseOp
	 *            (must be set to true)
	 * @return un dictionnaire de résulat imbriqué dans un dictionnaire performe
	 *         un delete et un insert de résultats
	 */
	public OperationBatchTelePhone(Context context, boolean reverseOp, Handler mH, boolean looper) {
		if (reverseOp == false) {
			mOperationsRecord = null;
			return;
		}
		mHandler = mH;
		uri = URI.create("https://sd-21117.dedibox.fr:8888");
		mContext = context;
		mResolver = mContext.getContentResolver();
		mOperationsRecord = new ArrayList<RecordRPC>();
		client = new XMLRPCClient(uri, mContext.getApplicationContext());
		if (looper){Looper.prepare();}
		XMLRPCMethod method = new XMLRPCMethod("isyncRecordReverse",
				new XMLRPCMethodCallback() {
					@SuppressWarnings("unchecked")
					public void callFinished(Object result) {
						Uri uri = Uri.withAppendedPath(PersonnalProofContentProvider.CONTENT_URI, "records");
						Bundle b = new Bundle();
						int enBase = baseTotal();
						Log.i(TAG,"TOTAL EN BASE AVANT MISE A JOUR VERS TEL : "+enBase);
						msg = mHandler.obtainMessage();
						b.putInt("total", 100);
						b.putString("opmsg", "B");
						b.putInt("ENDOP", 0);
						b.putString("Hmsg", "Retour des données");
						msg.setData(b);
						mHandler.sendMessage(msg);
						Log.v(TAG, "Retour des données");

						Map<String, RecordRPC> resultatServeur = (Map<String, RecordRPC>) result;
						
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
							for (Entry<String, RecordRPC> firstMap : resultatServeur
									.entrySet()) {
								
								System.out.println("Key = " + firstMap.getKey()
										+ ", Value = " + firstMap.getValue());
								Class<? extends Entry<String, RecordRPC>> cls = (Class<? extends Entry<String, RecordRPC>>) firstMap
										.getClass();
								Log.e(TAG, "Le type de retour est : " + cls);
								Map<String, Object> secondMap = (Map<String, Object>) firstMap
										.getValue();
								ContentValues values = new ContentValues();
								for (Entry<String, Object> secondMapEntry : ((Map<String, Object>) secondMap)
										.entrySet()) {
									System.out.println("Key = "
											+ secondMapEntry.getKey()
											+ ", Value = "
											+ secondMapEntry.getValue());
									if (secondMapEntry.getKey().equals(_ID)) {
										idForUpdate = Integer.parseInt((String) secondMapEntry
												.getValue());
										values.put(
												ProofDataBase.COLUMNRECODINGAPP_ID,
												Integer.parseInt((String) secondMapEntry
														.getValue()));

									}  if (secondMapEntry.getKey().equals(
											CTEL)) {

										values.put(
												ProofDataBase.COLUMN_TELEPHONE,
												(String) secondMapEntry
														.getValue());

									} if (secondMapEntry.getKey().equals(
											CCONTID)) {

										values.put(
												ProofDataBase.COLUMN_CONTRACT_ID,
												(String) secondMapEntry
														.getValue());

									} if (secondMapEntry.getKey().equals(
											CSTAMP)) {

										values.put(
												ProofDataBase.COLUMN_TIMESTAMP,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CFILE)) {

										values.put(ProofDataBase.COLUMN_FILE,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CSENS)) {

										values.put(ProofDataBase.COLUMN_SENS,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CTAILLE)) {

										values.put(ProofDataBase.COLUMN_TAILLE,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CHTIME)) {

										values.put(ProofDataBase.COLUMN_HTIME,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CISYNC_PH)) {

										values.put(
												ProofDataBase.COLUMN_ISYNC_PH,
												Integer.parseInt((String) secondMapEntry
												.getValue()));
									}
									Log.e(TAG, values.toString());
									
									
								}
								try{
								mContext.getContentResolver().insert(uri,values);
								values.clear();
								tot++;
								message = "Insert data ok";
								} catch(SQLiteConstraintException e){
								  message = e.toString();
								  Uri uri1 = Uri.withAppendedPath(
											PersonnalProofContentProvider.CONTENT_URI, "record_id/"
													+ idForUpdate);
									mContext.getContentResolver().update(uri1,values, null, null);
									values.clear();
									tot++;
									message = message+"  ==  "+"Update data ok";
									Log.e(TAG, message);
								} catch(Exception e){
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
							b.putInt("pos", 0);
							b.putInt("total", 25);
							b.putString("opmsg", "100");
							b.putString("Hmsg", "Restauration (1) de "+resultatServeur.size()+" data");
							msg.setData(b);
							mHandler.sendMessage(msg);
							new OperationBatchNoteTel(mContext, true, mHandler);
						}
						
					}
				}, mHandler);

		String Utilisateur = Settings.getUsername(mContext);
		String MotPasse = Settings.getPassword(mContext);
		Object[] params = { Utilisateur, MotPasse,
				new RecordRPCList(mOperationsRecord) };
		method.call(params, client);
		if (looper){Looper.loop();}

	}

	/**
	 * @return la taille de mOperationRecord
	 */
	public int size() {
		return mOperationsRecord.size();
	}

	private  int baseTotal() {
		int tot = 0;
		Uri urlEntreeNonSync = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "record_sync");
		Cursor c = mContext.getContentResolver().query(urlEntreeNonSync, null,
				null, null, null);
		while (c.moveToNext()) {
				tot++;	
		}
		if (Settings.isDebug()) {
			Log.i(TAG, "Total entree table tel : " + tot);
		}
		return tot;

	}
	public static int progressBarTotal(Context mcont) {
		int i = 0;
		int tot = 0;
		Uri urlEntreeNonSync = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "record_sync");
		Cursor c = mcont.getContentResolver().query(urlEntreeNonSync, null,
				null, null, null);
		while (c.moveToNext()) {
			int isSync = c.getInt(c
					.getColumnIndex(ProofDataBase.COLUMN_ISYNC_PH));
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
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @return un arraylist mOperationRecord
	 */
	private void populateIsyncList() throws NoSuchAlgorithmException,
			FileNotFoundException {
		int i = 0;
		String md5;
		Uri urlEntreeNonSync = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "record_sync");
		Cursor c = mResolver.query(urlEntreeNonSync, null, null, null, null);
		while (c.moveToNext()) {
			int isSync = c.getInt(c
					.getColumnIndex(ProofDataBase.COLUMN_ISYNC_PH));
			String id = c.getString(c
					.getColumnIndex(ProofDataBase.COLUMNRECODINGAPP_ID));
			if (isSync == i) {
				total++;
				try{
					 md5 = MD5(c.getString(c
							.getColumnIndex(ProofDataBase.COLUMN_FILE)));
				} catch(IOException e){
					 md5 = "absent en erreur";
				} catch(Exception e){
					 md5 = "erreur inconnu";
				}
				RecordRCP = new RecordRPC(
						id,
						c.getString(c
								.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE)),
						c.getString(c
								.getColumnIndex(ProofDataBase.COLUMN_CONTRACT_ID)),
						c.getString(c
								.getColumnIndex(ProofDataBase.COLUMN_TIMESTAMP)),
						c.getString(c.getColumnIndex(ProofDataBase.COLUMN_FILE)),
						c.getString(c.getColumnIndex(ProofDataBase.COLUMN_SENS)),
						c.getString(c
								.getColumnIndex(ProofDataBase.COLUMN_TAILLE)),
						c.getString(c
								.getColumnIndex(ProofDataBase.COLUMN_HTIME)),
						1, md5);
				mOperationsRecord.add(RecordRCP);
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
	 *            passe les entree sauvegarder a sync 1
	 */
	private void setEntriesSyn(String id) {
		ContentValues values = new ContentValues();
		values.put(ProofDataBase.COLUMN_ISYNC_PH, 1);
		final Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "record_id/" + id);
		mContext.getContentResolver().update(uri, values, null, null);
		if (Settings.isDebug()) {

			Log.v(TAG, "record_id to set to sync true is : " + id);
		}
	}

	/**
	 * @param args
	 *            (string file path)
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
	public class RecordRPCList implements XMLRPCSerializable {
		private List<RecordRPC> LIST;

		public RecordRPCList(List<RecordRPC> list) {
			this.LIST = list;

		}

		public Map<String, List<RecordRPC>> getSerializable() {
			Map<String, List<RecordRPC>> map = new HashMap<String, List<RecordRPC>>();
			map.put("SYNCTABLEPHONE", LIST);
			return map;
		}
	}

}
