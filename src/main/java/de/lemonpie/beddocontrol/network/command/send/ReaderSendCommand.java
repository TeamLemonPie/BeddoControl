package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonObject;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;

public class ReaderSendCommand extends CommandData
{
	public enum ReaderType
	{
		SEAT,
		BOARD
	}

	public ReaderSendCommand(ReaderType type, int readerId, int objectId)
	{
		super(Scope.ADMIN, CommandName.READER, readerId);
		JsonObject object = new JsonObject();
		object.addProperty("type", type.ordinal());
		if(type == ReaderType.SEAT)
		{
			object.addProperty("seatId", objectId);
		}
		else
		{
			object.addProperty("oldReaderId", objectId);
		}
		setValue(object);
	}
}
