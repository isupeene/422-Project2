import config.Config;
import config.ConfigException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.Arrays; // TODO: Remove - not needed

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

	public static void main(String[] argv) throws ConfigException
	{
		Arguments args = parseArgs(argv);
		System.out.println(Arrays.toString(Config.getKey(args.username)));
	}
}

