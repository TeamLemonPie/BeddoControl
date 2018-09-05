package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocontrol.listener.BoardListener;
import de.lemonpie.beddocontrol.model.card.Card;

public class BoardListenerImpl implements BoardListener
{
	private BoardController boardController;

	public BoardListenerImpl(BoardController boardController)
	{
		this.boardController = boardController;
	}

	@Override
	public void cardDidChangeAtIndex(int index, Card card)
	{
		boardController.setImageForImageView(ImageHandler.getImageForCard(card), index);
	}

	@Override
	public void boardReaderIdDidChange(int index, int readerId, int oldReaderId)
	{
	}

	@Override
	public void smallBlindDidChange(int newValue)
	{
	}

	@Override
	public void bigBlindDidChange(int newValue)
	{
	}

	@Override
	public void anteDidChange(int newValue)
	{
	}
}
