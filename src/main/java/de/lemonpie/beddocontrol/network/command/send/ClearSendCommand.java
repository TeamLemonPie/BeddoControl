package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;
import de.lemonpie.beddocommon.network.client.ControlCommandData;

public class ClearSendCommand extends ControlCommandData
{

	public ClearSendCommand()
	{
		super(Scope.ADMIN, CommandName.CLEAR, -1);
	}

	public ClearSendCommand(int key)
	{
		super(Scope.ADMIN, CommandName.CLEAR, key);
	}
}
