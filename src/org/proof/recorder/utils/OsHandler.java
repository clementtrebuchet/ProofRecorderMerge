package org.proof.recorder.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class OsHandler {

	private static final String TAG = "OsHandler";
	
	private static List<Record> calls, voices;
	private static List<String> filesListVoices, filesListCalls;

	private OsHandler() {

	}

	public static void deleteFileFromDisk(String mFileName) throws IOException {
		File file = new File(mFileName);
		file.delete();
	}
	
	private static void listAppDirs() {
		
		filesListVoices = new ArrayList<String>();
		filesListCalls = new ArrayList<String>();
		
		for (int i = 3; i< Settings.DEFAULT_FILE_PATHS.length; i++)
		{
			File file = new File(Settings.DEFAULT_FILE_PATHS[i]);
	        File[] files = file.listFiles();
	        if (files != null) 
	        {
	        	for(File f : files) {
	        		
	        		if(f.isFile() && (i == 3 || i == 4))
	        		{
	        			filesListVoices.add(f.getPath());
	        			if (Settings.isDebug())
		        			Log.e(TAG,
									"listAppDirs()->files: "
											+ f.getPath());
	        		}
	        		else if (f.isFile() && (i == 5 || i == 6))
	        		{
	        			filesListCalls.add(f.getPath());
	        			if (Settings.isDebug())
		        			Log.e(TAG,
									"listAppDirs()->files: "
											+ f.getPath());
	        		}
	        		else
	        			continue;
	        	}
	        }
		}		
	}

	private static void makeAppDirectoriesEqualsToDb() {
		
		listAppDirs();
		
		for (String absolutePath : filesListCalls) {
			if(PersonnalProofContentProvider.isRecordInDb(
					absolutePath, 
					Settings.mType.CALL) == -1 &&
					PersonnalProofContentProvider.isRecordInDb(
							absolutePath, 
							Settings.mType.CALL) == 0
			)
			{
				try {
					deleteFileFromDisk(absolutePath);
					if (Settings.isDebug())
						Log.e(TAG,
								"deleteFileFromDisk(): "
										+ absolutePath);
				} catch (IOException e) {
					if (Settings.isDebug())
						Log.e(TAG,
								"checkDirectoriesStructureIntegrity()->calls: "
										+ e.getMessage());
				}
			}
		}
		
		for (String absolutePath : filesListVoices) {
			if(PersonnalProofContentProvider.isRecordInDb(
					absolutePath, 
					Settings.mType.VOICE_TITLED) == -1 &&
					PersonnalProofContentProvider.isRecordInDb(
							absolutePath, 
							Settings.mType.VOICE_TITLED) == 0
			)
			{
				try {
					deleteFileFromDisk(absolutePath);
				} catch (IOException e) {
					if (Settings.isDebug())
						Log.e(TAG,
								"checkDirectoriesStructureIntegrity()->calls: "
										+ e.getMessage());
				}
			}
		}
	}
	
	private static void deleteFromUri(Context mContext, Uri uri) {
		try {
			mContext.getContentResolver().delete(uri, null, null);
		} catch (Exception e) {
			if (Settings.isDebug())
				Log.e(TAG, e.getMessage());
		}
	}

	public static void checkDirectoriesStructureIntegrity(Context mContext) {
		File tmp = null;
		String mFile = "";
		calls = PersonnalProofContentProvider
				.getRecordsFilesList(Settings.mType.CALL);
		for (Record call : calls) {
			mFile = call.getmFilePath().trim();
			tmp = new File(mFile);
			if (!tmp.isFile()) {
				Uri uri = PersonnalProofContentProvider.deleteItem(
						"record_id/", call.getmId().trim());
				deleteFromUri(mContext, uri);
			}
		}

		voices = PersonnalProofContentProvider
				.getRecordsFilesList(Settings.mType.VOICE_TITLED);
		for (Record voice : voices) {
			mFile = voice.getmFilePath().trim();
			tmp = new File(mFile);
			if (!tmp.isFile()) {
				Uri uri = PersonnalProofContentProvider.deleteItem("voice_id/",
						voice.getmId().trim());
				deleteFromUri(mContext, uri);
			}
		}
		
		if(Settings.isDebug())
			makeAppDirectoriesEqualsToDb();
	}

}
