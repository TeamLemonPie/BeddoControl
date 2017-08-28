package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.CommandName;
import de.lemonpie.beddocontrol.Scope;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class PlayerNameSendCommand extends ControlCommandData {

    public enum NameType {
        NAME(CommandName.PLAYER_NAME),
        TWITCH(CommandName.PLAYER_TWITCH);

        private CommandName command;

        NameType(CommandName command) {
            this.command = command;
        }

        public CommandName getCommand() {
            return command;
        }
    }

    public PlayerNameSendCommand(NameType nameType, int playerId, String value) {
        super(Scope.ADMIN, nameType.getCommand(), playerId);
        setValue(new JsonPrimitive(value));
    }
}
