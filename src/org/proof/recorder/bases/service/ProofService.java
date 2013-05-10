package org.proof.recorder.bases.service;

import org.proof.recorder.Settings;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ProofService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		setConsoleTagName();
		setStaticsContext(this);
	}
	
	private void setConsoleTagName() {
		Console.setTagName(this.getClass().getSimpleName());
	}
	
	private void setStaticsContext(Context context) {
		
		if(!QuickActionDlg.hasContext()) {
			QuickActionDlg.setmContext(context);
		}
		
		if(!Settings.hasContext()) {
			Settings.setSettingscontext(context);
		}

		if(!QuickActionDlg.hasContext()) {
			AlertDialogHelper.setContext(context);
		}		
	}
}
