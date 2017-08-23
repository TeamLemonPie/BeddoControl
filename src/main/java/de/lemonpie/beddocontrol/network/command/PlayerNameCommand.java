package de.lemonpie.beddocontrol.network.command;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.network.ControlCommand;

public class PlayerNameCommand extends ControlCommand {

    public enum NameType {
        NAME("name"),
        TWITCH("twitchName");

        private String command;

        NameType(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }

    public PlayerNameCommand(NameType nameType, int playerId, String value) {
        super("admin", nameType.getCommand(), playerId);
        setValue(new JsonPrimitive(value));
    }
}
