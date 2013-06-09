package org.proof.recorder.bases.activity;

import org.proof.recorder.bases.utils.SetStaticContext;

import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ProofFragmentActivity  extends SherlockFragmentActivity {
	
	private Context internalContext = null;
	
	protected Bundle savedInstance;

	@Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        savedInstance = icicle;
        initialize();
    }
	
	@Override
	public void onResume() {
		super.onResume();
		initialize();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
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
