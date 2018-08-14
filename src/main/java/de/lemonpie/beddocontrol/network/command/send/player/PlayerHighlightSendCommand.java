package de.lemonpie.beddocontrol.network.command.send.player;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class PlayerHighlightSendCommand extends CommandData
{

	public PlayerHighlightSendCommand(int playerId, boolean value)
	{
		super(Scope.ADMIN, CommandName.PLAYER_HIGHLIGHT, playerId);
		setValue(new JsonPrimitive(value));
	}
}
