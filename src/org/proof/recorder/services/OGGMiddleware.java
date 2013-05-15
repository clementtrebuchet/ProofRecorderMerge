package org.proof.recorder.services;

import org.proof.recorder.utils.PlugMiddleware;
import org.proof.recorder.utils.Log.Console;
import org.proofs.recorder.codec.ogg.utils.IServiceRecorderOggCx;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class OGGMiddleware extends Service implements PlugMiddleware {

	private IServiceRecorderOggCx remotePlugCnx;
	private String mFile;
	private int mSampleRate;
	private int audioSource;
	private int outBitrate;
	private String notificationIntent;
	private String notificationPkg;
	private float mQuality;

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

		this.remotePlugCnx = new IServiceRecorderOggCx(this);
		this.remotePlugCnx.safelyConnectTheService();

		while (this.remotePlugCnx == null) {
			Console.print_debug("this.remotePlugCnx is :" + this.remotePlugCnx);
		}
		/**
		 * 
		 * @param Filename
		 * @param mSampleRate
		 * @param mQuality
		 * @param audioSource
		 * @param notificationIntent
		 * @param notificationPkg
		 */
		Console.print_debug("proof onStartCommand: " + this.remotePlugCnx);
		mFile = intent.getStringExtra("file");
		mSampleRate = intent.getIntExtra("sampleRate", 44100);
		mQuality = intent.getFloatExtra("quality", 0.4f);
		audioSource = intent.getIntExtra("audioSource", 1);

		if (intent.getExtras().containsKey("notificationIntent")) {

			notificationIntent = intent.getStringExtra("notificationIntent");
		} else {

			notificationIntent = "org.proofs.recorder.codec.mp3.MainActivity";

		}

		if (intent.getExtras().containsKey("notificationPkg")) {

			notificationPkg = intent.getStringExtra("notificationPkg");

		} else {

			notificationPkg = "org.proofs.recorder.codec.mp3";
		}
		Console.print_debug(mFile + "/" + mSampleRate + "/" + audioSource + "/"
				+ outBitrate + "/");
		return (START_STICKY);
	}

	@Override
	public void callWhenReady() {
		this.remotePlugCnx.safelyPassParameters(mFile, mSampleRate, mQuality,
				audioSource, notificationIntent, notificationPkg);
		this.remotePlugCnx.safelyStartRec();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parametersRecAsynchronously(int message) {
		 Console.print_debug("proof parametersRecAsynchronously RC =: "+message);

	}

	@Override
	public void startRecAsynchronously(int message) {
		Console.print_debug("proof startRecAsynchronously RC =: "+message);

	}

	@Override
	public void stopRecAsynchronously(int message) {
		 Console.print_debug("proof stopRecAsynchronously RC =: "+message);

	}
	
	@Override
	public void onDestroy() {
		this.remotePlugCnx.safelyStopRec();
		this.remotePlugCnx.safelyDisconnectTheService();
		super.onDestroy();
		

	}

}
