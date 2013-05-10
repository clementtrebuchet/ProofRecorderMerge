package org.proof.recorder.services;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.proof.recorder.R;
import org.proof.recorder.utils.Log.Console;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

public class ServiceIntentRecorder3gp extends Service {
	
	private static final int NOTIFICATION_ID = 987654321;
	
	private static MediaRecorder audioRecorder = null;
	
	private static String audioFile = null;
	
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
		
		private String pendingIntent = null;
		private String pendingIntentPackage = null; 
		
		@SuppressWarnings("deprecation")
		public Notification mNotification(){
			Notification note=new Notification(R.drawable.plug_3gp,
	                getString(R.string.notification_3gp_text),
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
		    
		      PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		      
		      N.setLatestEventInfo(this, 
		    		  			   getString(R.string.notification_3gp_title), 
		    		  			   getString(R.string.notification_3gp_text),
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
		
		initialize3gp(intent);
		
		startThreeGpRecording();
		
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopThreeGpRecording();
		stopForegroundCompat(NOTIFICATION_ID);
	}
	
	private void initialize3gp(Intent intent) {
		
		lNotif = mNotification();
		mInitNotification(lNotif);
		
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
		
		audioFile = intent.getStringExtra("FileName");		
		Console.print_debug(audioFile);
		
		if(audioRecorder == null) {
			audioRecorder = new MediaRecorder();
		}
	}
	
	/**
	 * Handle the 3GP Format Recording :param: mAudioSource :type: int
	 * :possibilities: MediaRecorder.AudioSource { DEFAULT || MIC || (sure for
	 * almost all devices) VOICE_UPLINK || VOICE_DOWNLINK || VOICE_CALL ||
	 * CAMCORDER || VOICE_RECOGNITION || VOICE_COMMUNICATION || }
	 */

	private void startThreeGpRecording() {
		
		try {
			audioRecorder.reset();
		}
		catch (Exception e) {}

		try {

			audioRecorder.setAudioSource(1);
			audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		} catch (IllegalStateException e) {

			Console.print_exception(e);
		}

		try {
			audioRecorder.setOutputFile(audioFile);
		} catch (IllegalStateException e) {

			Console.print_exception(e);
		}

		try {
			audioRecorder.prepare();
		} catch (IOException e) {
			Console.print_exception(e);
			
		} catch (IllegalStateException e) {
			Console.print_exception(e);
		}

		try {
			audioRecorder.start();
		} catch (IllegalStateException e) {
			Console.print_exception(e);
		}

	}
	
	/**
	 * Stop the audio recording state
	 * 
	 * @param reset
	 * @param release
	 */
	private void stopThreeGpRecording() {
		if (audioRecorder != null) {
			
			try {
				audioRecorder.reset();
			}
			catch (Exception e) {
				Console.print_exception(e);
			}
			
			try {
				audioRecorder.stop();
			}
			catch (Exception e) {
				Console.print_exception(e);
			}
			
			try {				
				releaseThreeGpRecording();
			} catch (Exception e) {
				Console.print_exception(e);
			}

		}
	}

	/**
	 * Release the Audio Ressource
	 */
	public void releaseThreeGpRecording() {
		audioRecorder.release();
		audioRecorder = null;
	}
}
