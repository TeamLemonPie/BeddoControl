package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

public class PlayerStateSendCommand extends ControlCommandData {

    public PlayerStateSendCommand(int playerId, PlayerState state) {
        super(Scope.ADMIN, CommandName.PLAYER_STATE, playerId);
        setValue(new JsonPrimitive(state.name()));
    }
}
