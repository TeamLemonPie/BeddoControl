package de.lemonpie.beddocontrol.network.command.read;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.lemonpie.beddocontrol.model.Board;
import de.lemonpie.beddocontrol.model.DataAccessable;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.network.Command;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class DataReadCommand implements Command {

    private DataAccessable dataAccessable;

    public DataReadCommand(DataAccessable dataAccessable) {
        this.dataAccessable = dataAccessable;
    }

    @Override
    public CommandName name() {
        return CommandName.DATA;
    }

    @Override
    public void execute(ControlCommandData data) {
        if (data.getValue() instanceof JsonObject) {
            JsonObject values = (JsonObject) data.getValue();
            JsonArray players = values.getAsJsonArray("players");
            JsonArray board = values.getAsJsonArray("board");
            JsonArray reader = values.getAsJsonArray("reader");

            if (players != null) {
                // Clear old data
                dataAccessable.getPlayers().clear(); // TODO Should use some listener for ui

                players.forEach(elem -> {
                    if (elem instanceof JsonObject) {
                        JsonObject obj = (JsonObject) elem;
                        int id = obj.getAsJsonPrimitive("id").getAsInt();
                        String name = obj.getAsJsonPrimitive("name").getAsString();
                        String twitchName = obj.getAsJsonPrimitive("twitchName").getAsString();
                        boolean hide = obj.getAsJsonPrimitive("hide").getAsBoolean();
                        int chips = obj.getAsJsonPrimitive("chips").getAsInt();
                        Card cardLeft = Card.fromString(obj.getAsJsonPrimitive("cardLeft").getAsString());
                        Card cardRight = Card.fromString(obj.getAsJsonPrimitive("cardRight").getAsString());

                        Player player = new Player(id);
                        player.setName(name);
                        player.setTwitchName(twitchName);
                        // TODO Hide
                        player.setChips(chips);
                        player.setCardLeft(cardLeft);
                        player.setCardRight(cardRight);

                        dataAccessable.addPlayer(player);
                    }
                });
            }

            if (board != null) {
                Board b = dataAccessable.getBoard();

                board.forEach(elem -> {
                    if (elem instanceof JsonObject) {
                        JsonObject obj = (JsonObject) elem;
                        int id = obj.getAsJsonPrimitive("id").getAsInt();
                        Card card = Card.fromString(obj.getAsJsonPrimitive("card").getAsString());

                        b.setCard(id, card);
                    }
                });
            }

            if (reader != null) {
                // TODO
            }
        }
    }
}