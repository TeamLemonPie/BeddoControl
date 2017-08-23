package de.lemonpie.beddocontrol.network;

import com.google.gson.JsonElement;

public class ControlCommand {
    private final String scope;
    private final String command;
    private final int key;
    private JsonElement value;

    public ControlCommand(String scope, String command, int key) {
        this.scope = scope;
        this.command = command;
        this.key = key;
    }

    public String getScope() {
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
