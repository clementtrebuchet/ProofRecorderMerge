package org.proof.recorder.simplexfb;

import java.io.File;
import java.util.ArrayList;

import org.proof.recorder.Settings;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.syncron.fragment.SyncronUi;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class RapatFic {

	static Bundle b = new Bundle();
	private static final String TAG = "RapatFic";
	private static ArrayList<String> filesListVoices;
	private static ArrayList<String> filesListCalls;
	private static Context mContext;
	private final static String CALL = "/PHONE";
	private final static String VOX = "/VOICE";
	private final static String TGP = "/3GP/";
	private final static String WAV = "/WAV/";
	private static String[] projREC = { ProofDataBase.COLUMNRECODINGAPP_ID,
			ProofDataBase.COLUMN_FILE };
	private static String[] projVOX = { ProofDataBase.COLUMNVOICE_ID,
			ProofDataBase.COLUMN_VOICE_FILE };

	private static boolean isUPLOAD;

	public static void setIsUPLOAD(boolean isUP) {

		isUPLOAD = isUP;
	}

	public static boolean getIsUPLOAD() {
		return isUPLOAD;

	}

	public RapatFic(Context cont) {

		mContext = cont;
		listDbFile();
		Handler h = new Handler();
		Runnable t = new Runnable() {
			public void run() {

				
				try {
					downloadFileCalls();
					Thread.sleep(5000);
					downloadFileVoices();

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		h.postDelayed(t, 20000);

	}

	public static boolean operationDWL() {

		return false;

	}

	private static void listDbFile() {
		filesListVoices = new ArrayList<String>();
		filesListCalls = new ArrayList<String>();
		/*
		 * files calls from database
		 */
		ContentResolver CREC = mContext.getContentResolver();
		Uri UREC = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "records");

		Cursor CurREC = CREC.query(UREC, projREC, null, null, null);
		while (CurREC.moveToNext()) {

			String file = CurREC.getString(CurREC
					.getColumnIndex(ProofDataBase.COLUMN_FILE));
			if (Settings.isDebug()) {
				// Log.e(TAG, "listDbFile()->filesListCalls: " + file);
			}
			File testFile = new File(file);

			if (!testFile.exists()) {
				if (Settings.isDebug()) {
					Log.e(TAG, "This file IS NOT in the phone : " + file);
				}
				filesListCalls.add(file);
			} else {
				if (Settings.isDebug()) {
					// Log.e(TAG, "This file IS in the phone : " + file);
				}
			}

		}
		CurREC.close();
		/*
		 * files vox from database (Titled)
		 */
		ContentResolver CVOX = mContext.getContentResolver();
		Uri UVOX = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "voices");
		Cursor CurVOX = CVOX.query(UVOX, projVOX, null, null, null);
		while (CurVOX.moveToNext()) {
			String file = CurVOX.getString(CurVOX
					.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE));
			if (Settings.isDebug()) {
				// Log.e(TAG, "listDbFile()->filesListVoices: " + file);
			}
			File testFile = new File(file);

			if (!testFile.exists()) {
				if (Settings.isDebug()) {
					Log.e(TAG, "This file IS NOT in the phone : " + file);
				}
				filesListVoices.add(file);
			} else {
				if (Settings.isDebug()) {
					// Log.e(TAG, "This file IS in the phone : " + file);
				}
			}

		}
		CurVOX.close();
		/*
		 * files vox from database (Untitled)
		 */
		ContentResolver CVOX1 = mContext.getContentResolver();
		Uri UVOX1 = Uri
				.withAppendedPath(PersonnalProofContentProvider.CONTENT_URI,
						"voices_by_untitled");
		Cursor CurVOX1 = CVOX1.query(UVOX1, projVOX, null, null, null);
		while (CurVOX1.moveToNext()) {
			String file = CurVOX1.getString(CurVOX1
					.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE));
			if (Settings.isDebug()) {
				// Log.e(TAG, "listDbFile()->filesListVoices: " + file);
			}
			File testFile = new File(file);

			if (!testFile.exists()) {
				if (Settings.isDebug()) {
					Log.e(TAG, "This file IS NOT in the phone : " + file);
				}
				filesListVoices.add(file);
			} else {
				if (Settings.isDebug()) {
					// Log.e(TAG, "This file IS in the phone : " + file);
				}
			}

		}
		CurVOX1.close();

	}

	private static void downloadFileCalls() {
		if (!filesListCalls.isEmpty()) {
			new Thread(new Runnable() {
				public void run() {
					try {
						FtpCli.setHandler(SyncronUi.handlerFTP);
						b.putString("hostName", Settings.hostname);
						b.putString("username", Settings.getUsername(mContext));
						b.putString("password", Settings.getPassword(mContext));
						b.putStringArrayList("filesList", filesListCalls);
						b.putBoolean("multiple", true);
						if (isUPLOAD) {
							b.putString("action", "UPLOAD");

						} else {
							b.putString("action", "DOWNLOAD");
						}

						Intent intent = new Intent(mContext, FtpCli.class);
						intent.putExtras(b);
						mContext.startService(intent);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			if (Settings.isDebug())
				Log.e(TAG, "filesListCalls.size() = " + filesListCalls.size()
						+ " ABORT OPERATION FTP");
		}

	}

	private void downloadFileVoices() {
		if (!filesListVoices.isEmpty()) {
			new Thread(new Runnable() {
				public void run() {
					try {
						FtpCli.setHandler(SyncronUi.handlerFTP);
						b.putString("hostName", Settings.hostname);
						b.putString("username", Settings.getUsername(mContext));
						b.putString("password", Settings.getPassword(mContext));
						b.putStringArrayList("filesList", filesListVoices);

						if (isUPLOAD) {
							b.putString("action", "UPLOAD");

						} else {
							b.putString("action", "DOWNLOAD");
						}

						b.putBoolean("multiple", true);
						Intent intent = new Intent(mContext, FtpCli.class);
						intent.putExtras(b);
						mContext.startService(intent);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			if (Settings.isDebug())
				Log.e(TAG, "filesListVoices.size() = " + filesListVoices.size()
						+ " ABORT OPERATION FTP");
		}

	}

	public static String getRemoteFileName(String localeFileName) {
		boolean Tgp = false;
		boolean Wav = false;
		boolean calls = false;
		boolean voices = false;
		String path = null;
		/*
		 * format
		 */
		String[] format = localeFileName.split("\\.");
		if (Settings.isDebug()) {
			Log.e(TAG, "LocalFile{" + localeFileName
					+ "} Length LocalSplit on . : {" + format.length
					+ "} FirstParam{" + format[0] + "}");
		}
		if (format[1].equals("3gp")) {
			Tgp = true;
		} else if (format[1].equals("wav")) {
			Wav = true;
		}
		/*
		 * dossier
		 */

		String[] dir = localeFileName.split("\\/");
		if (Settings.isDebug()) {
			Log.e(TAG, "LocalFile{" + localeFileName
					+ "} Length LocalSplit on / : {" + dir.length + "} "
					+ "FirstParam{" + dir[0] + "} " + "SecondParam{" + dir[1]
					+ "} " + "ThirdParam{" + dir[2] + "} " + "ForthParam{"
					+ dir[3] + "} " + "FifthParam{" + dir[4] + "} "
					+ "SixthParam{" + dir[5] + "} " + "SeventhParam{" + dir[6]
					+ "}");
		}
		String filename = dir[6];
		for (String di : dir) {
			if (di.equals("calls")) {
				calls = true;
			} else if (di.equals("voices")) {
				voices = true;
			}
		}
		if (calls == true) {
			if (Tgp == true) {
				path = CALL + TGP + filename;
			} else if (Wav == true) {
				path = CALL + WAV + filename;
			}

		} else if (voices == true) {
			if (Tgp == true) {
				path = VOX + TGP + filename;
			} else if (Wav == true) {
				path = VOX + WAV + filename;
			}
		}
		if (Settings.isDebug()) {
			Log.e(TAG, "Format{TGP:" + Tgp + ", WAV:" + Wav + "} Dir{CALL:"
					+ calls + ", VOX:" + voices + "}");
			Log.e(TAG, "LocalFile{" + localeFileName + "} Remote{" + path + "}");
		}
		assert path != null;
		Log.e(TAG, "RemoteFile{" + path + "}");
		return path;

	}

}
