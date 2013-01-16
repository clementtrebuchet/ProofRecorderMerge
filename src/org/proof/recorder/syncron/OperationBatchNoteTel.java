package org.proof.recorder.syncron;

import java.io.FileNotFoundException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.NotesRecordRPC;
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


public class OperationBatchNoteTel {
	
	private Context mContext;
	private ContentResolver mResolver;
	private ArrayList<NotesRecordRPC> mOperationsRecord;
	private XMLRPCClient client;
	protected final static String TAG ="OperationBatchNoteTel";
	private final String  CnCOLUMNRECODINGAPP_ID = "COLUMN_ID_COLUMNRECODINGAPP_ID";
	private URI uri;
	private NotesRecordRPC NotesRecordRCP;
	private final String  CN_ID = "applicationID";
	private final String  CNTITLE = "COLUMN_TITLE";
	private final String  CNNOTE = "COLUMN_NOTE";
	private final String  CNDATELMOD = "COLUMN_DATE_LAST_MODIF";
	private final String  CISYNC_NOP = "COLUMN_ISYNC_NOP";
	public Handler mHandler;
	Message msg;
	int total;
	
	

	/**
	 * @param context
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * performe une sauvegarde de la table recordsproof et mais les entrée à 1(synchronisé) dans la table
	 * retourne une string ok
	 */
	public OperationBatchNoteTel(Context context, Handler mH) throws NoSuchAlgorithmException,
			FileNotFoundException {
		uri = URI.create("https://sd-21117.dedibox.fr:8888");
		mContext = context;
		mResolver = mContext.getContentResolver();
		mHandler = mH;
		mOperationsRecord = new ArrayList<NotesRecordRPC>();
		populateIsyncList();
		client = new XMLRPCClient(uri, mContext.getApplicationContext());
		//Looper.prepare();
		msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		
		b.putInt("ENDOP", 0);
		b.putInt("total", 30);
		b.putString("Hmsg", "Mise à jour ... (2)");
		b.putString("opmsg", "B");
		msg.setData(b);
		mHandler.sendMessage(msg);
		XMLRPCMethod method = new XMLRPCMethod("isyncNote",
				new XMLRPCMethodCallback() {
			@Override
			public void callFinished(Object result) {
					Bundle b = new Bundle();
					msg = mHandler.obtainMessage();
					b.putInt("ENDOP", 0);
					b.putInt("total", 50);
					b.putInt("pos", 2);
					b.putString("opmsg", ""+result);
					b.putString("Hmsg", "Mise à jour ... (2)");
					msg.setData(b);
					mHandler.sendMessage(msg);
					Log.i(TAG, "UPLOAD DE LA TABLE NOTE-ENREGISTREMENT FIN OP");
					try {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						new OperationBatchVoice(mContext, mHandler);
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
				new NotesRecordRPCList(mOperationsRecord) };
		method.call(params, client);
		//Looper.loop();
		
	}

	public static int progressBarTotal(Context mcont) {
		int i = 0;
		int tot = 0;
		Uri urlEntreeNonSync = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "note_sync");
		Cursor c = mcont.getContentResolver().query(urlEntreeNonSync, null, null, null, null);
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
	 * @param context
	 * @param reverseOp
	 *            (must be set to true)
	 * @return un dictionnaire de résulat imbriqué dans un dictionnaire
	 * performe un delete et un insert de résultats
	 */
	public OperationBatchNoteTel(Context context, boolean reverseOp, Handler mH) {
		if (reverseOp == false) {
			mOperationsRecord = null;
			return;
		}
		mHandler = mH; 
		uri = URI.create("https://sd-21117.dedibox.fr:8888");
		mContext = context;
		mResolver = mContext.getContentResolver();
		mOperationsRecord = new ArrayList<NotesRecordRPC>();
		client = new XMLRPCClient(uri, mContext.getApplicationContext());
		//Looper.prepare();
		XMLRPCMethod method = new XMLRPCMethod("isyncNotesReverse",
				new XMLRPCMethodCallback() {
					@Override
					@SuppressWarnings("unchecked")
					public void callFinished(Object result) {
						Bundle b = new Bundle();
						Uri uri = Uri.withAppendedPath(PersonnalProofContentProvider.CONTENT_URI, "notes");
						Log.v(TAG, "Retour des données");
						
						Map<String, NotesRecordRPC> resultatServeur = (Map<String, NotesRecordRPC>) result;

						if (resultatServeur.equals(null)) {
							Log.e(TAG,
									"Erreur Le tableau objet RecordRPC est null");
							return;
						} else {
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
							Log.v(TAG, " ok restauration lancée!! "
									+ resultatServeur.containsKey("LIST"));
							for (Entry<String, NotesRecordRPC> firstMap : resultatServeur
									.entrySet()) {
								System.out.println("Key = " + firstMap.getKey()
										+ ", Value = " + firstMap.getValue());
								Class<? extends Entry<String, NotesRecordRPC>> cls = (Class<? extends Entry<String, NotesRecordRPC>>) firstMap
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
									if (secondMapEntry.getKey().equals(CN_ID)) {
										idForUpdate = Integer.parseInt((String) secondMapEntry
												.getValue());
										values.put(
												ProofDataBase.COLUMNNOTES_ID,
												(String) secondMapEntry.getValue());

									}  if (secondMapEntry.getKey().equals(
											CnCOLUMNRECODINGAPP_ID)) {

										values.put(
												ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID,
												(String) secondMapEntry
														.getValue());

									}
									
									
									
									if (secondMapEntry.getKey().equals(
											CNTITLE)) {

										values.put(
												ProofDataBase.COLUMN_TITLE,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CNNOTE)) {

										values.put(
												ProofDataBase.COLUMN_NOTE,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CNDATELMOD)) {

										values.put(ProofDataBase.COLUMN_DATE_LAST_MODIF,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CISYNC_NOP)) {

										values.put(ProofDataBase.COLUMN_ISYNC_NOP,
												Integer.parseInt((String) secondMapEntry
														.getValue()));

									} 
									
									
								}
								try{
									mContext.getContentResolver().insert(uri,values);
									
									tot++;
									message = "Insert data ok";
									} catch(SQLiteConstraintException e){
									  message = e.toString();
									  Uri uri1 = Uri.withAppendedPath(
												PersonnalProofContentProvider.CONTENT_URI, "note_id/"
														+ idForUpdate);
										mContext.getContentResolver().update(uri1,values, null, null);
										
										tot++;
										message = message+"  ==  "+"Update data ok";
										Log.e(TAG, message);
										
									} catch (Exception e){
										Log.e(TAG, ""+e);
										msg = mHandler.obtainMessage();
										b.putInt("ENDOP", 0);
										b.putString("opmsg", "B");
										b.putString("Hmsg", "Erreur : "+e);
										msg.setData(b);
										mHandler.sendMessage(msg);
										
									}
								values.clear();
									
									
							}
							
							Log.e(TAG, "OK FIN DOWNLOAD REC_NOTES");
							msg = mHandler.obtainMessage();
							b.putInt("total", 50);
							b.putInt("ENDOP", 0);
							b.putInt("pos", 1);
							b.putString("opmsg", "100");
							b.putString("Hmsg", "Restauration (2) de "+resultatServeur.size()+" data");
							msg.setData(b);
							mHandler.sendMessage(msg);
							new OperationBatchVoice(mContext, true,mHandler);
						}
						

					}
				}, mHandler);
		String Utilisateur = Settings.getUsername(mContext);
		String MotPasse = Settings.getPassword(mContext);
		Object[] params = { Utilisateur, MotPasse,
				new NotesRecordRPCList(mOperationsRecord) };
		method.call(params, client);
		//Looper.loop();

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
	 */
	private void populateIsyncList() throws NoSuchAlgorithmException,
			FileNotFoundException {
		int i = 0;
		Uri urlEntreeNonSync = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "note_sync");
		Cursor c = mResolver.query(urlEntreeNonSync, null, null, null, null);
		while (c.moveToNext()) {
			int isSync = c.getInt(c
					.getColumnIndex(ProofDataBase.COLUMN_ISYNC_NOP));
			String id = c.getString(c
					.getColumnIndex(ProofDataBase.COLUMNVOICE_NOTES_ID));
			if (isSync == i) {
				total++;
				NotesRecordRCP = new NotesRecordRPC(id,
						c.getString(c.getColumnIndex(ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID)),
						c.getString(c.getColumnIndex(ProofDataBase.COLUMN_TITLE)),
						c.getString(c.getColumnIndex(ProofDataBase.COLUMN_NOTE)),
						c.getString(c.getColumnIndex(ProofDataBase.COLUMN_DATE_LAST_MODIF)), 0);
				mOperationsRecord.add(NotesRecordRCP);
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
		values.put(ProofDataBase.COLUMN_ISYNC_NOP, 1);
		final Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "note_id/" + id);
		mContext.getContentResolver().update(uri, values, null, null);
		if (Settings.isDebug()) {

			Log.v(TAG, "record_id to set to sync true is : " + id);
		}
	}


	/**
	 * @author clement
	 *
	 */
	public class NotesRecordRPCList implements XMLRPCSerializable {
		private List<NotesRecordRPC> LIST;

		public NotesRecordRPCList(List<NotesRecordRPC> list) {
			this.LIST = list;

		}

		@Override
		public Map<String, List<NotesRecordRPC>> getSerializable() {
			Map<String, List<NotesRecordRPC>> map = new HashMap<String, List<NotesRecordRPC>>();
			map.put("SYNCTABLENOTE", LIST);
			return map;
		}
	}

}
