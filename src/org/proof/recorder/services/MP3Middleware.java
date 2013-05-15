package org.proof.recorder.services;

import org.proof.recorder.utils.PlugMiddleware;
import org.proof.recorder.utils.Log.Console;
import org.proofs.recorder.codec.mp3.utils.IServiceIntentRecorderMP3;
import org.proofs.recorder.codec.mp3.utils.IServiceIntentRecorderMP3Cx;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MP3Middleware extends Service implements PlugMiddleware  {

	
	//private IServiceIntentRecorderMP3Cx connection = null;
	public IServiceIntentRecorderMP3 mService;
	private IServiceIntentRecorderMP3Cx remotePlugCnx;
	private String mFile;
	private int mSampleRate;
	private int audioSource;
	private int outBitrate;
	private int postEncode;
	private String broadcastClass;
	private String notificationIntent;
	private String notificationPkg;
	
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
				if (intent.getExtras().containsKey("notificationIntent")) {
					
					notificationIntent = intent
							.getStringExtra("notificationIntent");
				} else {
					
					notificationIntent = "org.proofs.recorder.codec.mp3.MainActivity";
					
				}
				
				if (intent.getExtras().containsKey("notificationPkg")) {
					
					notificationPkg = intent
							.getStringExtra("notificationPkg");
					
				} else {
					
					notificationPkg = "org.proofs.recorder.codec.mp3";
				}
				Console.print_debug(mFile+"/"+mSampleRate+"/"+audioSource+"/"+outBitrate+"/"+broadcastClass);			
		return(START_STICKY);	
	}
	
	
	@Override
	 public void parametersRecAsynchronously(int message) {
		 Console.print_debug("proof parametersRecAsynchronously RC =: "+message);
	 
	 }
	@Override
	 public void startRecAsynchronously(int message){
		 Console.print_debug("proof startRecAsynchronously RC =: "+message);
	 }
	@Override
	 public void stopRecAsynchronously(int message){
		 Console.print_debug("proof stopRecAsynchronously RC =: "+message);
	 }
	@Override
	public void callWhenReady() {

		this.remotePlugCnx.safelyPassParameters(mFile, mSampleRate,
				audioSource, outBitrate, postEncode, notificationIntent,
				notificationPkg, broadcastClass);
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
