package de.lemonpie.beddocontrol.network;

public interface ControlListener {
    String name();
    void execute(ControlCommandData data);
}
