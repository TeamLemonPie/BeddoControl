package de.lemonpie.beddocontrol.network.command.read;

import de.lemonpie.beddocommon.network.Command;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocontrol.model.DataAccessible;

public class PlayerWinProbabilityReadCommand implements Command
{

	private DataAccessible dataAccessable;

	public PlayerWinProbabilityReadCommand(DataAccessible dataAccessable)
	{
		this.dataAccessable = dataAccessable;
	}

	@Override
	public CommandName name()
	{
		return CommandName.WIN_PROBABILITY;
	}

	@Override
	public void execute(CommandData data)
	{
		dataAccessable.getPlayer(data.getKey()).ifPresent(player -> player.setWinprobability(data.getValue().getAsInt()));
	}
}
