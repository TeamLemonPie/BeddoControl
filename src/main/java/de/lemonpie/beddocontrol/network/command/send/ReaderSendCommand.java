package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonObject;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

public class ReaderSendCommand extends ControlCommandData {

    public enum ReaderType {
        PLAYER,
        BOARD
    }

    public ReaderSendCommand(ReaderType type, int readerId, int playerId) {
        super(Scope.ADMIN, CommandName.READER, readerId);
        JsonObject object = new JsonObject();
        object.addProperty("type", type.ordinal());
        if (type == ReaderType.PLAYER) {
            object.addProperty("playerId", playerId);
        }
        else
        {
        	object.addProperty("oldReaderId", playerId);
        }
        setValue(object);
    }
}
