package de.lemonpie.beddocontrol.midi.action;

import de.lemonpie.beddocontrol.ui.Controller;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;
import javafx.application.Platform;

public class LockAllToggleActionHandler extends ActionHandler
{
	private Controller controller;

	public LockAllToggleActionHandler(Controller controller)
	{
		this.controller = controller;
	}

	@Override
	public String actionType()
	{
		return "lock_all_toggle";
	}

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		Platform.runLater(() -> controller.lockAll(!controller.isAllLocked()));
		if(controller.isAllLocked())
		{
			return  FeedbackType.DEFAULT;
		}
		else
		{
			return FeedbackType.EVENT;
		}
	}

	@Override
	public FeedbackType getCurrentFeedbackType(Action action)
	{
		return FeedbackType.DEFAULT;
	}
}
