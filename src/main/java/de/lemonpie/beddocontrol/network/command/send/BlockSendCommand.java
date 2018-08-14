package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class BlockSendCommand extends CommandData
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
