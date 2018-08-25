package de.lemonpie.beddocontrol.network.listener;

import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocommon.model.seat.SeatListener;
import de.lemonpie.beddocommon.network.client.ControlSocket;
import de.lemonpie.beddocontrol.network.command.send.ReaderSendCommand;
import de.lemonpie.beddocontrol.network.command.send.SeatSendCommand;

import java.net.SocketException;

public class SeatListenerImpl implements SeatListener
{
	private ControlSocket socket;

	public SeatListenerImpl(ControlSocket socket)
	{
		this.socket = socket;
	}

	@Override
	public void readerIdDidChange(Seat seat, int readerId)
	{
		ReaderSendCommand cmd = new ReaderSendCommand(ReaderSendCommand.ReaderType.SEAT, readerId, seat.getId());
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
	public void playerIdDidChange(Seat seat, int playerId)
	{
		SeatSendCommand cmd = new SeatSendCommand(seat.getId(), playerId);
		try
		{
			socket.write(cmd);
		}
		catch(SocketException e)
		{
			e.printStackTrace();
		}
	}
}
