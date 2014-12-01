import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

import java.net.Socket;
import java.net.ServerSocket;

class Server
{
	public static void main(String[] args) throws IOException
	{
		ServerSocket serverSock = new ServerSocket(16000);

		while (true)
		{
			final Socket clientSock = serverSock.accept();
			System.out.println("Accepted");
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

			while (true)
			{
				String requestedFile = clientReader.readLine();
				if (requestedFile == null) break;
				clientWriter.println(requestedFile);
			}
		}
		catch (IOException ex) {} // The thread died - that's OK.
	}
}

