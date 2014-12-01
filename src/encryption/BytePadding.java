package encryption;

class BytePadding
{
	public static byte[] padTo8Bytes(byte[] bytes)
	{
		byte[] result = new byte[bytes.length + 7 & ~7];
		System.arraycopy(bytes, 0, result, 0, bytes.length);
		return result;
	}

	public static byte[] unpadBytes(byte[] bytes)
	{
		int padding = 0;
		for (int i = bytes.length - 1; i >=0 && bytes[i] == '\0'; --i, ++padding);

		byte[] result = new byte[bytes.length - padding];
		System.arraycopy(bytes, 0, result, 0, result.length);
		return result;
	}
}

