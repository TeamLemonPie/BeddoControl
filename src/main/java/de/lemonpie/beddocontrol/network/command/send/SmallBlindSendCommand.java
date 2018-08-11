package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;
import de.lemonpie.beddocommon.network.client.ControlCommandData;

public class SmallBlindSendCommand extends ControlCommandData
{

	public SmallBlindSendCommand(int value)
	{
		super(Scope.ADMIN, CommandName.SMALL_BLIND, -1);
		setValue(new JsonPrimitive(value));
	}
}
