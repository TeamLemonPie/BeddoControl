package de.lemonpie.beddocontrol.model;

import de.lemonpie.beddocontrol.listener.BoardListener;
import de.lemonpie.beddocontrol.model.card.Card;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Board
{

	private List<BoardListener> listeners;
	private Card[] cards;
	private int[] readerIds;

	private int smallBlind;
	private int bigBlind;
	private int ante;

	public static final int NUMBER_OF_CARDS = 5;

	public Board()
	{
		listeners = new LinkedList<>();
		cards = new Card[NUMBER_OF_CARDS];
		readerIds = new int[NUMBER_OF_CARDS];

		Arrays.fill(cards, Card.EMPTY);
		Arrays.fill(readerIds, -2);
	}

	public Card getCard(int index) throws IndexOutOfBoundsException
	{
		return cards[index];
	}

	public void setCard(int index, Card card) throws IndexOutOfBoundsException
	{
		cards[index] = card;
		fireListener(listener -> listener.cardDidChangeAtIndex(index, card));
	}

	public int getReaderId(int index) throws IndexOutOfBoundsException
	{
		return readerIds[index];
	}

	public void setReaderId(int index, int readerId) throws IndexOutOfBoundsException
	{
		int oldReaderId = readerIds[index];
		readerIds[index] = readerId;
		fireListener(listener -> listener.boardReaderIdDidChange(index, readerId, oldReaderId));
	}

	public int getSmallBlind()
	{
		return smallBlind;
	}

	public void setSmallBlind(int smallBlind)
	{
		this.smallBlind = smallBlind;
		fireListener(listener -> listener.smallBlindDidChange(smallBlind));
	}

	public int getBigBlind()
	{
		return bigBlind;
	}

	public void setBigBlind(int bigBlind)
	{
		this.bigBlind = bigBlind;
		fireListener(listener -> listener.bigBlindDidChange(bigBlind));
	}

	public int getAnte()
	{
		return ante;
	}

	public void setAnte(int ante)
	{
		this.ante = ante;
		fireListener(listener -> listener.anteDidChange(ante));
	}

	public void addListener(BoardListener boardListener)
	{
		this.listeners.add(boardListener);
	}

	public void removeListener(BoardListener boardListener)
	{
		this.listeners.remove(boardListener);
	}

	private void fireListener(Consumer<BoardListener> consumer)
	{
		for(BoardListener boardListener : listeners)
		{
			consumer.accept(boardListener);
		}
	}

	public Card[] getCards()
	{
		return cards;
	}

	public int getNumberOfMissingCards()
	{
		return (int) Stream.of(cards).filter(card -> card.equals(Card.EMPTY)).count();
	}

	public void clearCards()
	{
		for(int i = 0; i < getCards().length; i++)
		{
			setCard(i, Card.EMPTY);
		}
	}
}
