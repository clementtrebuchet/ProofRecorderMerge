package org.proof.recorder.services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.IBinder;

import org.proof.recorder.Settings;
import org.proof.recorder.bases.service.ProofFrontService;
import org.proof.recorder.database.collections.RecordsList;

@SuppressLint("Registered")
public class AutoCleaningService extends ProofFrontService {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void _onStartCommand(Intent intent, int flags, int startId) {
		
		// get the config var.
		String periodic = Settings.getPersistantData("auto_clean");
		
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
