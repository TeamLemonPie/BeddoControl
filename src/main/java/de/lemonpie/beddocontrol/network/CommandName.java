package de.lemonpie.beddocontrol.network;

public enum CommandName {

    CLEAR("clear"),
    CARD("card"),
    READER("reader"), // CONFIG

    PLAYER_NAME("name"),
    PLAYER_TWITCH("twitchName"),
    PLAYER_HIDE("hide"),
    PLAYER_CHIP("chip"),
    PLAYER_OP("player-op"),

    DATA("data");

    private String name;

    CommandName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
