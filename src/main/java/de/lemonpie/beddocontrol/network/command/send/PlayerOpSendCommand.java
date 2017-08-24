package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class PlayerOpSendCommand extends ControlCommandData {

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
    public PlayerOpSendCommand() {
        super("admin", "player-op", 0);
        setValue(new JsonPrimitive(Type.ADD.command));
    }

    /**
     * Remove Command.
     *
     * @param playerId player to remove
     */
    public PlayerOpSendCommand(int playerId) {
        super("admin", "player-op", playerId);
        setValue(new JsonPrimitive(Type.REMOVE.command));
    }
}
