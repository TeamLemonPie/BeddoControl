package de.lemonpie.beddocontrol.network.command.send.player;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class PlayerNameSendCommand extends CommandData
{

	public enum NameType
	{
		NAME(CommandName.PLAYER_NAME),
		TWITCH(CommandName.PLAYER_TWITCH);

		private CommandName command;

		NameType(CommandName command)
		{
			this.command = command;
		}

		public CommandName getCommand()
		{
			return command;
		}
	}

	public PlayerNameSendCommand(NameType nameType, int playerId, String value)
	{
		super(Scope.ADMIN, nameType.getCommand(), playerId);
		setValue(new JsonPrimitive(value));
	}
}
