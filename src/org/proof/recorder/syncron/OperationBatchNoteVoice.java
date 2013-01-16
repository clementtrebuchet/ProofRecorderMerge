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
import org.proof.recorder.database.models.NotesVoiceRPC;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.simplexfb.RapatFic;
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

public class OperationBatchNoteVoice {
	private Context mContext;
	private ContentResolver mResolver;
	private ArrayList<NotesVoiceRPC> mOperationsRecord;
	private XMLRPCClient client;
	protected final static String TAG ="OperationBatchNoteVoice";
	private URI uri;
	private NotesVoiceRPC NotesVoiceRCP;
	private String CV_NOTES_ID = "COLUMNVOICE_NOTES_ID";
	private String CV_ID_COLUMNVOICE_ID =  "COLUMNVOICE_ID_COLUMNVOICE_ID";
	private String CV_TITLE = "COLUMNVOICE_TITLE";
	private String CV_NOTE = "COLUMNVOICE_NOTE";
	private String CV_DATE_CREATION = "COLUMNVOICE_DATE_CREATION";
	private String COLUMN_ISYNC_NOV = "COLUMN_ISYNC_NOV";
	Handler mHandler;
	private Message msg;
	private int total; 
	

	/**
	 * @param context
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * performe une sauvegarde de la table recordsproof et mais les entrée à 1(synchronisé) dans la table
	 * retourne une string ok
	 */
	public OperationBatchNoteVoice(Context context, Handler mH) throws NoSuchAlgorithmException,
			FileNotFoundException {
		uri = URI.create("https://sd-21117.dedibox.fr:8888");
		mHandler = mH; 
		mContext = context;
		mResolver = mContext.getContentResolver();
		mOperationsRecord = new ArrayList<NotesVoiceRPC>();
		populateIsyncList();
		client = new XMLRPCClient(uri, mContext.getApplicationContext());
		//Looper.prepare();
		msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt("ENDOP", 0);
		b.putInt("total", 78);
		b.putString("Hmsg", "Mise à jour ... (4)");
		b.putString("opmsg", "B");
		msg.setData(b);
		mHandler.sendMessage(msg);
		XMLRPCMethod method = new XMLRPCMethod("isyncVoiceNote",
				new XMLRPCMethodCallback() {
					@Override
					public void callFinished(Object result) {
						Bundle b = new Bundle();
						msg = mHandler.obtainMessage();
						b.putInt("ENDOP", 1);
						b.putInt("total", 100);
						b.putString("opmsg", ""+result);
						b.putString("Hmsg", "Mise à jour ... (4)");
						b.putInt("pos", 4);
						msg.setData(b);
						mHandler.sendMessage(msg);
						Log.i(TAG, "UPLOAD DE LA TABLE VOICE NOTE FIN OP");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						RapatFic.setIsUPLOAD(true);
						new RapatFic(mContext.getApplicationContext());
						/*Bundle C = new Bundle();
						C.putBoolean("AUTO", true);
						Intent IJ = new Intent("android.intent.action.MAIN");
						IJ.setComponent(new ComponentName("org.proof.recorderftp", "org.proof.recorderftp.ProofRecorderFtp"));
						IJ.addCategory("android.intent.category.LAUNCHER");
						IJ.putExtras(C);
						IJ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(IJ);*/
						

					}
				}, mHandler);

		String Utilisateur = Settings.getUsername(mContext);
		String MotPasse = Settings.getPassword(mContext);
		Object[] params = { Utilisateur, MotPasse,
				new NotesVoiceRPCList(mOperationsRecord) };
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
	public OperationBatchNoteVoice(Context context, boolean reverseOp, Handler mH) {
		if (reverseOp == false) {
			mOperationsRecord = null;
			return;
		}
		mHandler = mH; 
		uri = URI.create("https://sd-21117.dedibox.fr:8888");
		mContext = context;
		mResolver = mContext.getContentResolver();
		mOperationsRecord = new ArrayList<NotesVoiceRPC>();
		client = new XMLRPCClient(uri, mContext.getApplicationContext());
		XMLRPCMethod method = new XMLRPCMethod("isyncVoiceNotesReverse",
				new XMLRPCMethodCallback() {
					@Override
					@SuppressWarnings("unchecked")
					public void callFinished(Object result) {
						Log.v(TAG, "Retour des données");
						Uri uri = Uri.withAppendedPath(PersonnalProofContentProvider.CONTENT_URI, "vnotes");
						Bundle b = new Bundle();
						Map<String, NotesRecordRPC> resultatServeur = (Map<String, NotesRecordRPC>) result;

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
							b.putString("Hmsg",  "");
							b.putString("opmsg", "B");
							msg.setData(b);
							mHandler.sendMessage(msg);
							int tot = 0;
							String message;
							int idForUpdate = 0;
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
									if (secondMapEntry.getKey().equals(CV_NOTES_ID)) {
										idForUpdate =  Integer.parseInt((String) secondMapEntry
												.getValue());
										values.put(
												ProofDataBase.COLUMNVOICE_NOTES_ID,
												(String) secondMapEntry.getValue());

									}  if (secondMapEntry.getKey().equals(
											CV_ID_COLUMNVOICE_ID)) {

										values.put(
												ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CV_TITLE)) {

										values.put(
												ProofDataBase.COLUMNVOICE_TITLE,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CV_NOTE)) {

										values.put(ProofDataBase.COLUMNVOICE_NOTE,
												(String) secondMapEntry
														.getValue());

									}  if (secondMapEntry.getKey().equals(
											CV_DATE_CREATION)) {

										values.put(ProofDataBase.COLUMNVOICE_DATE_CREATION,
												(String) secondMapEntry
												.getValue());

									} 
									 if (secondMapEntry.getKey().equals(
											COLUMN_ISYNC_NOV)) {

										values.put(ProofDataBase.COLUMN_ISYNC_NOV,
												Integer.parseInt((String) secondMapEntry
														.getValue()));

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
												PersonnalProofContentProvider.CONTENT_URI, "vnote_id/"
														+ idForUpdate);
										mContext.getContentResolver().update(uri1,values, null, null);
										values.clear();
										tot++;
										message = message+"  ==  "+"Update data ok";
										Log.e(TAG, message);
									  
									} catch(Exception e){
										
										msg = mHandler.obtainMessage();
										b.putInt("ENDOP", 0);
										b.putString("Hmsg", "Erreur  : "+e);
										b.putString("opmsg", "B");
										msg.setData(b);
										mHandler.sendMessage(msg);
									}
									

								}
								Log.e(TAG, "OK FIN DOWNLOAD RECORD");
								msg = mHandler.obtainMessage();
								b.putInt("total", 100);
								b.putInt("ENDOP", 1);
								b.putInt("pos", 3);
								b.putString("opmsg", "100");
								b.putString("Hmsg", "Restauration (4) de "+resultatServeur.size()+" data");
								msg.setData(b);
								mHandler.sendMessage(msg);
								RapatFic.setIsUPLOAD(false);
								new RapatFic(mContext.getApplicationContext());
								
							}

						}
					}, mHandler);


		String Utilisateur = Settings.getUsername(mContext);
		String MotPasse = Settings.getPassword(mContext);
		Object[] params = { Utilisateur, MotPasse,
				new NotesVoiceRPCList(mOperationsRecord) };
		method.call(params, client);

	}
	
	
	public static int progressBarTotal(Context mcont) {
		int i = 0;
		int tot = 0;
		Uri urlEntreeNonSync = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "vnote_sync");
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
	 */
	/*
	 * private String CV_NOTES_ID = "COLUMNVOICE_NOTES_ID";
	private String CV_ID_COLUMNVOICE_ID =  "COLUMNVOICE_ID_COLUMNVOICE_ID";
	private String CV_TITLE = "COLUMNVOICE_TITLE";
	private String CV_NOTE = "COLUMNVOICE_NOTE";
	private String CV_DATE_CREATION = "COLUMNVOICE_DATE_CREATION";
	private String COLUMN_ISYNC_NOV = "COLUMN_ISYNC_NOV";
	 */
	private void populateIsyncList() throws NoSuchAlgorithmException,
			FileNotFoundException {
		int i = 0;
		Uri urlEntreeNonSync = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "vnote_sync");
		Cursor c = mResolver.query(urlEntreeNonSync, null, null, null, null);
		while (c.moveToNext()) {
			int isSync = c.getInt(c
					.getColumnIndex(ProofDataBase.COLUMN_ISYNC_NOV));
			String id = c.getString(c
					.getColumnIndex(ProofDataBase.COLUMNVOICE_NOTES_ID));
			if (isSync == i) {
				total++;
				NotesVoiceRCP = new NotesVoiceRPC(id,
						c.getString(c.getColumnIndex(ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID)),
						c.getString(c.getColumnIndex(ProofDataBase.COLUMNVOICE_TITLE)),
						c.getString(c.getColumnIndex(ProofDataBase.COLUMNVOICE_NOTE)),
						c.getString(c.getColumnIndex(ProofDataBase.COLUMNVOICE_DATE_CREATION)), 1);
				mOperationsRecord.add(NotesVoiceRCP);
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
		values.put(ProofDataBase.COLUMN_ISYNC_NOV, 1);
		final Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "vnote_id/" + id);
		mContext.getContentResolver().update(uri, values, null, null);
		if (Settings.isDebug()) {

			Log.v(TAG, "record_id to set to sync true is : " + id);
		}
	}


	/**
	 * @author clement
	 *
	 */
	public class NotesVoiceRPCList implements XMLRPCSerializable {
		private List<NotesVoiceRPC> LIST;

		public NotesVoiceRPCList(List<NotesVoiceRPC> list) {
			this.LIST = list;

		}

		@Override
		public Map<String, List<NotesVoiceRPC>> getSerializable() {
			Map<String, List<NotesVoiceRPC>> map = new HashMap<String, List<NotesVoiceRPC>>();
			map.put("SYNCTABLENOTEVOICE", LIST);
			return map;
		}
	}

	

}
