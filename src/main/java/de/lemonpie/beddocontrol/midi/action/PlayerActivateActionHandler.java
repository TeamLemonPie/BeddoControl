package de.lemonpie.beddocontrol.midi.action;

import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocommon.model.seat.SeatList;
import de.lemonpie.beddocontrol.model.DataAccessible;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;

import java.util.List;
import java.util.Optional;

public class PlayerActivateActionHandler extends ActionHandler
{
	private DataAccessible controller;

	public PlayerActivateActionHandler(DataAccessible controller)
	{
		this.controller = controller;
	}

	@Override
	public String actionType()
	{
		return "activate";
	}

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		int seatId = Integer.valueOf(action.getPayload().get("seatId"));
		final List<Player> players = controller.getPlayers();
		final SeatList seats = controller.getSeats();

		if(seats.size() > seatId && seatId >= 0)
		{
			Optional<Seat> seatOptional = seats.getObject(seatId);
			if(seatOptional.isPresent())
			{
				int playerId = seatOptional.get().getPlayerId();
				if(playerId != -1)
				{
					Player player = players.get(playerId-1);
					player.setPlayerState(PlayerState.ACTIVE);
				}
			}
		}
		return FeedbackType.DEFAULT;
	}
}
