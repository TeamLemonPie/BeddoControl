package de.lemonpie.beddocontrol.network.command.send.player;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;
import de.lemonpie.beddocommon.network.client.ControlCommandData;

public class PlayerHighlightSendCommand extends ControlCommandData
{

	public PlayerHighlightSendCommand(int playerId, boolean value)
	{
		super(Scope.ADMIN, CommandName.PLAYER_HIGHLIGHT, playerId);
		setValue(new JsonPrimitive(value));
	}
}
