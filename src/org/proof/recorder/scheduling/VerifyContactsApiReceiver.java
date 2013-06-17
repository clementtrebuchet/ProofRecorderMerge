package org.proof.recorder.scheduling;

import org.proof.recorder.bases.broadcast.ProofScheduledBroadcastReceiver;
import org.proof.recorder.service.VerifyContactsApi;


/*  Update Deleted Contacts to the list of contacts
 *  in Excluded and not Excluded Contacts list.
 *  Put those deleted Known Contacts in the appropriated
 *  Tab.
 **/

public class VerifyContactsApiReceiver extends ProofScheduledBroadcastReceiver {

	@Override
	protected void handleJobInfo() {
		this.requestCode = 0x01;
		this.service = VerifyContactsApi.class;
	}

}
