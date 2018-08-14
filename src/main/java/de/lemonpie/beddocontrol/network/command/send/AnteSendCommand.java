package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class AnteSendCommand extends CommandData
{

	public AnteSendCommand(int value)
	{
		super(Scope.ADMIN, CommandName.ANTE, -1);
		setValue(new JsonPrimitive(value));
	}
}
