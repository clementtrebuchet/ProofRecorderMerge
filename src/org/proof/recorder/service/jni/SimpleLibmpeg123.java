package org.proof.recorder.service.jni;

import java.nio.ShortBuffer;

import org.proof.recorder.Settings;

import android.util.Log;

class SimpleLibmpeg123 extends MP3Decoder{

	private static final String TAG = "SimpleLibmpeg123";
	public SimpleLibmpeg123(String filename) {
		super(filename);
		for(Encoding mEncoding :MP3Decoder.getSupportedEncodings())
			{
				if(Settings.isDebug())Log.e(TAG,"SupportedEncoding{"+mEncoding+"}");
			}
	// TODO Auto-generated constructor stub
	}
	@Override
	public int readSamples (ShortBuffer samples)
    {
		int read = super.readSamples(samples);
		if(Settings.isDebug())Log.e(TAG, "super.readSamples(samples)={"+read+"}");
		return read;
		
    }
}
