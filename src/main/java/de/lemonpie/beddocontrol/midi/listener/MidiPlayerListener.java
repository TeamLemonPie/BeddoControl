package de.lemonpie.beddocontrol.midi.listener;

import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocontrol.listener.PlayerListener;
import de.lemonpie.beddocontrol.model.DataAccessible;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.model.card.Card;
import de.tobias.midi.Mapping;
import de.tobias.midi.action.Action;
import de.tobias.midi.feedback.FeedbackType;
import de.tobias.midi.mapping.MidiKey;

import java.util.List;
import java.util.Optional;

public class MidiPlayerListener implements PlayerListener
{
	private DataAccessible dataAccessible;

	public MidiPlayerListener(DataAccessible dataAccessible)
	{
		this.dataAccessible = dataAccessible;
	}

	@Override
	public void nameDidChange(Player player, String name)
	{

	}

	@Override
	public void twitchNameDidChange(Player player, String twitchName)
	{

	}

	@Override
	public void stateDidChange(Player player, PlayerState state)
	{
		Optional<Seat> seatOptional = dataAccessible.getSeats().getSeatByPlayerId(player.getId());
		if(!seatOptional.isPresent())
		{
			return;
		}

		int seatId = seatOptional.get().getId();

		Mapping mapping = Mapping.getCurrentMapping();
		for(Action action : mapping.getActions())
		{
			for(MidiKey key : action.getKeysForType(MidiKey.class))
			{
				if(!action.getPayload().containsKey("seatId"))
				{
					key.sendFeedback(FeedbackType.DEFAULT);
					continue;
				}

				if(action.getPayload().get("seatId").equalsIgnoreCase(String.valueOf(seatId)))
				{
					if(action.getActionType().equalsIgnoreCase(state.getMidiActionName()))
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

	@Override
	public void cardDidChangeAtIndex(Player player, int index, Card card)
	{

	}

	@Override
	public void chipsDidChange(Player player, int chips)
	{

	}

	@Override
	public void winProbabilityDidChange(Player player, int value)
	{

	}

	@Override
	public void isHighlightedDidChange(Player player, boolean value)
	{
		Optional<Seat> seatOptional = dataAccessible.getSeats().getSeatByPlayerId(player.getId());
		if(!seatOptional.isPresent())
		{
			return;
		}

		int seatId = seatOptional.get().getId();

		Mapping mapping = Mapping.getCurrentMapping();
		List<Action> actions = mapping.getActionsForType("highlight");
		for(Action action : actions)
		{
			for(MidiKey key : action.getKeysForType(MidiKey.class))
			{
				if(action.getPayload().get("seatId").equalsIgnoreCase(String.valueOf(seatId)))
				{
					if(value)
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

	@Override
	public void manageCardIdDidChange(Player player, int value)
	{

	}
}
