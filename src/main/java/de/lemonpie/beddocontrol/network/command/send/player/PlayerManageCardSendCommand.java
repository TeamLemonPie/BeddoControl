package de.lemonpie.beddocontrol.network.command.send.player;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class PlayerManageCardSendCommand extends CommandData
{
	public PlayerManageCardSendCommand(int playerId, int manageCardID)
	{
		super(Scope.ADMIN, CommandName.PLAYER_MANAGE_CARD, playerId);
		setValue(new JsonPrimitive(manageCardID));
	}
}
