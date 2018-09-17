package de.lemonpie.beddocontrol.network.listener;

import de.lemonpie.beddocommon.model.card.Card;
import de.lemonpie.beddocommon.network.client.ControlSocket;
import de.lemonpie.beddocontrol.listener.BoardListener;
import de.lemonpie.beddocontrol.network.command.send.ReaderSendCommand;
import de.lemonpie.beddocontrol.network.command.send.ReaderSendCommand.ReaderType;
import de.tobias.logger.Logger;

import java.net.SocketException;

public class BoardListenerImpl implements BoardListener
{

	private ControlSocket socket;

	public BoardListenerImpl(ControlSocket socket)
	{
		this.socket = socket;
	}

	@Override
	public void cardDidChangeAtIndex(int index, Card card)
	{
	}

	@Override
	public void boardReaderIdDidChange(int index, int readerId, int oldReaderId)
	{
		ReaderSendCommand cmd = new ReaderSendCommand(ReaderType.BOARD, readerId, oldReaderId);
		try
		{
			socket.write(cmd);
		}
		catch(SocketException e)
		{
			Logger.error(e);
		}
	}

	@Override
	public void smallBlindDidChange(int newValue)
	{
	}

	@Override
	public void bigBlindDidChange(int newValue)
	{
	}

	@Override
	public void anteDidChange(int newValue)
	{
	}

	@Override
	public void lockDidChange(boolean newValue)
	{
	}
}
