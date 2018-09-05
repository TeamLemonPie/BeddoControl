package de.lemonpie.beddocontrol.network.command.read;

import de.lemonpie.beddocommon.network.Command;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocontrol.model.DataAccessible;

public class PlayerHighlightReadCommand implements Command
{

	private DataAccessible dataAccessible;

	public PlayerHighlightReadCommand(DataAccessible dataAccessible)
	{
		this.dataAccessible = dataAccessible;
	}

	@Override
	public CommandName name()
	{
		return CommandName.PLAYER_HIGHLIGHT;
	}

	@Override
	public void execute(CommandData data)
	{
		dataAccessible.getPlayer(data.getKey()).ifPresent(player -> player.setHighlighted(data.getValue().getAsBoolean()));
	}
}
