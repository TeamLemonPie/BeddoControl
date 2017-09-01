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

	private Card cardLeft;
	private Card cardRight;
	
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

    public Card getCardLeft() {
        return cardLeft;
    }

    public void setCardLeft(Card cardLeft) {
        this.cardLeft = cardLeft;
        fireListener(listener -> listener.cardDidChangeAtIndex(this, 0, cardLeft));
    }

    public Card getCardRight() {
        return cardRight;
    }

    public void setCardRight(Card cardRight) {
        this.cardRight = cardRight;
        fireListener(listener -> listener.cardDidChangeAtIndex(this, 1, cardRight));
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
            setCardLeft(card);
        } else if (index == 1) {
            setCardRight(card);
        } else {
            throw new IllegalArgumentException("Index is " + index + " should be 0 or 1");
        }
    }

	@Override
	public String toString()
	{
		return "Player [listeners=" + listeners + ", id=" + id + ", readerId=" + readerId + ", name=" + name + ", twitchName=" + twitchName + ", cardLeft=" + cardLeft + ", cardRight=" + cardRight + ", chips=" + chips + ", playerState=" + playerState + "]";
	}	
}