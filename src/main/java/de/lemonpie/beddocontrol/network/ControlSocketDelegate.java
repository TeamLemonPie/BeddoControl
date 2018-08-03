package de.lemonpie.beddocontrol.network;

public interface ControlSocketDelegate
{
	void init(ControlSocket socket);

	void onConnectionEstablished();

	void onConnectionClosed();
}
