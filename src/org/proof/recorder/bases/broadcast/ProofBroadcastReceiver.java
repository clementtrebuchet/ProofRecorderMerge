package org.proof.recorder.bases.broadcast;

import org.proof.recorder.Settings;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ProofBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		setConsoleTagName();
		setStaticsContext(context);
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
