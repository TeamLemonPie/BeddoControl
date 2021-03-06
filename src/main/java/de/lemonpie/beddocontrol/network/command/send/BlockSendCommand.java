package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocommon.model.BlockOption;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class BlockSendCommand extends CommandData
{
	public BlockSendCommand(BlockOption blockOption)
	{
		super(Scope.ADMIN, CommandName.BLOCK, blockOption.ordinal());
	}
}
