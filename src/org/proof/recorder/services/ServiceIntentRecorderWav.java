package org.proof.recorder.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.utils.OsHandler;
import org.proof.recorder.utils.Log.Console;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.IBinder;
import android.util.Log;

public class ServiceIntentRecorderWav extends Service {
	
	private static final int NOTIFICATION_ID = 1234567890;
	
	private static String audioFile = null;
	private static String audioDirName = null;

	private int minBufferSize = 0;
	/*
	 * WAV
	 */
	static long wavLength;
	static int wavCompression;
	
	private boolean isRecording = false;

	private long totalAudioLen = 0;
	private long totalDataLen = totalAudioLen + 36;
	
	private Thread recordingThread = null;
	private AudioRecord audioWav;	
	
	private String pendingIntent = null;
	private String broadcastIntent = null;
	private String pendingIntentPackage = null; 
	
	// ForGround Service Mechanic
	
	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];
	private Notification lNotif;	
	
	@SuppressWarnings("deprecation")
	public Notification mNotification(){
		Notification note=new Notification(R.drawable.plug_wav,
                getString(R.string.notification_wav_text),
                System.currentTimeMillis());
		return note;
	}
	
	@SuppressWarnings("deprecation")
	public void mInitNotification(Notification N){
		
		
		  Intent intent = new Intent();
		  
		  if(pendingIntent != null) {
			  intent.setClassName(this, pendingIntent);
		  }
		  else {
			  intent.setComponent(
					  new ComponentName(pendingIntentPackage, pendingIntent));
		  }
	    
	      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
	                 Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    
	      PendingIntent pi=PendingIntent.getActivity(this, 0, intent, 0);
	      
	      N.setLatestEventInfo(this, 
	    		  			   getString(R.string.notification_wav_title), 
	    		  			   getString(R.string.notification_wav_text),
	    		  			   pi);
	      
	      N.flags|=Notification.FLAG_NO_CLEAR;
	}
	
	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Log.w("ApiDemos", "Unable to invoke method", e);
		} catch (IllegalAccessException e) {
			// Should not happen.
			Log.w("ApiDemos", "Unable to invoke method", e);
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			return;
		}

		// Fall back on the old API.
		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		mNM.notify(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mStopForeground, mStopForegroundArgs);
			return;
		}

		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		mSetForegroundArgs[0] = Boolean.FALSE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
	}
	
	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		try {
			mStartForeground = getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground",
					mStopForegroundSignature);
			return;
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
		}
		try {
			mSetForeground = getClass().getMethod("setForeground",
					mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(
					"OS doesn't have Service.startForeground OR Service.setForeground!");
		}
	}
	
	// End ForeGround

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Console.setTagName(this.getClass().getSimpleName());		
		
		inititialzeWavRecording(intent);
		
		startWavRecording();
		
		/*
		 * start startForegroundCompat
		 */
		startForegroundCompat(NOTIFICATION_ID, lNotif);

		return (START_STICKY);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 */
	private void inititialzeWavRecording(Intent intent) {
		int channel;
		int quality;
		
		lNotif = mNotification();
		mInitNotification(lNotif);

		audioFile = intent.getStringExtra("FileName");
		
		if(intent.getExtras().containsKey("notificationPkg")) {
			pendingIntentPackage = intent.getStringExtra("notificationPkg");
		}
		else {
			pendingIntentPackage = null;
		}	
		
		if(intent.getExtras().containsKey("notificationIntent")) {
			pendingIntent = intent.getStringExtra("notificationIntent");
		}
		else {
			pendingIntent = null;
		}	
		
		if(intent.getExtras().containsKey("broadcastClass")) {
			broadcastIntent = intent.getStringExtra("broadcastClass");
		}
		else {
			broadcastIntent = null;
		}
		
		File tFile = new File(audioFile);
		audioDirName = tFile.getParent();
		
		int mAudioSource = intent.getIntExtra("audioSource", 1);

		Console.print_debug(audioFile);

		channel = Settings.RECORDER_CHANNELS;
		quality = Settings.RECORDER_SAMPLERATE;

		minBufferSize = AudioRecord.getMinBufferSize(quality, channel,
				Settings.RECORDER_AUDIO_ENCODING);

		audioWav = new AudioRecord(mAudioSource,
				Settings.RECORDER_SAMPLERATE, Settings.RECORDER_CHANNELS,
				Settings.RECORDER_AUDIO_ENCODING, minBufferSize);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopWavRecording();
		
		stopForegroundCompat(NOTIFICATION_ID);
	}

	/**
	 * Handle WAV Format
	 */

	/**
	 * 
	 */
	private void startWavRecording() {

		try {
			audioWav.startRecording();
			isRecording = true;
		} catch (Exception e) {
			Console.print_exception(e);
			isRecording = false;
		}

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
						Console.print_exception(e);
					}
				}
			}

			try {
				os.close();
			} catch (IOException e) {
				Console.print_exception(e);
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

			new Thread(new Runnable() {

				@Override
				public void run() {

					copyWaveFile(
							audioDirName + 
							Settings.AUDIO_RECORDER_TEMP_FILE
					);

					deleteTempFile();					
					
					if(broadcastIntent != null) {
						Intent notifyReceiver = new Intent(broadcastIntent);
						sendBroadcast(notifyReceiver);
					}							
				}
			}).start();	
		}		
	}

	/**
	 * 
	 * @return
	 */
	private String getTempFilename() {
		File tempFile = new File(audioDirName
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
			OsHandler.deleteFileFromDisk(audioDirName
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
			out = new FileOutputStream(audioFile);
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
			Console.print_exception(e);
		} catch (IOException e) {
			Console.print_exception(e);
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
}
