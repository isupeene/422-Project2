package config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class Config
{
	private static final Path keysFilename = Paths.get("config/keys.config");

	private static Map<String, int[]> keys = null;
	private static ConfigException initializationError = null;

	static { initialize(); }

	// separate method because you can't return from a static initializer.
	private static void initialize() {
		try
		{
			String json = new String(Files.readAllBytes(keysFilename), StandardCharsets.UTF_8);
			keys = new Gson().fromJson(
				json,
				new TypeToken<HashMap<String, int[]>>() {}.getType()
			);

			if (keys == null) {
				initializationError = new ConfigException(
					"The keys file was empty!"
				);
				return;
			}

			for (int[] key : keys.values())
			{
				if (key == null)
				{
					initializationError = new ConfigException(
						"The keys file was malformed - found a null key."
					);
					return;
				}

				if (key.length != 4)
				{
					initializationError = new ConfigException(
						"The keys file was malformed - found a key of invalid length:\n" +
						Arrays.toString(key)
					);
					return;
				}
			}
		}
		catch (IOException ex)
		{
			initializationError = new ConfigException(
				"An error occurred reading the keys file: " + ex.getMessage(), ex
			);
		}
		catch (JsonSyntaxException ex)
		{
			initializationError = new ConfigException(
				"The keys file was malformed: " + ex.getMessage(), ex
			);
		}
	}

	public static int[] getKey(String username) throws ConfigException
	{
		if (initializationError != null) throw initializationError;

		return keys.get(username);
	}

	public static Iterable<Map.Entry<String, int[]>> iterateKeys() throws ConfigException
	{
		if (initializationError != null) throw initializationError;

		return keys.entrySet();
	}
}

