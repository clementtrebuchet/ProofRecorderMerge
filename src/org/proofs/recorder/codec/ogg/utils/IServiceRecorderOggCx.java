package org.proofs.recorder.codec.ogg.utils;

import org.proof.recorder.services.OGGMiddleware;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class IServiceRecorderOggCx implements ServiceConnection{
	
	
	private final OGGMiddleware parent;
	private IServiceRecorderOgg service;
	private IBinder mBoundService;
	private String TAG = IServiceRecorderOggCx.class.getName();

	public IServiceRecorderOggCx(OGGMiddleware parent){
		
		this.parent = parent;
		
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder boundService) {
		this.mBoundService = boundService;
		this.service = IServiceRecorderOgg.Stub.asInterface(mBoundService);
		Log.d(TAG, "onServiceConnected() connected service is " + this.service);
		this.parent.callWhenReady();
		
	}

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
                    parent.bindService(i, IServiceRecorderOggCx.this, Context.BIND_AUTO_CREATE);
                    Log.d(TAG, "The Service will be connected soon (asynchronus call)!");
            }
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
	public void safelyPassParameters(String Filename, int mSampleRate,
			float mQuality, int audioSource,
			String notificationIntent, String notificationPkg) {

		Log.d(TAG, "Trying to query the message from the Service.");
		if (service == null) { // if the service is null the connection is not
								// established.
			Log.d(TAG,
					"The service was not connected (safelyPassParameters) -> connecting.");
			safelyConnectTheService();

		} else {
			Log.d(TAG,
					"The Service is already connected (safelyPassParameters) -> querying the message.");
			try {
				parent.parametersRecAsynchronously(service.parametersRec(
						Filename, mSampleRate, mQuality, audioSource,
						notificationIntent, notificationPkg));
			} catch (RemoteException e) {
				Log.e(TAG,
						"An error occured during the call (safelyPassParameters).");
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

}
