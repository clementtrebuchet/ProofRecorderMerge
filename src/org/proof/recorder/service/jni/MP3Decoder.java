package org.proof.recorder.service.jni;

import java.nio.ShortBuffer;

public class MP3Decoder {
	public interface Error
    {
        int DONE = -12;            // Message: Track ended. Stop decoding.
        int NEW_FORMAT = -11;      // Message: Output format will be different on next call. Note that some libmpg123 versions between 1.4.3 and 1.8.0 insist on you calling mpg123_getformat() after getting this message code. Newer verisons behave like advertised: You have the chance to call mpg123_getformat(), but you can also just continue decoding and get your data.
        int NEED_MORE = -10;       // Message: For feed reader: "Feed me more!" (call mpg123_feed() or mpg123_decode() with some new input data).
        int ERR = -1;              // Generic Error
        int OK = 0;                // Success
        int BAD_OUTFORMAT = 1;     // Unable to set up output format!
        int BAD_CHANNEL = 2;       // Invalid channel number specified.
        int BAD_RATE = 3;          // Invalid sample rate specified. 
        int ERR_16TO8TABLE = 4;    // Unable to allocate memory for 16 to 8 converter table!
        int BAD_PARAM = 5;         // Bad parameter id!
        int BAD_BUFFER = 6;        // Bad buffer given -- invalid pointer or too small size.
        int OUT_OF_MEM = 7;        // Out of memory -- some malloc() failed.
        int NOT_INITIALIZED = 8;   // You didn't initialize the library!
        int BAD_DECODER = 9;       // Invalid decoder choice.
        int BAD_HANDLE = 10;       // Invalid mpg123 handle.
        int NO_BUFFERS = 11;       // Unable to initialize frame buffers (out of memory?).
        int BAD_RVA = 12;          // Invalid RVA mode.
        int NO_GAPLESS = 13;       // This build doesn't support gapless decoding.
        int NO_SPACE = 14;         // Not enough buffer space.
        int BAD_TYPES = 15;        // Incompatible numeric data types.
        int BAD_BAND = 16;         // Bad equalizer band.
        int ERR_NULL = 17;         // Null pointer given where valid storage address needed.
        int ERR_READER = 18;       // Error reading the stream.
        int NO_SEEK_FROM_END = 19; // Cannot seek from end (end is not known).
        int BAD_WHENCE = 20;       // Invalid 'whence' for seek function.
        int NO_TIMEOUT = 21;       // Build does not support stream timeouts.
        int BAD_FILE = 22;         // File access error.
        int NO_SEEK = 23;          // Seek not supported by stream.
        int NO_READER = 24;        // No stream opened.
        int BAD_PARS = 25;         // Bad parameter handle.
        int BAD_INDEX_PAR = 26;    // Bad parameters to mpg123_index() and mpg123_set_index()
        int OUT_OF_SYNC = 27;      // Lost track in bytestream and did not try to resync.
        int RESYNC_FAIL = 28;      // Resync failed to find valid MPEG data.
        int NO_8BIT = 29;          // No 8bit encoding possible.
        int BAD_ALIGN = 30;        // Stack aligmnent error
        int NULL_BUFFER = 31;      // NULL input buffer with non-zero size...
        int NO_RELSEEK = 32;       // Relative seek not possible (screwed up file offset)
        int NULL_POINTER = 33;     // You gave a null pointer somewhere where you shouldn't have.
        int BAD_KEY = 34;          // Bad key value given.
        int NO_INDEX = 35;         // No frame index in this build.
        int INDEX_FAIL = 36;       // Something with frame index went wrong.
        int BAD_DECODER_SETUP = 37;// Something prevents a proper decoder setup
        int MISSING_FEATURE = 38;  // This feature has not been built into libmpg123.
        int BAD_VALUE = 39;        // A bad value has been given, somewhere.
        int LSEEK_FAILED = 40;     // Low-level seek failed.
        int BAD_CUSTOM_IO = 41;    // Custom I/O not prepared.
        int LFS_OVERFLOW = 42;     // Offset value overflow during translation of large file API calls -- your client program cannot handle that large file.        
    }
    
    /**
     * Flag definitions for the features that can be queried for using getFeatures()
     */
    public enum Feature
    {
        ABI_UTF8OPEN,      // mpg123 expects path names to be given in UTF-8 encoding instead of plain native.
        OUTPUT_8BIT,       // 8bit output
        OUTPUT_16BIT,      // 16bit output
        OUTPUT_32BIT,      // 32bit output
        INDEX,             // support for building a frame index for accurate seeking
        PARSE_ID3V2,       // id3v2 parsing
        DECODE_LAYER1,     // mpeg layer-1 decoder enabled
        DECODE_LAYER2,     // mpeg layer-2 decoder enabled
        DECODE_LAYER3,     // mpeg layer-3 decoder enabled
        DECODE_ACCURATE,   // accurate decoder rounding
        DECODE_DOWNSAMPLE, // downsample (sample omit)
        DECODE_NTOM,       // flexible rate decoding
        PARSE_ICY,         // ICY support
        TIMEOUT_READ;      // Reader with timeout (network).
        
