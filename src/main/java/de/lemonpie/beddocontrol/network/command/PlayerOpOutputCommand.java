package de.lemonpie.beddocontrol.network.command;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class PlayerOpOutputCommand extends ControlCommandData {

    public enum Type {
        ADD("add"),
        REMOVE("remove");

        private String command;

        Type(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }

    /**
     * Add command.
     */
    public PlayerOpOutputCommand() {
        super("admin", "player-op", 0);
        setValue(new JsonPrimitive(Type.ADD.command));
    }

    /**
     * Remove Command.
     *
     * @param playerId player to remove
     */
    public PlayerOpOutputCommand(int playerId) {
        super("admin", "player-op", playerId);
        setValue(new JsonPrimitive(Type.REMOVE.command));
    }
}
