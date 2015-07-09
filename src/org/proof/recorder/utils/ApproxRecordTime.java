package org.proof.recorder.utils;

import android.annotation.TargetApi;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import org.proof.recorder.utils.Log.Console;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

// import java.util.ArrayList;


/**
 * 
 * @author clement
 *
 */
public class ApproxRecordTime {
	
	// public static ArrayList<MSong> MSongs;

	private final File mFile;
	// private ArrayList<String> MSongsParam;
	// private MSong mSong;
	private final String TAG = ApproxRecordTime.class.getName();
	private String mFormat;

	public String getmFormat() {
		return mFormat;
	}

	private void setmFormat(String mFormat) {
		this.mFormat = mFormat;
	}
	/**
	 * 
	 * @param song
	 */
	public ApproxRecordTime(File song) {
		this.mFile = song;
		_getMFormat();
		
	}
	

	
	private void _getMFormat() {
		
		int length = this.mFile.getAbsolutePath().length();
		int mEnd = 3;
		if(length <= mEnd){
			setmFormat(this.mFile.getAbsolutePath());
		}
		int startIndex = length - mEnd;
		setmFormat(this.mFile.getAbsolutePath().substring(startIndex));
		Log.v(TAG, "this.mFormat:"+this.mFormat);
		
	  }
	
	@SuppressWarnings("unused")
	private String getAndroidVersion() {
		
		
		String version = "UNKNOWN";

		switch (android.os.Build.VERSION.SDK_INT) {

		case Build.VERSION_CODES.BASE:
			version = "BASE";
			break;

		case Build.VERSION_CODES.BASE_1_1:
			version = "BASE_1_1";
			break;

		case Build.VERSION_CODES.CUPCAKE:
			version = "CUPCAKE";
			break;

		case Build.VERSION_CODES.CUR_DEVELOPMENT:
			version = "CUR_DEVELOPMENT";
			break;

		case Build.VERSION_CODES.DONUT:
			version = "DONUT";
			break;

		case Build.VERSION_CODES.ECLAIR:
			version = "ECLAIR";
			break;

		case Build.VERSION_CODES.ECLAIR_0_1:
			version = "ECLAIR_0_1";
			break;

		case Build.VERSION_CODES.ECLAIR_MR1:
			version = "ECLAIR_MR1";
			break;

		case Build.VERSION_CODES.FROYO:
			version = "FROYO";
			break;

		case Build.VERSION_CODES.GINGERBREAD:
			version = "GINGERBREAD";
			break;

		case Build.VERSION_CODES.GINGERBREAD_MR1:
			version = "GINGERBREAD_MR1";
			break;

		case Build.VERSION_CODES.HONEYCOMB:
			version = "HONEYCOMB";
			break;

		case Build.VERSION_CODES.HONEYCOMB_MR1:
			version = "HONEYCOMB_MR1";
			break;

		case Build.VERSION_CODES.HONEYCOMB_MR2:
			version = "HONEYCOMB_MR2";
			break;

		case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
			version = "ICE_CREAM_SANDWICH";
			break;

		case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
			version = "ICE_CREAM_SANDWICH_MR1";
			break;

		case Build.VERSION_CODES.JELLY_BEAN:
			version = "JELLY_BEAN";
			break;

		case Build.VERSION_CODES.JELLY_BEAN_MR1:
			version = "JELLY_BEAN_MR1";
			break;

		default:
			break;
		}
		
		return version;
	}
	
	/**
	 * 
	 */
	public String run() {
		
		String duration = "";
		
		/*if(MSongsParam == null) {
			MSongsParam = new ArrayList<String>();
		}
		
		MSongsParam.add(this.mFile.getAbsolutePath());*/
		
		// Console.print_debug(getAndroidVersion());
		
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
			
			try {
				// MSongsParam.add(getDuration());
				duration = getDuration();
			} catch (Exception e) {
				Console.print_exception(e);
				
				try {
					// MSongsParam.add(oldGetDuration());
					duration = oldGetDuration();
				} catch (IllegalArgumentException e1) {
					Console.print_exception(e);
					
				} catch (IllegalStateException e1) {
					Console.print_exception(e);
					
				} catch (IOException e1) {
					Console.print_exception(e);
					
				}
				
			}
		}
		
