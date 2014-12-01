import config.Config;
import config.ConfigException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

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

		// Here, we assume that we are on the same machine.
		Socket sock = new Socket("127.0.0.1", 16000);
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(sock.getInputStream())
		);
		PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);

		BufferedReader console = new BufferedReader(
			new InputStreamReader(System.in)
		);

		while (true)
		{
			String filename = console.readLine();
			writer.println(filename);
			String response = reader.readLine();
			System.out.println(response);
		}
	}
}

