package de.lemonpie.beddocontrol.network;

import de.lemonpie.beddocontrol.CommandName;

public interface Command {
    CommandName name();
    void execute(ControlCommandData data);
}
