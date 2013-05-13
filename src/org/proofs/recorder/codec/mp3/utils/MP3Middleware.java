package org.proofs.recorder.codec.mp3.utils;

import org.proof.recorder.utils.Log.Console;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class MP3Middleware extends Service {

	private static final String START_ = "org.proofs.recorder.codec.mp3.utils.MP3Middleware";
	private IServiceIntentRecorderMP3Cx connection = null;
	private IServiceIntentRecorderMP3 mService = null;
	private String mFile;
	private int mSampleRate;
	private int audioSource;
	private int outBitrate;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (intent.getAction().equals(START_)) {
			initService();
			mFile = intent.getStringExtra("FileName");
			mSampleRate = intent.getIntExtra("mSampleRate", 44100);
			audioSource = intent.getIntExtra("audioSource", 1);
			outBitrate = intent.getIntExtra("outBitrate", 192);
			
			try {
				mService.parametersRec(mFile, mSampleRate, audioSource, outBitrate, 0, "", "");
				mService.startRec();
			} catch (RemoteException e) {
				Console.print_debug("startService()  " + e.getMessage());
			}
			
		} 
		
		
		super.onStartCommand(intent, flags, startId);
		return(START_STICKY);
		
	}


	@Override
	public void onDestroy() {
		try {
			releaseService();
		} finally {
			super.onDestroy();
		}

	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	/** Binds to the service. */
	  private void initService() {
	    connection = new IServiceIntentRecorderMP3Cx();
	    Intent i = new Intent();
	    i.setClassName("org.proofs.recorder", "org.proofs.recorder.codec.mp3.utils.ServiceIntentRecorderMP3");
	    boolean ret = bindService(i, connection, Context.BIND_AUTO_CREATE);
	    mService = connection.getService();
	    Console.print_debug("initService() bound with " + ret);
	    
	    
	  }

	  /** Unbinds from the service. */
	  private void releaseService() {
		unbindService(connection);
	    connection = null;
	    Console.print_debug("releaseService() unbound.");
	   
	  }
}
