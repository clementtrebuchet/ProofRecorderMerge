package org.proofs.recorder.codec.mp3.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.proof.recorder.services.MP3Middleware;

public class IServiceIntentRecorderMP3Cx implements ServiceConnection {

	private IServiceIntentRecorderMP3 service;
	private final String TAG = IServiceIntentRecorderMP3Cx.class.getName();
	private final MP3Middleware parent;
	/**
	 * 
	 * @param parent the MP3Middleware parent Class
	 */
	public IServiceIntentRecorderMP3Cx(MP3Middleware parent){		
		this.parent = parent;		
	}

	/**
	 * 
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder boundService) {
		this.service = IServiceIntentRecorderMP3.Stub.asInterface(boundService);
		Log.d(TAG, "onServiceConnected() connected service is " + this.service);
		this.parent.callWhenReady();


	}

	/**
	 * 
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
		service = null;
		Log.d(TAG, "onServiceDisconnected() disconnected");

	}

	/**
	 * Method to disconnect the Service.
	 * This method is required because the onServiceDisconnected
	 * is only called when the connection got closed unexpectedly
	 * and not if the user requests to disconnect the service.
	 */
	public void safelyDisconnectTheService() {
		if(service != null) {
			service = null;
			parent.unbindService(this);
			Log.d(TAG, "The connection to the service was closed.!");
		}
	}

	/**
	 * Method to connect the Service.
	 */
	public void safelyConnectTheService() {
		if(service == null) {
			Intent i = new Intent("org.proofs.recorder.codec.mp3.utils.ServiceIntentRecorderMP3");
			//i.setAction();
			parent.bindService(i, IServiceIntentRecorderMP3Cx.this, Context.BIND_AUTO_CREATE);
			Log.d(TAG, "The Service will be connected soon (asynchronus call)!");
		}
	}
	/**
	 * 
	 * @param Filename the name of the file
	 * @param mSampleRate the sample rate
	 * @param audioSource audio source
	 * @param outBitRate the out bit rate
	 * @param postEcode the post encode parameter
	 * @param notificationIntent the notification agent
	 * @param notificationPkg the notification for package action
	 */
	public void safelyPassParameters(String Filename, int mSampleRate,
			int audioSource, int outBitRate, int postEcode,
			String notificationIntent, String notificationPkg,String broadcastClass) {

		Log.d(TAG, "Trying to query the message from the Service.");
		if (service == null) { // if the service is null the connection is not
			// established.
			Log.d(TAG, "The service was not connected (safelyPassParameters) -> connecting.");
			safelyConnectTheService();

		} else {
			Log.d(TAG,
					"The Service is already connected (safelyPassParameters) -> querying the message.");
			try {
				parent.parametersRecAsynchronously(service.parametersRec(
						Filename, mSampleRate, audioSource, outBitRate,
						postEcode, notificationIntent, notificationPkg, broadcastClass));
			} catch (RemoteException e) {
				Log.e(TAG, "An error occured during the call (safelyPassParameters).");
			}
		}

	}
	/**
	 * 
	 */
	public void safelyStartRec() {
		if (service == null) { // if the service is null the connection is not
			// established.
			Log.d(TAG, "The service was not connected (safelyStartRec) -> connecting.");
			safelyConnectTheService();

		} else {
			Log.d(TAG,
					"The Service is already connected (safelyStartRec) -> querying the message.");
			try {
				parent.startRecAsynchronously(service.startRec());
			} catch (RemoteException e) {
				Log.e(TAG, "An error occured during the call (safelyStartRec).");
			}
		}

	}
	/**
	 * 
	 */
	public void safelyStopRec(){

		if (service == null) { // if the service is null the connection is not
			// established.
			Log.d(TAG, "The service was not connected (safelyStopRec) -> connecting.");
			safelyConnectTheService();

		} else {
			Log.d(TAG,
					"The Service is already connected (safelyStopRec) -> querying the message.");
			try {
				parent.stopRecAsynchronously(service.stopRec());
			} catch (RemoteException e) {
				Log.e(TAG, "An error occured during the call (safelyStopRec).");
			}
		}
	}
	
	/**
	 * 
	 */
	public void safelyEncodeRawFile(){

		if (service == null) { // if the service is null the connection is not
			// established.
			Log.d(TAG, "The service was not connected (safelyEncodeRawFile) -> connecting.");
			safelyConnectTheService();

		} else {
			Log.d(TAG,
					"The Service is already connected (safelyEncodeRawFile) -> querying the message.");
			try {
				service.encodeFile();
				parent.EncodeRawFileAsynchronously();
			} catch (RemoteException e) {
				Log.e(TAG, "An error occured during the call (safelyEncodeRawFile).");
			}
		}
	}
}
