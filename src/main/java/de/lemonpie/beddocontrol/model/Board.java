package de.lemonpie.beddocontrol.model;

import de.lemonpie.beddocontrol.model.card.Card;

public class Board {

    private Card[] cards;

    public Board() {
        cards = new Card[5];
    }

    public Card getCard(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= cards.length) {
            throw new IndexOutOfBoundsException("Index: " + index + " size: " + cards.length);
        }
        return cards[index];
    }

    public void setCard(int index, Card card) throws IndexOutOfBoundsException {
        if (index < 0 || index >= cards.length) {
            throw new IndexOutOfBoundsException("Index: " + index + " size: " + cards.length);
        }
        cards[index] = card;
    }

    public Card[] getCards() {
        return cards;
    }
}
