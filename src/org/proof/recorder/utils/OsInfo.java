package org.proof.recorder.utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.util.Stack;

public class OsInfo {
	
	private static final String TAG = "OS INFOS";
	private static String mExternalPath;
	private static String mAppPath;
	
	public OsInfo() {
		mExternalPath = Environment.getExternalStorageDirectory()
	            .getPath();
		
		mAppPath = Environment.getExternalStorageDirectory()
	            .getPath() + "/ProofRecorder/";
	}
	
	public static String getBaseNameWithNoExt(String mPath) {		
		
		File tFile = new File(mPath);
		return tFile.getName().split("\\.(?=[^\\.]+$)")[0];		
	}
	
	public static String newFileName(String AudioFormat) {		
		
		String externalStorage = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		
		String appBaseStorage = externalStorage + "/proofRecorder/voices/";
		
		String format;
		if(!AudioFormat.equalsIgnoreCase("3gp") && !AudioFormat.equalsIgnoreCase("wav"))
			format = "wav";
		else 
			format = AudioFormat;
		
		return appBaseStorage + format + "/" + 
			   DateUtils.getCurrentMsDate() + "." + AudioFormat;		
	}
	
	public static String getFileSize(String file) {		
		return Long.valueOf(new File(file).length()).toString();		
	}
	
	private String getFreeSpace(String mPath) {
		long availableSpace = -1L;
		
		try {
	    	StatFs stat = new StatFs(mPath);
	        stat.restat(mPath);
	        availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
	    } catch (Exception e) {
	        Log.e(TAG, "getFreeSpaceOnDevice()->" + e);
	    }

	    return ServiceAudioHelper.transByteToKo(availableSpace + "");
	}
	
	private String getSizeOfDirectory(String mPath) {
		long result = 0;

	    Stack<File> dirlist= new Stack<File>();
	    dirlist.clear();

	    dirlist.push(new File(mPath));

	    while(!dirlist.isEmpty())
	    {
	        File dirCurrent = dirlist.pop();

	        File[] fileList = dirCurrent.listFiles();
			for (File aFileList : fileList) {

				if (aFileList.isDirectory())
					dirlist.push(aFileList);
				else
					result += aFileList.length();
			}
		}

	    return ServiceAudioHelper.transByteToKo(result + "");
	}

	public String getFreeSpaceOnExternalDevice() {		
	    return getFreeSpace(mExternalPath);
	}

	public String getSpaceConsumedByApp() {
		return getSizeOfDirectory(mAppPath);
	}

}
