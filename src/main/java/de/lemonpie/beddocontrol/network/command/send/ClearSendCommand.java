package de.lemonpie.beddocontrol.network.command.send;

import de.lemonpie.beddocontrol.network.ControlCommandData;

public class ClearSendCommand extends ControlCommandData {

    public ClearSendCommand() {
        super("admin", "clear", -1);
    }

    public ClearSendCommand(int key) {
        super("admin", "clear", key);
    }
}
