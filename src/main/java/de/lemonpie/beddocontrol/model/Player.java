package de.lemonpie.beddocontrol.model;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.model.listener.PlayerListener;

public class Player {

    private List<PlayerListener> listeners;

    private final int id;
    private int readerId;
    private String name;
    private String twitchName;

    private Card card1;
    private Card card2;

    private int chips;
    private PlayerState playerState;

    public Player(int id) {
        listeners = new LinkedList<>();

        this.id = id;
        this.readerId = -1;
        this.name = "[Player]";
        this.twitchName = "[TwitchName]";
        this.playerState = PlayerState.ACTIVE;
    }

    public int getId() {
        return id;
    }  

    public int getReaderId()
	{
		return readerId;
	}

	public void setReaderId(int readerId)
	{
		this.readerId = readerId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        fireListener(listener -> listener.nameDidChange(this, name));
    }

    public String getTwitchName() {
        return twitchName;
    }

    public void setTwitchName(String twitchName) {
        this.twitchName = twitchName;
        fireListener(listener -> listener.twitchNameDidChange(this, twitchName));
    }

    public Card getCard1() {
        return card1;
    }

    public void setCard1(Card card1) {
        this.card1 = card1;
        fireListener(listener -> listener.cardDidChangeAtIndex(this, 0, card1));
    }

    public Card getCard2() {
        return card2;
    }

    public void setCard2(Card card2) {
        this.card2 = card2;
        fireListener(listener -> listener.cardDidChangeAtIndex(this, 1, card2));
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
        fireListener(listener -> listener.chipsDidChange(this, chips));
    }

    public PlayerState getPlayerState()
	{
		return playerState;
	}

	public void setPlayerState(PlayerState playerState)
	{
		this.playerState = playerState;
	}

	public void addListener(PlayerListener playerListener) {
        this.listeners.add(playerListener);
    }

    public void removeListener(PlayerListener playerListener) {
        this.listeners.remove(playerListener);
    }

    private void fireListener(Consumer<PlayerListener> consumer) {
        for (PlayerListener playerListener : listeners) {
            consumer.accept(playerListener);
        }
    }

    public void setCard(int index, Card card) {
        if (index == 0) {
            setCard1(card);
        } else if (index == 1) {
            setCard2(card);
        } else {
            throw new IllegalArgumentException("Index is " + index + " should be 0 or 1");
        }
    }

	@Override
	public String toString()
	{
		return "Player [listeners=" + listeners + ", id=" + id + ", name=" + name + ", twitchName=" + twitchName + ", card1=" + card1 + ", card2=" + card2 + ", chips=" + chips + ", playerState=" + playerState + "]";
	}
}