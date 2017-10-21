package de.lemonpie.beddocontrol.listener;

import de.lemonpie.beddocontrol.model.card.Card;

public interface BoardListener {
	void cardDidChangeAtIndex(int index, Card card);
	
	void boardReaderIdDidChange(int index, int readerId);
}