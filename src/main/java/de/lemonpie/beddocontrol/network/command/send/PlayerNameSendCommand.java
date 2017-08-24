package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class PlayerNameSendCommand extends ControlCommandData {

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

    public PlayerNameSendCommand(NameType nameType, int playerId, String value) {
        super("admin", nameType.getCommand(), playerId);
        setValue(new JsonPrimitive(value));
    }
}
