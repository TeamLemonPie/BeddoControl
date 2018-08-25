package de.lemonpie.beddocontrol.network.command.read;

import de.lemonpie.beddocommon.network.Command;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocontrol.model.DataAccessible;


public class SeatPlayerReadCommand implements Command
{
	private DataAccessible dataAccessible;

	@Override
	public CommandName name()
	{
		return CommandName.SEAT_PLAYER_ID;
	}

	public SeatPlayerReadCommand(DataAccessible dataAccessible)
	{
		this.dataAccessible = dataAccessible;
	}

	@Override
	public void execute(CommandData data)
	{
		int id = data.getKey();
		dataAccessible.seatAssignNewPlayerId(id, data.getValue().getAsInt());
	}
}
