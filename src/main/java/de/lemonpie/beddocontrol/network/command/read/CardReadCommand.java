package de.lemonpie.beddocontrol.network.command.read;

import com.google.gson.JsonObject;
import de.lemonpie.beddocontrol.CommandName;
import de.lemonpie.beddocontrol.main.BeddoControlMain;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.network.Command;
import de.lemonpie.beddocontrol.network.ControlCommandData;

/**
 * Format:
 * <code>key=boardId | playerId</code>
 * <code>value={type, [index], card}</code>
 * <code>type = 0 (player), 1 (board)</code>
 */
public class CardReadCommand implements Command {
    @Override
    public CommandName name() {
        return CommandName.CARD;
    }

    @Override
    public void execute(ControlCommandData data) {
        int id = data.getKey();

        JsonObject jsonObject = data.getValue().getAsJsonObject();
        int type = jsonObject.get("type").getAsInt();

        String cardCode = jsonObject.get("card").getAsString();
        Card card = Card.fromString(cardCode);

        if (type == 0) {
            int index = jsonObject.get("index").getAsInt(); // Card 0 or Card 1 of player
            BeddoControlMain.getPlayer(id).ifPresent(p -> p.setCard(index, card));
        } else if (type == 1) {
            BeddoControlMain.getBoard().setCard(id, card);
        }
    }
}
