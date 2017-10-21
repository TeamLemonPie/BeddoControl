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
	private int[] readerIds;

	public Board() {
		listeners = new LinkedList<>();
		cards = new Card[5];
		readerIds = new int[5];

		Arrays.fill(cards, Card.EMPTY);
		Arrays.fill(readerIds, -2);
	}

	public Card getCard(int index) throws IndexOutOfBoundsException {
		return cards[index];
	}

	public void setCard(int index, Card card) throws IndexOutOfBoundsException {
		cards[index] = card;
		fireListener(listener -> listener.cardDidChangeAtIndex(index, card));
	}
	
	public int getReaderId(int index) throws IndexOutOfBoundsException {		
		return readerIds[index];
	}
	
	public void setReaderId(int index, int readerId) throws IndexOutOfBoundsException {
		readerIds[index] = readerId;
		fireListener(listener -> listener.boardReaderIdDidChange(index, readerId));
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
