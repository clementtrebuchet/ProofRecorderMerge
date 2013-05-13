package org.proofs.recorder.codec.mp3.utils;


// Declare the interface.
interface IServiceIntentRecorderMP3 {


    // You can pass values in, out, or inout. 
    // Primitive datatypes (such as int, boolean, etc.) can only be passed in.
    //int add(in int value1, in int value2);
    
    
    int parametersRec(String Filename, int mSampleRate, int audioSource, int outBitRate,
    						int postEcode, String  notificationIntent, String notificationPkg);
    									
    int startRec();
    int stopRec();
    int encodeFile(String inFileName, String outFileName);
    
}