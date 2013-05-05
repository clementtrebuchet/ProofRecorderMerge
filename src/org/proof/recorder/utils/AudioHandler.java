package org.proof.recorder.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.fragment.phone.FragmentListRecordTabs;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.Log.Console;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;


public class AudioHandler {

	/**
	 * Hanlde Audio Multiple Formats
	 */

	private static String DEFAULT_FILE_DIR;
	//private int NODOUBLONS;
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
	private static Intent mMP3Intent, mOggIntent;
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
	private static long lastId;

	private long totalAudioLen = 0;
	private long totalDataLen = totalAudioLen + 36;

	private String mNumber, mSense;
	private String wavFileSize;

	public void setmNumber(String mNumber) {
		this.mNumber = mNumber;
	}

	public void setmSense(String mSense) {
		this.mSense = mSense;
	}

	/**
	 * 
	 */
	private void inititialzeWavRecording() {
		int channel;
		int quality;

			channel = Settings.RECORDER_CHANNELS;
			quality = Settings.RECORDER_SAMPLERATE;

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
		
		Console.setTagName(this.getClass().getSimpleName());

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
			inititialzeWavRecording();
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
			startWavRecording();
			break;

		case MP3:
			
			setDefaultFilePath(".mp3");
			
			mMP3Intent = new Intent(
					"org.proofs.recorder.codec.mp3.utils.ServiceIntentRecorderMP3");
			
			Bundle B = new Bundle();
			B.putString("FileName", DEFAULT_FILE);
			B.putInt("mSampleRate", Settings.getMP3Hertz(getmContext()));
			B.putInt("mp3Channel", 1);
			B.putInt("audioSource", mAudioSource);
			B.putInt("outBitrate", mp3Compression);
			
			mMP3Intent.putExtras(B);
			mMP3Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			getmContext().startService(mMP3Intent);
			break;
			
		case OGG:
			
			setDefaultFilePath(".ogg");
			
			mOggIntent = new Intent(
					"org.proofs.recorder.codec.ogg.utils.ServiceIntentRecorderOgg");
			
			Bundle B1 = new Bundle();
			B1.putString("file", DEFAULT_FILE);
			B1.putInt("sampleRate", Settings.getMP3Hertz(getmContext()));
			B1.putInt("channel", 1);
			B1.putInt("audioSource", mAudioSource);
			B1.putFloat("quality", Settings.getOGGQual(getmContext()));
			
			// TODO: check the need to setAction whereas MP3 not ?!!
			// mOggIntent.setAction("org.proofs.recorder.codec.ogg.utils.ServiceIntentRecorderOgg");
			
			mOggIntent.putExtras(B1);
			mOggIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			getmContext().startService(mOggIntent);
			break;

		default:
			setDefaultFilePath(".wav");
			startWavRecording();
			break;
		}
	}

	public void stopRecording() {
		switch (format) {
		case THREE_GP:
			stopThreeGpRecording();
			break;

		case WAV:
			stopWavRecording();
			break;
		case MP3:
			getmContext().stopService(mMP3Intent);
			break;
		case OGG:
			getmContext().stopService(mOggIntent);
			break;
		default:
			break;
		}

		try {
			writeToDb();
			notifyOnEndingCall();
		} catch (IllegalArgumentException e) {
			Console.print_exception(e);
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

			Console.print_exception(e);
		}

		try {
			audio.setOutputFile(DEFAULT_FILE);
		} catch (IllegalStateException e) {

			Console.print_exception(e);
		}

		try {
			audio.prepare();
		} catch (IOException e) {
			Console.print_exception(e);
			
		} catch (IllegalStateException e) {
			Console.print_exception(e);
		}

		try {
			audio.start();
		} catch (IllegalStateException e) {
			Console.print_exception(e);
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
				Console.print_exception(e);
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
	private void startWavRecording() {
		try {
/*			if (NODOUBLONS <= 0) {
				MpthreeRec.Mpthree(Settings.getMP3Hertz(getmContext()));
				MpthreeRec
				.lameInit(mp3Compression, Settings.defaultQuality);
				NODOUBLONS++;
			}*/

			audioWav.startRecording();
		} catch (Exception e) {
			Console.print_exception(e);
		}

		isRecording = true;
		recordingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				writeAudioDataToFile();

			}
		}, "AudioRecorder Thread");
		recordingThread.start();
	}

	/**
	 * 
	 */
	private void writeAudioDataToFile() {

		byte data[] = new byte[minBufferSize];
		String filename = getTempFilename();
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			Console.print_exception(e);
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

	}

	/**
	 * 
	 */
	private void stopWavRecording() {
		
		if (audioWav != null) {
			isRecording = false;

			audioWav.stop();
			audioWav.release();

			audioWav = null;
			recordingThread = null;
			
			// TODO: check if running copy on thread is efficient !!!
			
			File tempAudio = new File(DEFAULT_FILE_DIR + 
					Settings.AUDIO_RECORDER_TEMP_FILE);	
			
			wavFileSize = Long.valueOf(tempAudio.length()).toString();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					copyWaveFile(
							DEFAULT_FILE_DIR + 
							Settings.AUDIO_RECORDER_TEMP_FILE
					);
					
					deleteTempFile();
					
				}
			}).start();		
		}		
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
			Console.print_exception(e);
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

		title = getmContext().getString(R.string.app_name);

		switch (type) {
		case CALL:
			
			Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
					getmContext(), mNumber);
			
			extraNotification.putBoolean("isNotify", true);
			extraNotification.putString("Sense", mSense);
			extraNotification.putLong("RecordId", lastId);
			
			String mFrom;
			if (!mContact.getContractId().equalsIgnoreCase("null")) {
				mFrom = mContact.getContactName();
			} else {
				mFrom = mNumber;
			}

			destination = FragmentListRecordTabs.class;			
			info = "";
			text = getmContext().getString(R.string.notifyEndOfCallOne) + " "
					+ mFrom + " "
					+ getmContext().getString(R.string.notifyEndOfCallTwo);
			break;

		default:
			return;
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

	private void prepareSendingData() {		

		Long humanreadable = Long.valueOf(getFileName());

		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(humanreadable);	
		
		@SuppressWarnings("deprecation")
		final String date = cal.getTime().toLocaleString();	
		
		String mSize;
		switch (format) {

		case WAV:
			mSize = wavFileSize;
			break;
		
		case THREE_GP:
		case MP3:
		case OGG:
			mSize = Long.valueOf(new File(DEFAULT_FILE).length()).toString();		
			break;

		default:
			throw new IllegalArgumentException(
					"writeCallToDb()->LE FORMAT SPECIFIé EST INCONNU!");
		}
		
		Console.print_debug("File size: " + mSize);

		/*
		 * @@Params MyRecordingContentProvider.CONTENT_URI for store info in db
		 */
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "voices");

		Uri uriNotes = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "vnotes");

		ArrayList<String> voice = new ArrayList<String>();
		ArrayList<String> voiceNote = new ArrayList<String>();

		voice.add(uri.toString());

		voice.add(DEFAULT_FILE.toString());
		voice.add(getFileName());
		voice.add(mSize);
		voice.add(date);

		voiceNote.add(uriNotes.toString());

		Bundle extra_data = new Bundle();
		extra_data.putStringArrayList("voice", voice);
		extra_data.putStringArrayList("voiceNote", voiceNote);		

		AlertDialogHelper.openVoiceEditDialog(extra_data);
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

		case WAV:
			mSize = wavFileSize;			
			break;
		
		case THREE_GP:
		case MP3:
		case OGG:
			mSize = Long.valueOf(new File(DEFAULT_FILE).length()).toString();			
			break;

		default:
			throw new IllegalArgumentException(
					"writeCallToDb()->LE FORMAT SPECIFIé EST INCONNU!");
		}
		
		Console.print_debug("File size: " + mSize);

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

		Console.print_debug(mContact.getPhoneNumber());
		Console.print_debug(mFileName);
		Console.print_debug(getFileName());
		Console.print_debug(mSense);
		Console.print_debug(mSize);
		Console.print_debug(date);
		Console.print_debug(ProofDataBase.COLUMN_CONTRACT_ID + ": "
							+ mContact.getContractId());
		/*
		 * @@Params MyRecordingContentProvider.CONTENT_UR for store info in db
		 */
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "records");

		Uri rowId = getmContext().getContentResolver().insert(uri, values);

		Uri uriNotes = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "notes");

		ContentValues valuesNote = new ContentValues();

		// NOTE'S INSERT

		lastId = Long.parseLong(rowId.toString());

		valuesNote.put(ProofDataBase.COLUMN_ID_COLUMNRECODINGAPP_ID, lastId);
		valuesNote.put(ProofDataBase.COLUMN_DATE_LAST_MODIF, date);
		getmContext().getContentResolver().insert(uriNotes, valuesNote);

		Console.print_debug("Note last insert id :" + lastId);
		Console.print_debug("Note :" + "Insérer une note");
		Console.print_debug("Note :" + "Aucune note pour cet appel");
		Console.print_debug("Note :" + date);
	}

	/**
	 * 
	 * @param totalDataLen
	 */
	private void writeToDb() {
		switch (type) {
		case CALL:
			writeCallToDb();
			break;

		case VOICE_TITLED:
		case VOICE_UNTITLED:
			prepareSendingData();
			break;

		default:
			throw new IllegalArgumentException(
					"writeToDb()->LE TYPE SPECIFIé EST INCONNU!");
		}
	}	
}
