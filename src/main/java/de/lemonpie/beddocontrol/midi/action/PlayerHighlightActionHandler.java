package de.lemonpie.beddocontrol.midi.action;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.ui.Controller;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;

public class PlayerHighlightActionHandler extends ActionHandler
{
	private Controller controller;

	public PlayerHighlightActionHandler(Controller controller)
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
		if(!controller.isAllLocked())
		{
			int seatId = Integer.valueOf(action.getPayload().get("seatId"));
			Player player = controller.getPlayerBySeat(seatId);
			if(player != null)
			{
				player.setHighlighted(!player.isHighlighted());
				if(player.isHighlighted())
				{
					return FeedbackType.EVENT;
				}
			}
		}
		return FeedbackType.DEFAULT;
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

		if(action.getActionType().equalsIgnoreCase("highlight"))
		{
			if(player.isHighlighted())
			{
				return FeedbackType.EVENT;
			}
		}

		return FeedbackType.DEFAULT;
	}
}
