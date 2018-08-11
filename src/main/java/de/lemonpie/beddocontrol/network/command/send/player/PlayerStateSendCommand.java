package de.lemonpie.beddocontrol.network.command.send.player;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.ControlCommandData;
import de.lemonpie.beddocommon.network.Scope;
import de.lemonpie.beddocontrol.model.PlayerState;

public class PlayerStateSendCommand extends ControlCommandData
{

	public PlayerStateSendCommand(int playerId, PlayerState state)
	{
		super(Scope.ADMIN, CommandName.PLAYER_STATE, playerId);
		setValue(new JsonPrimitive(state.name()));
	}
}
