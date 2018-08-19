package de.lemonpie.beddocontrol.network.command.read;

import de.lemonpie.beddocommon.network.Command;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocontrol.model.DataAccessible;

public class ReaderCountReadCommand implements Command
{

	public enum Type
	{
		ADD, REMOVE
	}

	private DataAccessible dataAccessable;

	public ReaderCountReadCommand(DataAccessible dataAccessable)
	{
		this.dataAccessable = dataAccessable;
	}

	@Override
	public CommandName name()
	{
		return CommandName.READER_COUNT;
	}

	@Override
	public void execute(CommandData data)
	{
		Type type = Type.values()[data.getValue().getAsInt()];

		if(type == Type.ADD)
		{
			dataAccessable.increaseBeddoFabrikCount();
		}
		else if(type == Type.REMOVE)
		{
			dataAccessable.decreaseBeddoFabrikCount();
		}
	}
}
