package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonObject;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class ReaderSendCommand extends ControlCommandData {

    public enum ReaderType {
        PLAYER,
        BOARD
    }

    public ReaderSendCommand(ReaderType type, int readerId, int id) {
        super("admin", "reader", readerId);
        JsonObject object = new JsonObject();
        object.addProperty("type", type.ordinal());
        if (type == ReaderType.PLAYER) {
            object.addProperty("playerId", id);
        } else if (type == ReaderType.BOARD) {
            object.addProperty("boardId", id);
        }
        setValue(object);
    }
}
