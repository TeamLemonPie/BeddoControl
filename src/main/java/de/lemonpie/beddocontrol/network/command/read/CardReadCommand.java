package de.lemonpie.beddocontrol.network.command.read;

import com.google.gson.JsonObject;
import de.lemonpie.beddocontrol.network.Command;
import de.lemonpie.beddocontrol.network.ControlCommandData;

/**
 * Format:
 * <code>key=boardId | (playerId</code>
 * <code>value={type, [index], card}</code>
 * <code>type = 0 (player), 1 (board)</code>
 */
public class CardReadCommand implements Command {
    @Override
    public String name() {
        return "card";
    }

    @Override
    public void execute(ControlCommandData data) {
        int id = data.getKey();
        JsonObject jsonObject = data.getValue().getAsJsonObject();
        int type = jsonObject.get("type").getAsInt();

        String cardCode = jsonObject.get("card").getAsString();

        if (type == 0) {
            int index = jsonObject.get("index").getAsInt(); // Card 0 or Card 1 of player
        } else if (type == 1) {

        }
    }
}
