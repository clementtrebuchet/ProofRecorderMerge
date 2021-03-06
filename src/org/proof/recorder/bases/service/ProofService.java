package org.proof.recorder.bases.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import org.proof.recorder.bases.utils.SetStaticContext;

@SuppressLint("Registered")
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

	private void initialize() {
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
