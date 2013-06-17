package org.proof.recorder.scheduling;

import org.proof.recorder.bases.broadcast.ProofScheduledBroadcastReceiver;
import org.proof.recorder.services.AutoCleaningService;

public class AutoCleaningReceiver extends ProofScheduledBroadcastReceiver {

	@Override
	protected void handleJobInfo() {
		
		this.requestCode = 0x056723;
		this.service = AutoCleaningService.class;
	}
}
