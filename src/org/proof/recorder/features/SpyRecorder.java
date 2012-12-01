package org.proof.recorder.features;

import java.util.Date;

import android.util.Log;

public class SpyRecorder implements Runnable, InputEventInterceptor {
    private static final String TAG = "SpyRecorder";
    
    private static final float MIN_SILENCE = 5f;

    // TODO Move to advanced settings
    private static final int SAMPLE_EVERY = 30;
    
    private static final float MIN_TAP_NOISE = 30f;
    private static final float MAX_TAP_NOISE = 50f;
    private static final int MIN_NEEDED_SILENCE = 20;
    // TODO Move to advanced settings
    private static final int TAP_MIN_SPACE = 4;
    // TODO Move to advanced settings
    private static final int TAP_MAX_SPACE = 10;
    // TODO Move to advanced settings
    private static final short TAP_MIN_MAX = 10000;

    // Maximum signal amplitude for 16-bit data.
    private static final float MAX_16_BIT = 32768;
    
    // This fudge factor is added to the output to make a realistically
    // fully-saturated signal come to 0dB.  Without it, the signal would
    // have to be solid samples of -32768 to read zero, which is not
    // realistic.  This really is a fudge, because the best value depends
    // on the input frequency and sampling rate.  We optimize here for
    // a 1kHz signal at 16,000 samples/sec.
    private static final float FUDGE = 55.6f;
    
    private float mLastPower;
    
    public boolean mStop = false;
    
    //private AudioHandler mAudio;

    private Thread mSamplingThread;
    
    private int mSilenceCount = 0;
    private boolean mHadFirstTap = false;
    
    private InputEventListener mListener = null;

	private short mLastMax;
    
    public SpyRecorder() {
        mLastPower = MIN_SILENCE;
        mLastMax = 0;
        
        //mAudio = new AudioHandler(null, null, null);
    }
    
    @Override
    public void run() {
        long runtime = 0;
        while (!mStop) {
            long before = new Date().getTime();
            takeSample();
            runtime = new Date().getTime() - before;
            if (!mStop && SAMPLE_EVERY > runtime) {
                try {
                    Thread.sleep(SAMPLE_EVERY - runtime);
                }
                catch (InterruptedException e) {
                    break;
                }
            }
        }
        // Note: This might cause an exception in a race condition with startIntercepting
        mSamplingThread = null;
    }

    @Override
    public void setListener(InputEventListener listener) {
        mListener = listener;
    }

    @Override
    public void startIntercepting() {
        if (mStop || mSamplingThread == null || !mSamplingThread.isAlive()) {
            //mAudio.startRecording();
            Log.e(TAG,"start intercepting");
            if (mSamplingThread != null) {
                mStop = true;
                while (mSamplingThread.isAlive()) {
                    try {
                        Thread.sleep(SAMPLE_EVERY);
                    }
                    catch (InterruptedException e) {
                        break;
                    }
                }
            }
            mStop = false;
            mSamplingThread = new Thread(this);
            mSamplingThread.start();
        }
    }

    @Override
    public void stopIntercepting() {
    	Log.e(TAG,"stop intercepting");
        mStop = true;
        //mAudio.stopRecording();
    }

    
    
    private void takeSample() {
        float sample = Math.abs(mLastPower);
        short max = mLastMax;
        
        if (max > TAP_MIN_MAX && sample >= MIN_TAP_NOISE && sample <= MAX_TAP_NOISE) {
            Log.v(TAG, String.format("Silence interrupted after %d. s: %f\tm: %d", mSilenceCount, sample, max));
            if (!mHadFirstTap) {
                // First tap is registered only after long enough silence
                mHadFirstTap = mSilenceCount >= MIN_NEEDED_SILENCE;
            }
            else if (mSilenceCount >= TAP_MIN_SPACE && mSilenceCount <= TAP_MAX_SPACE) {
                mHadFirstTap = false;
                Log.w(TAG, "TAP!");
                if (mListener != null) {
                    mListener.onEventTriggered(SpyEvt.ID);
                }
            }
            else {
                mHadFirstTap = false;
            }
            mSilenceCount = 0;
        }
        else if (sample >= MIN_TAP_NOISE) {
            Log.v(TAG, String.format("Noise... s: %f\tm: %d", sample, max));
            mHadFirstTap = false;
            mSilenceCount = 0;
        }
        else if (mSilenceCount < MIN_NEEDED_SILENCE) {
            ++mSilenceCount;
        }
    }

    /**
     * Calculate the power of the given input signal.
     * 
     * @param   sdata       Buffer containing the input samples to process.
     */
    private final static double calculatePowerDb(short[] sdata) {
        // Calculate the sum of the values, and the sum of the squared values.
        // We need longs to avoid running out of bits.
        double sum = 0;
        double sqsum = 0;
        for (int i = 0; i < sdata.length; i++) {
            final long v = sdata[i];
            sum += v;
            sqsum += v * v;
        }
        
        // sqsum is the sum of all (signal+bias)², so
        //     sqsum = sum(signal²) + samples * bias²
        // hence
        //     sum(signal²) = sqsum - samples * bias²
        // Bias is simply the average value, i.e.
        //     bias = sum / samples
        // Since power = sum(signal²) / samples, we have
        //     power = (sqsum - samples * sum² / samples²) / samples
        // so
        //     power = (sqsum - sum² / samples) / samples
        double power = (sqsum - sum * sum / sdata.length) / sdata.length;

        // Scale to the range 0 - 1.
        power /= MAX_16_BIT * MAX_16_BIT;

        // Convert to dB, with 0 being max power.  Add a fudge factor to make
        // a "real" fully saturated input come to 0 dB.
        return Math.log10(power) * 10f + FUDGE;
    }
}
