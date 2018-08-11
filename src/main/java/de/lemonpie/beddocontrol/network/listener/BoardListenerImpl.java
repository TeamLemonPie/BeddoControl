package de.lemonpie.beddocontrol.network.listener;

import de.lemonpie.beddocommon.network.ControlSocket;
import de.lemonpie.beddocontrol.listener.BoardListener;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.network.command.send.ReaderSendCommand;
import de.lemonpie.beddocontrol.network.command.send.ReaderSendCommand.ReaderType;

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
			e.printStackTrace();
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
}
