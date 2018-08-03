package de.lemonpie.beddocontrol.network;

import com.google.gson.Gson;
import de.lemonpie.beddocontrol.ui.Controller;
import javafx.application.Platform;
import logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class ControlSocket implements Runnable
{

	private static final int MAX = 10;
	private static final int SLEEP_TIME = 5000;

	private static Gson gson;
	private static String message;

	static
	{
		gson = new Gson();
	}

	private Map<CommandName, Command> commands;

	private String host;
	private int port;

	private Socket socket;
	private BufferedReader inputStream;
	private PrintWriter outputStream;

	private ControlSocketDelegate delegate;

	private Thread readerThread;

	public ControlSocket(String host, int port, ControlSocketDelegate delegate)
	{
		this.host = host;
		this.port = port;
		this.delegate = delegate;

		commands = new HashMap<>();
		init();
	}

	private void init()
	{
		if(delegate != null)
		{
			delegate.init(this);
		}
	}

	public void addCommand(Command command)
	{
		commands.put(command.name(), command);
	}

	public boolean connect()
	{
		message = "Trying to connect to " + host + ":" + port + "...";

		Logger.debug(message);
		Platform.runLater(() -> Controller.modalText.set(message));

		int counter = 0;
		while(counter < MAX)
		{
			try
			{
				initConnection();

				if(delegate != null)
				{
					delegate.onConnectionEstablished();
				}
				break;
			}
			catch(IOException e)
			{
				message = e.getMessage() + " Retry " + (counter + 1) + "/" + MAX + ".\nNext Retry in " + (SLEEP_TIME / 1000) + " seconds...";
				Logger.error(message);
				Platform.runLater(() -> Controller.modalText.set(message));
				try
				{
					Thread.sleep(SLEEP_TIME);
				}
				catch(InterruptedException e1)
				{
					Logger.error(e1);
					break;
				}
			}
			counter++;
		}
		return socket.isConnected();
	}

	private void initConnection() throws IOException
	{
		socket = new Socket();
		socket.connect(new InetSocketAddress(host, port));

		inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		outputStream = new PrintWriter(socket.getOutputStream(), true);

		readerThread = new Thread(this);
		readerThread.start();
	}

	public void interrupt()
	{
		readerThread.interrupt();
	}

	public void close() throws IOException
	{
		if(readerThread != null)
		{
			readerThread.interrupt();
		}

		inputStream.close();
		outputStream.close();
		socket.close();

		if(delegate != null)
		{
			delegate.onConnectionClosed();
		}
	}

	public void write(ControlCommandData command) throws SocketException
	{
		Logger.debug(gson.toJson(command));
		write(gson.toJson(command));
	}

	public void write(String data) throws SocketException
	{
		if(isNewValueComingFromServer())
		{
			return;
		}

		if(!socket.isClosed())
		{
			outputStream.println(data); // AutoFlush is enable
		}
		else
		{
			if(connect())
			{
				write(data);
			}
			else
			{
				throw new SocketException("Socket closed");
			}
		}
	}

	public static boolean isNewValueComingFromServer()
	{
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for(StackTraceElement element : stackTrace)
		{
			try
			{
				if(element.getClassName().startsWith("de."))
				{
					Class<?> clazz = Class.forName(element.getClassName());
					if(Command.class.isAssignableFrom(clazz))
					{
						return true;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void run()
	{
		try
		{
			String line;
			while((line = inputStream.readLine()) != null)
			{
				Logger.info("Read from Admin Socket: " + line);
				ControlCommandData commandData = gson.fromJson(line, ControlCommandData.class);

				commands.forEach((name, command) -> {
					if(name.getName().equalsIgnoreCase(commandData.getCommand()))
					{
						command.execute(commandData);
					}
				});

				if(Thread.interrupted())
				{
					break;
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
