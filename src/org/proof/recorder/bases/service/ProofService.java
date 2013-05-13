package org.proof.recorder.bases.service;

import org.proof.recorder.bases.utils.SetStaticContext;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ProofService extends Service {
	
	private Context internalContext = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		initialize();
	}
	
	protected void initialize() {
		SetStaticContext.setConsoleTagName(this.getClass().getSimpleName());
		SetStaticContext.setStaticsContext(this, 0);
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
