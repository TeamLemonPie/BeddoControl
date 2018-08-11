package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;
import de.lemonpie.beddocommon.network.client.ControlCommandData;

public class DataSendCommand extends ControlCommandData
{
	public DataSendCommand()
	{
		super(Scope.ADMIN, CommandName.DATA, 0);
	}
}
