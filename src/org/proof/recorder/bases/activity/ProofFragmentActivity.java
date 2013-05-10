package org.proof.recorder.bases.activity;


import org.proof.recorder.Settings;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.QuickActionDlg;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ProofFragmentActivity  extends SherlockFragmentActivity {

	@Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);		
        setStaticsContext();
    }
	
	@Override
	public void onResume() {
		super.onResume();
		setStaticsContext();
	}
	
	private void setStaticsContext() {
		QuickActionDlg.setmContext(this);
		Settings.setSettingscontext(this);
		AlertDialogHelper.setContext(this);
	}
}
