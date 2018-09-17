package de.lemonpie.beddocontrol.network.listener;

import de.lemonpie.beddocommon.model.ObservableListListener;
import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocommon.network.client.ControlSocket;
import de.lemonpie.beddocontrol.midi.listener.MidiSeatListener;
import de.lemonpie.beddocontrol.model.DataAccessible;

public class SeatListListenerImpl implements ObservableListListener<Seat>
{
	private ControlSocket socket;
	private DataAccessible dataAccessible;

	public SeatListListenerImpl(ControlSocket socket, DataAccessible dataAccessible)
	{
		this.socket = socket;
		this.dataAccessible = dataAccessible;
	}

	@Override
	public void addObjectToList(Seat obj)
	{
		obj.addListener(new SeatListenerImpl(socket));
		obj.addListener(new MidiSeatListener(dataAccessible));
	}

	@Override
	public void removeObjectFromList(Seat obj)
	{
	}
}
