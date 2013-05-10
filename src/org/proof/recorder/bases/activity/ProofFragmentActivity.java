package org.proof.recorder.bases.activity;


import org.proof.recorder.Settings;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ProofFragmentActivity  extends SherlockFragmentActivity {

	@Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setConsoleTagName();
        setStaticsContext();
    }
	
	@Override
	public void onResume() {
		super.onResume();
		setConsoleTagName();
		setStaticsContext();
	}
	
	private void setConsoleTagName() {
		Console.setTagName(this.getClass().getSimpleName());
	}
	
	private void setStaticsContext() {
		
		if(!QuickActionDlg.hasContext()) {
			QuickActionDlg.setmContext(this);
		}
		
		if(!Settings.hasContext()) {
			Settings.setSettingscontext(this);
		}

		if(!QuickActionDlg.hasContext()) {
			AlertDialogHelper.setContext(this);
		}		
	}
}
