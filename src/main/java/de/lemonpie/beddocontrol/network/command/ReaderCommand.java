package de.lemonpie.beddocontrol.network.command;

import com.google.gson.JsonObject;
import de.lemonpie.beddocontrol.network.ControlCommand;

public class ReaderCommand extends ControlCommand {

    public ReaderCommand(int readerId, int boardId) {
        super("admin", "reader", readerId);
        JsonObject object = new JsonObject();
        object.addProperty("type", 1);
        object.addProperty("index", boardId);
        setValue(object);
    }

    public ReaderCommand(int readerId, int playerId, int index) {
        super("admin", "reader", readerId);
        JsonObject object = new JsonObject();
        object.addProperty("type", 0);
        object.addProperty("playerId", playerId);
        object.addProperty("index", index);
        setValue(object);
    }
}
