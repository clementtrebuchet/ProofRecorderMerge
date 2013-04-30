package org.proof.recorder.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.fragment.phone.FragmentListRecordTabs;
import org.proof.recorder.fragment.voice.FragmentListVoiceTabs;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.service.MpthreeRec;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class AudioHandler {

	/**
	 * Hanlde Audio Multiple Formats
	 */

	private final static String LOG_TAG = "UTILS_AUDIO_HANDLER";
	private static String DEFAULT_FILE_DIR;
	private int NODOUBLONS;
	private MediaRecorder audio;

	public MediaRecorder getAudio() {
		return audio;
	}

	public void setAudio(MediaRecorder audio) {
		this.audio = audio;
	}

	private AudioRecord audioWav;
	private static Context mContext;
	private static int mAudioSource;
	Intent mIntent;
	private String DEFAULT_FILE;

	/**
	 * Handle WAV format recording
	 * 
	 * @return
	 */

	private int minBufferSize = 0;
	/*
	 * MP3
	 */
	static long mp3length;
	static int mp3Compression;
	private Thread recordingThread = null;
	private boolean isRecording = false;
	private static int lastId;

	private long totalAudioLen = 0;
	private long totalDataLen = totalAudioLen + 36;

	private String mNumber, mSense;

	public void setmNumber(String mNumber) {
		this.mNumber = mNumber;
	}

	public void setmSense(String mSense) {
		this.mSense = mSense;
	}

	/**
	 * 
	 */
	private void inititialzeWavRecording(boolean MP3) {
		int channel;
		int quality;
		if (MP3) {
			channel = AudioFormat.CHANNEL_IN_MONO;
			quality = Settings.getMP3Hertz(getmContext());
		} else {
			channel = Settings.RECORDER_CHANNELS;
			quality = Settings.RECORDER_SAMPLERATE;
		}
		minBufferSize = AudioRecord.getMinBufferSize(quality, channel,
				Settings.RECORDER_AUDIO_ENCODING);
	}

	private static Settings.mFormat format;
	private static Settings.mType type;

	private void setDefaultFilePath(String mExtension) {
		DEFAULT_FILE = DEFAULT_FILE_DIR + getCurrentMsDate() + mExtension;
	}

	/**
	 * 
	 * @return
	 */
	private static Context getmContext() {
		return mContext;
	}

	/**
	 * 
	 * @param mContext
	 */
	public static void setmContext(Context mContext) {
		AudioHandler.mContext = mContext;
		mp3Compression = Settings.getMp3Compression(mContext);
	}

	/**
	 * 
	 */
	public AudioHandler(Settings.mFormat format, Settings.mType type) {
		AudioHandler.format = format;
		AudioHandler.type = type;

		initialize();
	}

	/**
	 * 
	 * @param mContext
	 * @param type
	 */
	public AudioHandler(Context mContext, Settings.mFormat format,
			Settings.mType type) {
		AudioHandler.format = format;
		AudioHandler.type = type;
		setmContext(mContext);
		initialize();
	}

	/**
	 * 
	 */
	private void initialize() {

		mAudioSource = new ServiceAudioHelper(getmContext()).maConfAudio();
		String externalStorage = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		String appBaseStorage = "";

		switch (type) {

		case CALL:
			appBaseStorage = externalStorage + "/proofRecorder/calls/";
			break;

		case VOICE_TITLED:
		case VOICE_UNTITLED:
			appBaseStorage = externalStorage + "/proofRecorder/voices/";
			break;

		default:
			break;
		}

		switch (format) {
		case THREE_GP:
			DEFAULT_FILE_DIR = appBaseStorage + "3gp/";
			audio = new MediaRecorder();
			break;

		case WAV:
			DEFAULT_FILE_DIR = appBaseStorage + "wav/";
			inititialzeWavRecording(false);
			audioWav = new AudioRecord(mAudioSource,
					Settings.RECORDER_SAMPLERATE, Settings.RECORDER_CHANNELS,
					Settings.RECORDER_AUDIO_ENCODING, minBufferSize);
			break;
		case MP3:
			DEFAULT_FILE_DIR = appBaseStorage + "wav/";

			break;
		case OGG:
			DEFAULT_FILE_DIR = appBaseStorage + "wav/";
			break;
		default:
			break;
		}
	}

	public void startRecording() {
		switch (format) {
		case THREE_GP:
			setDefaultFilePath(".3gp");
			startThreeGpRecording();
			break;

		case WAV:
			setDefaultFilePath(".wav");
			startWavRecording(false);
			break;

		case MP3:
			setDefaultFilePath(".mp3");
			mIntent = new Intent(
					"org.proofs.recorder.codec.mp3.utils.ServiceIntentRecorderMP3");
			Bundle B = new Bundle();
			B.putString("FileName", DEFAULT_FILE);
			B.putInt("mSampleRate", Settings.getMP3Hertz(getmContext()));
			B.putInt("mp3Channel", 1);
			B.putInt("audioSource", mAudioSource);
			B.putInt("outBitrate", mp3Compression);
			mIntent.putExtras(B);
			getmContext().startService(mIntent);
			break;
		case OGG:
			setDefaultFilePath(".ogg");
			mIntent = new Intent(
					"org.proofs.recorder.codec.ogg.utils.ServiceIntentRecorderOgg");
			Bundle B1 = new Bundle();
			B1.putString("file", DEFAULT_FILE);
			B1.putInt("sampleRate", Settings.getMP3Hertz(getmContext()));
			B1.putInt("channel", 1);
			B1.putInt("audioSource", mAudioSource);
			B1.putFloat("quality", Settings.getOGGQual(getmContext()));
			mIntent.setAction("org.proofs.recorder.codec.ogg.utils.ServiceIntentRecorderOgg");
			mIntent.putExtras(B1);
			getmContext().startService(mIntent);
			break;

		default:
			setDefaultFilePath(".wav");
			startWavRecording(false);
			break;
		}
	}

	public void stopRecording() {
		switch (format) {
		case THREE_GP:
			stopThreeGpRecording();
			break;

		case WAV:
			stopWavRecording(false);
			break;
		case MP3:
			getmContext().stopService(mIntent);
			// stopWavRecording(true);
			break;
		case OGG:
			getmContext().stopService(mIntent);
			// stopWavRecording(true);
			break;
		default:
			break;
		}

		try {
			writeToDb();
			notifyOnEndingCall();
		} catch (IllegalArgumentException e) {
			Log.e(LOG_TAG, "writeToDb()->IllegalArgumentException()->e() \n"
					+ e);
		}

	}

	/**
	 * Utils Method : get the current time in milliseconds
	 */

	private long getCurrentMsDate() {
		return System.currentTimeMillis();
	}

	/**
	 * Handle the 3GP Format Recording :param: mAudioSource :type: int
	 * :possibilities: MediaRecorder.AudioSource { DEFAULT || MIC || (sure for
	 * almost all devices) VOICE_UPLINK || VOICE_DOWNLINK || VOICE_CALL ||
	 * CAMCORDER || VOICE_RECOGNITION || VOICE_COMMUNICATION || }
	 */

	private void startThreeGpRecording() {

		audio.reset();

		try {

			audio.setAudioSource(1);
			audio.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			audio.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		} catch (IllegalStateException e) {

			if (Settings.isDebug())
				Log.e(LOG_TAG, "IllegalStateException->setAudioSource("
						+ mAudioSource + ") not registered \n" + e);
		}

		try {
			audio.setOutputFile(DEFAULT_FILE);
		} catch (IllegalStateException e) {

			if (Settings.isDebug())
				Log.e(LOG_TAG, "IllegalStateException->setOutputFile("
						+ DEFAULT_FILE + ") failed \n" + e);
		}

		try {
			audio.prepare();
		} catch (IOException e) {

			if (Settings.isDebug())
				Log.e(LOG_TAG, "IOException->prepare() failed");
		} catch (IllegalStateException e) {

			if (Settings.isDebug())
				Log.e(LOG_TAG, "IllegalStateException->prepare() failed\n" + e);
		}

		try {
			audio.start();
		} catch (IllegalStateException e) {

			if (Settings.isDebug())
				Log.e(LOG_TAG,
						"IllegalStateException->start() Attempt 1 failed \n"
								+ e);
		}

	}

	/**
	 * Stop the audio recording state
	 * 
	 * @param reset
	 * @param release
	 */
	private void stopThreeGpRecording() {
		if (null != audio) {
			try {
				audio.reset();
				audio.stop();
				releaseThreeGpRecording();

			} catch (IllegalStateException e) {
				if (Settings.isDebug())
					Log.e(LOG_TAG, "IllegalStateException->stop() failed \n"
							+ e);
			}

		}
		/*
		 * ftpThread = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { UploadFile(); } }, "FTP Thread");
		 * ftpThread.start();
		 */
	}

	/**
	 * Release the Audio Ressource
	 */
	public void releaseThreeGpRecording() {
		audio.release();
		audio = null;
	}

	/**
	 * Handle WAV Format
	 */

	/**
	 * 
	 */
	private void startWavRecording(boolean MP3) {
		final boolean MPP = MP3;
		try {

			if (MPP) {
				if (NODOUBLONS <= 0) {
					MpthreeRec.Mpthree(Settings.getMP3Hertz(getmContext()));
					MpthreeRec
							.lameInit(mp3Compression, Settings.defaultQuality);
					NODOUBLONS++;
				}

			}
			audioWav.startRecording();
		} catch (Exception e) {

			if (Settings.isDebug())
				Log.e(LOG_TAG, "" + e);
		}

		isRecording = true;
		recordingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (MPP)
					android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				writeAudioDataToFile(MPP);

			}
		}, "AudioRecorder Thread");
		recordingThread.start();
	}

	/**
	 * 
	 */
	private void writeAudioDataToFile(boolean MP3) {
		if (!MP3) {
			byte data[] = new byte[minBufferSize];
			String filename = getTempFilename();
			FileOutputStream os = null;

			try {
				os = new FileOutputStream(filename);
			} catch (FileNotFoundException e) {

				if (Settings.isDebug())
					Log.e(LOG_TAG, e.getMessage());
			}

			int read = 0;

			if (null != os) {
				while (isRecording) {
					read = audioWav.read(data, 0, minBufferSize);

					if (AudioRecord.ERROR_INVALID_OPERATION != read) {
						try {
							os.write(data);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (MP3) {
			/**
			 * MP3 write
			 */
			/*
			 * 
			 * short[] buffer = MpthreeRec.getBuffer(); byte[] mp3buffer =
			 * MpthreeRec.getMp3Buffer(buffer); String filename1 = DEFAULT_FILE;
			 * FileOutputStream os1 = null;
			 * 
			 * try { os1 = new FileOutputStream(filename1); } catch
			 * (FileNotFoundException e) {
			 * 
			 * if (Settings.isDebug()) Log.e(LOG_TAG, e.getMessage()); }
			 * 
			 * int read1 = 0;
			 * 
			 * if (null != os1) { while (isRecording) { read1 =
			 * audioWav.read(buffer, 0, minBufferSize);
			 * 
			 * if (AudioRecord.ERROR_INVALID_OPERATION != read1 &&
			 * AudioRecord.ERROR_BAD_VALUE != read1 ) {
			 * 
			 * int encResult = MpthreeRec.lameEncodeResult(buffer,read1,
			 * mp3buffer); if(Settings.isDebug()) Log.e(LOG_TAG,
			 * "Taille de read1 :"+read1+" Taille de encResult :"+encResult); if
			 * (encResult < 0) { if(Settings.isDebug())Log.e(LOG_TAG,
			 * "Error encResult < 0"+encResult);
			 * 
			 * }
			 * 
			 * try { os1.write(mp3buffer, 0, encResult); } catch (IOException e)
			 * { if(Settings.isDebug())Log.e(LOG_TAG, "Error " + e.toString());
			 * break; }
			 * 
			 * 
			 * 
			 * } else { if(Settings.isDebug())Log.e(LOG_TAG,
			 * "ERROR AudioRecord{"+read1+"}"); } } int flushResult =
			 * MpthreeRec.flush(mp3buffer);
			 * 
			 * if (flushResult < 0) { if(Settings.isDebug())Log.e(LOG_TAG,
			 * "flushResult " +flushResult); } if (flushResult != 0) { try {
			 * os1.write(mp3buffer, 0, flushResult); } catch (IOException e) {
			 * if(Settings.isDebug())Log.e(LOG_TAG, "Error " + e.toString()); }
			 * } try { os1.close(); mp3length = new File(filename1).length();
			 * return; } catch (IOException e) {
			 * if(Settings.isDebug())Log.e(LOG_TAG, "Error " + e.toString());
			 * 
			 * } }
			 *//**
			 * 
			 */
		}
	}

	/**
	 * 
	 */
	private void stopWavRecording(boolean MP3) {
		if (null != audioWav) {
			isRecording = false;

			audioWav.stop();
			audioWav.release();

			if (MP3)
				MpthreeRec.close();

			audioWav = null;
			recordingThread = null;
		}

		if (!MP3)
			copyWaveFile(DEFAULT_FILE_DIR + Settings.AUDIO_RECORDER_TEMP_FILE);
		deleteTempFile();
	}

	/**
	 * 
	 * @return
	 */
	private String getTempFilename() {
		File tempFile = new File(DEFAULT_FILE_DIR
				+ Settings.AUDIO_RECORDER_TEMP_FILE);

		if (tempFile.exists())
			tempFile.delete();

		return tempFile.getAbsolutePath();
	}

	/**
	 * 
	 */
	private void deleteTempFile() {
		try {
			OsHandler.deleteFileFromDisk(DEFAULT_FILE_DIR
					+ Settings.AUDIO_RECORDER_TEMP_FILE);
		} catch (IOException e) {
			if (Settings.isDebug())
				Log.v(LOG_TAG, "->deleteTempFile()" + e.getMessage());
		}
	}

	/**
	 * 
	 * @param inFilename
	 * @param outFilename
	 */
	private void copyWaveFile(String inFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long longSampleRate = Settings.RECORDER_SAMPLERATE;
		int channels = 2;
		long byteRate = Settings.RECORDER_BPP * Settings.RECORDER_SAMPLERATE
				* channels / 8;

		byte[] data = new byte[minBufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(DEFAULT_FILE);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;
			/*
			 * @@End
			 */

			WriteWaveFileHeader(out, longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
			/*
			 * if(MP3){ Log.d(LOG_TAG, "MP3 was set to :"+MP3); Intent
			 * MP3Convertion = new Intent(getmContext(), Converter.class);
			 * Bundle b = new Bundle(); b.putString("convertFile",
			 * DEFAULT_FILE); b.putInt("sampleRate", 44100); b.putInt("bitRate",
			 * 64);//TODO array compress quality MP3Convertion.putExtras(b);
			 * getmContext().startService(MP3Convertion); }
			 */

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param out
	 * @param totalAudioLen
	 * @param totalDataLen
	 * @param longSampleRate
	 * @param channels
	 * @param byteRate
	 * @throws IOException
	 */
	private void WriteWaveFileHeader(FileOutputStream out, long longSampleRate,
			int channels, long byteRate) throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = Settings.RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

	/**
	 * 
	 */
	private void notifyOnEndingCall() {

		String title, info, text;
		Bundle extraNotification = new Bundle();
		Class<?> destination;

		switch (type) {
		case CALL:
			Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
					getmContext(), mNumber);
			String mFrom;
			if (!mContact.getContractId().equalsIgnoreCase("null")) {
				extraNotification.putString("mWhereClause", "android_id");
				extraNotification.putString("mIdOrTelephone",
						mContact.getContractId());
				mFrom = mContact.getContactName();
			} else {
				extraNotification.putString("mWhereClause", "phone");
				extraNotification.putString("mIdOrTelephone", mNumber);
				mFrom = mNumber;
			}

			destination = FragmentListRecordTabs.class;
			title = "Proof Recorder";
			info = "CALL INFO";
			text = getmContext().getString(R.string.notifyEndOfCallOne) + " "
					+ mFrom + " "
					+ getmContext().getString(R.string.notifyEndOfCallTwo);
			break;

		case VOICE_TITLED:
		case VOICE_UNTITLED:
			destination = FragmentListVoiceTabs.class;
			title = "Proof Recorder";
			info = "VOICE INFO";
			text = getmContext().getString(R.string.notifyEndOfVoice);
			break;

		default:
			throw new IllegalArgumentException(
					"notifyOnEndingCall()->LE TYPE SPECIFIé EST INCONNU!");
		}

		StaticNotifications.show(getmContext(), destination, extraNotification,
				title, info, text, StaticNotifications.ICONS.DEFAULT, true,
				true, 0);
	}

	private String getFileName() {
		String fileName = DEFAULT_FILE.substring(
				DEFAULT_FILE.lastIndexOf('/') + 1, DEFAULT_FILE.length());
		String fileNameWithoutExtn = fileName.substring(0,
				fileName.lastIndexOf('.'));
		return fileNameWithoutExtn;
	}

	private void writeVoiceToDb() {

		ContentValues values = new ContentValues();
		final Calendar cal = Calendar.getInstance();

		Long humanreadable;
		String mFileName = DEFAULT_FILE.toString();
		String mSize;

		switch (format) {
		case THREE_GP:
			Uri threegp = Uri.parse("file://" + DEFAULT_FILE);
			File f = new File(threegp.getPath());
			long size = f.length();
			mSize = Long.valueOf(size).toString();
			break;
		case WAV:
			mSize = Long.valueOf(totalDataLen).toString();
			break;
		case MP3:
			mSize = Long.valueOf(new File(DEFAULT_FILE).length()).toString();
			break;
		case OGG:
			mSize = Long.valueOf(new File(DEFAULT_FILE).length()).toString();
			break;

		default:
			throw new IllegalArgumentException(
					"writeVoiceToDb()->LE FORMAT SPECIFIé EST INCONNU!");

		}

		values.put(ProofDataBase.COLUMN_VOICE_FILE, mFileName);
		values.put(ProofDataBase.COLUMN_VOICE_TIMESTAMP, getFileName());
		values.put(ProofDataBase.COLUMN_VOICE_TAILLE, mSize);
		/*
		 * human readable date
		 */
		humanreadable = Long.valueOf(getFileName());
		cal.setTimeInMillis(humanreadable);
		
		@SuppressWarnings("deprecation")
		final String date = cal.getTime().toLocaleString();
		values.put(ProofDataBase.COLUMN_VOICE_HTIME, date);

		if (Settings.isDebug()) {
			Log.v(LOG_TAG, DEFAULT_FILE);
			Log.v(LOG_TAG, getFileName());
			Log.v(LOG_TAG, mSize);
			Log.v(LOG_TAG, date);
		}

		/*
		 * @@Params MyRecordingContentProvider.CONTENT_URI for store info in db
		 */
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "voices");
		getmContext().getContentResolver().insert(uri, values);

		Uri uriNotes = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "vnotes");
		/*
		 * make a default note
		 */
		String Nomdelabase = ProofDataBase.TABLE_VOICES;
		String defaultNote = "Aucune note pour cet enregistrement Vocal";
		String defaultTitleNote = "Insérer une note";

		ContentValues valuesNote = new ContentValues();
		int lastId = PersonnalProofContentProvider.lastInsertId(Nomdelabase);
		valuesNote.put(ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID, lastId);
		valuesNote.put(ProofDataBase.COLUMNVOICE_TITLE, defaultTitleNote);
		valuesNote.put(ProofDataBase.COLUMNVOICE_NOTE, defaultNote);
		valuesNote.put(ProofDataBase.COLUMNVOICE_DATE_CREATION, date);
		getmContext().getContentResolver().insert(uriNotes, valuesNote);

		if (Settings.isDebug()) {
			Log.v(LOG_TAG, "Voice Note last insert id :" + lastId);
			Log.v(LOG_TAG, "Voice Note :" + defaultTitleNote);
			Log.v(LOG_TAG, "Voice Note :" + defaultNote);
			Log.v(LOG_TAG, "Voice Note :" + date);
		}
	}

	private void writeCallToDb() {
		ContentValues values = new ContentValues();
		final Calendar cal = Calendar.getInstance();

		Long humanreadable;
		String mSize;
		String mFileName = DEFAULT_FILE.toString();

		Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
				mContext, mNumber);

		values.put(ProofDataBase.COLUMN_TELEPHONE, mContact.getPhoneNumber());
		values.put(ProofDataBase.COLUMN_SENS, mSense);

		switch (format) {
		case THREE_GP:
			Uri threegp = Uri.parse("file://" + DEFAULT_FILE);
			File f = new File(threegp.getPath());
			long size = f.length();
			mSize = Long.valueOf(size).toString();
			break;

		case WAV:

			if (Settings.isDebug()) {
				Log.v(LOG_TAG, "File size: " + totalDataLen);
			}
			mSize = Long.valueOf(totalDataLen).toString();
			break;

		case MP3:

			mSize = Long.valueOf(new File(DEFAULT_FILE).length()).toString();
			if (Settings.isDebug()) {
				Log.v(LOG_TAG, "File size: " + mSize);
			}
			break;
		case OGG:

			mSize = Long.valueOf(new File(DEFAULT_FILE).length()).toString();
			if (Settings.isDebug()) {
				Log.v(LOG_TAG, "File size: " + mSize);
			}
			break;

		default:
			throw new IllegalArgumentException(
					"writeCallToDb()->LE FORMAT SPECIFIé EST INCONNU!");
		}

		values.put(ProofDataBase.COLUMN_TIMESTAMP, getFileName());
		values.put(ProofDataBase.COLUMN_FILE, mFileName);
		values.put(ProofDataBase.COLUMN_TAILLE, mSize);

		/*
		 * We save the ID of the contact found in the phone's contacts list
		 */
		values.put(ProofDataBase.COLUMN_CONTRACT_ID, mContact.getContractId());
		/*
		 * human readable date
		 */
		humanreadable = Long.valueOf(getFileName());
		cal.setTimeInMillis(humanreadable);
		@SuppressWarnings("deprecation")
		final String date = cal.getTime().toLocaleString();

		values.put(ProofDataBase.COLUMN_HTIME, date);

		if (Settings.isDebug()) {
			Log.v(LOG_TAG, mContact.getPhoneNumber());
			Log.v(LOG_TAG, mFileName);
			Log.v(LOG_TAG, getFileName());
			Log.v(LOG_TAG, mSense);
			Log.v(LOG_TAG, mSize);
			Log.v(LOG_TAG, date);
			Log.v(LOG_TAG,
					ProofDataBase.COLUMN_CONTRACT_ID + ": "
							+ mContact.getContractId());
		}
		/*
		 * @@Params MyRecordingContentProvider.CONTENT_UR for store info in db
		 */
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "records");

		getmContext().getContentResolver().insert(uri, values);

		Uri uriNotes = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "notes");

		// * make a default note

		String Nomdelabase = ProofDataBase.TABLE_RECODINGAPP;
		ContentValues valuesNote = new ContentValues();

		// NOTE'S INSERT

		lastId = PersonnalProofContentProvider.lastInsertId(Nomdelabase);

		valuesNote.put(ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID, lastId);
		// valuesNote.put(ProofDataBase.COLUMN_TITLE, "Insérer une note");
		// valuesNote.put(ProofDataBase.COLUMN_NOTE,
		// "Aucune note pour cet appel");
		valuesNote.put(ProofDataBase.COLUMN_DATE_LAST_MODIF, date);
		getmContext().getContentResolver().insert(uriNotes, valuesNote);

		if (Settings.isDebug()) {
			Log.v(LOG_TAG, "Note last insert id :" + lastId);
			Log.v(LOG_TAG, "Note :" + "Insérer une note");
			Log.v(LOG_TAG, "Note :" + "Aucune note pour cet appel");
			Log.v(LOG_TAG, "Note :" + date);
		}
	}

	/**
	 * 
	 * @param totalDataLen
	 */
	private void writeToDb() {
		switch (type) {
		case CALL:
			writeCallToDb();
			
			/*
			 * new Thread(new Runnable() { public void run() { try {
			 * UploadFile("call"); } catch (Exception e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } } }).start();
			 */

			break;

		case VOICE_TITLED:
		case VOICE_UNTITLED:
			writeVoiceToDb();
			
			/*
			 * new Thread(new Runnable() { public void run() { try {
			 * UploadFile("voice"); } catch (Exception e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } } }).start();
			 */

			break;

		default:
			throw new IllegalArgumentException(
					"writeToDb()->LE TYPE SPECIFIé EST INCONNU!");
		}
	}

	/*
	 * private ServiceConnection mConnection = new ServiceConnection() {
	 * 
	 * public void onServiceConnected(ComponentName className, IBinder service)
	 * { // We've bound to LocalService, cast the IBinder and get //
	 * LocalService instance LocalBinder binder = (LocalBinder) service; FtpCli
	 * mService = binder.getService(); boolean mBound = true; }
	 * 
	 * public void onServiceDisconnected(ComponentName arg0) { boolean mBound =
	 * false; } };
	 */

	
}
