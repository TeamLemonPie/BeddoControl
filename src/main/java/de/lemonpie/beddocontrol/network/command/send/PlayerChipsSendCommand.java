package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class PlayerChipsSendCommand extends ControlCommandData {

    public PlayerChipsSendCommand(int playerId, int chips) {
        super("admin", "chips", playerId);
        setValue(new JsonPrimitive(chips));
    }
}
