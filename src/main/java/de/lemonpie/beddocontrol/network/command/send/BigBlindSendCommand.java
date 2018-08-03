package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

public class BigBlindSendCommand extends ControlCommandData
{

	public BigBlindSendCommand(int value)
	{
		super(Scope.ADMIN, CommandName.BIG_BLIND, -1);
		setValue(new JsonPrimitive(value));
	}
}
