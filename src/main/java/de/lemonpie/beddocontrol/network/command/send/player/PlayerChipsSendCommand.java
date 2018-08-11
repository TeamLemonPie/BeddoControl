package de.lemonpie.beddocontrol.network.command.send.player;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;
import de.lemonpie.beddocommon.network.client.ControlCommandData;

public class PlayerChipsSendCommand extends ControlCommandData
{

	public PlayerChipsSendCommand(int playerId, int chips)
	{
		super(Scope.ADMIN, CommandName.PLAYER_CHIP, playerId);
		setValue(new JsonPrimitive(chips));
	}
}
