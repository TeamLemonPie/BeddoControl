package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.CommandName;
import de.lemonpie.beddocontrol.Scope;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class PlayerChipsSendCommand extends ControlCommandData {

    public PlayerChipsSendCommand(int playerId, int chips) {
        super(Scope.ADMIN, CommandName.PLAYER_CHIP, playerId);
        setValue(new JsonPrimitive(chips));
    }
}
