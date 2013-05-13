package org.proof.recorder.bases.broadcast;

import org.proof.recorder.bases.utils.SetStaticContext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ProofBroadcastReceiver extends BroadcastReceiver {
	
	private Context internalContext = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		initialize(context);
	}
	
	protected void initialize(Context context) {
		SetStaticContext.setConsoleTagName(this.getClass().getSimpleName());
		SetStaticContext.setStaticsContext(context, 0);
		setInternalContext(context);
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
