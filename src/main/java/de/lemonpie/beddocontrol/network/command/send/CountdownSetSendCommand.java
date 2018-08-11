package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.ControlCommandData;
import de.lemonpie.beddocommon.network.Scope;
import de.lemonpie.beddocontrol.model.timeline.CountdownType;

public class CountdownSetSendCommand extends ControlCommandData
{
	public CountdownSetSendCommand(int minutes, CountdownType type)
	{
		super(Scope.ADMIN, CommandName.PAUSE, type.ordinal());
		setValue(new JsonPrimitive(minutes));
	}
}
