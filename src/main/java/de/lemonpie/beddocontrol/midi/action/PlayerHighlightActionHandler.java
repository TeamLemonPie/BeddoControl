package de.lemonpie.beddocontrol.midi.action;

import de.lemonpie.beddocontrol.model.DataAccessible;
import de.lemonpie.beddocontrol.model.Player;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;

import java.util.List;

public class PlayerHighlightActionHandler extends ActionHandler
{

	private DataAccessible controller;

	public PlayerHighlightActionHandler(DataAccessible controller)
	{
		this.controller = controller;
	}

	@Override
	public String actionType()
	{
		return "highlight";
	}

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		int playerId = Integer.valueOf(action.getPayload().get("playerId"));
		final List<Player> players = controller.getPlayers();

		if(players.size() > playerId && playerId >= 0)
		{
			Player player = players.get(playerId);
			player.setHighlighted(true);
		}
		return FeedbackType.DEFAULT;
	}
}
