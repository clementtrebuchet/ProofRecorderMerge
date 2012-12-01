package org.proof.recorder.service;

import org.proof.recorder.Settings;
import org.proof.recorder.service.jni.SimpleLame;

import android.content.Context;
import android.media.AudioFormat;
import android.util.Log;


public class MpthreeRec {
	
	
	static {
		System.loadLibrary("mp3lame");
	}
	
	public MpthreeRec(){
		
	}
	private static int INIT =0;
	static int mSampleRate;
	static short[] buffer;
	private static int mp3Channel;  //Lame doesn't support stereo
	private static final String TAG = null;
	
	public static void Mpthree(int sampleRate){
		MpthreeRec.mp3Channel = AudioFormat.CHANNEL_IN_MONO;
		MpthreeRec.mSampleRate = sampleRate;
		
	}
	
	public static short[] getBuffer(){
		MpthreeRec.buffer = new short[mSampleRate * (16 / 8) * 1 * 5];
		return buffer;
	}
	
	public static byte[] getMp3Buffer(short[] buffer){
		if(Settings.isDebug())Log.e(TAG, " getMp3Buffer()");
		byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];
		return mp3buffer;
	}
	
	public static void  lameInit(int outBitrate, int quality){
		if(Settings.isDebug())Log.e(TAG, "lameInit() to lame");
		if(INIT==0){
			SimpleLame.init(mSampleRate, mp3Channel, mSampleRate, outBitrate, 7);
			INIT++;
		}else if (INIT > 0){
			try{
				SimpleLame.close();
				if(Settings.isDebug())Log.e(TAG, "Try whith success lameInit()/lameClose()");
			} catch (Exception e){
				
				if(Settings.isDebug())Log.e(TAG, "Try whitout success lameInit()/lameClose() -> lame SimpleLame.close() cause lame was init "+e.toString());
				
			} finally {
				if(Settings.isDebug())Log.e(TAG, "Try whith success lameInit() -> After closing it lameClose()");
				SimpleLame.init(mSampleRate, mp3Channel, mSampleRate, outBitrate, 7);
				INIT = 0;
			}
		}
	}
	
	
	public static int lameEncodeResult(short[] buffer, int readSize, byte[] mp3buffer){
		int lameEncodeResult = SimpleLame.encode(buffer, buffer, readSize, mp3buffer);
		if(Settings.isDebug())Log.e(TAG, "lameEncodeResult "+lameEncodeResult);
		return lameEncodeResult;
	}
	
	public static int  flush(byte[] mp3buf){
		if(Settings.isDebug())Log.e(TAG, "flush mp3buf to lame");
		return SimpleLame.flush(mp3buf);
	}
	
	public static void close(){
		if(Settings.isDebug())Log.e(TAG, "Close lame");
		SimpleLame.close();
		INIT = 0;
	}
}

