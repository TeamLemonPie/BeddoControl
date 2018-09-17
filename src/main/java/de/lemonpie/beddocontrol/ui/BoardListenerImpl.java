package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocommon.model.card.Card;
import de.lemonpie.beddocontrol.listener.BoardListener;

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

	@Override
	public void lockDidChange(boolean newValue)
	{
	}
}
