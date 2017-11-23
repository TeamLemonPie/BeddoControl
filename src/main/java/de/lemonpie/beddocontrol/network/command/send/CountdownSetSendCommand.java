package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.model.timeline.CountdownType;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

public class CountdownSetSendCommand extends ControlCommandData {
	public CountdownSetSendCommand(int minutes, CountdownType type) {
		super(Scope.ADMIN, CommandName.PAUSE, type.ordinal());
		setValue(new JsonPrimitive(minutes));
	}
}
