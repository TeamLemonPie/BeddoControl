package de.lemonpie.beddocontrol.network;

public interface Command {
    String name();
    void execute(ControlCommandData data);
}
