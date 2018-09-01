package de.lemonpie.beddocontrol.midi;

import de.lemonpie.beddocontrol.model.DataAccessible;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;

import java.util.List;

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
		int playerId = Integer.valueOf(action.getPayload().get("playerId"));
		final List<Player> players = controller.getPlayers();

		if(players.size() > playerId && playerId >= 0)
		{
			Player player = players.get(playerId);
			player.setPlayerState(PlayerState.ACTIVE);
		}
		return FeedbackType.NONE;
	}
}
