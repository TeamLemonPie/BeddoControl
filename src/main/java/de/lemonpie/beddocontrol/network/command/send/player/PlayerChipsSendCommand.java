package de.lemonpie.beddocontrol.network.command.send.player;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

public class PlayerChipsSendCommand extends ControlCommandData
{

	public PlayerChipsSendCommand(int playerId, int chips)
	{
		super(Scope.ADMIN, CommandName.PLAYER_CHIP, playerId);
		setValue(new JsonPrimitive(chips));
	}
}
