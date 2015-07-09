package org.proof.recorder.bases.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import org.proof.recorder.R;
import org.proof.recorder.bases.utils.SetStaticContext;

@SuppressLint("Registered")
public class ProofPreferenceActivity extends SherlockPreferenceActivity {
	
	private Context internalContext = null;
	private volatile boolean screenLocked = false;

	private ProgressDialog progressDlg;
	private int progressDlgText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initialize();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		this.initialize();
	}

	private void initialize() {
		SetStaticContext.setConsoleTagName(this.getClass().getSimpleName());
		SetStaticContext.setStaticsContext(this, 1);
		this.setInternalContext(this);
		this.setupProgressDlg();
	}

	/**
	 * @return the internalContext
	 */
	protected Context getInternalContext() {
		return this.internalContext;
	}

	/**
	 * @param internalContext the internalContext to set
	 */
	private void setInternalContext(Context internalContext) {
		this.internalContext = internalContext;
	}
	
	private void setupProgressDlg() {
		
		this.destroyProgress();
		
		this.progressDlg = new ProgressDialog(this.getInternalContext());
		
		this.progressDlg.setCancelable(false);
		this.progressDlg.setIndeterminate(true);
		
		this.progressDlg.setMessage(
				this.getInternalContext().getText(
						this.progressDlgText == 0 ? R.string.loading : this.progressDlgText));
	}

	void displayProgress() {
		if(!this.progressDlg.isShowing())
			this.progressDlg.show();
	}

	void hideProgress() {
		if(this.progressDlg.isShowing())
			this.progressDlg.hide();
	}
	
	private void destroyProgress() {
		if(this.progressDlg != null) {
			this.hideProgress();
			this.progressDlg.cancel();
			this.progressDlg = null;
		}
	}

	@SuppressLint("InlinedApi")
	void lockScreenOrientation() {

		if(!this.screenLocked) {
			switch (this.getResources().getConfiguration().orientation) {

			case Configuration.ORIENTATION_PORTRAIT:
				if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
					this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else {
					int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
					if(rotation == android.view.Surface.ROTATION_90|| rotation == android.view.Surface.ROTATION_180){
						this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
					} else {
						this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					}
				}   
				break;

			case Configuration.ORIENTATION_LANDSCAPE:
				if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
					this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else {
					int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
					if(rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90){
						this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					} else {
						this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
					}
				}
				break;
			}

			this.screenLocked = true;
		}	
	}

	void unlockScreenOrientation() {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		this.screenLocked = false;
	}
}
