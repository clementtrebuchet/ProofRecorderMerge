package org.proof.recorder.services;

import org.proof.recorder.Settings;
import org.proof.recorder.bases.service.ProofFrontService;
import org.proof.recorder.database.collections.RecordsList;

import android.content.Intent;
import android.os.IBinder;

public class AutoCleaningService extends ProofFrontService {
	
	private String periodic = "NEVER";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void _onStartCommand(Intent intent, int flags, int startId) {
		
		// get the config var.
		periodic = Settings.getPersistantData("auto_clean");
		
		// Instantiate Collection.
		RecordsList collection = new RecordsList();
		
		// trigger autoClean.
		collection.autoClean(periodic);
	}

	@Override
	protected void _onDestroy() {
		// Nothing to do.
	}

}
