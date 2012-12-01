#include "org_proof_recorder_service_jni_MP3Decoder.h"
#include "org_proof_recorder_service_jni_MP3Decoder_Error.h"
#include "org_proof_recorder_service_jni_MP3Decoder_Feature.h"
#include "org_proof_recorder_service_jni_Encoding_Ordinals.h"
#include <stddef.h>
#include <fcntl.h>
#include "libmpg123/compat.h"
#include "mpg123.h"
#include <jni.h>

struct Mp3File
{
	mpg123_handle* handle;
	int channels;
	long rate;
	float length;
	size_t buffer_size;
	unsigned char* buffer;
	size_t leftSamples;
	size_t offset;

	Mp3File(mpg123_handle *handle) : handle(handle), buffer(NULL), leftSamples(0), offset(0)
	{
	}

	~Mp3File()
	{
		mpg123_close(handle);
		mpg123_delete(handle);
		free(buffer);
	}
};


// This should really throw an exception, but I'm a bit lazy
// to do that for a case that won't ever occur.
#define CHECK_ERROR_CODE(code) \
	if(MPG123_##code != org_proof_recorder_service_jni_MP3Decoder_Error_##code)\
	{\
		return MPG123_BAD_VALUE;\
	}

JNIEXPORT jint JNICALL Java_nobleworks_libmpg_MP3Decoder_initialize
  (JNIEnv *env, jclass)
{
    CHECK_ERROR_CODE(DONE)
    CHECK_ERROR_CODE(NEW_FORMAT)
    CHECK_ERROR_CODE(NEED_MORE)
    CHECK_ERROR_CODE(ERR)
    CHECK_ERROR_CODE(OK)
    CHECK_ERROR_CODE(BAD_OUTFORMAT)
    CHECK_ERROR_CODE(BAD_CHANNEL)
    CHECK_ERROR_CODE(BAD_RATE)
    CHECK_ERROR_CODE(ERR_16TO8TABLE)
    CHECK_ERROR_CODE(BAD_PARAM)
    CHECK_ERROR_CODE(BAD_BUFFER)
    CHECK_ERROR_CODE(OUT_OF_MEM)
    CHECK_ERROR_CODE(NOT_INITIALIZED)
    CHECK_ERROR_CODE(BAD_DECODER)
    CHECK_ERROR_CODE(BAD_HANDLE)
    CHECK_ERROR_CODE(NO_BUFFERS)
    CHECK_ERROR_CODE(BAD_RVA)
    CHECK_ERROR_CODE(NO_GAPLESS)
    CHECK_ERROR_CODE(NO_SPACE)
    CHECK_ERROR_CODE(BAD_TYPES)
    CHECK_ERROR_CODE(BAD_BAND)
    CHECK_ERROR_CODE(ERR_NULL)
    CHECK_ERROR_CODE(ERR_READER)
    CHECK_ERROR_CODE(NO_SEEK_FROM_END)
    CHECK_ERROR_CODE(BAD_WHENCE)
    CHECK_ERROR_CODE(NO_TIMEOUT)
    CHECK_ERROR_CODE(BAD_FILE)
    CHECK_ERROR_CODE(NO_SEEK)
    CHECK_ERROR_CODE(NO_READER)
    CHECK_ERROR_CODE(BAD_PARS)
    CHECK_ERROR_CODE(BAD_INDEX_PAR)
    CHECK_ERROR_CODE(OUT_OF_SYNC)
    CHECK_ERROR_CODE(RESYNC_FAIL)
    CHECK_ERROR_CODE(NO_8BIT)
    CHECK_ERROR_CODE(BAD_ALIGN)
    CHECK_ERROR_CODE(NULL_BUFFER)
    CHECK_ERROR_CODE(NO_RELSEEK)
    CHECK_ERROR_CODE(NULL_POINTER)
    CHECK_ERROR_CODE(BAD_KEY)
    CHECK_ERROR_CODE(NO_INDEX)
    CHECK_ERROR_CODE(INDEX_FAIL)
    CHECK_ERROR_CODE(BAD_DECODER_SETUP)
    CHECK_ERROR_CODE(MISSING_FEATURE)
    CHECK_ERROR_CODE(BAD_VALUE)
    CHECK_ERROR_CODE(LSEEK_FAILED)
    CHECK_ERROR_CODE(BAD_CUSTOM_IO)
    CHECK_ERROR_CODE(LFS_OVERFLOW)

    return mpg123_init();
}

JNIEXPORT jintArray JNICALL Java_nobleworks_libmpg_MP3Decoder_getSupportedRates
  (JNIEnv *env, jclass)
{
	const long *list;
	size_t number;

	mpg123_rates(&list, &number);

	jintArray result = env->NewIntArray(number);

	jint *resultData = (jint *)env->GetPrimitiveArrayCritical(result, 0);

	for(size_t i = 0; i < number; i++)
	{
		resultData[i] = list[i];
	}

	env->ReleasePrimitiveArrayCritical(result, resultData, 0);

	return result;
}

int convertToEncodingOrdinal(int encoding)
{
	switch(encoding)
	{
		default : return -1;
		case MPG123_ENC_ALAW_8:		 return org_proof_recorder_service_jni_Encoding_Ordinals_ALAW_8;
		case MPG123_ENC_ULAW_8:		 return org_proof_recorder_service_jni_Encoding_Ordinals_ULAW_8;
		case MPG123_ENC_SIGNED_8:	 return org_proof_recorder_service_jni_Encoding_Ordinals_PCM_SIGNED_8;
		case MPG123_ENC_UNSIGNED_8:	 return org_proof_recorder_service_jni_Encoding_Ordinals_PCM_UNSIGNED_8;
		case MPG123_ENC_SIGNED_16:	 return org_proof_recorder_service_jni_Encoding_Ordinals_PCM_SIGNED_16;
		case MPG123_ENC_UNSIGNED_16: return org_proof_recorder_service_jni_Encoding_Ordinals_PCM_UNSIGNED_16;
		case MPG123_ENC_SIGNED_24:	 return org_proof_recorder_service_jni_Encoding_Ordinals_PCM_SIGNED_24;
		case MPG123_ENC_UNSIGNED_24: return org_proof_recorder_service_jni_Encoding_Ordinals_PCM_UNSIGNED_24;
		case MPG123_ENC_SIGNED_32:	 return org_proof_recorder_service_jni_Encoding_Ordinals_PCM_SIGNED_32;
		case MPG123_ENC_UNSIGNED_32: return org_proof_recorder_service_jni_Encoding_Ordinals_PCM_UNSIGNED_32;
		case MPG123_ENC_FLOAT_32:	 return org_proof_recorder_service_jni_Encoding_Ordinals_PCM_FLOAT_32;
		case MPG123_ENC_FLOAT_64:	 return org_proof_recorder_service_jni_Encoding_Ordinals_PCM_FLOAT_64;
	}
}

JNIEXPORT jintArray JNICALL Java_nobleworks_libmpg_MP3Decoder_getEncodings
  (JNIEnv *env, jclass)
{
	const int *list;
	size_t number;

	mpg123_encodings(&list, &number);

	jintArray result = env->NewIntArray(number);

	jint *resultData = (jint *)env->GetPrimitiveArrayCritical(result, 0);

	for(size_t i = 0; i < number; i++)
	{
		resultData[i] = convertToEncodingOrdinal(list[i]);
	}

	env->ReleasePrimitiveArrayCritical(result, resultData, 0);

	return result;
}

JNIEXPORT jstring JNICALL Java_nobleworks_libmpg_MP3Decoder_getErrorMessage
  (JNIEnv *env, jclass clz, jint error)
{
	return env->NewStringUTF(mpg123_plain_strerror(error));
}

JNIEXPORT jlong JNICALL Java_nobleworks_libmpg_MP3Decoder_openFile(JNIEnv *env, jobject, jstring file)
{
	int  err  = MPG123_OK;
	mpg123_handle *mh = mpg123_new(NULL, &err);

	if( err == MPG123_OK && mh != NULL)
	{
		Mp3File* mp3 = new Mp3File(mh);

		const char* fileString = env->GetStringUTFChars(file, NULL);

		err = mpg123_open(mh, fileString);

		env->ReleaseStringUTFChars(file, fileString);

		if (err == MPG123_OK)
		{
			int encoding;
			if (mpg123_getformat(mh, &mp3->rate, &mp3->channels, &encoding) == MPG123_OK)
			{
				if(encoding == MPG123_ENC_SIGNED_16)
				{
				    // Signed 16 is the default output format anyways; it would actually by only different if we forced it.
					// So this check is here just for this explanation.

					// Ensure that this output format will not change (it could, when we allow it).
					mpg123_format_none(mh);
					mpg123_format(mh, mp3->rate, mp3->channels, encoding);

					mp3->buffer_size = mpg123_outblock(mh);
					mp3->buffer = (unsigned char*)malloc(mp3->buffer_size);

					int length = mpg123_length( mh );
					if( length == MPG123_ERR )
						mp3->length = 0;
					else
						mp3->length = length / mp3->rate;

					return (jlong)mp3;
				}
			}
		}

		delete mp3;
	}
	return 0;
}

JNIEXPORT void JNICALL Java_nobleworks_libmpg_MP3Decoder_delete
  (JNIEnv *env, jobject instance, jlong handle)
{
	Mp3File *mp3 = (Mp3File *)handle;

	delete mp3;
}

JNIEXPORT jboolean JNICALL Java_nobleworks_libmpg_MP3Decoder_00024Feature_isFeatureSupported
  (JNIEnv *env, jclass, jint feature)
{
	return mpg123_feature( (enum mpg123_feature_set)feature);
}

static inline int readBuffer( Mp3File* mp3 )
{
	size_t done = 0;
	int err = mpg123_read( mp3->handle, mp3->buffer, mp3->buffer_size, &done );

	mp3->leftSamples = done / 2;
	mp3->offset = 0;

	if( err != MPG123_OK )
		return 0;
	else
		return done;
}

JNIEXPORT jint JNICALL Java_nobleworks_libmpg_MP3Decoder_readSamples(JNIEnv *env, jobject instance, jlong handle, jobject buffer, jint numSamples)
{
	Mp3File *mp3 = (Mp3File *)handle;
	short* target = (short*)env->GetDirectBufferAddress(buffer);

	int idx = 0;
	while( idx != numSamples )
	{
		if( mp3->leftSamples > 0 )
		{
			short* src = ((short*)mp3->buffer) + mp3->offset;
			for( ; idx < numSamples && mp3->offset < mp3->buffer_size / 2; mp3->leftSamples--, mp3->offset++, target++, src++, idx++ )
			{
				*target = *src;
			}
		}
		else
		{
			int result = readBuffer( mp3 );
			if( result == 0 )
				return 0;
		}
	}

	if( idx > numSamples )
		return 0;

	return idx;
}

JNIEXPORT jint JNICALL Java_nobleworks_libmpg_MP3Decoder_skipSamples(JNIEnv *env, jobject instance, jlong handle, jint numSamples)
{
	Mp3File *mp3 = (Mp3File *)handle;

	int idx = 0;
	while( idx != numSamples )
	{
		if( mp3->leftSamples > 0 )
		{
			for( ; idx < numSamples && mp3->offset < mp3->buffer_size / 2; mp3->leftSamples--, mp3->offset++, idx++ );
		}
		else
		{
			int result = readBuffer( mp3 );
			if( result == 0 )
				return 0;
		}

	}

	if( idx > numSamples )
		return 0;

	return idx;
}

JNIEXPORT jint JNICALL Java_nobleworks_libmpg_MP3Decoder_getNumChannels(JNIEnv *env, jobject instance, jlong handle)
{
	Mp3File *mp3 = (Mp3File *)handle;
	return mp3->channels;
}

JNIEXPORT jint JNICALL Java_nobleworks_libmpg_MP3Decoder_getRate(JNIEnv *env, jobject instance, jlong handle)
{
	Mp3File *mp3 = (Mp3File *)handle;
	return mp3->rate;
}

JNIEXPORT jfloat JNICALL Java_nobleworks_libmpg_MP3Decoder_getLength(JNIEnv *env, jobject instance, jlong handle)
{
	Mp3File *mp3 = (Mp3File *)handle;
	return mp3->length;
}
