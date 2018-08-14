package de.lemonpie.beddocontrol.network.command.send.player;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class PlayerOpSendCommand extends CommandData
{

	public enum Type
	{
		ADD("add"),
		REMOVE("remove");

		private String command;

		Type(String command)
		{
			this.command = command;
		}

		public String getCommand()
		{
			return command;
		}
	}

	/**
	 * Add command.
	 */
	public PlayerOpSendCommand()
	{
		super(Scope.ADMIN, CommandName.PLAYER_OP, 0);
		setValue(new JsonPrimitive(Type.ADD.command));
	}

	/**
	 * Remove Command.
	 *
	 * @param playerId player to remove
	 */
	public PlayerOpSendCommand(int playerId)
	{
		super(Scope.ADMIN, CommandName.PLAYER_OP, playerId);
		setValue(new JsonPrimitive(Type.REMOVE.command));
	}
}
