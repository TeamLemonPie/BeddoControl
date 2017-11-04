package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

public class BlockSendCommand extends ControlCommandData {

	public enum Option {
		NONE,
		BOARD,
		ALL;
	}

	public BlockSendCommand(Option option) {
		super(Scope.ADMIN, CommandName.BLOCK, option.ordinal());
	}
}
