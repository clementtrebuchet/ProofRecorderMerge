package org.proof.recorder.simplexfb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.proof.recorder.ProofRecorderActivity;
import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.utils.StaticNotifications;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class FtpCli extends Service {

	public static final String PATH_SEPARATOR = "/";
	protected static final String COPY_TAG = "FTPCLI STREAM";
	private final static String TAG = "FtpCli";
	public static String hostName;
	public static String username;
	public static String password;
	public static String location;
	public static String remoteLocation;
	public static String fileExtension;
	public static String action;
	public static String methode;
	private FTPClient ftp;
	private final IBinder mBinder = new LocalBinder();
	/*
	 * rc
	 */
	private static String UPOK;
	private static String UPKO;
	private static String DWOK;
	private static String DWKO;
	private static String CONOFF;
	private static String CONON;
	private static String AUTHKO;
	private static String AUTHOK;
	private static String ERR;
	public static Context context;
	/*
	 * end rc
	 */
	public static ArrayList<String> filesList;
	private boolean multiple;
	private static String DIRECTORY_PHONE = "/PHONE";
	private static String DIRECTORY_VOICE = "/VOICE";
	private static String TroisGP = "/3GP/";
	private static String WAV = "/WAV/";

	public static Handler mHandler;
	Bundle informationTransfer;
	static Message msg;
	public static Bundle b = new Bundle();

	@Override
	public void onCreate() {
		Log.v(TAG, "FTPClient");
		FtpCli.context = getApplicationContext();

	}
	public static Context getAppContext() {
        return FtpCli.context;
    }

	public static void setHandler(Handler h) {
		mHandler = h;
	}

	public class LocalBinder extends Binder {
		public FtpCli getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return FtpCli.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		informationTransfer = intent.getExtras();
		hostName = informationTransfer.getString("hostName");
		username = informationTransfer.getString("username");
		password = informationTransfer.getString("password");
		location = informationTransfer.getString("location");
		remoteLocation = informationTransfer.getString("remoteLocation");
		fileExtension = informationTransfer.getString("fileExtension");
		methode = informationTransfer.getString("methode");
		action = informationTransfer.getString("action");
		multiple = informationTransfer.getBoolean("multiple");
		filesList = informationTransfer.getStringArrayList("filesList");
		UPOK = FtpCli.this.getResources().getString(R.string.FTPUPOK);
		UPKO = FtpCli.this.getResources().getString(R.string.FTPUPKO);
		DWOK = FtpCli.this.getResources().getString(R.string.FTPDWOK);
		DWKO = FtpCli.this.getResources().getString(R.string.FTPDWKO);
		CONOFF = FtpCli.this.getResources().getString(R.string.FTPCONOFF);
		CONON = FtpCli.this.getResources().getString(R.string.FTPCONON);
		AUTHKO = FtpCli.this.getResources().getString(R.string.FTPAUTHKO);
		AUTHOK = FtpCli.this.getResources().getString(R.string.FTPAUTHOK);
		ERR = FtpCli.this.getResources().getString(R.string.FTPFATALERROR);

		if (Settings.isDebug()) {
			Log.d(TAG, "" + hostName);
			Log.d(TAG, "" + username);
			Log.d(TAG, "" + password);
			Log.d(TAG, "" + location);
			Log.d(TAG, "" + remoteLocation);
			Log.d(TAG, "" + fileExtension);
			Log.d(TAG, "" + action);
			Log.d(TAG, "" + methode);
			Log.d(TAG, "" + multiple);

			try {
				Log.d(TAG, "filesList length :{" + filesList.size() + "}");
			} catch (java.lang.NullPointerException e) {
				if (Settings.isDebug()) {
					Log.d(TAG, "filesList non present un seul fichier");
				}
			}

		}
		if (!multiple) {
			if (action.equals("UPLOAD")) {
				Log.d(TAG, "action upload");
				transfer();
			} else if (action.equals("DOWNLOAD")) {
				Log.d(TAG, "action download");
				File fileL = new File(location);
				try {
					download(fileL, remoteLocation);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (action.equals("DELETE")) {
				Log.d(TAG, "action download");
				delete(remoteLocation);
			}
		} else if (multiple) {
			if (action.equals("UPLOAD")) {
				Log.d(TAG, "action mulitple UPLOAD");
				try {
					transferMultipleFiles(filesList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (action.equals("DOWNLOAD")) {
				Log.d(TAG, "action multiple DOWNLOAD");
				try {
					downloadMultiple(filesList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals("DELETE")) {
				Log.d(TAG, "action DELETE");

			}
		}
		return START_FLAG_RETRY;

	}

	private void transfer() {

		// InputStream in = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			ftp = new FTPClient();
			ftp.addProtocolCommandListener(new PrintCommandListener(
					new PrintWriter(out), true));
			ftp.connect(hostName);
			ftp.login(username, password);
			ftp.setAutodetectUTF8(true);
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			try {
				if (methode.equals(Settings.methodCALL)) {
					if (fileExtension.equals(".3gp")) {
						ftp.changeWorkingDirectory(DIRECTORY_PHONE + TroisGP);
					} else if (fileExtension.equals(".wav")) {
						ftp.changeWorkingDirectory(DIRECTORY_PHONE + WAV);
					}
				} else if (methode.equals(Settings.methodVOICE)) {
					if (fileExtension.equals(".3gp")) {
						ftp.changeWorkingDirectory(DIRECTORY_VOICE + TroisGP);
					} else if (fileExtension.equals(".wav")) {
						ftp.changeWorkingDirectory(DIRECTORY_VOICE + WAV);

					}
				}
			} catch (Exception e) {
				Log.v(TAG, "Erreur d'upload : " + e);
				return;

			}

			int reply = ftp.getReplyCode();

			Log.v(TAG, "Received Reply from FTP Connection:" + reply);
			if (FTPReply.isPositivePreliminary(reply)) {
				Log.v(TAG, "isPositivePreliminary" + reply);
			}

			if (FTPReply.isPositiveCompletion(reply)) {
				Log.v(TAG, "Connected Success" + reply);
			}

			File f1 = new File(location);
			// in = new FileInputStream(f1);
			// ftp.setCopyStreamListener(createListener());
			store(ftp, f1, remoteLocation + fileExtension, false);

			if (FTPReply.isPositiveCompletion(reply)) {
				Log.v(TAG, " UPLOAD SUCCESS" + reply);
			} else {
				Log.v(TAG, " UPLOAD FAILLED" + reply);
			}

			ftp.logout();
			ftp.disconnect();
			StaticNotifications.show(this.getApplicationContext(),
					ProofRecorderActivity.class, b,
					getResources().getString(R.string.NOTIFYTITLE),
					getResources().getString(R.string.FTPUPOK),
					fileExtension,
					StaticNotifications.ICONS.DEFAULT, false, false, 0);
			Log.e(TAG, out.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void transferMultipleFiles(ArrayList<String> localFile) {

		// InputStream in = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ftp = new FTPClient();
			ftp.addProtocolCommandListener(new PrintCommandListener(
					new PrintWriter(out), true));
			ftp.connect(hostName);
			ftp.login(username, password);
			//sendMessage(AUTHOK, true);
			ftp.setAutodetectUTF8(true);
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);

			for (String localeFileName : localFile) {
				boolean Tgp = false;
				boolean Wav = false;
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
				String[] dir = localeFileName.split("\\/");
				// String filename = dir[6];
				try {
					for (String di : dir) {
						if (di.equals("calls")) {
							if (Tgp) {
								ftp.changeWorkingDirectory(DIRECTORY_PHONE
										+ TroisGP);
							} else if (Wav) {
								ftp.changeWorkingDirectory(DIRECTORY_PHONE
										+ WAV);
							}
						} else if (di.equals("voices")) {
							if (Tgp) {
								ftp.changeWorkingDirectory(DIRECTORY_VOICE
										+ TroisGP);
							} else if (Wav) {
								ftp.changeWorkingDirectory(DIRECTORY_VOICE
										+ WAV);

							}
						}
					}
				} catch (Exception e) {
					Log.v(TAG, "Erreur d'upload : " + e);
					////sendMessage(ERR + "{Trace:" + e.toString() + "}", false);
					return;

				}

				int reply = ftp.getReplyCode();

				Log.v(TAG, "Received Reply from FTP Connection:" + reply);
				if (FTPReply.isPositivePreliminary(reply)) {
					Log.v(TAG, "isPositivePreliminary for change directory : "
							+ reply);
				}

				if (FTPReply.isPositiveCompletion(reply)) {
					Log.v(TAG, "Connected Success" + reply);

				}
				if (FTPReply.isNegativePermanent(reply)) {
					Log.v(TAG, "Connected failled" + reply);
				}

				File f1 = new File(localeFileName);
				// in = new FileInputStream(f1);
				// ftp.setCopyStreamListener(createListener());
				String remoteLocatio = RapatFic
						.getRemoteFileName(localeFileName);
				store(ftp, f1, remoteLocatio, false);

				if (FTPReply.isPositiveCompletion(reply)) {
					Log.v(TAG, " UPLOAD SUCCESS" + reply);
					//sendMessage(UPOK, true);

				} else if (FTPReply.isNegativePermanent(reply)) {
					Log.v(TAG, "UPLOAD FAIL" + reply);
					//sendMessage(UPKO, false);
				}
			}
			StaticNotifications.show(this.getApplicationContext(),
					ProofRecorderActivity.class, b,
					getResources().getString(R.string.NOTIFYTITLE),
					getResources().getString(R.string.NOTIFYINFO),
					UPOK,
					StaticNotifications.ICONS.DEFAULT, false, false, 0);
			ftp.logout();
			ftp.disconnect();
			//sendMessage(CONOFF, true);
			Log.e(TAG, out.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static CopyStreamListener createListener() {
		return new CopyStreamListener() {
			private long megsTotal = 0;

			public void bytesTransferred(CopyStreamEvent event) {
				bytesTransferred(event.getTotalBytesTransferred(),
						event.getBytesTransferred(), event.getStreamSize());
			}

			public void bytesTransferred(long totalBytesTransferred,
					int bytesTransferred, long streamSize) {
				long megs = totalBytesTransferred / 1000000;
				for (long l = megsTotal; l < megs; l++) {
					System.err.print("#");
				}
				Log.v(COPY_TAG, "TOTAL TRANSFERERRED : "
						+ totalBytesTransferred + " BYTES TRANSFERERERED : "
						+ bytesTransferred);
				megsTotal = megs;
			}
		};
	}

	protected static void store(FTPClient ftp, File localFile,
			String remotePath, boolean createDir) throws FileNotFoundException,
			IOException, Exception {
		if (localFile.isFile()) {
			ftp.setCopyStreamListener(createListener());
			boolean stored = ftp.storeFile(remotePath, new FileInputStream(
					localFile));
			if (!stored) {
				throw new Exception("Cannot store " + localFile.getName()
						+ " to " + remotePath);
			}
		} else if (localFile.isDirectory()) {
			String newDirectory = remotePath;
			if (createDir) {
				newDirectory = remotePath;
				ftp.makeDirectory(newDirectory);
			}
			String[] files = localFile.list();
			ftp.setCopyStreamListener(createListener());
			for (int i = 0; i < files.length; i++) {
				store(ftp, new File(localFile, files[i]), newDirectory
						+ FtpCli.PATH_SEPARATOR + files[i], true);
			}
		}
	}

	protected static long getFileSize(FTPClient ftp, String remoteFile) {
		long size = 0;

		try {
			FTPFile[] files = ftp.listFiles(remoteFile);
			if (files.length == 1) {
				FTPFile ftpFile = files[0];
				if (ftpFile != null) {
					size = ftpFile.getSize();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return size;
	}

	public static void download(File localFile, String remotePath)
			throws Exception {
		Log.e(TAG,
				"public static void download(File localFile, String remotePath)");
		if (!localFile.exists()) {
			Log.e(TAG, "localFile : " + localFile + "  exist ? {boolean:"
					+ localFile.exists() + "}");
			FTPClient ftpS = new FTPClient();

			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				ftpS.addProtocolCommandListener(new PrintCommandListener(
						new PrintWriter(out), true));
				ftpS.connect(hostName);
				boolean loggedIn = ftpS.login(username, password);
				Log.d(TAG, "Connected to " + hostName);
				Log.d(TAG, ftpS.getReplyString());

				if (loggedIn) {
					Log.d(TAG, "downloading file: " + remotePath);
					ftpS.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
					ftpS.setFileType(FTPClient.BINARY_FILE_TYPE);
					ftpS.enterLocalPassiveMode();
					// final long fileSize = getFileSize(ftpS, remotePath);
					FileOutputStream dfile = new FileOutputStream(localFile);
					ftpS.retrieveFile(remotePath, dfile);
					dfile.flush();
					dfile.close();
					ftpS.logout();
				} else {
					throw new Exception("Invalid login");
				}
				StaticNotifications.show(getAppContext(),
						ProofRecorderActivity.class, b,
						getAppContext().getResources().getString(R.string.NOTIFYTITLE),
						getAppContext().getResources().getString(R.string.FTPUPOK),
						fileExtension,
						StaticNotifications.ICONS.DEFAULT, false, false, 0);
				ftpS.disconnect();
				
			} catch (SocketException e) {
				Log.e(TAG,
						"File download failed with message: " + e.getMessage());
				throw new Exception("File download failed with message: "
						+ e.getMessage());
			} catch (IOException e) {
				Log.e(TAG,
						"File download failed with message: " + e.getMessage());
				throw new Exception("File download failed with message: "
						+ e.getMessage());
			} finally {
				if (ftpS.isConnected()) {
					try {
						ftpS.disconnect();
					} catch (IOException ioe) {
						throw new Exception(
								"File download failed with message: "
										+ ioe.getMessage());
					}
				}
			}
		} else {
			Log.e(TAG, "localFile : " + localFile + "  exist ? {boolean:"
					+ localFile.exists() + "}");
		}
	}

	/*
	 * Start dw multiple
	 */

	private static void sendMessage(String Message, boolean mes) {
		msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putBoolean("downloadFIN", mes);
		b.putString("HUMANIZE", Message);
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	public static void downloadMultiple(ArrayList<String> localFile)
			throws Exception {

		Log.e(TAG,
				"public static void download(File localFile, String remotePath)");

		FTPClient ftpS = new FTPClient();

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			ftpS.addProtocolCommandListener(new PrintCommandListener(
					new PrintWriter(out), true));
			ftpS.connect(hostName);
			boolean loggedIn = ftpS.login(username, password);
			Log.d(TAG, "Connected to " + hostName);
			Log.d(TAG, ftpS.getReplyString());
			int reply = ftpS.getReplyCode();
			if (loggedIn) {
				//sendMessage(AUTHOK, true);
				ftpS.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
				ftpS.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftpS.enterLocalPassiveMode();

				for (String fichier : localFile) {
					File FichierLocal = new File(fichier);
					String remoteP = RapatFic.getRemoteFileName(fichier);
					FileOutputStream dfile = new FileOutputStream(FichierLocal);
					Log.e(TAG, "FichierLocal : " + FichierLocal
							+ "  exist ? {boolean:" + FichierLocal.exists()
							+ "}");
					ftpS.retrieveFile(remoteP, dfile);
					if (FTPReply.isPositiveCompletion(reply)) {
						//sendMessage(DWOK, true);
					} else if (FTPReply.isNegativePermanent(reply)) {
						//sendMessage(DWKO, false);
					}
					dfile.flush();
					dfile.close();
					Log.d(TAG, "downloading file: " + remoteP);
				}
				
				ftpS.logout();
				//sendMessage(CONOFF, true);
			} else {
				//sendMessage(ERR, true);
				throw new Exception("Invalid login");

			}

			ftpS.disconnect();
			StaticNotifications.show(getAppContext(),
					ProofRecorderActivity.class, b,
					getAppContext().getResources().getString(R.string.NOTIFYTITLE),
					getAppContext().getResources().getString(R.string.FTPUPOK),
					fileExtension,
					StaticNotifications.ICONS.DEFAULT, false, false, 0);
		} catch (SocketException e) {
			Log.e(TAG, "File download failed with message: " + e.getMessage());
			//sendMessage(DWKO, false);
			throw new Exception("File download failed with message: "
					+ e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "File download failed with message: " + e.getMessage());
			//sendMessage(DWKO, false);
			throw new Exception("File download failed with message: "
					+ e.getMessage());
		} finally {
			if (ftpS.isConnected()) {
				try {
					ftpS.disconnect();
				} catch (IOException ioe) {
					//sendMessage(DWKO, false);
					throw new Exception("File download failed with message: "
							+ ioe.getMessage());
				}
			}
		}

	}

	/*
	 * ens dw multiple
	 */
	public static void delete(String remoteFile) {
		FTPClient ftpS1 = new FTPClient();
		String remotePath = PATH_SEPARATOR + remoteFile;

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			ftpS1.addProtocolCommandListener(new PrintCommandListener(
					new PrintWriter(out), true));
			ftpS1.connect(hostName);
			boolean loggedIn = ftpS1.login(username, password);
			Log.d(TAG, "Connected to " + hostName);
			Log.d(TAG, ftpS1.getReplyString());
			if (loggedIn) {
				ftpS1.deleteFile(remotePath);

				ftpS1.logout();
			}
			ftpS1.disconnect();

		} catch (SocketException e) {
			Log.e(TAG, "File chmod failed with message: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "File chmod failed with message: " + e.getMessage());
		} finally {
			if (ftpS1.isConnected()) {
				try {
					ftpS1.disconnect();
				} catch (IOException ioe) {
					// do nothing
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		ftp = null;
		if (Settings.isDebug())
			Log.e(TAG, "Service FTP Destroyed.. ");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

}