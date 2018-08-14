package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class ClearSendCommand extends CommandData
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
