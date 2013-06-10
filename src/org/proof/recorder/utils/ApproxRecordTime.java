package org.proof.recorder.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.proof.recorder.adapter.phone.RecorderDetailAdapter;

import android.annotation.TargetApi;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;


/**
 * 
 * @author clement
 *
 */
public class ApproxRecordTime {
	
	private File mFile;
	private File[] mFiles;
	private MediaPlayer mPlayer;
	public static ArrayList<MSong> MSongs;
	private ArrayList<String> MSongsParam;
	private MSong mSong;
	private String TAG = ApproxRecordTime.class.getName();
	private Boolean TXTV;
	
	/**
	 * 
	 * @param song
	 */
	public ApproxRecordTime(File song){
		this.mFile = song;
		
	}
	public ApproxRecordTime(File song, Boolean txt){
		this.mFile = song;
		this.TXTV = txt;
		
	}
	public ApproxRecordTime(File[] songs){
		this.mFiles = songs;
		
	}
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String run() {
		
		if(MSongsParam == null){
			MSongsParam = new ArrayList<String>();
		}
		MSongsParam.add(this.mFile.getAbsolutePath());
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1){
			
			try {
				MSongsParam.add(getDuration());
			} catch (Exception e) {
				Log.e(TAG,"Exception:"+e.getMessage());
				System.exit(1);
			}
		} else {
			try {
				MSongsParam.add(oldGetDuration());
			} catch (IllegalArgumentException e) {
				Log.e(TAG,"IllegalArgumentException:"+e.getMessage());
				System.exit(1);
			} catch (IllegalStateException e) {
				Log.e(TAG,"IllegalStateException:"+e.getMessage());
				System.exit(1);
			} catch (IOException e) {
				Log.e(TAG,"IOException:"+e.getMessage());
				System.exit(1);
			}
		}
		
		this.mSong = new MSong(MSongsParam);
		if(MSongs == null){
			MSongs = new ArrayList<MSong>();
		}
		MSongs.add(this.mSong);
		if(this.TXTV){
			return this.mSong.getmDuration();
		}
		return this.mSong.getmDuration();
	}
	
	/**
	 * 
	 * @author clement
	 *
	 */
	public class MSong{
		
		private String mPath;
		private String mDuration;

		public String getmPath() {
			return mPath;
		}

		public void setmPath(String mPath) {
			this.mPath = mPath;
		}

		public String getmDuration() {
			return mDuration;
		}

		public void setmDuration(String mDuration) {
			this.mDuration = mDuration;
		}

		private MSong(ArrayList<String>...strings){
			for (ArrayList<String> st : strings){
				this.mPath = st.get(0);
				this.mDuration = st.get(1);
				
			}
			Log.v(TAG, "this.mPath:"+this.mPath);
			Log.v(TAG, "this.mDuration:"+this.mDuration);
		}
	
	}

	/**
	 * 
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	private String getDuration() {

		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		metaRetriever.setDataSource(this.mFile.getAbsolutePath());
		String out = "";

		String duration = metaRetriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		Log.v(TAG, duration);
		long dur = Long.parseLong(duration);
		String seconds = String.valueOf((dur % 60000) / 1000);
		Log.v(TAG, seconds);
		String minutes = String.valueOf(dur / 60000);
		Log.v(TAG, minutes);
		if (seconds.length() == 1) {
			out = "" +minutes + ":0" + seconds;
			Log.d(TAG, "seconds.length() == 1 " + minutes + ":0" + seconds);

		} else {
			out = "" +minutes + ":" + seconds;
			Log.d(TAG, "" + minutes + ":" + seconds);
		}
		Log.d(TAG, "out = " +out);
		// close object
		metaRetriever.release();
		return out;
	}
	/**
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private String oldGetDuration() throws IllegalArgumentException,
			IllegalStateException, IOException {
		mPlayer = new MediaPlayer();
		FileInputStream fs;
		FileDescriptor fd;
		fs = new FileInputStream(this.mFile);
		fd = fs.getFD();
		mPlayer.setDataSource(fd);
		mPlayer.prepare(); // might be optional
		int length = mPlayer.getDuration();
		mPlayer.release();
		mPlayer = null;
		String result = String.valueOf(length);
		Log.v(TAG, result);
		return result;
	}

}
