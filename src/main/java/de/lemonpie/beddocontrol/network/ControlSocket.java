package de.lemonpie.beddocontrol.network;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ControlSocket implements Runnable {

	private static final int MAX = 10;

	private String host;
	private int port;

	private Socket socket;
	private BufferedReader inputStream;
	private PrintWriter outputStream;

	private Thread readerThread;

	public ControlSocket(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public boolean connect() {
		int counter = 0;
		while (counter < MAX) {
			try {
				initConnection();
				break;
			} catch (IOException e) {
				e.printStackTrace();
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					break;
				}
			}
			counter++;
		}
		return socket.isConnected();
	}

	private void initConnection() throws IOException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(host, port));

		inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		outputStream = new PrintWriter(socket.getOutputStream(), true);

		readerThread = new Thread(this);
		readerThread.start();
	}

	public void interrupt() {
		readerThread.interrupt();
	}

	public void close() throws IOException {
		inputStream.close();
		outputStream.close();
		socket.close();
	}

	public void write(ControlCommandData command) {
		Gson gson = new Gson();
		write(gson.toJson(command));
	}

	public void write(String data) {
		outputStream.println(data); // AutoFlush is enable
	}

	@Override
	public void run() {
		try {
			String line;
			while ((line = inputStream.readLine()) != null) {
				System.out.println(line);

				if (Thread.interrupted()) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
