package de.lemonpie.beddocontrol.network.listener;

import de.lemonpie.beddocommon.model.ObservableListListener;
import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocommon.network.client.ControlSocket;

public class SeatListListenerImpl implements ObservableListListener<Seat>
{
	private ControlSocket socket;

	public SeatListListenerImpl(ControlSocket socket)
	{
		this.socket = socket;
	}

	@Override
	public void addObjectToList(Seat obj)
	{
		obj.addListener(new SeatListenerImpl(socket));
	}

	@Override
	public void removeObjectFromList(Seat obj)
	{
	}
}
