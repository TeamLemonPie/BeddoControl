package de.lemonpie.beddocontrol.network;

import com.google.gson.JsonElement;
import de.lemonpie.beddocontrol.CommandName;
import de.lemonpie.beddocontrol.Scope;

public class ControlCommandData {
    private final Scope scope;
    private final String command;
    private final int key;
    private JsonElement value;

    public ControlCommandData(Scope scope, CommandName command, int key) {
        this.scope = scope;
        this.command = command.getName();
        this.key = key;
    }

    public Scope getScope() {
        return scope;
    }

    public String getCommand() {
        return command;
    }

    public int getKey() {
        return key;
    }

    public JsonElement getValue() {
        return value;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }
}
