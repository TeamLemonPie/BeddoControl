package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

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
