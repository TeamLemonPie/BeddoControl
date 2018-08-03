package de.lemonpie.beddocontrol.network.command.read;

import de.lemonpie.beddocontrol.model.DataAccessable;
import de.lemonpie.beddocontrol.network.Command;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class PlayerWinProbabilityReadCommand implements Command
{

	private DataAccessable dataAccessable;

	public PlayerWinProbabilityReadCommand(DataAccessable dataAccessable)
	{
		this.dataAccessable = dataAccessable;
	}

	@Override
	public CommandName name()
	{
		return CommandName.WIN_PROBABILITY;
	}

	@Override
	public void execute(ControlCommandData data)
	{
		dataAccessable.getPlayer(data.getKey()).ifPresent(player -> player.setWinprobability(data.getValue().getAsInt()));
	}
}
