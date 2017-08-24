package de.lemonpie.beddocontrol.model;

import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.model.listener.PlayerListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Player {

    private List<PlayerListener> listeners;

    private final int id;
    private String name;
    private String twitchName;

    private Card card1;
    private Card card2;

    private int chips;

    public Player(int id) {
        listeners = new LinkedList<>();

        this.id = id;
        this.name = "[Player]";
        this.twitchName = "[TwitchName]";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        fireListener(listener -> listener.nameDidChange(name));
    }

    public String getTwitchName() {
        return twitchName;
    }

    public void setTwitchName(String twitchName) {
        this.twitchName = twitchName;
        fireListener(listener -> listener.twitchNameDidChange(twitchName));
    }

    public Card getCard1() {
        return card1;
    }

    public void setCard1(Card card1) {
        this.card1 = card1;
        fireListener(listener -> listener.cardDidChangeAtIndex(0, card1));
    }

    public Card getCard2() {
        return card2;
    }

    public void setCard2(Card card2) {
        this.card2 = card2;
        fireListener(listener -> listener.cardDidChangeAtIndex(1, card2));
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
        fireListener(listener -> listener.chipsDidChange(chips));
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("twitchName", twitchName);
        map.put("card1", card1 != null ? card1.toString() : "back");
        map.put("card2", card2 != null ? card2.toString() : "back");
        return map;
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
}
