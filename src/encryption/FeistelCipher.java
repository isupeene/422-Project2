package encryption;

import java.util.Arrays;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

public class FeistelCipher
{
	static
	{
		System.loadLibrary("encryption");
	}

	private int[] key;

	public FeistelCipher(int[] key)
	{
		if (key.length != 4)
		{
			throw new IllegalArgumentException("Key must be of length 4.  Got " + Arrays.toString(key));
		}

		this.key = key;
	}

	private void checkDataLength(byte[] data)
	{
		if (data.length % 8 != 0)
		{
			throw new IllegalArgumentException(
				"Data length must be a multiple of 8.  Got data of length " + data.length
			);
		}
	}

	public String encrypt(String plaintext)
	{
		return encrypt(plaintext.getBytes());
	}

	public String decrypt(String ciphertext)
	{
		return decrypt(decodeBase64(ciphertext));
	}

	public String encrypt(byte[] plaintext)
	{
		byte[] ciphertext = BytePadding.padTo8Bytes(plaintext);
		encryptImpl(ciphertext, key);
		return encodeBase64String(ciphertext);
	}

	public String decrypt(byte[] ciphertext)
	{
		decryptImpl(ciphertext, key);
		return new String(BytePadding.unpadBytes(ciphertext));
	}

	private static native void encryptImpl(byte[] data, int[] key);
	private static native void decryptImpl(byte[] data, int[] key);
}
