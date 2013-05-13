package org.proof.recorder.bases.activity;

import org.proof.recorder.bases.utils.SetStaticContext;

import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class ProofPreferenceActivity extends SherlockPreferenceActivity {
	
	private Context internalContext = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initialize();
	}
	
	protected void initialize() {
		SetStaticContext.setConsoleTagName(this.getClass().getSimpleName());
		SetStaticContext.setStaticsContext(this, 1);
		setInternalContext(this);
	}

	/**
	 * @return the internalContext
	 */
	protected Context getInternalContext() {
		return internalContext;
	}

	/**
	 * @param internalContext the internalContext to set
	 */
	private void setInternalContext(Context internalContext) {
		this.internalContext = internalContext;
	}
}
