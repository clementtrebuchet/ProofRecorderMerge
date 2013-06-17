package org.proof.recorder.bases.broadcast;

import java.util.Calendar;

import org.proof.recorder.utils.DateUtils;
import org.proof.recorder.utils.Log.Console;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public abstract class ProofScheduledBroadcastReceiver extends ProofBroadcastReceiver {

	protected int numOfScheduledDays = 1;
	
	protected int requestCode = 0;	
	protected Class<?> service = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		handleJobInfo();
		
		try {
			this.startService();
		} catch (ProofBroadcastReceiverException e) {
			Console.print_exception(e);
		}
		
		try {
			this.scheduleNextCall();
		} catch (ProofBroadcastReceiverException e) {
			Console.print_exception(e);
		}
	}
	
	protected abstract void handleJobInfo();

	protected void startService() throws ProofBroadcastReceiverException {
		
		if(service == null) {
			throw new ProofBroadcastReceiverException(
					"You must override << protected Class<?> service >> " +
					"with the service class you want to run!");
		}
		
		Console.print(String.format("Starting service: %s", service.getName()));
		Intent mIntent = new Intent(getInternalContext(), service);
		getInternalContext().startService(mIntent);
	}
	
	protected void scheduleNextCall() throws ProofBroadcastReceiverException {
		
		if(requestCode == 0) {
			throw new ProofBroadcastReceiverException(
					"You must provide a complex request (ie. 0x12345678) code to avoid collisions!");
		}
		
		// We schedule next run to 'numOfScheduledDays' day(s) from now.
		Calendar nextSchedule = DateUtils.scheduleTimeFromNow(numOfScheduledDays);
		
		AlarmManager am = (AlarmManager) getInternalContext().getSystemService(Context.ALARM_SERVICE);
		
		Intent intent  = new Intent(getInternalContext(), this.getClass()); 
		
		PendingIntent sender = PendingIntent.getBroadcast(getInternalContext(), 
														  requestCode, 
														  intent, 
														  PendingIntent.FLAG_UPDATE_CURRENT);
		
		am.cancel(sender);		
		am.set(AlarmManager.RTC_WAKEUP, nextSchedule.getTimeInMillis(), sender);
	}
}
