/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_proof_recorder_service_jni_MP3Decoder */

#ifndef _Included_org_proof_recorder_service_jni_MP3Decoder
#define _Included_org_proof_recorder_service_jni_MP3Decoder
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    initialize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_initialize
  (JNIEnv *, jclass);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    getSupportedRates
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_getSupportedRates
  (JNIEnv *, jclass);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    getEncodings
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_getEncodings
  (JNIEnv *, jclass);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    getErrorMessage
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_getErrorMessage
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    delete
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_delete
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    setFlags
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_setFlags
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    getFlags
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_getFlags
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    openFile
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_openFile
  (JNIEnv *, jobject, jstring);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    readSamples
 * Signature: (JLjava/nio/ShortBuffer;I)I
 */
JNIEXPORT jint JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_readSamples
  (JNIEnv *, jobject, jlong, jobject, jint);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    skipSamples
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_skipSamples
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    getNumChannels
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_getNumChannels
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    getRate
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_getRate
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_proof_recorder_service_jni_MP3Decoder
 * Method:    getLength
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_org_proof_recorder_service_jni_MP3Decoder_getLength
  (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif
