package org.proofs.recorder.codec.ogg.utils;


// Declare Interface
interface IServiceRecorderOgg {
	
	// You can pass values in, out, or inout. 
    // Primitive datatypes (such as int, boolean, etc.) can only be passed in.
    //int add(in int value1, in int value2);
	
	int parametersRec(in String Filename,in int mSampleRate,in float mQuality, in int audioSource,in String  notificationIntent, in String notificationPkg);
    									
    int startRec();
    int stopRec();
}