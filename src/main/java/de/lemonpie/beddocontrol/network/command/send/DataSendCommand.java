package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.ControlCommandData;
import de.lemonpie.beddocommon.network.Scope;

public class DataSendCommand extends ControlCommandData
{
	public DataSendCommand()
	{
		super(Scope.ADMIN, CommandName.DATA, 0);
	}
}
