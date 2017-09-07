package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

public class PlayerHideSendCommand extends ControlCommandData {

    public PlayerHideSendCommand(int playerId, boolean hide) {
        super(Scope.ADMIN, CommandName.PLAYER_HIDE, playerId);
        setValue(new JsonPrimitive(hide));
    }
}
