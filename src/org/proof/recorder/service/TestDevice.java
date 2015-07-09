package org.proof.recorder.service;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.proof.recorder.Settings;

import java.io.File;

/**
 * @author clement
 *
 */
public class TestDevice  extends AsyncTask<Void ,Integer, Void> {
	
	private static boolean toProcess = true;

    private static boolean mic;
    private static boolean voice_call;
    private static boolean voice_com;
    private static boolean voice_reco;
    private static boolean voice_up;
    private static boolean voice_down;
    private static boolean cam;

    private static final boolean[] all = {
            mic,
            voice_up,
		voice_down, 
		voice_call,
		cam, 
		voice_reco, 
		voice_com
	};

    private static final String[] toStr = {
            "mic",
            "voice_up",
		"voice_down",
		"voice_call",
		"cam", 
		"voice_reco", 
		"voice_com"
	};

    public static final Bundle BUNDLECONFIGURATIONAUDIO = new Bundle();

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void makeDirectoriesStructure() {

		for (String path : Settings.DEFAULT_FILE_PATHS)
		{
			File dir = new File(path);
			if(!dir.exists() && !dir.isDirectory()) {
				dir.mkdirs();
			}
		}
	}

    private void recorder(int source) {

        AudioRecord recorder = null;

        boolean resultat;
        try {
            int bufferSize = AudioRecord.getMinBufferSize(Settings.RECORDER_SAMPLERATE,
                    Settings.RECORDER_CHANNELS, Settings.RECORDER_AUDIO_ENCODING);

            recorder = new AudioRecord(
                    source,
					Settings.RECORDER_SAMPLERATE, Settings.RECORDER_CHANNELS,
					Settings.RECORDER_AUDIO_ENCODING, bufferSize);
			
			recorder.startRecording();

			BUNDLECONFIGURATIONAUDIO.putBoolean(toStr[source - 1], true);

            print("position : " + toStr[source - 1] + " capabilitie : " + true);

			recorder.stop();
			recorder.release();
			
			recorder = null;
			
		}catch (Exception e){

			print_exception("Capabilitie for " + toStr[source-1] + " : " + e);

			BUNDLECONFIGURATIONAUDIO.putBoolean(toStr[source - 1], false);

            if (recorder != null) {
                try {
					recorder.release();
				}
				catch (Exception exc) {
                    Log.d(this.getClass().getName(), e.getMessage());
                }
            }
		}
	}

    private void testAll() {

        recorder(capabiltiesHw.all[0]);
        recorder(capabiltiesHw.all[1]);
        recorder(capabiltiesHw.all[2]);
        recorder(capabiltiesHw.all[3]);
        recorder(capabiltiesHw.all[4]);
        recorder(capabiltiesHw.all[5]);
        recorder(capabiltiesHw.all[6]);

	}

	/**
	 * @category Hardware abstraction
	 * @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	 */
	
	interface capabiltiesHw {

        int mic = MediaRecorder.AudioSource.MIC;                        //1
        int voice_up = MediaRecorder.AudioSource.VOICE_UPLINK;            //2
        int voice_down = MediaRecorder.AudioSource.VOICE_DOWNLINK;        //3
        int voice_call = MediaRecorder.AudioSource.VOICE_CALL;            //4
        int cam = MediaRecorder.AudioSource.CAMCORDER;                    //5
        int voice_reco = MediaRecorder.AudioSource.VOICE_RECOGNITION;    //6
        int voice_com = MediaRecorder.AudioSource.VOICE_COMMUNICATION;    //7

        int[] all = {
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
            testAll();
            setToProcess();
        }

        return null;
	}

	/**
	 * @return the toProcess
	 */
    private static boolean isToProcess() {
        return toProcess;
    }

	/**
     */
    private static void setToProcess() {
        TestDevice.toProcess = false;
    }

}

