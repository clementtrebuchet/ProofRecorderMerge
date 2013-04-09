package org.proof.recorder.service;

import java.io.File;

import org.proof.recorder.Settings;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * @author clement
 *
 */
public class TestDevice  extends AsyncTask<Void ,Integer, Void> {
	
	private static boolean toProcess = true;

	private int bufferSize = 0;
	
	public static  boolean mic;
	public static  boolean voice_call;
	public static  boolean voice_com ;
	public static  boolean voice_reco;
	public static  boolean voice_up ;
	public static  boolean voice_down ;
	public static  boolean cam ;
	
	public static  boolean[] all = {
		mic, 
		voice_up, 
		voice_down, 
		voice_call,
		cam, 
		voice_reco, 
		voice_com
	};
	
	public static String[] toStr = {
		"mic",
		"voice_up", 
		"voice_down",
		"voice_call",
		"cam", 
		"voice_reco", 
		"voice_com"
	};

	public static Bundle BUNDLECONFIGURATIONAUDIO = new Bundle();
	
	/**
	 * @param message
	 */
	private void print(String message) {
		if(Settings.isDebug())
			Log.d(this.getClass().getName(), "" + message);
	}

	/**
	 * @param message
	 */
	private void print_exception(String message) {
		Log.e(this.getClass().getName(), "" + message);
	}



	public TestDevice(){	

	}

	private void makeDirectoriesStructure(){

		for (String path : Settings.DEFAULT_FILE_PATHS)
		{
			File dir = new File(path);
			if(!dir.exists() && !dir.isDirectory()) {
				dir.mkdirs();
			}
		}
	}

	private void recorder(int source, boolean resultat){
		try{
			bufferSize = AudioRecord.getMinBufferSize(Settings.RECORDER_SAMPLERATE,
					Settings.RECORDER_CHANNELS, Settings.RECORDER_AUDIO_ENCODING);
			AudioRecord recorder = new AudioRecord(
					source,
					Settings.RECORDER_SAMPLERATE, Settings.RECORDER_CHANNELS,
					Settings.RECORDER_AUDIO_ENCODING, bufferSize);
			recorder.startRecording();
			resultat = true;
			BUNDLECONFIGURATIONAUDIO.putBoolean(toStr[source-1], resultat);

			print("position : " + toStr[source-1] + " capabilitie : " + resultat);

			recorder.stop();
		}catch (Exception e){

			print_exception("Capabilitie for " + toStr[source-1] + " : " + e);

			resultat = false;
			BUNDLECONFIGURATIONAUDIO.putBoolean(toStr[source-1], resultat);

		}

	}

	private void testAll(int[] all, boolean[] res){

		recorder(all[0], res[0]);
		recorder(all[1], res[1]);
		recorder(all[2], res[2]);
		recorder(all[3], res[3]);
		recorder(all[4], res[4]);
		recorder(all[5], res[5]);
		recorder(all[6], res[6]);

	}

	/**
	 * @category Hardware abstraction
	 * @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	 */
	
	interface capabiltiesHw {

		public static final int mic = MediaRecorder.AudioSource.MIC;						//1
		public static final int voice_up = MediaRecorder.AudioSource.VOICE_UPLINK; 			//2
		public static final int voice_down = MediaRecorder.AudioSource.VOICE_DOWNLINK;		//3
		public static final int voice_call = MediaRecorder.AudioSource.VOICE_CALL; 			//4
		public static final int cam = MediaRecorder.AudioSource.CAMCORDER;					//5
		public static final int voice_reco = MediaRecorder.AudioSource.VOICE_RECOGNITION; 	//6
		public static final int voice_com = MediaRecorder.AudioSource.VOICE_COMMUNICATION; 	//7
				
		public static final int[] all = {
			mic, 
			voice_up, 
			voice_down, 
			voice_call,
			cam, 
			voice_reco, 
			voice_com 
		};
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		
		if(isToProcess()) {
			makeDirectoriesStructure();		 
			testAll(capabiltiesHw.all, all);
			setToProcess(false);
		}
		
		return null;
	}

	/**
	 * @return the toProcess
	 */
	public static boolean isToProcess() {
		return toProcess;
	}

	/**
	 * @param toProcess the toProcess to set
	 */
	public static void setToProcess(boolean toProcess) {
		TestDevice.toProcess = toProcess;
	}

}

