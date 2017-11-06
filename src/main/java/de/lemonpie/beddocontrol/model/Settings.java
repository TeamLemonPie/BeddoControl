package de.lemonpie.beddocontrol.model;

public class Settings
{
	private String hostName;
	private int port;
	
	public Settings()
	{
		
	}
	
	public Settings(String hostName, int port)
	{		
		this.hostName = hostName;
		this.port = port;
	}

	public String getHostName()
	{
		return hostName;
	}

	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	@Override
	public String toString()
	{
		return "Settings [hostName=" + hostName + ", port=" + port + "]";
	}
}