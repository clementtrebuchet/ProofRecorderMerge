package org.proof.recorder.scheduling;

import java.util.Calendar;

import org.proof.recorder.bases.broadcast.ProofBroadcastReceiver;
import org.proof.recorder.service.VerifyContactsApi;
import org.proof.recorder.utils.Log.Console;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


/*  Update Deleted Contacts to the list of contacts
 *  in Excluded and not Excluded Contacts list.
 *  Put those deleted Known Contacts in the appropriated
 *  Tab.
 **/

public class VerifyContactsApiReceiver extends ProofBroadcastReceiver {
	
	private static int requestCode = 0x01;
	
	private static String mMessage = "Receiving VerifyContactsApi broadcast ...";
	
	public static void handleAlarmStateChange(final Context mContext) {		
		
		Calendar updateTime = Calendar.getInstance();
		
		AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent  = new Intent(mContext, VerifyContactsApiReceiver.class); 
		
		PendingIntent sender = PendingIntent.getBroadcast(
				mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		am.cancel(sender);
		
		updateTime.add(Calendar.DAY_OF_WEEK, 1);
		
		am.set(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), sender);
	}	

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		Console.print(mMessage);   	
        
        Intent mIntent = new Intent(context, VerifyContactsApi.class);
        
        // start the intent
        context.startService(mIntent);
        
        handleAlarmStateChange(context);
	}

}
