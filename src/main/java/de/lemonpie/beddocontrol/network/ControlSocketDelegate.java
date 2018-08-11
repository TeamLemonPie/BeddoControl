package de.lemonpie.beddocontrol.network;

public interface ControlSocketDelegate
{
	void init(ControlSocket socket);

	void onConnectionEstablished();

	void onConnectionClosed();

	void startConnecting(String host, int port);

	void onConnectionFailed(Exception e, int retry);
}