        private static native boolean isFeatureSupported(int value);

        public boolean isSupported()
        {
            return isFeatureSupported(ordinal());
        }
    }
    
    public interface Flags
    {
        /** Force playback of left channel only. */
        int MONO_LEFT = 0x1;

        /** Force playback of right channel only. */
        int MONO_RIGHT = 0x2;

        /** Force playback of mixed mono. */
        int MONO_MIX = 0x4;

        /** Force stereo output. */
        int FORCE_STEREO = 0x8;

        /** Force 8bit formats. */
        int FORCE_8BIT = 0x10;

        /** Suppress any printouts (overrules verbose). */
        int QUIET = 0x20;

        /** Enable gapless decoding (default on if libmpg123 has support). */
        int GAPLESS = 0x40;

        /** Disable resync stream after error. */
        int NO_RESYNC = 0x80;

        /** Enable small buffer on non-seekable streams to allow some peek-ahead (for better MPEG sync). */
        int SEEKBUFFER = 0x100;

        /** Enable fuzzy seeks (guessing byte offsets or using approximate seek points from Xing TOC) */
        int FUZZY = 0x200;

        /** Force floating point output (32 or 64 bits depends on mpg123 internal precision). */
        int FORCE_FLOAT = 0x400;

        /** Do not translate ID3 text data to UTF-8. ID3 strings will contain the raw text data, with the first byte containing the ID3 encoding code. */
        int PLAIN_ID3TEXT = 0x800;

        /** Ignore any stream length information contained in the stream, which can be contained in a 'TLEN' frame of an ID3v2 tag or a Xing tag */
        int IGNORE_STREAMLENGTH = 0x1000;

        /** Do not parse ID3v2 tags, just skip them. */
        int SKIP_ID3V2 = 0x2000;

        /** Do not parse the LAME/Xing info frame, treat it as normal MPEG data. */
        int IGNORE_INFOFRAME = 0x4000;

        /** Force some mono mode: This is a test bitmask for seeing if any mono forcing is active. */
        int FORCE_MONO = MONO_LEFT | MONO_RIGHT | MONO_MIX;
    }

    private static native int initialize();

    static
    {
        System.loadLibrary("mpg");
        int result = initialize();

        if(result != Error.OK)
        {
            throw new java.lang.Error("Error " + result + "initializing MP3Decoder");
        }
    }

    public static native int[] getSupportedRates();

    private static native int[] getEncodings();
    
    public static Encoding[] getSupportedEncodings()
    {
        int[] encodings = getEncodings();
        
        Encoding[] result = new Encoding[encodings.length];
        
        for(int i = 0; i < encodings.length; i++)
        {
            result[i] = Encoding.values()[encodings[i]];
        }
        
        return result;
    }
    
    public static native String getErrorMessage(int errorCode);
    
    private long handle;
    
    private native void delete(long handle);

    /**
     * Opens the given file for mp3 decoding. Throws an IllegalArugmentException in case the file could not be opened.
     * 
     * @param filename the filename
     */
    public MP3Decoder(String filename)
    {
        handle = openFile(filename);

  //
 //       if (handle == 0) throw new IllegalArgumentException("couldn't open file");
    }

    public native int setFlags(long handle, int flags);
    
    public int setFlags(int flags)
    {
        return setFlags(handle, flags);
    }
    
    public native int getFlags(long handle);
    
    public int getFlags()
    {
        return getFlags(handle);
    }
    
    private native long openFile (String filename);
 
    public void dispose()
    {
        if(handle != 0)
        {
            delete(handle);
            handle = 0;
        }
    }
    
    @Override
	public void finalize()
    {
        dispose();
    }

    public int readSamples (ShortBuffer samples)
    {
        int read = readSamples(handle, samples, samples.capacity());
        samples.position(0);
        return read;
    }

    private native int readSamples (long handle, ShortBuffer buffer, int numSamples);

    public native int skipSamples(long handle, int numSamples);

    public int skipSamples(int numSamples)
    {
        return skipSamples(handle, numSamples);
    }

    private native int getNumChannels(long handle);
    private native int getRate(long handle);
    private native float getLength(long handle);


    public int getNumChannels()
    {
        return getNumChannels(handle);
    }

    public int getRate()
    {
        return getRate(handle);
    }

    public float getLength()
    {
        return getLength(handle);
    }

}
