package de.lemonpie.beddocontrol.model;

import de.lemonpie.beddocontrol.listener.BoardListener;
import de.lemonpie.beddocontrol.model.card.Card;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Board {

	private List<BoardListener> listeners;
	private Card[] cards;

	public Board() {
		listeners = new LinkedList<>();
		cards = new Card[5];

		Arrays.fill(cards, Card.EMPTY);
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
		fireListener(listener -> listener.cardDidChangeAtIndex(index, card));
	}

	public void addListener(BoardListener boardListener) {
		this.listeners.add(boardListener);
	}

	public void removeListener(BoardListener boardListener) {
		this.listeners.remove(boardListener);
	}

	private void fireListener(Consumer<BoardListener> consumer) {
		for (BoardListener boardListener : listeners) {
			consumer.accept(boardListener);
		}
	}

	public Card[] getCards() {
		return cards;
	}

	public int getNumberOfMissingCards() {
		return (int) Stream.of(cards).filter(c -> c == Card.EMPTY).count();
	}

	public void clearCards() {
		for (int i = 0; i < getCards().length; i++) {
			setCard(i, Card.EMPTY);
		}
	}
}
