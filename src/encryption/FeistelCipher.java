package encryption;

import java.util.Arrays;

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

	public void encrypt(byte[] data)
	{
		checkDataLength(data);
		encryptImpl(data, key);
	}

	public void decrypt(byte[] data)
	{
		checkDataLength(data);
		decryptImpl(data, key);
	}

	private static native void encryptImpl(byte[] data, int[] key);
	private static native void decryptImpl(byte[] data, int[] key);
}
