package org.proof.recorder.syncron;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.ControllerRPC;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.xmlrpc.XMLRPCClient;
import org.proof.recorder.xmlrpc.XMLRPCSerializable;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public  class OperationBatchController {

	protected static final String TAG = "OperationBatchController";
	protected Context mContext;
	protected Handler mHandler;
	private ArrayList<ControllerRPC> mOperationsRecord;
	private URI uri;
	private ContentResolver mResolver;
	private XMLRPCClient client;
	private Message msg;
	boolean justAChec;

	/**
	 * @param context
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 *             performe une sauvegarde de la table recordsproof et met les
	 *             entrée à 1(synchronisé) dans la table retourne une string ok
	 */
	public OperationBatchController(Context context, Handler mH, boolean looper, final boolean justACheck)
			throws NoSuchAlgorithmException, FileNotFoundException {
		justAChec = justACheck;
		mHandler = mH;
		uri = URI.create("https://sd-21117.dedibox.fr:8888");
		mContext = context;
		mResolver = mContext.getContentResolver();
		mOperationsRecord = new ArrayList<ControllerRPC>();
		populateIsyncList();
		client = new XMLRPCClient(uri, mContext.getApplicationContext());
		if (looper){Looper.prepare();}
		msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt("ENDOP", 0);
		b.putInt("total", 50);
		b.putString("Hmsg", "Controle préliminaire");
		b.putString("opmsg", "B");
		msg.setData(b);
		mHandler.sendMessage(msg);
		XMLRPCMethod method = new XMLRPCMethod("PREOPS",
				new XMLRPCMethodCallback() {
					public void callFinished(Object result) {
						Bundle b = new Bundle();
						msg = mHandler.obtainMessage();
						b.putInt("ENDOP", 0);
						b.putString("opmsg", ""+result);
						b.putString("Hmsg", "Controle préliminaire ...");
						b.putInt("total", 100);
						if (!justACheck){
							b.putInt("pos", 0);
						} else {
							b.putInt("pos", 50);//IMPORTANT 50 @LINK syncronUi Handler
						}
						msg.setData(b);
						mHandler.sendMessage(msg);
						Log.i(TAG, "PREOPS CONTROLLE:{"+result+"}");
						if (!justACheck){
							try {
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								new OperationBatchTelePhone(mContext,  mHandler,false);
							} catch (NoSuchAlgorithmException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						

					}
				}, mHandler);

		String Utilisateur = Settings.getUsername(mContext);
		String MotPasse = Settings.getPassword(mContext);
		Object[] params = { Utilisateur, MotPasse,
				new ControllerRPCList(mOperationsRecord) };
		method.call(params, client);
		if (looper){Looper.loop();}
		
	}

	private void populateIsyncList() {
		int rec = 0;
		int vox = 0;
		int vox1 = 0;
		int totalVox=0;
		int noterec = 0;
		int notevox = 0;
	
		Uri urlREC = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "records");
		Cursor cREC = mResolver.query(urlREC, null, null, null, null);
		while (cREC.moveToNext()) {
			rec++;
			if(Settings.isDebug())Log.d(TAG, "records :{"+rec+"}");
		}
		cREC.close();
		Uri urlVOX = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "voices_by_untitled");
		Cursor cVOX = mResolver.query(urlVOX, null, null, null, null);
		while (cVOX.moveToNext()) {
			vox++;
			if(Settings.isDebug())Log.d(TAG, "voices_by_untitled :{"+vox+"}");
		}
		cVOX.close();
		Uri urlnoteREC = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "notes");
		Cursor cNREC = mResolver.query(urlnoteREC, null, null, null, null);
		while (cNREC.moveToNext()) {
			noterec++;
			if(Settings.isDebug())Log.d(TAG, "notes :{"+noterec+"}");
		}
		cNREC.close();
		Uri urlnoteVOX = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "vnotes");
		Cursor cNVOX = mResolver.query(urlnoteVOX, null, null, null, null);
		while (cNVOX.moveToNext()) {
			notevox++;
			if(Settings.isDebug())Log.d(TAG, "vnotes :{"+notevox+"}");
		}
		cNVOX.close();
		Uri urlVOX1 = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "voices");
		Cursor cVOX1 = mResolver.query(urlVOX1, null, null, null, null);
		while (cVOX1.moveToNext()) {
			vox1++;
			if(Settings.isDebug())Log.d(TAG, "voices :{"+vox1+"}");
		}
		totalVox = vox + vox1;
		if(Settings.isDebug())Log.d(TAG, "totalVox :{"+totalVox+"}");
		ControllerRPC monController = new ControllerRPC(mContext);
		monController.setNoterecord(noterec);
		monController.setRecord(rec);
		monController.setNotevoice(notevox);
		monController.setVoice(totalVox);
		monController.setJutTcheck(justAChec);
		mOperationsRecord.add(monController);
	}
	/**
	 * @author clement
	 * 
	 */
	public class ControllerRPCList implements XMLRPCSerializable {
		private List<ControllerRPC> LIST;

		public ControllerRPCList(List<ControllerRPC> list) {
			this.LIST = list;

		}

		public Map<String, List<ControllerRPC>> getSerializable() {
			Map<String, List<ControllerRPC>> map = new HashMap<String, List<ControllerRPC>>();
			map.put("PREOPS", LIST);
			return map;
		}
	}
}
