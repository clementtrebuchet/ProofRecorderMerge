package org.proof.recorder.services;

import org.proof.recorder.utils.Log.Console;
import org.proofs.recorder.codec.mp3.utils.IServiceIntentRecorderMP3;
import org.proofs.recorder.codec.mp3.utils.IServiceIntentRecorderMP3Cx;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class MP3Middleware extends Service  {

	
	//private IServiceIntentRecorderMP3Cx connection = null;
	public IServiceIntentRecorderMP3 mService;
	private IServiceIntentRecorderMP3Cx remotePlugCnx;
	private String mFile;
	private int mSampleRate;
	private int audioSource;
	private int outBitrate;
	
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
				Console.print_debug(mFile+"/"+mSampleRate+"/"+audioSource+"/"+outBitrate);			
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

	 public void callWhenReady(){
		 
		this.remotePlugCnx.safelyPassParameters(mFile, mSampleRate, audioSource, outBitrate, 0, "org.proofs.recorder.codec.mp3", "org.proofs.recorder.codec.mp3.MainActivity");
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
