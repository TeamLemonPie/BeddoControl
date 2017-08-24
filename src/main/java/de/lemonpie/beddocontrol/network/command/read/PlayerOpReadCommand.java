package de.lemonpie.beddocontrol.network.command.read;

import de.lemonpie.beddocontrol.main.BeddoControlMain;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.network.Command;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class PlayerOpReadCommand implements Command {
    @Override
    public String name() {
        return "player-op";
    }

    @Override
    public void execute(ControlCommandData command) {
        String op = command.getValue().getAsString();
        if (op.equals("add")) {
            int playerId = command.getKey();

            Player player = new Player(playerId);
            BeddoControlMain.addPlayer(player);
        }
    }
}
