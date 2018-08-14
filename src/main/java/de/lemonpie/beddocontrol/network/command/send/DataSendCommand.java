package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class DataSendCommand extends CommandData
{
	public DataSendCommand()
	{
		super(Scope.ADMIN, CommandName.DATA, 0);
	}
}
