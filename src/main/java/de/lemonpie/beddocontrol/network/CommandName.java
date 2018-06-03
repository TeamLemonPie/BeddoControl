package de.lemonpie.beddocontrol.network;

public enum CommandName {

    CLEAR("clear"),
    CARD("card"),
	BOARD_CARD("board_card"),
	READER("reader"), // CONFIG

    PLAYER_NAME("name"),
    PLAYER_TWITCH("twitchName"),
    PLAYER_STATE("state"),
    PLAYER_CHIP("chip"),
    PLAYER_OP("player-op"),

	WIN_PROBABILITY("winprobability"),
	READER_COUNT("reader-count"),

	DATA("data"),

	BLOCK("block"),

	PAUSE("pause"),
	SMALL_BLIND("small-blind"),
	BIG_BLIND("big-blind"),
	ANTE("ante");

    private String name;

    CommandName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