		// this.mSong = new MSong(MSongsParam);

		// return this.mSong.getmDuration();
		
		return duration;
	}
	
	/**
	 * 
	 * @author clement
	 *
	 */
	/*public class MSong{
		
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
		
		private MSong(ArrayList<String>... strings) {
			
			for (ArrayList<String> st : strings) {
				
				this.mPath = st.get(0);
				this.mDuration = st.get(1);				
			}
			
			Log.v(TAG, "this.mPath:"+this.mPath);
			Log.v(TAG, "this.mDuration:"+this.mDuration);
			
		}
	
	}*/

	/**
	 * 
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	private String getDuration() {
		
		if (this.mFormat.equalsIgnoreCase("ogg")) {
			
			Log.e(TAG, "mFormat equal ogg, humm file goto MediaPlayer :(");
			try {
				return oldGetDuration();
			} catch (IllegalArgumentException e) {
				Console.print_exception(e);
			} catch (IllegalStateException e) {
				Console.print_exception(e);
			} catch (IOException e) {
				Console.print_exception(e);
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
		String out;
		if (seconds.length() == 1 && minutes.length() == 1) {
			out = "00:0" + minutes + ":0" + seconds;
			Log.d(TAG, "seconds.length() == 1 0" + minutes + ":0" + seconds);

		} else if (seconds.length() == 1 && minutes.length() > 1) {
			out = "00:" + minutes + ":0" + seconds;
			Log.d(TAG, "seconds.length() == 1 " + minutes + ":0" + seconds);

		} else if (seconds.length() > 1 && minutes.length() == 1) {
			out = "00:0" + minutes + ":" + seconds;
			Log.d(TAG, "minutes.length() == 1 0" + minutes + ":" + seconds);

		} else {
			out = "00:" + minutes + ":" + seconds;
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
		float realMinutes = (hoursminutes - Float.parseFloat(String
				.valueOf(hour))) * 60;// (4.3 - 4.0) * 60
		int minute = Math.round(realMinutes);
		Log.d(TAG,
				"hoursminutes:" + hoursminutes
						+ " Float.parseFloat(String.valueOf(hour)): "
						+ Float.parseFloat(String.valueOf(hour)));
		if (hour < 10 && minute < 10 && seconds < 10) {
			return "0" + hour + ":0" + minute + ":0" + seconds + "";
		}

		if (hour < 10 && seconds < 10 && minute > 10) {

			return "0" + hour + ":" + minute + ":0" + seconds + "";
		}

		if (hour < 10 && seconds > 10 && minute < 10) {

			return "0" + hour + ":0" + minute + ":" + seconds + "";
		}

		if (hour < 10 && seconds > 10 && minute > 10) {

			return "0" + hour + ":" + minute + ":" + seconds + "";
		}
		//
		if (hour > 10 && minute < 10 && seconds < 10) {
			return "" + hour + ":0" + minute + ":0" + seconds + "";
		}

		if (hour > 10 && seconds < 10 && minute > 10) {

			return "" + hour + ":" + minute + ":0" + seconds + "";
		}

		if (hour > 10 && seconds > 10 && minute < 10) {

			return "" + hour + ":0" + minute + ":" + seconds + "";
		}

		if (hour > 10 && seconds > 10 && minute > 10) {

			return "" + hour + ":" + minute + ":" + seconds + "";
		}

		return " " + hour + ":" + minute + ":" + seconds + "";

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
		MediaPlayer mPlayer = new MediaPlayer();
		FileInputStream fs;
		FileDescriptor fd;
		fs = new FileInputStream(this.mFile);
		fd = fs.getFD();
		mPlayer.setDataSource(fd);
		mPlayer.prepare(); // might be optional
		int length = mPlayer.getDuration();
		long dur = (long) length;
		/*
		 * @avoid
		 * http://stackoverflow.com/questions/9609479/android-mediaplayer-
		 * went-away-with-unhandled-events
		 */
		mPlayer.reset();
		mPlayer.release();
		String result = mParseTime(dur);
		Log.v(TAG, "" + result);
		return result;
	}

}
