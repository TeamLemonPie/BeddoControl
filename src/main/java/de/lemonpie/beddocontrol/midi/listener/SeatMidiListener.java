package de.lemonpie.beddocontrol.midi.listener;

import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocommon.model.seat.SeatListener;
import de.lemonpie.beddocontrol.model.DataAccessible;
import de.lemonpie.beddocontrol.model.Player;
import de.tobias.midi.Mapping;
import de.tobias.midi.Midi;
import de.tobias.midi.action.Action;
import de.tobias.midi.feedback.Feedback;
import de.tobias.midi.feedback.FeedbackType;
import de.tobias.midi.mapping.MidiKey;

import java.util.List;
import java.util.Optional;

public class SeatMidiListener implements SeatListener
{
	private DataAccessible dataAccessible;

	public SeatMidiListener(DataAccessible dataAccessible)
	{
		this.dataAccessible = dataAccessible;
	}

	@Override
	public void readerIdDidChange(Seat seat, int readerId)
	{
	}

	@Override
	public void playerIdDidChange(Seat seat, int playerId)
	{
		Mapping mapping = Mapping.getCurrentMapping();
		List<Action> actions = mapping.getActions();
		Optional playerOptional = dataAccessible.getPlayer(playerId);

		for(Action action : actions)
		{
			for(MidiKey key : action.getKeysForType(MidiKey.class))
			{
				if(!action.getPayload().containsKey("seatId"))
				{
					continue;
				}

				if(action.getPayload().get("seatId").equalsIgnoreCase(String.valueOf(seat.getId())))
				{
					if(playerOptional.isPresent())
					{
						Player player = (Player)playerOptional.get();
						if(action.getActionType().equalsIgnoreCase(player.getState().getMidiActionName()))
						{
							key.sendFeedback(FeedbackType.EVENT);
							continue;
						}

						if(action.getActionType().equals("highlight") && player.isHighlighted())
						{
							key.sendFeedback(FeedbackType.EVENT);
							continue;
						}

						key.sendFeedback(FeedbackType.DEFAULT);
					}
					else
					{
						Feedback feedback = new Feedback(-112, 0);
						Midi.getInstance().sendMessage(feedback.getChannel(), key.getValue(), feedback.getValue());
					}
				}
			}
		}
	}
}
