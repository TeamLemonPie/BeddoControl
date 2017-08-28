package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocontrol.CommandName;
import de.lemonpie.beddocontrol.Scope;
import de.lemonpie.beddocontrol.network.ControlCommandData;

public class ClearSendCommand extends ControlCommandData {

    public ClearSendCommand() {
        super(Scope.ADMIN, CommandName.CLEAR, -1);
    }

    public ClearSendCommand(int key) {
        super(Scope.ADMIN, CommandName.CLEAR, key);
    }
}
