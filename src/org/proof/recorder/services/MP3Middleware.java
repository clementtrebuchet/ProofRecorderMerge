package org.proof.recorder.services;

import org.proof.recorder.utils.Log.Console;
import org.proofs.recorder.codec.mp3.utils.IServiceIntentRecorderMP3;
import org.proofs.recorder.codec.mp3.utils.IServiceIntentRecorderMP3Cx;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MP3Middleware extends Service  {

	
	//private IServiceIntentRecorderMP3Cx connection = null;
	public IServiceIntentRecorderMP3 mService;
	private IServiceIntentRecorderMP3Cx remotePlugCnx;
	private String mFile;
	private int mSampleRate;
	private int audioSource;
	private int outBitrate;
	private int postEncode;
	private String broadcastClass;
	
	 @Override
	  public void onCreate() {
	    super.onCreate();
	    
	  	
	}
	 /**
	  * 
	  */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
				this.remotePlugCnx = new IServiceIntentRecorderMP3Cx(this);
				this.remotePlugCnx.safelyConnectTheService();
				while(this.remotePlugCnx == null){
					Console.print_debug("this.remotePlugCnx is :"+this.remotePlugCnx);
				}
				Console.print_debug("proof onStartCommand: "+ this.remotePlugCnx+"mService is "+mService);
				mFile = intent.getStringExtra("FileName");
				mSampleRate = intent.getIntExtra("mSampleRate", 44100);
				audioSource = intent.getIntExtra("audioSource", 1);
				outBitrate = intent.getIntExtra("outBitrate", 192);
				postEncode = intent.getIntExtra("postEncode", 0);
				broadcastClass = intent.getStringExtra("broadcastClass");
				if (broadcastClass == null){
					broadcastClass = "org.proofs.recorder.codec.mp3.MainActivity";
				}
				Console.print_debug(mFile+"/"+mSampleRate+"/"+audioSource+"/"+outBitrate+"/"+broadcastClass);			
		return(START_STICKY);	
	}
		
	 public void parametersRecAsynchronously(int message) {
		 Console.print_debug("proof parametersRecAsynchronously: "+message);
	 
	 }
	 
	 public void startRecAsynchronously(int message){
		 Console.print_debug("proof startRecAsynchronously: "+message);
	 }
	 
	 public void stopRecAsynchronously(int message){
		 Console.print_debug("proof stopRecAsynchronously: "+message);
	 }

	public void callWhenReady() {

		this.remotePlugCnx.safelyPassParameters(mFile, mSampleRate,
				audioSource, outBitrate, postEncode,
				"org.proofs.recorder.codec.mp3",
				"org.proofs.recorder.codec.mp3.MainActivity", broadcastClass);
		this.remotePlugCnx.safelyStartRec();

	}
	
	
	@Override
	public void onDestroy() {
		this.remotePlugCnx.safelyStopRec();
		this.remotePlugCnx.safelyDisconnectTheService();
		super.onDestroy();
		

	}

	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	

}
