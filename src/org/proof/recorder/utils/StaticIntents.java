package org.proof.recorder.utils;

import java.util.List;

import org.proof.recorder.ProofRecorderActivity;
import org.proof.recorder.fragment.contacts.FragmentListPhoneContactsTabs;
import org.proof.recorder.fragment.phone.FragmentListRecordFoldersTabs;
import org.proof.recorder.fragment.voice.FragmentListVoiceTabs;
import org.proof.recorder.fragment.voice.FragmentVoiceMediaRecorder;
import org.proof.recorder.preferences.SettingsTabs;
import org.proof.recorder.syncron.fragment.SyncronUi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

public final class StaticIntents extends Intent {
	
	private static StaticIntents mIntent;
	
	private static StaticIntents getIntent(){
		mIntent = new StaticIntents();		
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);	
		return mIntent;
	}
	
	public static StaticIntents create(Context origin, Class<?> destination)
	{
		mIntent = getIntent();
		mIntent.setClass(origin, destination);
		return mIntent;
	}
	
	public static StaticIntents create(Context origin, Class<?> destination, Bundle b)
	{
		mIntent = getIntent();
		mIntent.putExtras(b);
		mIntent.setClass(origin, destination);
		return mIntent;
	}
	
	public static StaticIntents goHome(Context origin)
	{
		return create(origin, ProofRecorderActivity.class);
	}
	
	public static StaticIntents goSettings(Context origin)
	{
		return create(origin, SettingsTabs.class);
	}
	
	public static StaticIntents goPhone(Context origin)
	{
		return create(origin, FragmentListRecordFoldersTabs.class);
	}
	
	public static StaticIntents goVoice(Context origin)
	{
		return create(origin, FragmentListVoiceTabs.class);
	}
	
	public static StaticIntents goContactsList(Context origin)
	{
		return create(origin, FragmentListPhoneContactsTabs.class);
	}
	
	public static StaticIntents goVoiceRecorder(Context origin)
	{
		return create(origin, FragmentVoiceMediaRecorder.class);
	}
	
	public static StaticIntents goPerfsDashBoard(Context origin) 
	{
		return create(origin, SyncronUi.class);
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	        packageManager.queryIntentActivities(intent,
	            PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	private StaticIntents() {}

}
