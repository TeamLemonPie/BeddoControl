package de.lemonpie.beddocontrol.network.command.send.player;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class PlayerChipsSendCommand extends CommandData
{

	public PlayerChipsSendCommand(int playerId, int chips)
	{
		super(Scope.ADMIN, CommandName.PLAYER_CHIP, playerId);
		setValue(new JsonPrimitive(chips));
	}
}
