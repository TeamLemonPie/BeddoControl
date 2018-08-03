package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

public class DataSendCommand extends ControlCommandData
{
	public DataSendCommand()
	{
		super(Scope.ADMIN, CommandName.DATA, 0);
	}
}
