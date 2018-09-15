package de.lemonpie.beddocontrol.midi.action;

import de.lemonpie.beddocontrol.model.DataAccessible;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;

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
		Player player = controller.getPlayerBySeat(seatId);
		if(player != null)
		{
			player.setState(PlayerState.ACTIVE);
		}
		return FeedbackType.EVENT;
	}

	@Override
	public FeedbackType getCurrentFeedbackType(Action action)
	{
		int seatId = Integer.valueOf(action.getPayload().get("seatId"));
		Player player = controller.getPlayerBySeat(seatId);
		if(player == null)
		{
			return FeedbackType.DEFAULT;
		}

		if(player.getState().getMidiActionName().equalsIgnoreCase(action.getActionType()))
		{
			return FeedbackType.EVENT;
		}
		else
		{
			return FeedbackType.DEFAULT;
		}
	}
}
