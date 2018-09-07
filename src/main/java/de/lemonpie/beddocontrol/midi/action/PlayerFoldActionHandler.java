package de.lemonpie.beddocontrol.midi.action;

import de.lemonpie.beddocontrol.model.DataAccessible;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;

import java.util.List;

public class PlayerFoldActionHandler extends ActionHandler
{

	private DataAccessible controller;

	public PlayerFoldActionHandler(DataAccessible controller)
	{
		this.controller = controller;
	}

	@Override
	public String actionType()
	{
		return "fold";
	}

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		int playerId = Integer.valueOf(action.getPayload().get("playerId"));
		final List<Player> players = controller.getPlayers();

		if(players.size() > playerId && playerId >= 0)
		{
			Player player = players.get(playerId);
			player.setPlayerState(PlayerState.OUT_OF_ROUND);
		}
		return FeedbackType.DEFAULT;
	}
}
