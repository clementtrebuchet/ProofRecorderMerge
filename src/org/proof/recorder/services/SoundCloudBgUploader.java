package org.proof.recorder.services;

import java.io.IOException;

import org.proof.recorder.R;
import org.proof.recorder.apis.soundcloud.FileUploader;
import org.proof.recorder.bases.service.ProofFrontService;

import android.content.Intent;

public class SoundCloudBgUploader extends ProofFrontService {
	
	protected static final int NOTIFICATION_ID = 552889171;
	
	protected int notifIcon = R.drawable.icon_soundcloud;
	protected int notifText = R.string.sc_upload_file_msg;
	
	private String[] recordPaths = null;
	
	private Thread uploadingThread = null;
	private UploadingRunnable uploadingRunnable = null;

	private int selectedCount = 0;
	
	class UploadingRunnable implements Runnable {
		
		// set this to true to stop the thread
		volatile boolean canceled = false;

		@Override
		public void run() {
			
			final FileUploader uploader = new FileUploader(SoundCloudBgUploader.this);
			
			while (!canceled) {
				for(int index=0; index < selectedCount; index++) {					
					singleSoundCShare(uploader, index);					
				}
				
				this.canceled = true;
				stopSelf();
		    }
		}		
	}

	@Override
	protected void _onStartCommand(Intent intent, int flags, int startId) {
		
		selectedCount = intent.getExtras().getInt("selectedCount");
		recordPaths = intent.getExtras().getStringArray("recordPaths");
		
		uploadingRunnable = new UploadingRunnable();		
		uploadingThread = new Thread(uploadingRunnable, "sc_files_uploader_thread");

		uploadingThread.start();

	}

	@Override
	protected void _onDestroy() {
		uploadingRunnable.canceled = true;
		try {
			uploadingThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}

	}
	
	private void singleSoundCShare(final FileUploader uploader, final int index) {		
		
		final String song = recordPaths[index];	
		
		try {
			uploader.upload(song);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
