package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class SmallBlindSendCommand extends CommandData
{

	public SmallBlindSendCommand(int value)
	{
		super(Scope.ADMIN, CommandName.SMALL_BLIND, -1);
		setValue(new JsonPrimitive(value));
	}
}
