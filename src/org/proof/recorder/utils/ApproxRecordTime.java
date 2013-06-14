package org.proof.recorder.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;


/**
 * 
 * @author clement
 *
 */
public class ApproxRecordTime {
	
	private File mFile;
	private MediaPlayer mPlayer;
	public static ArrayList<MSong> MSongs;
	private ArrayList<String> MSongsParam;
	private MSong mSong;
	private String TAG = ApproxRecordTime.class.getName();
	private String mFormat;
	private int mEnd = 3;

	public String getmFormat() {
		return mFormat;
	}

	public void setmFormat(String mFormat) {
		this.mFormat = mFormat;
	}
	/**
	 * 
	 * @param song
	 */
	public ApproxRecordTime(File song){
		this.mFile = song;
		_getMFormat();
		
	}
	public ApproxRecordTime(File song, Boolean txt){
		this.mFile = song;
		_getMFormat();
		
	}
	
	private void _getMFormat(){
		int length = this.mFile.getAbsolutePath().length();
		if(length <= mEnd){
			setmFormat(this.mFile.getAbsolutePath());
		}
		int startIndex = length-mEnd;
		setmFormat(this.mFile.getAbsolutePath().substring(startIndex));
		Log.v(TAG, "this.mFormat:"+this.mFormat);
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
				
				try {
					MSongsParam.add(oldGetDuration());
				} catch (IllegalArgumentException e1) {
					Log.e(TAG,"IllegalArgumentException:"+e1.getMessage());
					
				} catch (IllegalStateException e1) {
					Log.e(TAG,"IllegalStateException:"+e1.getMessage());
					
				} catch (IOException e1) {
					Log.e(TAG,"IOException:"+e1.getMessage());
					
				}
				
			}
		}
		
		this.mSong = new MSong(MSongsParam);

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
		if (this.mFormat.equalsIgnoreCase("ogg")) {
			Log.e(TAG, "mFormat equal ogg, humm file goto MediaPlayer :(");
			try {
				String out = oldGetDuration();
				return out;
			} catch (IllegalArgumentException e) {
				Log.e(TAG, e.getMessage());
			} catch (IllegalStateException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		metaRetriever.setDataSource(this.mFile.getAbsolutePath());
		

		String duration = metaRetriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		Log.v(TAG, "" + duration);
		long dur = Long.parseLong(duration);
		String time = mParseTime(dur);
		// close object
		metaRetriever.release();
		return time;
	}
	private String mParseTime(long dur){
		String seconds = String.valueOf((dur % 60000) / 1000);
		Log.v(TAG, "" + seconds);
		String minutes = String.valueOf(dur / 60000) ;
		Log.v(TAG, "" + minutes);
		String out = "";
		if (seconds.length() == 1 && minutes.length() == 1) {
			out = "0" + minutes + ":0" + seconds;
			Log.d(TAG, "seconds.length() == 1 0" + minutes + ":0" + seconds);

		} else if (seconds.length() == 1 && minutes.length() > 1) {
			out = "" + minutes + ":0" + seconds;
			Log.d(TAG, "seconds.length() == 1 " + minutes + ":0" + seconds);

		} else if (seconds.length() > 1 && minutes.length() == 1) {
			out = "0" + minutes + ":" + seconds;
			Log.d(TAG, "minutes.length() == 1 0" + minutes + ":" + seconds);

		} else {
			out = "" + minutes + ":" + seconds;
			Log.d(TAG, "" + minutes + ":" + seconds);
		}
		
		 if (Integer.parseInt(minutes) >= 60) {
				int m = Integer.parseInt(minutes);
				int s = Integer.parseInt(seconds);
				out = convertToHours(m,s);

			} 
		Log.d(TAG, "out = " + out);
		return out;
	}
	/**
	 * 
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	private String convertToHours(int minutes, int seconds) {
		float hoursminutes = (float) minutes / 60;// 258(m)/60=4.3
		int hour = minutes / 60;// 258(m)/60 = 4
		float realMinutes = (float) (hoursminutes - Float.parseFloat(String
				.valueOf(hour))) * 60;// (4.3 - 4.0) * 60
		int minute = Math.round(realMinutes);
		int second = seconds;
		Log.d(TAG,
				"hoursminutes:" + hoursminutes
						+ " Float.parseFloat(String.valueOf(hour)): "
						+ Float.parseFloat(String.valueOf(hour)));
		if (hour < 10 && minute < 10 && second < 10) {
			return "0" + hour + ":0" + minute + ":0" + second + "";
		}

		if (hour < 10 && second < 10 && minute > 10) {

			return "0" + hour + ":" + minute + ":0" + second + "";
		}

		if (hour < 10 && second > 10 && minute < 10) {

			return "0" + hour + ":0" + minute + ":" + second + "";
		}

		if (hour < 10 && second > 10 && minute > 10) {

			return "0" + hour + ":" + minute + ":" + second + "";
		}
		//
		if (hour > 10 && minute < 10 && second < 10) {
			return "" + hour + ":0" + minute + ":0" + second + "";
		}

		if (hour > 10 && second < 10 && minute > 10) {

			return "" + hour + ":" + minute + ":0" + second + "";
		}

		if (hour > 10 && second > 10 && minute < 10) {

			return "" + hour + ":0" + minute + ":" + second + "";
		}

		if (hour > 10 && second > 10 && minute > 10) {

			return "" + hour + ":" + minute + ":" + second + "";
		}

		return " " + hour + ":" + minute + ":" + second + "";

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
		long dur = Long.valueOf(length);
		/*
		 * @avoid
		 * http://stackoverflow.com/questions/9609479/android-mediaplayer-
		 * went-away-with-unhandled-events
		 */
		mPlayer.reset();
		mPlayer.release();
		mPlayer = null;
		String result = mParseTime(dur);
		Log.v(TAG, "" + result);
		return result;
	}

}
