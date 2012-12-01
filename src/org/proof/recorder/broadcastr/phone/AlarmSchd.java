package org.proof.recorder.broadcastr.phone;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.proof.recorder.R;
import org.proof.recorder.service.ServiceSchd;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;


public class AlarmSchd extends BroadcastReceiver 
{    
	private static final String TAG = "AlarmSchd";
	private static int requestCode = 0x06;
	private static boolean manyDate;
	static int[] reps ;
	static int Rep;
	static String jour;
	public static void HandleAlarmStateChange(Context context)
    {
    	//SharedPreferences settings = getPreferences(0);		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String time = settings.getString("timePreference", "0");
		
		String[] temp = time.split(":");
    	int Hour = Integer.parseInt(temp[0]);
    	int Minute = Integer.parseInt(temp[1]);
    	Log.d(TAG, "HEURE :"+Hour+" MINUTE : "+Minute);
    	jour = settings.getString("DayRep", "0");
    	try{
    		
    		Rep = Integer.parseInt(jour);
    		manyDate = false;
    		Log.d(TAG, "HEURE :"+Hour+" MINUTE : "+Minute+" JOUR : "+Rep);
    	} catch (NumberFormatException e){
    		String[] jours  = jour.split("-");
    		reps = new int[jours.length];
    		Log.d(TAG, "JOURS :"+jour+" JOUR : "+jours[1]);
    		manyDate = true;
    		for(int i = 0; i < jours.length; i++){
    			reps[i] = Integer.parseInt(jours[i]);
    			Log.d(TAG, "HEURE :"+Hour+" MINUTE : "+Minute+" JOUR : "+reps[i]);
    		}
    		
    	}
    	
    	boolean AlarmEnable = settings.getBoolean("syncperiod", false);
    	AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    	
    	
    	    	
    	if (AlarmEnable == true && manyDate == false )
    	{
    	Log.d(TAG,"Alarm enabled one date");
    	//Calculate when the next alarm should go off
    	
    	//First check which day it is
    	Calendar CurrDateTime = new GregorianCalendar();
    	Calendar NextAlarmTime = new GregorianCalendar();
    	
    	NextAlarmTime.set(Calendar.HOUR_OF_DAY, Hour);
    	NextAlarmTime.set(Calendar.MINUTE, Minute);
    	NextAlarmTime.set(Calendar.SECOND, 0);
    	
    	//Check which day of the week it is
    	int WeekDay = CurrDateTime.get(Calendar.DAY_OF_WEEK);
    	int WeekDayAdjust = ((WeekDay+5) % 7); //Adjust so that monday is 0, tuesday 1 and so on  
    	Log.d(TAG, "WeekDayAdjust : "+WeekDayAdjust+" WeekDay : "+WeekDay);
    	boolean FoundAlarm = false;
    	
    	//Check if the alarm should be active today
    	if (Rep == WeekDayAdjust &&
    	   (NextAlarmTime.compareTo(CurrDateTime) > 0))
    	{
    		//The alarm should be activated this
    		//day at the time set by NextAlarmTime    		    		
    		FoundAlarm = true;
    	}
    	else
    	{
    		//The next alarmtime is the first day after today that 
    		//the alarm is active. Find that day
    		int DayCount = 1;
    		NextAlarmTime.add(Calendar.DAY_OF_MONTH, 1);
	    		while(true)
	    		{    		
		    		WeekDay = NextAlarmTime.get(Calendar.DAY_OF_WEEK);
		        	WeekDayAdjust = ((WeekDay+5) % 7); //Adjust so monday is 0, tuesday 1 and so on
		        	Log.e(TAG, "OPERATION : "+(Rep & (1<<WeekDayAdjust)));
		        	if (Rep == WeekDayAdjust)
		        	{
		        		//Found the day
		        		FoundAlarm = true;
		        		break;
		        	}
		        	
		    		if (DayCount >= 7)
		    			break;
		    		NextAlarmTime.add(Calendar.DAY_OF_MONTH, 1);
	    		}
    	}
    	    	
    		Intent intent  = new Intent(context, AlarmSchd.class);
    		
    		
    		PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    		
    		//Cancel the current alarm since it may have been changed
    		am.cancel(sender);
    		
	    	if (FoundAlarm)
	    	{
	    		Log.d(TAG,"Next alarm will be " + NextAlarmTime.getTime());	    		
	    		Resources res = context.getResources();
	    		String[] items = res.getStringArray(R.array.days_options);
	    		Log.d(TAG, "New alarm set on :" + items[WeekDayAdjust]);
	    		am.set(AlarmManager.RTC_WAKEUP, NextAlarmTime.getTimeInMillis(), sender); // to be alerted 30 seconds from now
	    	}	    	
	    		    
    	} else if (AlarmEnable == true && manyDate == true ){
    		Log.d(TAG,"Alarm enabled whith manydate");
        	//Calculate when the next alarm should go off
        	
        	//First check which day it is
        	Calendar CurrDateTime = new GregorianCalendar();
        	Calendar NextAlarmTime = new GregorianCalendar();
        	
        	NextAlarmTime.set(Calendar.HOUR_OF_DAY, Hour);
        	NextAlarmTime.set(Calendar.MINUTE, Minute);
        	NextAlarmTime.set(Calendar.SECOND, 0);
        	
        	//Check which day of the week it is
        	int WeekDay = CurrDateTime.get(Calendar.DAY_OF_WEEK);
        	int WeekDayAdjust = ((WeekDay+5) % 7); //Adjust so that monday is 0, tuesday 1 and so on  
        	Log.d(TAG, "WeekDayAdjust : "+WeekDayAdjust+" WeekDay : "+WeekDay);
        	boolean FoundAlarm = false;
        	
        	for(int i=0; i < reps.length; i++){
        	//Check if the alarm should be active today
	        	if (reps[i] == WeekDayAdjust &&
	        	   (NextAlarmTime.compareTo(CurrDateTime) > 0))
	        	{
	        		//The alarm should be activated this
	        		//day at the time set by NextAlarmTime    		    		
	        		FoundAlarm = true;
	        	}
	        	else
	        	{
	        		//The next alarmtime is the first day after today that 
	        		//the alarm is active. Find that day
	        		int DayCount = 1;
	        		NextAlarmTime.add(Calendar.DAY_OF_MONTH, 1);
	    	    		while(true)
	    	    		{    		
	    		    		WeekDay = NextAlarmTime.get(Calendar.DAY_OF_WEEK);
	    		        	WeekDayAdjust = ((WeekDay+5) % 7); //Adjust so monday is 0, tuesday 1 and so on
	    		        	Log.e(TAG, "OPERATION : "+(reps[i] & (1<<WeekDayAdjust)));
	    		        	if (reps[i] == WeekDayAdjust)
	    		        	{
	    		        		//Found the day
	    		        		FoundAlarm = true;
	    		        		break;
	    		        	}
	    		        	
	    		    		if (DayCount >= 7)
	    		    			break;
	    		    		NextAlarmTime.add(Calendar.DAY_OF_MONTH, 1);
	    	    		}
	        	}
        	    	
        			Intent intent  = new Intent(context, AlarmSchd.class);
        		
        		
        			PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        		
        			//Cancel the current alarm since it may have been changed
        			am.cancel(sender);
        		
        			if (FoundAlarm)
        			{
	    	    		Log.d(TAG,"Next alarm will be " + NextAlarmTime.getTime());	    		
	    	    		Resources res = context.getResources();
	    	    		String[] items = res.getStringArray(R.array.days_options);
	    	    		Log.d(TAG, "New alarm set on :" + items[WeekDayAdjust]);
	    	    		am.set(AlarmManager.RTC_WAKEUP, NextAlarmTime.getTimeInMillis(), sender); // to be alerted 30 seconds from now
        			}
        		}
    		
    		}
    	else
    		{
	    		Log.d(TAG,"Alarm diabled");
	        	Intent intent = new Intent(context, AlarmSchd.class);
	        	PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
	        	am.cancel(sender);
        	
    	}
    }
	
	@Override
	public void onReceive(Context con, Intent in) {
			Log.d(TAG,"Alarm received");					
			Intent ServiceIntent = new Intent(con, ServiceSchd.class);
        	//ServiceIntent.addFlags(ServiceSchd.ALARM_START);    
        	//Log.d(SRPlayer.TAG,"Alarm receivde. " + in.getIntExtra("AlarmStationID",132) + " " + in.getStringExtra("AlarmStationName") + " " + in.getStringExtra("AlarmStationURL"));
        	con.startService(ServiceIntent);
        	
        	HandleAlarmStateChange(con);
	}
}
