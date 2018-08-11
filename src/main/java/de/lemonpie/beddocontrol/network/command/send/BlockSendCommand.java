package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.ControlCommandData;
import de.lemonpie.beddocommon.network.Scope;

public class BlockSendCommand extends ControlCommandData
{

	public enum Option
	{
		NONE,
		BOARD,
		ALL
	}

	public BlockSendCommand(Option option)
	{
		super(Scope.ADMIN, CommandName.BLOCK, option.ordinal());
	}
}
