package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

public class CountdownSetSendCommand extends ControlCommandData {
	public CountdownSetSendCommand(int minutes) {
		super(Scope.ADMIN, CommandName.COUNTDOWN, 0);
		setValue(new JsonPrimitive(minutes));
	}
}
