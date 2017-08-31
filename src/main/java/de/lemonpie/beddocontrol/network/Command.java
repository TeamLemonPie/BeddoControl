package de.lemonpie.beddocontrol.network;

public interface Command {
    CommandName name();
    void execute(ControlCommandData data);
}
