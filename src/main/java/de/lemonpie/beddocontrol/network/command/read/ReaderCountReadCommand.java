package de.lemonpie.beddocontrol.network.command.read;

import de.lemonpie.beddocontrol.model.DataAccessable;
import de.lemonpie.beddocontrol.network.Command;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class ReaderCountReadCommand implements Command {

	public enum Type {
		ADD, REMOVE
	}

	private DataAccessable dataAccessable;

	public ReaderCountReadCommand(DataAccessable dataAccessable) {
		this.dataAccessable = dataAccessable;
	}

	@Override
	public CommandName name() {
		return CommandName.READER_COUNT;
	}

	@Override
	public void execute(ControlCommandData data) {
		Type type = Type.values()[data.getValue().getAsInt()];

		if (type == Type.ADD) {
			dataAccessable.increaseBeddoFabrikCount();
		} else if (type == Type.REMOVE) {
			dataAccessable.decreaseBeddoFabrikCount();
		}
	}
}
