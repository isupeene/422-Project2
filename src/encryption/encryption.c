#include "encryption.h"

#include <stdint.h>
#include <stdio.h>

void encrypt8Bytes(uint32_t *v, uint32_t *k)
{
	uint32_t y = v[0], z=v[1], sum = 0;
	uint32_t delta = 0x9e3779b9, n=32;

	while (n-- > 0)
	{
		sum += delta;
		y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
	}

	v[0] = y;
	v[1] = z;
}

void decrypt8Bytes(uint32_t *v, uint32_t *k)
{
	uint32_t n=32, sum, y=v[0], z=v[1];
	uint32_t delta=0x9e3779b9;

	sum = delta<<5;
	while (n-- > 0)
	{
		z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
		y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		sum -= delta;
	}
	v[0] = y;
	v[1] = z;
}

void encrypt(jbyte* myData, jint* myKey, jsize dataLength)
{
	for (jsize i = 0; i < dataLength; i += 8)
	{
		encrypt8Bytes((uint32_t*) (myData + i), myKey);
	}
}

void decrypt(jbyte* myData, jint* myKey, jsize dataLength)
{
	for (jsize i = 0; i < dataLength; i += 8)
	{
		decrypt8Bytes((uint32_t*) (myData + i), myKey);
	}
}

JNIEXPORT void JNICALL Java_encryption_FeistelCipher_encryptImpl
  (JNIEnv* env, jclass class, jbyteArray data, jintArray key)
{
	jint* myKey = (jint*) (*env)->GetIntArrayElements(env, key, 0);
	jbyte* myData = (jbyte*) (*env)->GetByteArrayElements(env, data, 0);
	jsize dataLength = (*env)->GetArrayLength(env, data);

	encrypt(myData, myKey, dataLength);

	(*env)->ReleaseIntArrayElements(env, key, myKey, 0);
	(*env)->ReleaseByteArrayElements(env, data, myData, 0);
}


JNIEXPORT void JNICALL Java_encryption_FeistelCipher_decryptImpl
  (JNIEnv* env, jclass class, jbyteArray data, jintArray key)
{
	jint* myKey = (jint*) (*env)->GetIntArrayElements(env, key, 0);
	jbyte* myData = (jbyte*) (*env)->GetByteArrayElements(env, data, 0);
	jsize dataLength = (*env)->GetArrayLength(env, data);

	decrypt(myData, myKey, dataLength);

	(*env)->ReleaseIntArrayElements(env, key, myKey, 0);
	(*env)->ReleaseByteArrayElements(env, data, myData, 0);
}

