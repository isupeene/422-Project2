import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.net.Socket;
import java.net.ServerSocket;

import encryption.FeistelCipher;

import config.Config;
import config.ConfigException;

import java.util.Map;

class Server
{
	public static void main(String[] args) throws IOException
	{
		ServerSocket serverSock = new ServerSocket(16000);

		while (true)
		{
			final Socket clientSock = serverSock.accept();
			new Thread() {
				public void run() { handleClient(clientSock); }
			}.start();
		}
	}

	private static int[] findKey(String encryptedUsername) throws ConfigException
	{
		for (Map.Entry<String, int[]> pair : Config.iterateKeys())
		{
			if (new FeistelCipher(pair.getValue()).decrypt(encryptedUsername).equals(pair.getKey()))
			{
				System.out.println("Accepted a new user: " + pair.getKey());
				return pair.getValue();
			}
		}
		return null;
	}

	private static void handleClient(Socket clientSock)
	{
		try
		{
			BufferedReader clientReader = new BufferedReader(
				new InputStreamReader(clientSock.getInputStream())
			);
			PrintWriter clientWriter = new PrintWriter(clientSock.getOutputStream(), true);
			String username = clientReader.readLine();

			int[] key = findKey(username);
			if (key == null)
			{
				System.out.println("No matching user.");
				clientSock.close();
				return;
			}

			FeistelCipher cipher = new FeistelCipher(key);
			while (true)
			{
				String base64Filename = clientReader.readLine();
				if (base64Filename == null) break;

				String filename = cipher.decrypt(base64Filename);
				System.out.println("Got a request for " + filename);
				
				byte[] fileBytes = null;				
				try
				{
					RandomAccessFile file = new RandomAccessFile(filename, "r");
					fileBytes = new byte[(int)file.length()];
					file.readFully(fileBytes);
				}
				catch (IOException ex)
				{
					ex.printStackTrace();

					clientWriter.println(cipher.encrypt("Couldn't read the file. :("));
					continue;
				}
				
				clientWriter.println(cipher.encrypt(fileBytes));
			}
		}
		catch (IOException ex) {} // The thread died - that's OK.
		catch (ConfigException ex)
		{
			ex.printStackTrace();
		}
	}
}

