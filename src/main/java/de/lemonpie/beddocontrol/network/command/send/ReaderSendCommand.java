package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonObject;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class ReaderSendCommand extends ControlCommandData {

    public ReaderSendCommand(int readerId, int boardId) {
        super("admin", "reader", readerId);
        JsonObject object = new JsonObject();
        object.addProperty("type", 1);
        object.addProperty("index", boardId);
        setValue(object);
    }

    public ReaderSendCommand(int readerId, int playerId, int index) {
        super("admin", "reader", readerId);
        JsonObject object = new JsonObject();
        object.addProperty("type", 0);
        object.addProperty("playerId", playerId);
        object.addProperty("index", index);
        setValue(object);
    }
}
