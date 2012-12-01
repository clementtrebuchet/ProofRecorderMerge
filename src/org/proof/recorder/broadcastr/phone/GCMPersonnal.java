package org.proof.recorder.broadcastr.phone;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMPersonnal extends GCMBroadcastReceiver {

	
	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		return getDefaultIntentServiceClassName(context);
	}

	static final String getDefaultIntentServiceClassName(Context context) {
		String className = context.getPackageName() + ".service.GCMIntentService";

		return className;
	}

	public GCMPersonnal() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
