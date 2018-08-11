package de.lemonpie.beddocontrol.network.command.read;

import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.client.Command;
import de.lemonpie.beddocommon.network.client.ControlCommandData;
import de.lemonpie.beddocontrol.model.DataAccessable;

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
