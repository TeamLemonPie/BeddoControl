package de.lemonpie.beddocontrol.midi.listener;

import de.lemonpie.beddocommon.model.card.Card;
import de.tobias.midi.Mapping;
import de.tobias.midi.action.Action;
import de.tobias.midi.feedback.FeedbackType;
import de.tobias.midi.mapping.MidiKey;

import java.util.List;

public class MidiBoardListener implements de.lemonpie.beddocontrol.listener.BoardListener
{
	@Override
	public void cardDidChangeAtIndex(int index, Card card)
	{
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
		System.out.println(newValue);
		Mapping mapping = Mapping.getCurrentMapping();
		List<Action> actions = mapping.getActionsForType("unlock_board");

		for(Action action : actions)
		{
			for(MidiKey key : action.getKeysForType(MidiKey.class))
			{
				if(newValue)
				{
					key.sendFeedback(FeedbackType.EVENT);
				}
				else
				{
					key.sendFeedback(FeedbackType.DEFAULT);
				}
			}
		}
	}
}
