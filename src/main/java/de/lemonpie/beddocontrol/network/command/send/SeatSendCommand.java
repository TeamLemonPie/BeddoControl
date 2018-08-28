package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class SeatSendCommand extends CommandData
{
	public SeatSendCommand(int seatId, int playerId)
	{
		super(Scope.ADMIN, CommandName.SEAT_PLAYER_ID, seatId);
		setValue(new JsonPrimitive(playerId));
	}
}
