package org.proof.recorder.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OsHandler {

	private static List<String> filesListVoices, filesListCalls;
	
	/**
	 * @param message
	 */
	private static void print(String message) {
		if(Settings.isDebug())
			Log.d(OsHandler.class.getName(), "" + message);
		else
			Log.i(OsHandler.class.getName(), "" + message);
	}

	/**
	 * @param message
	 */
	private static void print_exception(String message) {
		Log.e(OsHandler.class.getName(), "" + message);
	}

	/**
	 * 
	 */
	private OsHandler() {
		print("Initialisation ...");
	}

	/**
	 * @param mFileName
	 * @throws IOException
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void deleteFileFromDisk(String mFileName) {
		File file = new File(mFileName);
		file.delete();
	}
	
	/**
	 * 
	 */
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
	        			print("Voice file: " + f.getPath());
	        		}
	        		else if (f.isFile() && (i == 5 || i == 6))
	        		{
	        			filesListCalls.add(f.getPath());
	        			print("Call file: "	+ f.getPath());
					}
				}
			}
		}		
	}
	
	/**
	 * 
	 * @param absolutePath the system absolute path to be evaluated.
	 */
	private static void evaluateContext(String absolutePath) {
		
		int count = PersonnalProofContentProvider.isRecordInDb(
				absolutePath
		);
		
		if(count <= 0)
		{
			deleteFileFromDisk(absolutePath);
			print("deleteFileFromDisk(): " + absolutePath);
		}
	}

	/**
	 * 
	 */
	private static void makeAppDirectoriesEqualsToDb() {
		
		listAppDirs();
		
		for (String absolutePath : filesListCalls) {			
			evaluateContext(absolutePath);
		}
		
		for (String absolutePath : filesListVoices) {
			evaluateContext(absolutePath);
		}
	}
	
	/**
	 * @param mContext
	 * @param uri
	 */
	private static void deleteFromUri(Context mContext, Uri uri) {
		try {
			mContext.getContentResolver().delete(uri, null, null);
		} catch (Exception e) {
			print_exception(e.getMessage());
		}
	}

	/**
	 * @param mContext
	 */
	public static void checkDirectoriesStructureIntegrity(Context mContext) {

		File tmp;
		String mFile;

		List<Record> calls = PersonnalProofContentProvider
				.getRecordsFilesList(Settings.mType.CALL);
		
		for (Record call : calls) {
			mFile = call.getmFilePath();
			tmp = new File(mFile);
			if (!tmp.isFile()) {
				Uri uri = PersonnalProofContentProvider.deleteItem(
						"record_id/", call.getmId().trim());
				deleteFromUri(mContext, uri);
			}
		}

		List<Record> voices = PersonnalProofContentProvider
				.getRecordsFilesList(Settings.mType.VOICE_TITLED);
		
		for (Record voice : voices) {
			mFile = voice.getmFilePath();
			tmp = new File(mFile);
			if (!tmp.isFile()) {
				Uri uri = PersonnalProofContentProvider.deleteItem("voice_id/",
						voice.getmId());
				deleteFromUri(mContext, uri);
			}
		}
		
		if(Settings.isDebug())
			makeAppDirectoriesEqualsToDb();
	}

}
