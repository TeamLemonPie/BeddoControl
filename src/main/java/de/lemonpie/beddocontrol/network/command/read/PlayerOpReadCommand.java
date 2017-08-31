package de.lemonpie.beddocontrol.network.command.read;

import de.lemonpie.beddocontrol.model.DataAccessable;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.network.Command;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class PlayerOpReadCommand implements Command {
	
	private DataAccessable dataAccessable;
	
    @Override
    public CommandName name() {
        return CommandName.PLAYER_OP;
    }
    
    public PlayerOpReadCommand(DataAccessable dataAccessable)
	{	
		this.dataAccessable = dataAccessable;
	}

	@Override
    public void execute(ControlCommandData command) {
        String op = command.getValue().getAsString();
        if (op.equals("add")) {
            int playerId = command.getKey();

            Player player = new Player(playerId);
            dataAccessable.addPlayer(player);
        }
    }
}
