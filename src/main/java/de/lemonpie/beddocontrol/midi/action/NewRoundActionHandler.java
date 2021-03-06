package de.lemonpie.beddocontrol.midi.action;

import de.lemonpie.beddocontrol.ui.Controller;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;
import javafx.application.Platform;

public class NewRoundActionHandler extends ActionHandler
{
	private Controller controller;

	public NewRoundActionHandler(Controller controller)
	{
		this.controller = controller;
	}

	@Override
	public String actionType()
	{
		return "new_round";
	}

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		if(!controller.isAllLocked())
		{
			Platform.runLater(controller::newRound);
		}
		return FeedbackType.NONE;
	}

	@Override
	public FeedbackType getCurrentFeedbackType(Action action)
	{
		return FeedbackType.DEFAULT;
	}
}
