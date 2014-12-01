package config;

public class ConfigException extends Exception
{
	public ConfigException(String message)
	{
		super(message);
	}

	public ConfigException(String message, Throwable innerException)
	{
		super(message, innerException);
	}
}

