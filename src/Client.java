import config.Config;
import config.ConfigException;

import encryption.FeistelCipher;
import static encryption.BytePadding.padTo8Bytes;
import static encryption.BytePadding.unpadBytes;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

import java.net.Socket;

public class Client
{

	static class Arguments
	{
		public static Arguments create(String[] argv) {
			Arguments args = new Arguments();
			JCommander commander = new JCommander(args, argv);
			args.commander = commander;
			return args;
		}
		public JCommander commander;

		@Parameter(names = { "-u", "--username" }, description = "The client's username.")
		public String username = "isupeene";
        
		@Parameter(names = { "-h", "--help" }, help = true, description = "Displays this help message and exits.")
		public boolean help;
	}

	private static Arguments parseArgs(String[] argv)
	{
		Arguments args = Arguments.create(argv);
		if (args.help)
		{
			args.commander.usage();
			System.exit(1);
		}

		return args;
	}

	public static void main(String[] argv) throws ConfigException, IOException
	{
		Arguments args = parseArgs(argv);
		int[] key = Config.getKey(args.username);
		FeistelCipher cipher = new FeistelCipher(key);

		// Here, we assume that we are on the same machine.
		Socket sock = new Socket("127.0.0.1", 16000);
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(sock.getInputStream())
		);
		PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);

		BufferedReader console = new BufferedReader(
			new InputStreamReader(System.in)
		);


		writer.println(args.username);
		while (true)
		{
			String filename = console.readLine();
			byte[] filenameBytes = padTo8Bytes(filename.getBytes());
			cipher.encrypt(filenameBytes);
			String base64Filename = encodeBase64String(filenameBytes);
			writer.println(base64Filename);

			String base64Response = reader.readLine();
			byte[] responseBytes = decodeBase64(base64Response);
			cipher.decrypt(responseBytes);
			String response = new String(unpadBytes(responseBytes));
			System.out.println(response);
		}
	}
}

