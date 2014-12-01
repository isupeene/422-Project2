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

	private static void handleClient(Socket clientSock)
	{
		try
		{
			BufferedReader clientReader = new BufferedReader(
				new InputStreamReader(clientSock.getInputStream())
			);
			PrintWriter clientWriter = new PrintWriter(clientSock.getOutputStream(), true);
			String username = clientReader.readLine();
			int[] key = Config.getKey(username);
			System.out.println("Accepted a new user: " + username);
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

