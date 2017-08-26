package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocontrol.network.ControlCommandData;

public class ClearCommand extends ControlCommandData {

    public ClearCommand() {
        super("admin", "clear", -1);
    }

    public ClearCommand(int key) {
        super("admin", "clear", key);
    }
}
