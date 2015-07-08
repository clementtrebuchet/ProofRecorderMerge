package org.proof.recorder.bases.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.proof.recorder.bases.utils.SetStaticContext;

public class ProofBroadcastReceiver extends BroadcastReceiver {
	
	protected class ProofBroadcastReceiverException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1925333564252642300L;
		
		public ProofBroadcastReceiverException(String detailMessage)
	    {
	        super(detailMessage);
	    }
		
	}
	
	private Context internalContext = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		initialize(context);
	}

	private void initialize(Context context) {
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
